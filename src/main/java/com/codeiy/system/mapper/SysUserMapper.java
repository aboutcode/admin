package com.codeiy.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.system.dto.UserDTO;
import com.codeiy.system.entity.SysUser;
import com.codeiy.system.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 通过用户名查询用户信息（含有角色信息）
     * @param username 用户名
     * @return userVo
     */
    UserVO getUserVoByUsername(String username);

    /**
     * 分页查询用户信息（含角色）
     * @param page 分页
     * @param userDTO 查询参数
     * @param dataScope
     * @return list
     */
    IPage<UserVO> getUserVosPage(Page page, @Param("query") UserDTO userDTO);

    /**
     * 通过ID查询用户信息
     * @param id 用户ID
     * @return userVo
     */
    UserVO getUserVoById(Long id);

    /**
     * 查询用户列表
     * @param userDTO 查询条件
     * @param dataScope 数据权限声明
     * @return
     */
    List<UserVO> selectVoList(@Param("query") UserDTO userDTO);

}
