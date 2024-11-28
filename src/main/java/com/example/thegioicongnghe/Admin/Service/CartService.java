package com.example.thegioicongnghe.Admin.Service;

import com.example.thegioicongnghe.Admin.Model.CartItem;

import java.util.List;

public interface CartService {

    public CartItem saveCart(Integer productId, Integer userId);

    public List<CartItem> getCartsByUser(Integer userId);

    public Integer getCountCart(Integer userId);

    public void updateQuantity(String sy, Integer cid);

}
