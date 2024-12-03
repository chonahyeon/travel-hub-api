package com.travelhub.travelhub_api.common.configuration.web;

import com.travelhub.travelhub_api.common.component.common.LoggingInterceptor;
import com.travelhub.travelhub_api.common.component.common.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns(ALL_PATH_PATTERN);

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(ALL_PATH_PATTERN);
    }
}
