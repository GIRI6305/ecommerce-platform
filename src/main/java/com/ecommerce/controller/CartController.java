package com.ecommerce.controller;

import com.ecommerce.config.JwtUtil;
import com.ecommerce.dto.CartRequest;
import com.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final JwtUtil jwtUtil;

    public CartController(CartService cartService, JwtUtil jwtUtil) {
        this.cartService = cartService;
        this.jwtUtil = jwtUtil;
    }

    private String getEmail(String authHeader) {
        return jwtUtil.extractEmail(authHeader.substring(7));
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(cartService.getCart(getEmail(auth)));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String auth, @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(getEmail(auth), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.ok("Removed");
    }
}
