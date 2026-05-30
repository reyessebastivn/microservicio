package com.example.ProductosJSS.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.ProductosJSS.dto.CheckoutRequest;
import com.example.ProductosJSS.dto.CheckoutResponse;
import com.example.ProductosJSS.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@Validated @RequestBody CheckoutRequest request) {
        CheckoutResponse resp = orderService.checkout(request);
        return ResponseEntity.ok(resp);
    }
}
