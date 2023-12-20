package com.codeiy.common.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.codeiy.common.enums.DeviceType;
import com.codeiy.system.dto.LoginDTO;

public class SecurityUtils {
    public static final String LOGIN_USER_KEY = "loginUser";

    /**
     * 登录系统 基于 设备类型 针对相同用户体系不同设备
     * @param loginUser 登录用户信息
     */
    public static void loginByDevice(LoginDTO loginUser, DeviceType deviceType) {
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        StpUtil.login(loginUser.getUserId(), deviceType.name());
        setLoginUser(loginUser);
    }

    /**
     * 设置用户数据(多级缓存)
     * @param loginUser LoginDTO
     */
    public static void setLoginUser(LoginDTO loginUser) {
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     * @return LoginDTO
     */
    public static LoginDTO getLoginUser() {
        LoginDTO loginUser = (LoginDTO) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (loginUser != null) {
            return loginUser;
        }
        loginUser = (LoginDTO) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        return loginUser;
    }

    /**
     * 获取部门ID
     * @return Long
     */
    public static Long getDeptId() {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取用户账户
     * @return String
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    /**
     * 是否为管理员
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 是否为管理员
     * @return boolean
     */
    public static boolean isAdmin() {
        return isAdmin(getLoginUser().getUserId());
    }

//    public static String getUsername() {
//        if (!StpUtil.isLogin()) {
//            return null;
//        }
//		// 连缀写法追加多个
//		StpUtil.login(10001, SaLoginConfig
//				.setExtra("username", "zhangsan")
//				.setExtra("age", 18)
//				.setExtra("role", "超级管理员"));
//        Object username = StpUtil.getExtra("username");
//        if (ObjectUtil.isNotEmpty(username)) {
//            return username.toString();
//        }
//        return StpUtil.getLoginIdAsString();
//    }
}
