package com.codeiy.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Collection;

@Data
@TableName(schema = "information_schema", value = "TABLES")
public class Model {
    private String tableSchema;
    private String tableName;
    private String tableComment;
    @TableField(exist = false)
    private Collection<ModelField> fields;
}
