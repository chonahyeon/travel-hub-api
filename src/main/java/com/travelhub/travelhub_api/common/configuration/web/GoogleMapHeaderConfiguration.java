package com.travelhub.travelhub_api.common.configuration.web;

import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleMapHeaderConfiguration {

    @Bean
    public RequestInterceptor googleAuthHeaderInterceptor() {
        // 여기서 헤더에 값 추가
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json; charset=utf-8");
            requestTemplate.header("X-Goog-Api-Key", TravelHubResource.googleMapKey);
        };
    }
}
