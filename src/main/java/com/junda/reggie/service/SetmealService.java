package com.junda.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.junda.reggie.dto.SetmealDto;
import com.junda.reggie.entity.Dish;
import com.junda.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    /**
     * 保存套餐，同时储存菜品关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     *
     * 删除套餐，同时删除setmeal_dish
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealDto findWithDish(Long id);

}
