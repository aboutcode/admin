package com.codeiy.system.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codeiy.common.base.BaseController;
import com.codeiy.system.entity.Model;
import com.codeiy.system.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model")
public class ModelController extends BaseController<Model> {
    @Autowired
    private ModelService modelService;

    @Override
    protected IService<Model> getService() {
        return modelService;
    }
}
