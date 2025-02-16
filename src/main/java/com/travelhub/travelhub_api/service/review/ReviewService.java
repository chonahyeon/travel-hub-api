package com.travelhub.travelhub_api.service.review;

import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.review.request.ReviewCreateRequest;
import com.travelhub.travelhub_api.controller.review.response.ReviewCreateResponse;
import com.travelhub.travelhub_api.controller.review.response.ReviewListResponse;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.ReviewRepository;
import com.travelhub.travelhub_api.data.mysql.repository.contents.ContentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.travelhub.travelhub_api.data.enums.ImageType.RV;
import static com.travelhub.travelhub_api.data.enums.common.ErrorCodes.INVALID_PARAM;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentsRepository contentsRepository;
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public List<ReviewListResponse> findReviews(Long ctIdx) {
        List<ReviewEntity> contentReviews = reviewRepository.findByCtIdx(ctIdx);
        return ReviewListResponse.ofList(contentReviews);
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> findReviewsUser(String uId) {
        List<ReviewEntity> userReviews = reviewRepository.findByUsId(uId);
        return ReviewListResponse.ofList(userReviews);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ReviewCreateResponse createReview(ReviewCreateRequest request) {
        ContentsEntity contents = contentsRepository.findById(request.ctIdx())
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "ctIdx"));

        // 리뷰 저장
        ReviewEntity reviewEntity = request.ofReview();
        ReviewEntity save = reviewRepository.save(reviewEntity);

        // 컨텐츠 리뷰 점수 업데이트
        updateReviewScore(contents);

        return ReviewCreateResponse.builder()
                .rvIdx(save.getRvIdx())
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateReview(Long rvIdx, ReviewCreateRequest request) {
        String uId = LoginUserDTO.get();

        ContentsEntity contents = contentsRepository.findById(request.ctIdx())
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "ctIdx"));

        ReviewEntity reviewEntity = reviewRepository.findByUsIdAndRvIdx(uId, rvIdx)
                .orElseThrow(() -> new CustomException(INVALID_PARAM, "rvIdx"));

        reviewEntity.updateReview(request.rvText(), request.rvScore());

        // update score
        updateReviewScore(contents);
    }

    private void updateReviewScore(ContentsEntity content) {
        List<ReviewEntity> contentReviews = reviewRepository.findByCtIdx(content.getCtIdx());

        double scores = contentReviews.stream()
                .mapToDouble(ReviewEntity::getRvScore)
                .sum();

        Double contentScore = scores / contentReviews.size();
        content.updateScore(contentScore);
    }

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
}
