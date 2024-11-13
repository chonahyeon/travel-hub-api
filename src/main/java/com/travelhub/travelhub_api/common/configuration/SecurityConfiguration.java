package com.travelhub.travelhub_api.common.configuration;

import com.travelhub.travelhub_api.service.OAuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final OAuthUserService oAuthUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 인증 범위 설정
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/login/oauth2/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                );

        // oauth 설정
        http.oauth2Login(oauth2Configurer ->
                oauth2Configurer.userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(oAuthUserService)
                )
        );

        return http.build();
    }
}
