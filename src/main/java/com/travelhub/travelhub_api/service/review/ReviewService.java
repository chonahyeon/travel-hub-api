package com.travelhub.travelhub_api.service.review;

import com.travelhub.travelhub_api.controller.review.request.ReviewCreateRequest;
import com.travelhub.travelhub_api.controller.review.request.ReviewImageRequest;
import com.travelhub.travelhub_api.controller.review.response.ReviewListResponse;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ImageRepository;
import com.travelhub.travelhub_api.data.mysql.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.travelhub.travelhub_api.data.enums.ImageType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;

    public List<ReviewListResponse> findReviews(Long ctIdx) {
        List<ReviewEntity> contentReviews = reviewRepository.findByCtIdx(ctIdx);
        return ReviewListResponse.ofList(contentReviews);
    }

    public List<ReviewListResponse> findReviewsUser(String uId) {
        List<ReviewEntity> userReviews = reviewRepository.findByUId(uId);
        return ReviewListResponse.ofList(userReviews);
    }

    @Transactional
    public void createReview(ReviewCreateRequest request) {
        // 리뷰 저장
        ReviewEntity reviewEntity = request.ofReview();
        ReviewEntity save = reviewRepository.save(reviewEntity);

        // 이미지 저장
        Long rvIdx = save.getRvIdx();
        List<ReviewImageRequest> reviewImageRequests = request.imageUrls();

        List<ImageEntity> images = reviewImageRequests.stream()
                .map(url -> request.ofImage(rvIdx, url.igUrl()))
                .toList();

        imageRepository.saveAll(images);
    }

    @Transactional
    public void updateReview(Long rvIdx, ReviewCreateRequest request) {
        ReviewEntity reviewEntity = reviewRepository.findById(rvIdx)
                .orElseThrow(() -> new RuntimeException());

        reviewEntity.updateReview(request.rvText());

        List<ReviewImageRequest> reviewImageRequests = request.imageUrls();

        List<ImageEntity> images = reviewImageRequests.stream()
                .map(image -> request.ofImage(rvIdx, image.igUrl()))
                .toList();
        imageRepository.saveAll(images);
    }

    @Transactional
    public void deleteReview(Long rvIdx) {
        String uId = LoginUserDTO.get();
        Boolean bExist = reviewRepository.existsByRvIdxAndUId(rvIdx, uId);

        // 이미지 삭제 및 리뷰 삭제
        if (bExist) {
            reviewRepository.deleteById(rvIdx);
            imageRepository.deleteByIdxAndIgType(rvIdx, RV);
        } else {
            throw new RuntimeException();
        }
    }
}
