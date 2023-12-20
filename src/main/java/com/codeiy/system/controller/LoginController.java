package com.codeiy.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.codeiy.common.util.R;
import com.codeiy.system.dto.LoginDTO;
import com.codeiy.system.service.LoginService;
import com.codeiy.system.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 用户登录
     *
     * @param loginDTO 用户登录信息
     * @return JsonResult<String>
     */
    @SaIgnore
    @PostMapping("/oauth2/token")
    public R<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = loginService.login(loginDTO);
        return R.ok(loginVO);
    }

    /**
     * 退出登录
     *
     * @return JsonResult<String>
     */
    @SaIgnore
    @PostMapping("/token/logout")
    public R<String> logout() {
        loginService.logout();
        return R.ok();
    }

}
