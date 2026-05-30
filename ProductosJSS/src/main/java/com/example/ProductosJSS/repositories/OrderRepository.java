package com.example.ProductosJSS.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ProductosJSS.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
