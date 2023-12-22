package com.codeiy.common.dynamicModel;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.Collection;

/**
 * 条件
 */
@Data
public class Criteria {
    public static final String EQ = "=";
    public static final String NE = "<>";
    public static final String NULL = "IS NULL";
    public static final String NOT_NULL = "IS NOT NULL";
    public static final String GT = ">";
    public static final String GE = ">=";
    public static final String LT = "<";
    public static final String LE = "<=";
    public static final String LIKE = "LIKE";
    public static final String NOT_LIKE = "NOT LIKE";
    public static final String IN = "IN";
    public static final String NOT_IN = "NOT IN";

    private Collection<String> fields;
    private String predicate;
    private Collection<String> values;

    public String getField() {
        if (CollectionUtil.isEmpty(fields)) {
            return null;
        }
        return CollectionUtil.getFirst(fields);
    }

    public void setField(String field) {
        if (CollectionUtil.isEmpty(fields)) {
            fields = CollectionUtil.newArrayList();
        }
        fields.add(field);
    }

    public String getValue() {
        if (CollectionUtil.isEmpty(values)) {
            return null;
        }
        return CollectionUtil.getFirst(values);
    }

    public void setValue(String value) {
        if (CollectionUtil.isEmpty(values)) {
            values = CollectionUtil.newArrayList();
        }
        values.add(value);
    }
}
