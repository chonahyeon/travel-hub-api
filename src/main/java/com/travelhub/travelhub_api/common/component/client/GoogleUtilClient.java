package com.travelhub.travelhub_api.common.component.client;

import com.travelhub.travelhub_api.data.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "utilClient", url = "https://www.googleapis.com/oauth2/v2")
public interface GoogleUtilClient {

    /*
     * GET /userinfo
     * user 상세 정보 조회
     */
    @GetMapping(value = "/userinfo")
    UserInfoResponse getUserResources();
}
