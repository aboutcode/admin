package com.codeiy.common.dynamicModel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * join查询条件
 */
@Data
@Slf4j
public class JoinCriteria {
    public String LEFT_JOIN = "left";
    protected String joinType;
    /**
     * 被连接实体的名称
     */
    protected String joinClassName;
    /**
     * 主实体内保存连接实体返回数据的字段
     */
    protected String joinField;
    /**
     * 连接查询的on语句 SELECT * FROM mainEntity m left join leftEntity l ON m.onMain = l.onJoin
     */
    protected String onMain;
    protected String onJoin;

    protected <F> Class<F> getJoinClass(){
        return ClassUtil.loadClass(joinClassName);
    }

    public <T, M, F> void addLeftJoin(MPJLambdaWrapper<T> queryWrapper) {
        if (StrUtil.isBlank(joinClassName)){
            log.warn("joinClassName is empty");
            return;
        }
        if (StrUtil.isBlank(joinField)){
            log.warn("joinField is empty");
            return;
        }
        joinType = LEFT_JOIN;
        Class<F> leftJoinClass = getJoinClass();
        SFunction<M, Collection<F>> dtoField = (mainEntity -> (Collection<F>) BeanUtil.getFieldValue(mainEntity, joinField));
        queryWrapper
                .selectAll(queryWrapper.getEntityClass())
                .selectCollection(leftJoinClass, dtoField)
                .leftJoin(leftJoinClass, on -> on.eq(mainEntity -> BeanUtil.getFieldValue(mainEntity, onMain), leftJoinEntity -> BeanUtil.getFieldValue(leftJoinEntity, onJoin))
                );
    }
}
