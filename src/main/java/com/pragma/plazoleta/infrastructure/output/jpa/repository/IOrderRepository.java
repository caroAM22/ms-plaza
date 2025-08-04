package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderRepository extends JpaRepository<OrderEntity, String> {
    
    @Query("SELECT o FROM OrderEntity o WHERE o.clientId = :clientId AND o.status IN (:statuses)")
    List<OrderEntity> findByClientIdAndStatusIn(@Param("clientId") String clientId, @Param("statuses") List<OrderStatusEntity> statuses);
    
    boolean existsByClientIdAndStatusIn(String clientId, List<OrderStatusEntity> statuses);

    Page<OrderEntity> findByStatusAndRestaurantId(OrderStatusEntity status, String restaurantId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OrderEntity o SET o.chefId = :chefId, o.status = :status WHERE o.id = :id")
    int updateChefId(@Param("id") String id, @Param("chefId") String chefId, @Param("status") OrderStatusEntity status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OrderEntity o SET o.securityPin = :securityPin, o.status = :status WHERE o.id = :id")
    int updateSecurityPin(@Param("id") String id, @Param("securityPin") String securityPin, @Param("status") OrderStatusEntity status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OrderEntity o SET o.status = :status WHERE o.id = :id")
    int updateOrderStatusToDelivered(@Param("id") String id, @Param("status") OrderStatusEntity status);
} 