package com.travelhub.travelhub_api.common.configuration;

import com.travelhub.travelhub_api.common.component.auth.JwtAuthenticationFilter;
import com.travelhub.travelhub_api.common.component.auth.JwtExceptionHandlingFilter;
import com.travelhub.travelhub_api.common.component.auth.OAuth2SuccessHandler;
import com.travelhub.travelhub_api.service.auth.OAuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final OAuthUserService OAuthUserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionHandlingFilter jwtExceptionHandlingFilter;

    /**
     * 시큐리티 미적용 항목
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    /*
     * AntPathRequestMatcher 단순히 oauth 만 제외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       // 인증 범위 및 정책 설정
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(
                                        new AntPathRequestMatcher("/"),
                                        new AntPathRequestMatcher("/login/**"),
                                        new AntPathRequestMatcher("/travel/v1/auth/**"),
                                        new AntPathRequestMatcher("/home")
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                );

        // oauth 및 token filter 설정
        http.oauth2Login(oauth2Configurer -> oauth2Configurer.userInfoEndpoint(
                userInfoEndpointConfig -> userInfoEndpointConfig.userService(OAuthUserService))
                .successHandler(oAuth2SuccessHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionHandlingFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
