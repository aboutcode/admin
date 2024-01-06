package com.codeiy.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Collection;

@Data
@TableName(schema = "information_schema", value = "TABLES")
public class TableInfo {
    protected String tableSchema;
    protected String tableName;
    protected String tableComment;
    @TableField(exist = false)
    protected Collection<ColumnInfo> fields;
}
