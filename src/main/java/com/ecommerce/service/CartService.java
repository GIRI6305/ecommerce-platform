package com.ecommerce.service;

import com.ecommerce.dto.CartRequest;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartItem addToCart(String email, CartRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Product product = productRepository.findById(request.getProductId()).orElseThrow();
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            return cartItemRepository.save(item);
        }
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        return cartItemRepository.save(item);
    }

    public List<CartItem> getCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return cartItemRepository.findByUserId(user.getId());
    }

    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        cartItemRepository.deleteByUserId(user.getId());
    }
}
