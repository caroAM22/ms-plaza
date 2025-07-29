package com.pragma.plazoleta.infrastructure.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDishId implements Serializable {
    
    @Column(name = "order_id", length = 36)
    private String orderId;
    
    @Column(name = "dish_id", length = 36)
    private String dishId;
} 