package com.travelhub.travelhub_api.service.review;

import com.travelhub.travelhub_api.controller.review.request.ReviewCreateRequest;
import com.travelhub.travelhub_api.controller.review.response.ReviewListResponse;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.mysql.entity.ReviewEntity;
import com.travelhub.travelhub_api.data.mysql.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewListResponse> findReviews(Long ctIdx) {
        List<ReviewEntity> contentReviews = reviewRepository.findByCtIdx(ctIdx);
        return ReviewListResponse.ofList(contentReviews);
    }

    @Transactional
    public void createReview(ReviewCreateRequest request) {
        ReviewEntity reviewEntity = request.ofEntity();
        reviewRepository.save(reviewEntity);
    }

    @Transactional
    public void deleteReview(Long rvIdx) {
        String uId = LoginUserDTO.get();
        List<ReviewEntity> userReview = reviewRepository.findByRvIdxAndUId(rvIdx, uId);

        reviewRepository.deleteById(rvIdx);
    }
}
