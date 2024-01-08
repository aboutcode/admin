package com.codeiy.common.base;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public abstract class BaseController<T> {
    protected IService<T> service;

    protected abstract IService<T> getService();

    // 分页查询
    @GetMapping("/page")
    public IPage<T> page(@RequestParam(defaultValue = "1") Integer current,
                         @RequestParam(defaultValue = "10") Integer size,
                         @RequestParam(required = false) String sortField,
                         @RequestParam(required = false) String sortOrder) {
        // 创建分页对象
        Page<T> page = new Page<>(current, size);
        // 添加排序条件（如果提供了排序字段和排序顺序）
        if (sortField != null && !"".equals(sortField.trim())) {
            boolean asc = "asc".equalsIgnoreCase(sortOrder);
            page.addOrder(new OrderItem(sortField, asc));
        }
        // 执行分页查询
        // TODO：这里的lambda表达式是一个占位符，实际使用时需要替换为具体的查询条件。
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
    public List<T> listByCondition(@RequestBody JSONObject queryParams) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        // todo: query params to condition
//        queryWrapper.eq(fieldName, fieldValue);
        return service.list(queryWrapper);
    }
}
