package com.codeiy.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.system.entity.TableInfo;
import com.github.yulichang.base.MPJBaseService;

public interface ModelService extends MPJBaseService<TableInfo> {
    /**
     * query by left join
     * @param page page
     * @return page data
     */
    IPage<TableInfo> listByJoin(Page<TableInfo> page);

    /**
     * create model, create entity and mapper, then register to spring
     * @param query query model
     * @return result
     */
    JSONObject createModel(TableInfo query);
}
