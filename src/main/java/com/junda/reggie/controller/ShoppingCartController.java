package com.junda.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junda.reggie.common.BaseContext;
import com.junda.reggie.common.CustomException;
import com.junda.reggie.common.R;
import com.junda.reggie.entity.ShoppingCart;
import com.junda.reggie.service.CategoryService;
import com.junda.reggie.service.DishService;
import com.junda.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
//        log.info("userId={}",userId);
        Long userId = BaseContext.getCurrentId();
        //构造条件查询器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        //排序条件
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 添加菜品进入购物车
     * 该功能有缺陷，菜品不能选择不同口味,购物车严重缺陷不能隔离每名用户的购物车数据（已修改）
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //获取用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //判断是菜品还是套餐
        if (shoppingCart.getSetmealId()!=null){
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }else {
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }

        //准确查询用户数据
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        //查询购物车中是否已有数据
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne!=null){
            //购物车中已有该菜品，数量加1
            cartServiceOne.setNumber(cartServiceOne.getNumber()+1);
            shoppingCartService.updateById(cartServiceOne);

        }else{
            //购物车中没有菜品，将数量设置为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);

    }

    /**
     * 减少数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //判断菜品还是套餐
        if (shoppingCart.getSetmealId()!=null){
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId()).eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        }else {
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId()).eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        }



        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(shoppingCartServiceOne == null){
            shoppingCartServiceOne  = new ShoppingCart();
            shoppingCartServiceOne.setNumber(0);
            return R.success(shoppingCartServiceOne);
        }
        if (shoppingCartServiceOne.getNumber()>1){
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()-1);
            shoppingCartService.updateById(shoppingCartServiceOne);
            return R.success(shoppingCartServiceOne);
        }else {
            shoppingCartService.removeById(shoppingCartServiceOne.getId());
            shoppingCartServiceOne=null;
            shoppingCartServiceOne  = new ShoppingCart();
            shoppingCartServiceOne.setNumber(0);
            return R.success(shoppingCartServiceOne);
        }

    }

    /**
     *清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("购物车已清空");
    }

}
