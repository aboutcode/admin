package com.codeiy.system.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.codeiy.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.util.Date;

@Schema(description = "系统访问记录表")
@Data
public class SysLoginInfo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "序号")
    @ExcelProperty(value = "序号")
    private Long infoId;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    @ExcelProperty(value = "用户账号")
    private String userName;

    /**
     * 状态 0成功 1失败
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态(1=登录成功,2=退出成功,0=登录失败)")
    private String status;

    /**
     * 地址
     */
    @Schema(description = "地址")
    @ExcelProperty(value = "地址")
    private String ipaddr;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @ExcelProperty(value = "描述")
    private String msg;

    /**
     * 访问时间
     */
    @Schema(description = "访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "访问时间")
    private Date accessTime;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    @ExcelProperty(value = "部门ID")
    private Long deptId;
}
