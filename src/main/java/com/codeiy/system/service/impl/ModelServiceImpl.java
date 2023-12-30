package com.codeiy.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.common.entity.BaseEntity;
import com.codeiy.system.entity.Model;
import com.codeiy.system.entity.ModelField;
import com.codeiy.system.mapper.ModelMapper;
import com.codeiy.system.service.ModelService;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModelServiceImpl extends MPJBaseServiceImpl<ModelMapper, Model> implements ModelService {
    public static final Set<String> BaseFields = CollectionUtil.newHashSet("createBy", "createTime", "updateBy", "updateTime");
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * query by left join
     * @param page page
     * @return page data
     */
    @Override
    public IPage<Model> listByJoin(Page<Model> page) {
        MPJLambdaWrapper<Model> wrapper = new MPJLambdaWrapper<Model>()
                .selectAll(Model.class)
                .selectCollection(ModelField.class, Model::getFields)
                .leftJoin(ModelField.class, on -> on
                        .eq(Model::getTableSchema, ModelField::getTableSchema)
                        .eq(Model::getTableName, ModelField::getTableName));

        return this.getBaseMapper().selectJoinPage(page, Model.class, wrapper);
    }

    /**
     * create model, create entity and mapper, then register to spring
     * @param query query model
     * @return result
     */
    @Override
    public JSONObject createModel(Model query) {
        MPJLambdaWrapper<Model> wrapper = new MPJLambdaWrapper<Model>()
                .selectAll(Model.class)
                .selectCollection(ModelField.class, Model::getFields)
                .leftJoin(ModelField.class, on -> on
                        .eq(Model::getTableSchema, ModelField::getTableSchema)
                        .eq(Model::getTableName, ModelField::getTableName));
        wrapper.eq(Model::getTableSchema, query.getTableSchema());
        wrapper.eq(Model::getTableName, query.getTableName());
        Model model = this.getBaseMapper().selectJoinOne(Model.class, wrapper);
        MPJBaseMapper<?> mapper = createEntityAndMapper(model);
        List<?> list = mapper.selectList(Wrappers.emptyWrapper());
        log.info(JSON.toJSONString(list));
        return new JSONObject();
    }

    /**
     * base model, create entity and mapper, then register to spring
     * @param model model info
     * @return model mapper
     */
    protected MPJBaseMapper<?> createEntityAndMapper(Model model) {
        if (model == null) {
            log.error("model is null");
            return null;
        }
        if (CollectionUtil.isEmpty(model.getFields())) {
            log.error("fields is empty");
            return null;
        }
        String packageName = "com.codeiy.dynamic";

        String tableName = model.getTableName();
        String entityClassName = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
        ByteBuddy entityBuddy = new ByteBuddy();
        DynamicType.Builder<?> entityBuilder = entityBuddy.subclass(BaseEntity.class).name(packageName + ".entity." + entityClassName);
        entityBuilder = entityBuilder.annotateType(AnnotationDescription.Builder.ofType(TableName.class).define("value", tableName).define("schema", model.getTableSchema()).build());
        Collection<ModelField> fields = model.getFields();
        fields = fields.stream().filter(f ->!BaseFields.contains(StrUtil.toCamelCase(f.getColumnName()))).collect(Collectors.toList());
        for (ModelField field : fields) {
            String fieldName = StrUtil.toCamelCase(field.getColumnName());
            Type javaType = field.getJavaType();
            if (ModelField.PRIMARY_KEY.equals(field.getColumnKey())) {
                entityBuilder = entityBuilder.defineField(fieldName, javaType, Modifier.PROTECTED).annotateField(AnnotationDescription.Builder.ofType(TableId.class).build());
                log.info("add field(PRI): {}", fieldName);
            } else {
                entityBuilder = entityBuilder.defineField(fieldName, javaType, Modifier.PROTECTED);
                log.info("add field: {}", fieldName);
            }
            entityBuilder = entityBuilder
                    .defineMethod("get" + StrUtil.upperFirst(fieldName), javaType, Modifier.PUBLIC)
                    .intercept(FieldAccessor.ofField(fieldName));
            entityBuilder = entityBuilder
                    .defineMethod("set" + StrUtil.upperFirst(fieldName), void.class, Modifier.PUBLIC).withParameter(javaType)
                    .intercept(FieldAccessor.ofField(fieldName));
        }

        Class<?> entityClass;
        try (DynamicType.Unloaded<?> unloaded = entityBuilder.make()) {
            entityClass = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        ByteBuddy mapperBuddy = new ByteBuddy();
        String mapperClassName = entityClassName + "Mapper";
        DynamicType.Builder<?> mapperBuilder = mapperBuddy
                .makeInterface(TypeDescription.Generic.Builder.parameterizedType(MPJBaseMapper.class, entityClass).build())
                .name((packageName + ".mapper." + mapperClassName))
                .annotateType(AnnotationDescription.Builder.ofType(Mapper.class).build());
        Class<?> mapperClass;
        try (DynamicType.Unloaded<?> unloaded = mapperBuilder.make()) {
            mapperClass = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperClass);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        sqlSessionFactory.getConfiguration().addMapper(mapperClass);
        try {
            SpringUtil.registerBean(mapperClassName, factoryBean.getObject());
            log.info("register mapper Bean -> name:{}", mapperClassName);
            return SpringUtil.getBean(mapperClassName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
