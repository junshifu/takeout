package com.junda.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junda.reggie.common.BaseContext;
import com.junda.reggie.common.R;
import com.junda.reggie.dto.OrderDto;
import com.junda.reggie.entity.Category;
import com.junda.reggie.entity.OrderDetail;
import com.junda.reggie.entity.Orders;
import com.junda.reggie.entity.User;
import com.junda.reggie.service.OrderDetailService;
import com.junda.reggie.service.OrderService;
import com.junda.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 用户分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){

        Long userId = BaseContext.getCurrentId();
        //构建分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        Page<OrderDto> dtoPage = new Page<>();
        //构造条件查询器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(userId!=null,Orders::getUserId,userId);
        //排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");


        List<Orders> orders = pageInfo.getRecords();
        List<OrderDto> orderDtos = orders.stream().map((item)->{
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item,orderDto);

            LambdaQueryWrapper<OrderDetail> queryWrapperDetail = new LambdaQueryWrapper<>();
            queryWrapperDetail.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapperDetail);

            if(orderDetails!=null){
                orderDto.setOrderDetails(orderDetails);
            }
            return orderDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(orderDtos);

        return R.success(dtoPage);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String number, String beginTime, String endTime){

        log.info("beginTime:{},endTime:{}",beginTime,endTime);

        //构建分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);


        //构造条件查询器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.between(beginTime!=null,Orders::getOrderTime,beginTime,endTime);
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        //排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行查询
        orderService.page(pageInfo,queryWrapper);


//        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
////        List<Orders> orders = orderService.list(queryWrapper);
//
//        List<Orders> orders = pageInfo.getRecords();
//        List<OrderDto> orderDtos = orders.stream().map((item)->{
//            OrderDto orderDto = new OrderDto();
//            BeanUtils.copyProperties(item,orderDto);
//
//            log.info("userId:{}",item.getUserId());
//            User user = userService.getById(item.getUserId());
//
//            if(user!=null){
//                orderDto.setUserName(user.getName());
//            }
//            return orderDto;
//        }).collect(Collectors.toList());
//        dtoPage.setRecords(orderDtos);

        return R.success(pageInfo);
    }


}
