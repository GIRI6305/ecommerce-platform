package com.ecommerce.service;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public Order placeOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + email);
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(
            request.getShippingAddress() != null && !request.getShippingAddress().isEmpty()
                ? request.getShippingAddress() : user.getAddress()
        );
        order.setItems(new ArrayList<>());
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        double total = 0;
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            savedOrder.getItems().add(orderItem);
        }

        savedOrder.setTotalAmount(total);
        Order finalOrder = orderRepository.save(savedOrder);

        cartItemRepository.deleteByUserId(user.getId());

        return finalOrder;
    }

    public List<Order> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders != null ? orders : new ArrayList<>();
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders != null ? orders : new ArrayList<>();
    }

    public Order updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(Order.Status.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }
}
