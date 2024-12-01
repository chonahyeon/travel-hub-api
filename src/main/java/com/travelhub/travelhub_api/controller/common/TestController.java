package com.travelhub.travelhub_api.controller.common;

import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class TestController {

    @GetMapping("/test")
    public String login() {
        log.info("사용자 진입. {}", LoginUserDTO.get());
        return "home";
    }

    @GetMapping("/home")
    public String mainForm() {
        return "home";
    }
}
