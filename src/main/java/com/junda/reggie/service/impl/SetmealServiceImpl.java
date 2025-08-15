package com.junda.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.junda.reggie.common.CustomException;
import com.junda.reggie.dto.SetmealDto;
import com.junda.reggie.entity.Setmeal;
import com.junda.reggie.entity.SetmealDish;
import com.junda.reggie.mapper.SetmealMapper;
import com.junda.reggie.service.SetmealDishService;
import com.junda.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal
        this.save(setmealDto);

        //保存套餐中的菜品关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 删除套餐，同时删除setmeal_dish
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //查询判断商品是否停售
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("选项中含有在售商品");
        }

        //删除setmeal中的数据
        removeByIds(ids);

        //删除setmeal_dish中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapperDish = new LambdaQueryWrapper<>();
        queryWrapperDish.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapperDish);

    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto findWithDish(Long id) {
        //查询setmeal基本信息
//        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
////        queryWrapper.eq(Setmeal::getId,id);
////        Setmeal setmeal =this.getById(queryWrapper);
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询setmeal_dish数据
        LambdaQueryWrapper <SetmealDish> queryWrapperDish = new LambdaQueryWrapper<>();
        queryWrapperDish.eq(SetmealDish::getSetmealId,id);

        log.info("准备查询");
        List<SetmealDish> setmealDish = setmealDishService.list(queryWrapperDish);
        setmealDto.setSetmealDishes(setmealDish);


        return setmealDto;
    }
}
