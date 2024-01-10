package com.codeiy.common.dynamicModel;

import cn.hutool.core.collection.CollectionUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 条件
 */
@Data
@Slf4j
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

    private List<String> fields;
    private String predicate;
    private List<Object> values;

    public String getField() {
        if (CollectionUtil.isEmpty(fields)) {
            return null;
        }
        return CollectionUtil.getFirst(fields);
    }

    public void setField(String field) {
        fields = CollectionUtil.newArrayList();
        fields.add(field);
    }

    public Object getValue() {
        if (CollectionUtil.isEmpty(values)) {
            return null;
        }
        return CollectionUtil.getFirst(values);
    }

    public void setValue(Object value) {
        values = CollectionUtil.newArrayList();
        values.add(value);
    }

    public void addCondition(MPJLambdaWrapper<?> queryWrapper) {
        if (CollectionUtil.isEmpty(fields)){
            log.warn("fields is empty");
            return;
        }
        if (CollectionUtil.isEmpty(values) && !NULL.equals(predicate) && !NOT_NULL.equals(predicate)){
            log.warn("values is empty");
            return;
        }
        if (fields.size() != values.size() && !NULL.equals(predicate) && !NOT_NULL.equals(predicate)) {
            log.warn("fields.size != values.size");
            return;
        }
        int size = fields.size();
        switch (predicate) {
            default:
                break;
            case "EQ":
                for(int i = 0; i < size; i++) {
                    queryWrapper.eq(fields.get(i), values.get(i));
                }
                break;
            case "NE":
                for(int i = 0; i < size; i++) {
                    queryWrapper.ne(fields.get(i), values.get(i));
                }
                break;
            case "NULL":
                for(int i = 0; i < size; i++) {
                    queryWrapper.isNull(fields.get(i));
                }
                break;
            case "NOT_NULL":
                for(int i = 0; i < size; i++) {
                    queryWrapper.isNotNull(fields.get(i));
                }
                break;
            case "GT":
                for(int i = 0; i < size; i++) {
                    queryWrapper.gt(fields.get(i), values.get(i));
                }
                break;
            case "GE":
                for(int i = 0; i < size; i++) {
                    queryWrapper.ge(fields.get(i), values.get(i));
                }
                break;
            case "LT":
                for(int i = 0; i < size; i++) {
                    queryWrapper.lt(fields.get(i), values.get(i));
                }
                break;
            case "LE":
                for(int i = 0; i < size; i++) {
                    queryWrapper.le(fields.get(i), values.get(i));
                }
                break;
            case "LIKE":
                for(int i = 0; i < size; i++) {
                    queryWrapper.like(fields.get(i), values.get(i));
                }
                break;
            case "NOT_LIKE":
                for(int i = 0; i < size; i++) {
                    queryWrapper.notLike(fields.get(i), values.get(i));
                }
                break;
            case "IN":
                for(int i = 0; i < size; i++) {
                    queryWrapper.in(fields.get(i), values.get(i));
                }
                break;
            case "NOT_IN":
                for(int i = 0; i < size; i++) {
                    queryWrapper.notIn(fields.get(i), values.get(i));
                }
                break;
        }
    }
}
