package com.junda.reggie.dto;


import com.junda.reggie.entity.OrderDetail;
import com.junda.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;

}
