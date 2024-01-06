package com.codeiy.common.dynamicModel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codeiy.common.entity.BaseEntity;
import com.codeiy.system.entity.Model;
import com.codeiy.system.service.ModelService;
import com.github.yulichang.base.MPJBaseMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DynamicModel {
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected SqlSessionFactory sqlSessionFactory;
    protected Map<String, MPJBaseMapper<? extends BaseEntity>> mapperContainer = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化动态模型
        LambdaQueryWrapper<Model> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Model::getEnableFlag, true);
        List<Model> models = modelService.list(wrapper);
        if (CollectionUtil.isEmpty(models)){
            return;
        }
        models.forEach(model -> {
            Class<?> mapperClass = modelService.createModel(model);
            MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperClass);
            factoryBean.setSqlSessionFactory(sqlSessionFactory);
            sqlSessionFactory.getConfiguration().addMapper(mapperClass);
            try {
                String beanName = model.getModelCode();
                SpringUtil.registerBean(beanName, factoryBean.getObject());
                log.info("register mapper Bean -> name:{}", beanName);
                MPJBaseMapper<? extends BaseEntity> mapper = SpringUtil.getBean(beanName);
                mapperContainer.put(beanName, mapper);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }
}
