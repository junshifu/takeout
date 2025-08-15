package com.junda.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.junda.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}
