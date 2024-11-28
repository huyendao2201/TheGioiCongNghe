package com.example.thegioicongnghe.Admin.Repository;

import com.example.thegioicongnghe.Admin.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Integer> {

    public List<CartItem> findByUserId(Integer userId);

    Integer countByUserId(Integer userId);

}
