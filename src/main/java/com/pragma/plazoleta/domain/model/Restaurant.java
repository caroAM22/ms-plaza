package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private UUID id;
    private String name;
    private long nit;
    private String address;
    private String phone;
    private String logoUrl;
    private UUID ownerId;
}