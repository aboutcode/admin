package com.codeiy.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.codeiy.common.constant.CommonConstants;
import com.codeiy.common.enums.DeviceType;
import com.codeiy.common.exception.CheckedException;
import com.codeiy.common.log.SysLoginLogEvent;
import com.codeiy.common.util.IpUtils;
import com.codeiy.common.util.SecurityUtils;
import com.codeiy.common.util.SpringContextHolder;
import com.codeiy.system.dto.LoginDTO;
import com.codeiy.system.entity.SysLoginInfo;
import com.codeiy.system.mapper.SysUserMapper;
import com.codeiy.system.service.LoginService;
import com.codeiy.system.vo.LoginVO;
import com.codeiy.system.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserMapper userMapper;

//    @Autowired
//    private ISysPermissionService iSysPermissionService;

    /**
     * 登录
     *
     * @param loginDTO LoginDTO
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        UserVO sysUser = loadUserByUsername(loginDTO.getUsername());

        if (!BCrypt.checkpw(loginDTO.getPassword(), sysUser.getPassword())) {
            throw new CheckedException("账号密码不正确");
        }

        // 构建用户信息
        LoginDTO loginUser = buildLoginUser(sysUser);

        // 生成token
        SecurityUtils.loginByDevice(loginUser, DeviceType.PC);

        LoginVO loginVO = new LoginVO();

        loginVO.setAccessToken(StpUtil.getTokenValue());

        return loginVO;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        Long deptId = SecurityUtils.getDeptId();
        String username = SecurityUtils.getUsername();

        // 发送异步日志事件
        SysLoginInfo sysLoginInfo = new SysLoginInfo();
        sysLoginInfo.setStatus(CommonConstants.LOGIN_SUCCESS);
        sysLoginInfo.setUserName(username);
        sysLoginInfo.setDeptId(deptId);
        sysLoginInfo.setIpaddr(IpUtils.getIpAddr());
        sysLoginInfo.setMsg("退出登录");
        // 发送异步日志事件
        sysLoginInfo.setCreateTime(DateUtil.date().toLocalDateTime());
        sysLoginInfo.setCreateBy(username);
        sysLoginInfo.setUpdateBy(username);
        SpringContextHolder.publishEvent(new SysLoginLogEvent(sysLoginInfo));

        StpUtil.logout();
    }

    /**
     * 通过账号名称登录
     *
     * @param username 账号名称
     * @return 用户
     */
    private UserVO loadUserByUsername(String username) {
        UserVO sysUser = userMapper.getUserVoByUsername(username);
        if (sysUser == null) {
            throw new CheckedException("登录用户：" + username + " 不存在.");
        }

        // 发送异步日志事件
        Long deptId = sysUser.getDeptId();
        SysLoginInfo sysLoginInfo = new SysLoginInfo();
        sysLoginInfo.setStatus(CommonConstants.LOGIN_SUCCESS);
        sysLoginInfo.setUserName(username);
        sysLoginInfo.setDeptId(deptId);
        sysLoginInfo.setIpaddr(IpUtils.getIpAddr());
        sysLoginInfo.setMsg("登录成功");

        if (sysUser.getLockFlag().equals("1")) {
            String error = "账号已被冻结";
            sysLoginInfo.setMsg(error);
            sysLoginInfo.setStatus(CommonConstants.LOGIN_FAIL);
            // 发送异步日志事件
            sysLoginInfo.setCreateTime(DateUtil.date().toLocalDateTime());
            sysLoginInfo.setCreateBy(username);
            sysLoginInfo.setUpdateBy(username);
            SpringContextHolder.publishEvent(new SysLoginLogEvent(sysLoginInfo));
            throw new CheckedException(error);
        }

        // 发送异步日志事件
        sysLoginInfo.setCreateTime(DateUtil.date().toLocalDateTime());
        sysLoginInfo.setCreateBy(username);
        sysLoginInfo.setUpdateBy(username);
        SpringContextHolder.publishEvent(new SysLoginLogEvent(sysLoginInfo));
        return sysUser;
    }

    /**
     * 构建用户信息
     *
     * @param sysUser SysUser
     * @return LoginUser
     */
    private LoginDTO buildLoginUser(UserVO sysUser) {
        LoginDTO loginUser = new LoginDTO();

        loginUser.setUserId(sysUser.getUserId());
        loginUser.setUsername(sysUser.getUsername());
        loginUser.setDeptId(sysUser.getDeptId());

//        // 角色集合
//        Set<String> roles = iSysPermissionService.getRolePermission(sysUser.getUserId());
//        // 权限集合
//        Set<String> permissions = iSysPermissionService.getMenuPermission(sysUser.getUserId());

//        loginUser.setPermissions(permissions);
//        loginUser.setRoles(roles);
        return loginUser;
    }
}
