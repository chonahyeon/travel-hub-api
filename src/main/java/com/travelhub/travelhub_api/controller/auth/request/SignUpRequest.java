package com.travelhub.travelhub_api.controller.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        String id,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Pattern(regexp = "^[가-힣]*$",
                message = "한글만 입력하세요.")
        String name,

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        String email,

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        String nickName
) {
}
