package com.ecommerce.controller;

import com.ecommerce.config.JwtUtil;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    private String getEmail(String authHeader) {
        return jwtUtil.extractEmail(authHeader.substring(7));
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestHeader("Authorization") String auth,
                                         @RequestBody OrderRequest request) {
        try {
            return ResponseEntity.ok(orderService.placeOrder(getEmail(auth), request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> myOrders(@RequestHeader("Authorization") String auth) {
        try {
            return ResponseEntity.ok(orderService.getMyOrders(getEmail(auth)));
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> allOrders(@RequestHeader("Authorization") String auth) {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                           @RequestParam String status) {
        try {
            return ResponseEntity.ok(orderService.updateStatus(id, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
