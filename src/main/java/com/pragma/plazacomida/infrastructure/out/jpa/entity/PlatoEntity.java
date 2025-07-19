package com.pragma.plazacomida.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "platos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlatoEntity {
    
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "id_categoria", length = 36, nullable = false)
    private String idCategoria;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;
    
    @Column(name = "id_restaurante", length = 36, nullable = false)
    private String idRestaurante;
    
    @Column(name = "url_imagen", length = 500)
    private String urlImagen;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
} 