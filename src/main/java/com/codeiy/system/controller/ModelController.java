package com.codeiy.system.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeiy.system.entity.Model;
import com.codeiy.system.entity.TableInfo;
import com.codeiy.system.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/model")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @GetMapping("/list")
    public List<Model> list() {
        return modelService.list();
    }
}
