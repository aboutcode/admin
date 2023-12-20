package com.codeiy.system.service;

import com.codeiy.system.dto.LoginDTO;
import com.codeiy.system.vo.LoginVO;

public interface LoginService {

    /**
     * 登录
     *
     * @param loginDTO LoginDTO
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 退出登录
     */
    void logout();

}
