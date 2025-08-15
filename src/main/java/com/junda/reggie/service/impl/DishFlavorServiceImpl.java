package com.junda.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.junda.reggie.dto.DishDto;
import com.junda.reggie.entity.DishFlavor;
import com.junda.reggie.mapper.DishFlavorMapper;
import com.junda.reggie.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService{


}
