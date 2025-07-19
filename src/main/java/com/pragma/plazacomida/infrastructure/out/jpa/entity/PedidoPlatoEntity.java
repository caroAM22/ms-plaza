package com.pragma.plazacomida.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "pedidos_platos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PedidoPlatoEntity {
    
    @EmbeddedId
    private PedidoPlatoId id;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PedidoPlatoId {
        
        @Column(name = "id_pedido", length = 36, nullable = false)
        private String idPedido;
        
        @Column(name = "id_plato", length = 36, nullable = false)
        private String idPlato;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PedidoPlatoId that = (PedidoPlatoId) o;
            return Objects.equals(idPedido, that.idPedido) && Objects.equals(idPlato, that.idPlato);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(idPedido, idPlato);
        }
    }
} 