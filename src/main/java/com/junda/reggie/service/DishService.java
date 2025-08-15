package com.junda.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.junda.reggie.dto.DishDto;
import com.junda.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品同时插入口味数据，操作两张波表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //查询菜品的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //根据id修改菜品信息，操作两张表：dish、dish_flavor
    public void updateWithFlavor(DishDto dishDto);

    //根据提供的id删除菜品，操作两张表：dish、dish_flavor
    public void removeWithFlavor(List<Long> ids);
}
