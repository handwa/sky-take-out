package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    //添加购物车
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //判断当前商品是否在购物车中
        ShoppingCart shoppingcart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingcart);
        Long currentId = BaseContext.getCurrentId();
        shoppingcart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingcart);
        //若在，则增加数量
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        }else{
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //添加到购物车，数量默认为1
                Dish dish = dishMapper.getById(dishId);
                shoppingcart.setName(dish.getName());
                shoppingcart.setImage(dish.getImage());
                shoppingcart.setAmount(dish.getPrice());
            }else{
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingcart.setName(setmeal.getName());
                shoppingcart.setImage(setmeal.getImage());
                shoppingcart.setAmount(setmeal.getPrice());
            }
            shoppingcart.setNumber(1);
            shoppingcart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingcart);
        }
    }

    //查看购物车
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingcart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingcart);
        return list;

    }

    //清空购物车
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByIds(userId);
    }

    //减购物车
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingcart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingcart);
        Long currentId = BaseContext.getCurrentId();
        shoppingcart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingcart);
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            if (cart.getNumber() == 1) {
                shoppingCartMapper.deleteById(cart.getId());
            }else{
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.update(cart);
            }
        }

    }


}
