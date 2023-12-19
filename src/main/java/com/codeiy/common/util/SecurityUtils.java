package com.codeiy.common.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;

public class SecurityUtils {
    public static String getUsername() {
        if (!StpUtil.isLogin()) {
            return null;
        }
//		// 连缀写法追加多个
//		StpUtil.login(10001, SaLoginConfig
//				.setExtra("username", "zhangsan")
//				.setExtra("age", 18)
//				.setExtra("role", "超级管理员"));
        Object username = StpUtil.getExtra("username");
        if (ObjectUtil.isNotEmpty(username)) {
            return username.toString();
        }
        return StpUtil.getLoginIdAsString();
    }
}
