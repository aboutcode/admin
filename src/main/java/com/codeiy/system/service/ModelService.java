package com.codeiy.system.service;

import com.codeiy.system.entity.Model;
import com.github.yulichang.base.MPJBaseService;

public interface ModelService extends MPJBaseService<Model> {

    /**
     * create model, create entity and mapper, then register to spring
     * @param query query model
     * @return result
     */
    Class<?> createModel(Model query);
}
