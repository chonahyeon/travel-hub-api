package com.travelhub.travelhub_api.controller.review;

import com.travelhub.travelhub_api.controller.common.response.ApiResponse;
import com.travelhub.travelhub_api.controller.review.request.ReviewCreateRequest;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;

@RestController
@RequestMapping(API_V1_REVIEW)
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 목록 조회
     * GET /travel/v1/review/list?ctIdx=1
     */
    @GetMapping(LIST)
    public ResponseEntity<Object> findReviews(@RequestParam Long ctIdx, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.findReviews(ctIdx, pageable)));
    }

    /**
     * 사용자 리뷰 목록 조회
     * GET /travel/v1/review/list-user
     */
    @GetMapping(LIST_USER)
    public ResponseEntity<Object> findReviewsByUser(Pageable pageable) {
        String uId = LoginUserDTO.get();

        return ResponseEntity.ok(ApiResponse.success(reviewService.findReviewsUser(uId, pageable)));
    }

    /**
     * 리뷰 작성
     * POST /travel/v1/review
     * @param request request body
     */
    @PostMapping
    public ResponseEntity<Object> createReview(@RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.createReview(request)));
    }

    /**
     * 리뷰 업데이트
     * PUT /travel/v1/review
     */
    @PatchMapping("/{rvIdx}")
    public ResponseEntity<Object> updateReview(@PathVariable Long rvIdx, @RequestBody ReviewCreateRequest request){
        reviewService.updateReview(rvIdx, request);

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 리뷰 삭제
     * DELETE /travel/v1/review
     * @param rvIdx 리뷰 idx
     */
    @DeleteMapping("/{rvIdx}")
    public ResponseEntity<Object> deleteReview(@PathVariable Long rvIdx) {
        reviewService.deleteReview(rvIdx);

        return ResponseEntity.ok(ApiResponse.success());
    }
}
