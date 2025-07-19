package com.pragma.plazacomida.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "restaurantes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestauranteEntity {
    
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "direccion", length = 200, nullable = false)
    private String direccion;
    
    @Column(name = "id_propietario", length = 36, nullable = false)
    private String idPropietario; // FK a tabla users (ms-users)
    
    @Column(name = "telefono", length = 20, nullable = false)
    private String telefono;
    
    @Column(name = "url_logo", length = 500)
    private String urlLogo;
    
    @Column(name = "nit", length = 20, nullable = false, unique = true)
    private String nit;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
} 