package com.junda.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.junda.reggie.common.CustomException;
import com.junda.reggie.entity.Category;
import com.junda.reggie.entity.Dish;
import com.junda.reggie.entity.Setmeal;
import com.junda.reggie.mapper.CategoryMapper;
import com.junda.reggie.service.CategoryService;
import com.junda.reggie.service.DishService;
import com.junda.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需判断
     * @param id
     */
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据id进行查询菜品表
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //判断返回数据
        if (count1 > 0) {
            log.info("菜品");
            throw new CustomException("当前分类关联了菜品，不能删除");
        }


        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据id进行查询套餐表
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //判断返回数据
        if (count2 > 0) {
            log.info("套餐");
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        log.info("删除");
        super.removeById(id);
    }
}
