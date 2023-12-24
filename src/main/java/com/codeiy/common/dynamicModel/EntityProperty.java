package com.codeiy.common.dynamicModel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.codeiy.common.util.ClassUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
public class EntityProperty {
    private String fieldName;
    private String fieldType;
    private Class<?> fieldClass;
    private JSONObject fieldAnnotations;

    public Set<String> getAnnotationNames() {
        if (CollectionUtil.isNotEmpty(fieldAnnotations)){
            return fieldAnnotations.keySet();
        }
        return Collections.EMPTY_SET;
    }
    public Set<String> getAnnotationPropertyName(String annotationName) {
        if (StrUtil.isBlank(annotationName)){
            return Collections.EMPTY_SET;
        }
        if (CollectionUtil.isEmpty(fieldAnnotations)){
            return Collections.EMPTY_SET;
        }
        JSONObject properties = fieldAnnotations.getJSONObject(annotationName);
        if (CollectionUtil.isEmpty(properties)){
            return Collections.EMPTY_SET;
        }
        return properties.keySet();
    }
    public Object getAnnotationPropertyValue(String annotationName, String propertyName) {
        if (StrUtil.isBlank(annotationName) || StrUtil.isBlank(propertyName)){
            return null;
        }
        if (CollectionUtil.isEmpty(fieldAnnotations)){
            return null;
        }
        JSONObject properties = fieldAnnotations.getJSONObject(annotationName);
        if (CollectionUtil.isEmpty(properties)){
            return null;
        }
        return properties.get(propertyName);
    }
    public Class<?> getType() {
        if (fieldClass != null) {
            return fieldClass;
        }
        if (StrUtil.isBlank(fieldType)) {
            fieldClass = Object.class;
            return fieldClass;
        }
        switch (fieldType.toLowerCase()) {
            case "string":
                fieldClass = String.class;
                break;
            case "integer":
                fieldClass = Integer.class;
                break;
            case "long":
                fieldClass = Long.class;
                break;
            case "double":
                fieldClass = Double.class;
                break;
            case "float":
                fieldClass = Float.class;
                break;
            case "boolean":
                fieldClass = Boolean.class;
                break;
            case "date":
                fieldClass = java.util.Date.class;
                break;
            case "bigdecimal":
                fieldClass = java.math.BigDecimal.class;
                break;
            default:
                try {
                    fieldClass = ClassUtils.forName(fieldType, ClassUtils.getDefaultClassLoader());
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                    fieldClass = Object.class;
                }
                break;
        }
        return fieldClass;
    }
}
