package com.junda.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junda.reggie.common.R;
import com.junda.reggie.entity.Category;
import com.junda.reggie.entity.Employee;
import com.junda.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("Category{}",category);
        categoryService.save(category);
        return R.success("新增分类成功！");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize) {

        //构建分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //构造条件查询器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
//        queryWrapper.like(StringUtils.isNotEmpty(name),Category::getName,name);
        //排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);


    }


    /**
     * 删除分类
     * @param id
     * @return
     */

    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("删除分类{}",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    /**
     *修改分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("Category{}",category);
        if (categoryService.updateById(category)) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //构造条件查询器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
