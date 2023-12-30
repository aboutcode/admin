package com.codeiy.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.system.entity.Model;
import com.github.yulichang.base.MPJBaseService;

public interface ModelService extends MPJBaseService<Model> {
    /**
     * query by left join
     * @param page page
     * @return page data
     */
    IPage<Model> listByJoin(Page<Model> page);

    /**
     * create model, create entity and mapper, then register to spring
     * @param query query model
     * @return result
     */
    JSONObject createModel(Model query);
}
