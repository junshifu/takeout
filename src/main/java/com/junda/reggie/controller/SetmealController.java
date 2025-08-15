package com.junda.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junda.reggie.common.R;
import com.junda.reggie.dto.SetmealDto;
import com.junda.reggie.entity.Category;
import com.junda.reggie.entity.Dish;
import com.junda.reggie.entity.Setmeal;
import com.junda.reggie.service.CategoryService;
import com.junda.reggie.service.DishService;
import com.junda.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("SetmeaDto{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
     //   setmealService.save(setmealDto);
        return R.success("新增套餐成功！");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构建分页构造器
        Page pageInfo = new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page();
        //构造条件查询器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);

            //分类Id
            Long CategoryId = item.getCategoryId();
            //根据分类Id查询分类对象
            Category category = categoryService.getById(CategoryId);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐，同时删除setmeal_dish中的数据
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);

        return R.success("删除套餐成功");
    }


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public SetmealDto selectById(@PathVariable Long id) {
        log.info("id：{}",id);
//        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Setmeal::getId,id);
//        Setmeal setmeal = setmealService.getOne(queryWrapper);
        SetmealDto setmealDto = setmealService.findWithDish(id);
        return setmealDto;


    }


    /**
     * 修改套餐状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("status:{}",status);
        log.info("ids:{}",ids);

        List<Setmeal> setmeals = new ArrayList<>();
        for (Long num:ids){
            Setmeal setmeal = new Setmeal();
            setmeal.setId(num);
            setmeals.add(setmeal);
        }

        setmeals = setmeals.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals);

        return R.success("修改套餐状态成功");
    }

    /**
     * 根据id查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId()).eq(Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }

}
