package com.pragma.plazacomida.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PedidoEntity {
    
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    @Column(name = "id_cliente", length = 36, nullable = false)
    private String idCliente; // FK a tabla users (ms-users)
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @Column(name = "estado", length = 50, nullable = false)
    private String estado;
    
    @Column(name = "id_chef", length = 36)
    private String idChef; // FK a tabla users (ms-users)
    
    @Column(name = "id_restaurante", length = 36, nullable = false)
    private String idRestaurante;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
} 