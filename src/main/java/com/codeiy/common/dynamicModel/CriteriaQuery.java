package com.codeiy.common.dynamicModel;

import lombok.Data;
import java.util.List;


/**
 * 条件查询
 */
@Data
public class CriteriaQuery {
    private String tableName;
    private List<Criteria> filters;
    private int page;
    private int size;
    private String orderBy;
    private List<CriteriaQuery> leftJoins;
}

