package com.codeiy.common.dynamicModel;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;

import java.util.List;


/**
 * 条件查询
 */
@Data
public class CriteriaQuery {
    private String tableName;
    private List<Criteria> filters;
    private int page = 1;
    private int size = 10;
    private OrderItem orderBy;
    private List<JoinCriteria> leftJoins;
}

