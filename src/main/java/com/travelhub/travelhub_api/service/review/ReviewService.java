package com.travelhub.travelhub_api.service.review;

import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.common.response.ListResponse;
import com.travelhub.travelhub_api.controller.review.request.ReviewCreateRequest;
import com.travelhub.travelhub_api.controller.review.response.ReviewCreateResponse;
import com.travelhub.travelhub_api.controller.review.response.ReviewListResponse;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.dto.review.ContentReviewsDTO;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.ReviewRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsRepository;
import com.travelhub.travelhub_api.data.mysql.support.ReviewRepositorySupport;
import com.travelhub.travelhub_api.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.travelhub.travelhub_api.data.enums.ImageType.RV;
import static com.travelhub.travelhub_api.data.enums.common.ResponseCodes.INVALID_PARAM;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentsRepository contentsRepository;
    private final ImageRepository imageRepository;
    private final ReviewRepositorySupport reviewRepositorySupport;
    private final StorageService storageService;

    /*
     * 리뷰 조회
     */
    @Transactional(readOnly = true)
    public ListResponse<ReviewListResponse> findReviews(Long ctIdx, Pageable pageable) {
        List<ContentReviewsDTO> reviews = reviewRepositorySupport.findReviews(ctIdx, pageable);
        String imageDomain = storageService.getImageDomain();
        return new ListResponse<>(ReviewListResponse.ofList(reviews, imageDomain));
    }

    /*
     * 사용자 작성 리뷰 조회
     */
    @Transactional(readOnly = true)
    public ListResponse<ReviewListResponse> findReviewsUser(String usId, Pageable pageable) {
        List<ContentReviewsDTO> userReviews = reviewRepositorySupport.findUserReviews(usId, pageable);
        String imageDomain = storageService.getImageDomain();
        return new ListResponse<>(ReviewListResponse.ofList(userReviews, imageDomain));
    }

    /*
     * 리뷰 작성
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ReviewCreateResponse createReview(ReviewCreateRequest request) {
        ContentsEntity contents = contentsRepository.findById(request.ctIdx())
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "ctIdx"));

        // 리뷰 저장
        ReviewEntity reviewEntity = request.ofReview();
        ReviewEntity save = reviewRepository.save(reviewEntity);

        // 컨텐츠 리뷰 점수 업데이트
        updateReviewScore(contents);

        // 이미지 정보 저장 (없으면 null 로 반환)
        Long rvIdx = save.getRvIdx();
        Long igIdx = null;
        if (!request.igPath().isEmpty()) {
            ImageEntity imageEntity = request.ofImage(rvIdx);
            ImageEntity imageSave = imageRepository.save(imageEntity);
            igIdx = imageSave.getIgIdx();
        }

        return ReviewCreateResponse.builder()
                .rvIdx(rvIdx)
                .igIdx(igIdx)
                .build();
    }

    /*
     * 리뷰 본문 수정
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateReview(Long rvIdx, ReviewCreateRequest request) {
        String uId = LoginUserDTO.get();

        ContentsEntity contents = contentsRepository.findById(request.ctIdx())
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "ctIdx"));

        ReviewEntity reviewEntity = reviewRepository.findByUsIdAndRvIdx(uId, rvIdx)
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "rvIdx"));

        // 이미지 정보가 있을 때
        if (request.igIdx() != null) {
            ImageEntity imageEntity = imageRepository.findById(request.igIdx())
                    .orElseThrow(() -> new CustomException(INVALID_PARAM, "igIdx"));

            // 이미지 업데이트
            if (!request.igPath().isEmpty()) {
                imageEntity.updateImagePath(request.concatIgPath());
            } else {
                // 경로가 없으면 delete 처리 (idx O, path empty)
                imageRepository.deleteById(request.igIdx());
            }
        } else if (!request.igPath().isEmpty()){
            // 이미지 정보가 없다면 추가 (idx X, path exists)
            ImageEntity imageEntity = request.ofImage(rvIdx);
            imageRepository.save(imageEntity);
        }

        // 리뷰 업데이트
        reviewEntity.updateReview(request.rvText(), request.rvScore());

        // 컨텐츠 리뷰 점수 업데이트
        updateReviewScore(contents);
    }

    /*
     * 리뷰 삭제 진행
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteReview(Long rvIdx) {
        String uId = LoginUserDTO.get();
        ReviewEntity reviewEntity = reviewRepository.findByUsIdAndRvIdx(uId, rvIdx)
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "rvIdx"));

        // delete image and review
        reviewRepository.deleteById(rvIdx);
        imageRepository.deleteByIdxAndIgType(rvIdx, RV);

        ContentsEntity contents = contentsRepository.findById(reviewEntity.getCtIdx()).get();
        // update score
        updateReviewScore(contents);
    }

    /*
     * 컨텐츠 리뷰 점수 업데이트
     */
    private void updateReviewScore(ContentsEntity content) {
        List<ReviewEntity> contentReviews = reviewRepository.findByCtIdx(content.getCtIdx());

        double scores = contentReviews.stream()
                .mapToDouble(ReviewEntity::getRvScore)
                .sum();

        Double contentScore = scores / contentReviews.size();
        content.updateScore(contentScore);
    }
}
