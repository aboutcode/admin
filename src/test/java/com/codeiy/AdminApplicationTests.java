package com.codeiy;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.codeiy.common.dynamicModel.DynamicModel;
import com.codeiy.common.dynamicModel.EntityProperty;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class AdminApplicationTests {

    @Test
    void testCreateEntity() {
        JSONObject classAnnotations = new JSONObject();
        JSONObject schemaProps = new JSONObject();
        schemaProps.put("description", "用户");
        classAnnotations.put("io.swagger.v3.oas.annotations.media.Schema", schemaProps);
        List<EntityProperty> fieldProps = new ArrayList<>();
        EntityProperty entityProperty = new EntityProperty();
        entityProperty.setFieldName("userId");;
        entityProperty.setFieldType("Long");
        JSONObject fieldAnnotations = new JSONObject();
        JSONObject fieldAnnotationProp = new JSONObject();
        fieldAnnotationProp.put("value", "user_id");
        fieldAnnotationProp.put("type", IdType.ASSIGN_ID);
        fieldAnnotations.put("com.baomidou.mybatisplus.annotation.TableId", fieldAnnotationProp);
        fieldAnnotationProp = new JSONObject();
        fieldAnnotationProp.put("description", "主键id");
        fieldAnnotations.put("io.swagger.v3.oas.annotations.media.Schema", fieldAnnotationProp);
        entityProperty.setFieldAnnotations(fieldAnnotations);
        fieldProps.add(entityProperty);
        Object entity = DynamicModel.createEntity("com.codeiy.dynamicModel.entity", "java.io.Serializable", "User", classAnnotations, fieldProps);
        BeanUtil.setProperty(entity, "userId", 1L);

        log.info(JSONObject.toJSONString(entity));
    }

}
