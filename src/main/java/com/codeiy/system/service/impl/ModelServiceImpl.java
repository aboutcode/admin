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
import com.codeiy.system.entity.TableInfo;
import com.codeiy.system.entity.ColumnInfo;
import com.codeiy.system.mapper.TableInfoMapper;
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
public class ModelServiceImpl extends MPJBaseServiceImpl<TableInfoMapper, TableInfo> implements ModelService {
    public static final Set<String> BaseFields = CollectionUtil.newHashSet("createBy", "createTime", "updateBy", "updateTime");
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * query by left join
     * @param page page
     * @return page data
     */
    @Override
    public IPage<TableInfo> listByJoin(Page<TableInfo> page) {
        MPJLambdaWrapper<TableInfo> wrapper = new MPJLambdaWrapper<TableInfo>()
                .selectAll(TableInfo.class)
                .selectCollection(ColumnInfo.class, TableInfo::getFields)
                .leftJoin(ColumnInfo.class, on -> on
                        .eq(TableInfo::getTableSchema, ColumnInfo::getTableSchema)
                        .eq(TableInfo::getTableName, ColumnInfo::getTableName));

        return this.getBaseMapper().selectJoinPage(page, TableInfo.class, wrapper);
    }

    /**
     * create model, create entity and mapper, then register to spring
     * @param query query model
     * @return result
     */
    @Override
    public JSONObject createModel(TableInfo query) {
        MPJLambdaWrapper<TableInfo> wrapper = new MPJLambdaWrapper<TableInfo>()
                .selectAll(TableInfo.class)
                .selectCollection(ColumnInfo.class, TableInfo::getFields)
                .leftJoin(ColumnInfo.class, on -> on
                        .eq(TableInfo::getTableSchema, ColumnInfo::getTableSchema)
                        .eq(TableInfo::getTableName, ColumnInfo::getTableName));
        wrapper.eq(TableInfo::getTableSchema, query.getTableSchema());
        wrapper.eq(TableInfo::getTableName, query.getTableName());
        TableInfo tableInfo = this.getBaseMapper().selectJoinOne(TableInfo.class, wrapper);
        MPJBaseMapper<?> mapper = createEntityAndMapper(tableInfo);
        List<?> list = mapper.selectList(Wrappers.emptyWrapper());
        log.info(JSON.toJSONString(list));
        return new JSONObject();
    }

    /**
     * base model, create entity and mapper, then register to spring
     * @param tableInfo model info
     * @return model mapper
     */
    protected MPJBaseMapper<?> createEntityAndMapper(TableInfo tableInfo) {
        if (tableInfo == null) {
            log.error("model is null");
            return null;
        }
        if (CollectionUtil.isEmpty(tableInfo.getFields())) {
            log.error("fields is empty");
            return null;
        }
        String packageName = "com.codeiy.dynamic";

        String tableName = tableInfo.getTableName();
        String entityClassName = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
        ByteBuddy entityBuddy = new ByteBuddy();
        DynamicType.Builder<?> entityBuilder = entityBuddy.subclass(BaseEntity.class).name(packageName + ".entity." + entityClassName);
        entityBuilder = entityBuilder.annotateType(AnnotationDescription.Builder.ofType(TableName.class).define("value", tableName).define("schema", tableInfo.getTableSchema()).build());
        Collection<ColumnInfo> fields = tableInfo.getFields();
        fields = fields.stream().filter(f ->!BaseFields.contains(StrUtil.toCamelCase(f.getColumnName()))).collect(Collectors.toList());
        for (ColumnInfo field : fields) {
            String fieldName = StrUtil.toCamelCase(field.getColumnName());
            Type javaType = field.getJavaType();
            if (ColumnInfo.PRIMARY_KEY.equals(field.getColumnKey())) {
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
