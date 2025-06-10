package com.travelhub.travelhub_api.service.auth;

import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.controller.auth.request.SignUpRequest;
import com.travelhub.travelhub_api.data.mysql.entity.common.UserEntity;
import com.travelhub.travelhub_api.data.mysql.repository.common.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.travelhub.travelhub_api.data.enums.common.ResponseCodes.TOKEN_INVALID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원 가입
     * 해당 uri에 들어오기 전에  oauth 로그인 정보는 이미 저장된 상태이므로 업데이트만 진행.
     * @param usId 고유 id
     * @param request 회원 가입 정보
     */
    @Transactional
    public void signUp(String usId, SignUpRequest request) {
        UserEntity userEntity = userRepository.findById(usId)
                .orElseThrow(() -> new AuthException(TOKEN_INVALID));

        userEntity.updateSignUpInfo(request);
        userRepository.save(userEntity);
    }
}
