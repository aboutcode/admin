package com.codeiy.common.base;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codeiy.common.dynamicModel.CriteriaQuery;
import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public abstract class BaseController<T> {
    protected IService<T> service;

    protected abstract IService<T> getService();

    // 分页查询
    @GetMapping("/page")
    public IPage<T> page(@RequestBody CriteriaQuery criteriaQuery) {
        // 创建分页对象
        Page<T> page = new Page<>(criteriaQuery.getPage(), criteriaQuery.getSize());
        // 添加排序条件（如果提供了排序字段和排序顺序）
        if (criteriaQuery.getOrderBy() != null) {
            page.addOrder(criteriaQuery.getOrderBy());
        }
        // 执行分页查询
        MPJQueryWrapper<T> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.page(page);
        if (CollectionUtil.isNotEmpty(criteriaQuery.getFilters())) {
            criteriaQuery.getFilters().forEach(criteria -> {
                criteria.addCondition(queryWrapper);
            });
        }
        return service.page(page);
    }

    // 查询所有
    @GetMapping("/listAll")
    public List<T> listAll() {
        return service.list();
    }

    // 根据ID查询
    @GetMapping("/getById/{id}")
    public T getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // 新增
    @PostMapping("/save")
    public boolean save(@RequestBody T entity) {
        return service.save(entity);
    }

    // 更新
    @PutMapping("/update")
    public boolean update(@RequestBody T entity) {
        return service.updateById(entity);
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.removeById(id);
    }

    // 根据条件查询
    @GetMapping("/listByCondition")
    public List<T> listByCondition(@RequestBody CriteriaQuery criteriaQuery) {
        // query params to condition
        MPJQueryWrapper<T> queryWrapper = new MPJQueryWrapper<>();
        if (CollectionUtil.isNotEmpty(criteriaQuery.getFilters())) {
            criteriaQuery.getFilters().forEach(criteria -> {
                criteria.addCondition(queryWrapper);
            });
        }
        return service.list(queryWrapper);
    }
}
