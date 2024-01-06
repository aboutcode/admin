package com.codeiy.system.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.common.entity.BaseEntity;
import com.codeiy.system.entity.Model;
import com.codeiy.system.entity.TableInfo;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseService;

public interface ModelService extends MPJBaseService<Model> {

    /**
     * create model, create entity and mapper, then register to spring
     * @param query query model
     * @return result
     */
    Class<?> createModel(Model query);
}
