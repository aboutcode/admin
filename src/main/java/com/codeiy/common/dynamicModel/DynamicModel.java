package com.codeiy.common.dynamicModel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DynamicModel {
//    public static Object createEntity(String packageName, String supperClassName, String className, JSONObject classAnnotationProps, List<EntityProperty> fieldProps) {
//        try {
//            String name = packageName + "." + className;
//            ClassLoader classLoader = ByteBuddy.class.getClassLoader();
//            try {
//                Class<?> classType = ClassUtils.forName(className, classLoader);
//                return classType.getDeclaredConstructor().newInstance();
//            } catch (IllegalAccessError var3) {
//                throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
//            } catch (Throwable var4) {
//                // not present
//            }
//
//            ByteBuddy byteBuddy = new ByteBuddy();
//            DynamicType.Builder<?> builder;
//            if (StrUtil.isNotBlank(supperClassName)) {
//                Class<?> supperClass = Class.forName(supperClassName);
//                builder = byteBuddy.subclass(Object.class).implement(supperClass);
//            } else {
//                builder = byteBuddy.subclass(Object.class);
//            }
//            builder = builder.name(name);
//            if (CollectionUtil.isNotEmpty(classAnnotationProps)) {
//                List<AnnotationDescription> annotations = new ArrayList<>();
//                for (String annotationName : classAnnotationProps.keySet()) {
//                    Class<? extends Annotation> annotationClass = Class.forName(annotationName).asSubclass(Annotation.class);
//                    AnnotationDescription.Builder annotationBuilder = AnnotationDescription.Builder.ofType(annotationClass);
//                    JSONObject annotationProps = classAnnotationProps.getJSONObject(annotationName);
//                    for (String propName : annotationProps.keySet()) {
//                        Object propValue = annotationProps.get(propName);
//                        if (ObjectUtil.isBasicType(propValue) || propValue instanceof String) {
//                            annotationBuilder = annotationBuilder.define(propName, AnnotationValue.ForConstant.of(propValue));
//                        } else {
//                            annotationBuilder = annotationBuilder.define(propName, AnnotationDescription.ForLoadedAnnotation.asValue(propValue, propValue.getClass()));
//                        }
//                    }
//                    annotations.add(annotationBuilder.build());
//                }
//                builder = builder.annotateType(annotations);
//            }
//
//            if (CollectionUtil.isNotEmpty(fieldProps)) {
//                for (EntityProperty fieldProp : fieldProps) {
//                    Set<String> annotationNames = fieldProp.getAnnotationNames();
//                    if (CollectionUtil.isNotEmpty(annotationNames)) {
//                        List<AnnotationDescription> fieldAnnotations = new ArrayList<>();
//                        for (String annotationName : annotationNames) {
//                            Class<? extends Annotation> annotationClass = Class.forName(annotationName).asSubclass(Annotation.class);
//                            AnnotationDescription.Builder annotationBuilder = AnnotationDescription.Builder.ofType(annotationClass);
//                            for (String propName : fieldProp.getAnnotationPropertyName(annotationName)) {
//                                Object propValue = fieldProp.getAnnotationPropertyValue(annotationName, propName);
//                                if (ObjectUtil.isBasicType(propValue) || propValue instanceof String) {
//                                    annotationBuilder = annotationBuilder.define(propName, AnnotationValue.ForConstant.of(propValue));
//                                } else {
//                                    annotationBuilder = annotationBuilder.define(propName, AnnotationDescription.ForLoadedAnnotation.asValue(propValue, propValue.getClass()));
//                                }
//                            }
//                            fieldAnnotations.add(annotationBuilder.build());
//                        }
//                        builder = builder.defineField(fieldProp.getFieldName(), fieldProp.getType(), Modifier.PROTECTED).annotateField(fieldAnnotations);
//                    } else {
//                        builder = builder.defineField(fieldProp.getFieldName(), fieldProp.getType(), Modifier.PROTECTED);
//                    }
//                    builder = builder
//                            .defineMethod("get" + StrUtil.upperFirst(fieldProp.getFieldName()), fieldProp.getType(), Modifier.PUBLIC)
//                            .intercept(FieldAccessor.ofField(fieldProp.getFieldName()));
//                    builder = builder
//                            .defineMethod("set" + StrUtil.upperFirst(fieldProp.getFieldName()), void.class, Modifier.PUBLIC).withParameter(fieldProp.getType())
//                            .intercept(FieldAccessor.ofField(fieldProp.getFieldName()));
//                }
//            }
//
//            // 加载新类型，默认WRAPPER策略
//            try (DynamicType.Unloaded<?> unloaded = builder.make()) {
//                File file = new File("target/classes");
//                return unloaded.saveIn(file).getClass().getConstructor().newInstance();
////                return unloaded.load(ByteBuddy.class.getClassLoader())
////                        .getLoaded()
////                        .getDeclaredConstructor().newInstance();
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw new RuntimeException("create instance fail: " + e.getMessage(), e);
//        }
//    }



}
