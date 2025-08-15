package com.junda.reggie.dto;


import com.junda.reggie.entity.Setmeal;
import com.junda.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
