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
    protected Long modelId;
    protected String modelCode;
    protected String modelName;
    protected String schemaName;
    protected String tableName;
    protected String remark;
    protected Boolean enableFlag;
}
