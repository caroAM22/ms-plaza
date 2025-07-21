package com.pragma.plazoleta.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "category_id", nullable = false)
    private int categoryId;

    @Column(name = "restaurant_id", nullable = false, length = 36)
    private String restaurantId;

    @Column(nullable = false)
    private boolean active;
}
