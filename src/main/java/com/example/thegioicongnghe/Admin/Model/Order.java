package com.example.thegioicongnghe.Admin.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;  // Khóa chính cho đơn hàng

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDtls user;  // Người dùng đã thực hiện đơn hàng

    @Column(nullable = false)
    private String orderStatus;  // Trạng thái đơn hàng (ví dụ: "Đang xử lý", "Đã hoàn thành")

    @Column(nullable = false)
    private BigDecimal totalPrice;  // Tổng giá trị đơn hàng

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;  // Thời gian tạo đơn hàng

    @Column(nullable = false)
    private Timestamp updatedAt;  // Thời gian cập nhật đơn hàng

    // Constructor
    public Order() {
        this.createdAt = new Timestamp(System.currentTimeMillis()); // Đặt thời gian tạo mặc định
        this.updatedAt = new Timestamp(System.currentTimeMillis()); // Đặt thời gian cập nhật mặc định
    }

    // Getters và Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public UserDtls getUser() {
        return user;
    }

    public void setUser(UserDtls user) {
        this.user = user;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
