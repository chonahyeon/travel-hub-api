package com.travelhub.travelhub_api.common.component.common;

import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    /*
     * 로그인 정보 ThreadLocal 세팅
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String usId = authentication.getPrincipal().toString();
            LoginUserDTO.set(usId);
        }

        // 인증 비활성화 일때 테스트 유저로 세팅
        if (!TravelHubResource.authEnabled) {
            LoginUserDTO.set(TravelHubResource.testUser);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex){
        LoginUserDTO.remove();
    }
}
