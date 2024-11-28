package com.example.thegioicongnghe.Admin.Repository;

import com.example.thegioicongnghe.Admin.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByproductSlug(String productSlug);

    boolean existsByProductSlug(String productSlug);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findTop10ByOrderByCreatedAtDesc();
}
