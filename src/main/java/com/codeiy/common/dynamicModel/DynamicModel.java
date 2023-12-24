package com.codeiy.common.dynamicModel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.codeiy.common.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Slf4j
public class DynamicModel {
    public static Object createEntity(String packageName, String supperClassName, String className, JSONObject classAnnotationProps, List<EntityProperty> fieldProps) {
        try {
            String name = packageName + "." + className;
            ClassLoader classLoader = ByteBuddy.class.getClassLoader();
            try {
                Class<?> classType = ClassUtils.forName(className, classLoader);
                return classType.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessError var3) {
                throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
            } catch (Throwable var4) {
                // not present
            }

            ByteBuddy byteBuddy = new ByteBuddy();
            DynamicType.Builder<?> builder;
            if (StrUtil.isNotBlank(supperClassName)) {
                Class<?> supperClass = Class.forName(supperClassName);
                builder = byteBuddy.subclass(Object.class).implement(supperClass);
            } else {
                builder = byteBuddy.subclass(Object.class);
            }
            builder = builder.name(name);
            if (CollectionUtil.isNotEmpty(classAnnotationProps)) {
                for (String annotationName : classAnnotationProps.keySet()) {
                    Class<? extends Annotation> annotationClass = Class.forName(annotationName).asSubclass(Annotation.class);
                    AnnotationDescription.Builder annotationBuilder = AnnotationDescription.Builder.ofType(annotationClass);
                    JSONObject annotationProps = classAnnotationProps.getJSONObject(annotationName);
                    annotationProps.forEach((propName, propValue) -> {
                        if (ObjectUtil.isBasicType(propValue) || propValue instanceof String) {
                            annotationBuilder.define(propName, AnnotationValue.ForConstant.of(propValue));
                        } else {
                            annotationBuilder.define(propName, AnnotationDescription.ForLoadedAnnotation.asValue(propValue, propValue.getClass()));
                        }
                    });
                    builder = builder.annotateType(annotationBuilder.build());
                }
            }

            if (CollectionUtil.isNotEmpty(fieldProps)) {
                for (EntityProperty fieldProp : fieldProps) {
                    DynamicType.Builder.FieldDefinition.Optional.Valuable defineField = builder.defineField(fieldProp.getFieldName(), fieldProp.getType(), Modifier.PROTECTED);
                    Set<String> annotationNames = fieldProp.getAnnotationNames();
                    if (CollectionUtil.isNotEmpty(annotationNames)) {
                        for (String annotationName : annotationNames) {
                            Class<? extends Annotation> annotationClass = Class.forName(annotationName).asSubclass(Annotation.class);
                            AnnotationDescription.Builder annotationBuilder = AnnotationDescription.Builder.ofType(annotationClass);
                            for (String propName : fieldProp.getAnnotationPropertyName(annotationName)) {
                                Object propValue = fieldProp.getAnnotationPropertyValue(annotationName, propName);
                                log.info("propName: {}, propValue: {}", propName, propValue);
                                if (ObjectUtil.isBasicType(propValue) || propValue instanceof String) {
                                    annotationBuilder.define(propName, AnnotationValue.ForConstant.of(propValue));
                                } else {
                                    annotationBuilder.define(propName, AnnotationDescription.ForLoadedAnnotation.asValue(propValue, propValue.getClass()));
                                }
                            }
                            builder = defineField.annotateField(annotationBuilder.build());
                        }
                    } else {
                        builder = defineField;
                    }
                    builder = builder
                            .defineMethod("get" + StrUtil.upperFirst(fieldProp.getFieldName()), fieldProp.getType(), Modifier.PUBLIC)
                            .intercept(FieldAccessor.ofField(fieldProp.getFieldName()));
                    builder = builder
                            .defineMethod("set" + StrUtil.upperFirst(fieldProp.getFieldName()), void.class, Modifier.PUBLIC).withParameter(fieldProp.getType())
                            .intercept(FieldAccessor.ofField(fieldProp.getFieldName()));
                }
            }

            // 加载新类型，默认WRAPPER策略
            try (DynamicType.Unloaded<?> unloaded = builder.make()) {
                String classpath = "D:\\code\\admin";
                Path outputPath = Paths.get(classpath + "/target/classes/com/codeiy/common/dynamicModel/User.class");
                try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
                    outputStream.write(unloaded.getBytes());
                }
                return unloaded.load(ByteBuddy.class.getClassLoader())
                        .getLoaded()
                        .getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("create instance fail: " + e.getMessage(), e);
        }
    }
}
