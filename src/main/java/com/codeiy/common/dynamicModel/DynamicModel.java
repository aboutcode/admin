package com.codeiy.common.dynamicModel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.codeiy.common.util.ClassUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

public class DynamicModel {
    public static Object createEntity(String className, List<EntityProperty> fieldProps, String packageName, String supperClassName) {
        try {
            String name = packageName + "." + className;
            ClassLoader classLoader = ByteBuddy.class.getClassLoader();
            try {
                Class<?> classType = ClassUtils.forName(className, classLoader);
                return classType.newInstance();
            } catch (IllegalAccessError var3) {
                throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
            } catch (Throwable var4) {
                // not present
            }

            ByteBuddy byteBuddy = new ByteBuddy();
            DynamicType.Builder<?> builder;
            if (StrUtil.isNotBlank(supperClassName)) {
                Class<?> supperClass = Class.forName(supperClassName);
                builder = byteBuddy.subclass(supperClass);
            } else {
                builder = byteBuddy.subclass(Object.class);
            }
            builder.name(name);

            if (CollectionUtil.isNotEmpty(fieldProps)) {
                for (EntityProperty fieldProp : fieldProps) {
                    builder = builder.defineField(fieldProp.getFieldName(), fieldProp.getType(), Modifier.PROTECTED);
                    Set<String> annotationNames = fieldProp.getAnnotationNames();
                    if (CollectionUtil.isNotEmpty(annotationNames)) {
                        for (String annotationName : annotationNames) {
                            for (String annotationPropertyName : fieldProp.getAnnotationPropertyName(annotationName)) {
                                Object annotationPropertyValue = fieldProp.getAnnotationPropertyValue(annotationName, annotationPropertyName);
//                                builder.annotateType(annotationName).with(annotationPropertyName, annotationPropertyValue);
                            }
                        }
                    }
                    builder = builder.defineMethod("get" + StrUtil.upperFirst(fieldProp.getFieldName()), fieldProp.getType(), Modifier.PUBLIC)
                            .intercept(FieldAccessor.ofField(fieldProp.getFieldName()));
                    if (CollectionUtil.isNotEmpty(annotationNames)) {
                        for (String annotationName : annotationNames) {
                            for (String annotationPropertyName : fieldProp.getAnnotationPropertyName(annotationName)) {
                                Object annotationPropertyValue = fieldProp.getAnnotationPropertyValue(annotationName, annotationPropertyName);
//                                builder.annotateMethod(annotationName).with(annotationPropertyName, annotationPropertyValue);
                            }
                        }
                    }
                    builder = builder.defineMethod("set" + StrUtil.upperFirst(fieldProp.getFieldName()), void.class, Modifier.PUBLIC)
                            .intercept(FieldAccessor.ofField(fieldProp.getFieldName()));
                    if (CollectionUtil.isNotEmpty(annotationNames)) {
                        for (String annotationName : annotationNames) {
                            for (String annotationPropertyName : fieldProp.getAnnotationPropertyName(annotationName)) {
                                Object annotationPropertyValue = fieldProp.getAnnotationPropertyValue(annotationName, annotationPropertyName);
//                                builder.annotateMethod(annotationName).with(annotationPropertyName, annotationPropertyValue);
                            }
                        }
                    }
                }
            }

            // 加载新类型，默认WRAPPER策略
            return builder.make().load(ByteBuddy.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("create instance fail: " + e.getMessage(), e);
        } finally {
            System.out.println("create instance success");
        }
    }
}
