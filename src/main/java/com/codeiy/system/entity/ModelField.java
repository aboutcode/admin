package com.codeiy.system.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName(schema = "information_schema", value = "COLUMNS")
public class ModelField {
    public static final String PRIMARY_KEY = "PRI";
    private String tableSchema;
    private String tableName;
    private String columnName;
    private Integer ordinalPosition;
    private String columnDefault;
    private String isNullable;
    private String dataType;
//    private Integer characterMaximumLength;
//    private Integer numericPrecision;
//    private Integer numericScale;
    private String columnKey;
    private String extra;
    private String columnComment;
    public Type getJavaType() {
        if (StrUtil.isBlank(dataType)) {
            return Object.class;
        }
        switch (dataType.toLowerCase()) {
            default:
            case "blob":
            case "mediumblob":
            case "longblob":
            case "binary":
            case "varbinary":
                return Object.class;
            case "char":
            case "json":
            case "text":
            case "longtext":
            case "mediumtext":
            case "varchar":
                return String.class;
            case "bigint":
                return Long.class;
            case "int":
            case "tinyint":
            case "smallint":
                return Integer.class;
            case "float":
            case "double":
                return Double.class;
            case "decimal":
                return BigDecimal.class;
            case "date":
            case "time":
            case "datetime":
            case "timestamp":
                return Date.class;
            case "enum":
                return Enum.class;
            case "bit":
                return Boolean.class;
            case "set":
                return List.class;
        }
    }
}
