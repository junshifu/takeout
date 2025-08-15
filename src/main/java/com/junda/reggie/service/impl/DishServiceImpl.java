package com.junda.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.junda.reggie.common.CustomException;
import com.junda.reggie.dto.DishDto;
import com.junda.reggie.entity.Dish;
import com.junda.reggie.entity.DishFlavor;
import com.junda.reggie.entity.Setmeal;
import com.junda.reggie.entity.SetmealDish;
import com.junda.reggie.mapper.DishMapper;
import com.junda.reggie.service.DishFlavorService;
import com.junda.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品同时保存口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish
        this.save(dishDto);

        Long dishId=dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品的信息，口味
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息，从dish中查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor中查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;


    }

    /**
     * 更新菜品信息，口味
     * @param dishDto
     */

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
         //更新dish基本信息
         this.updateById(dishDto);

         //清除dish_flavor中的对应口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

         //插入dish_flavor中的对应口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据提供的id删除菜品，操作两张表：dish、dish_flavor
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        //查询判断商品是否停售
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("选项中含有在售商品");
        }

        //删除dish中的数据
        removeByIds(ids);

        //删除dish_flavor中的数据
        LambdaQueryWrapper<DishFlavor> queryWrapperFlavor = new LambdaQueryWrapper<>();
        queryWrapperFlavor.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapperFlavor);
    }
}
