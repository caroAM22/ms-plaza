package com.pragma.plazoleta.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "client_id", length = 36, nullable = false)
    private String clientId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEntity status;

    @Column(name = "chef_id", length = 36)
    private String chefId;

    @Column(name = "restaurant_id", length = 36, nullable = false)
    private String restaurantId;

    @Column(name = "security_pin", length = 6)
    private String securityPin;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDishEntity> orderDishes;
} 