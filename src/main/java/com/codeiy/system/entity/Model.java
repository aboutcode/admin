package com.codeiy.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(schema = "admin", value = "model_info")
public class Model {
    /**
     * 编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long modelId;
    private String modelCode;
    private String modelName;
    private String schemaName;
    private String tableName;
    private String remark;
}
