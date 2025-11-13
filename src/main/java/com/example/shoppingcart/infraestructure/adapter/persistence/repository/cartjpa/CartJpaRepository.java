package com.example.shoppingcart.infraestructure.adapter.persistence.repository.cartjpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shoppingcart.infraestructure.adapter.persistence.entity.CartEntity;
import com.example.shoppingcart.domain.model.enums.CartStatus;

public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {

    @Query("select c from CartEntity c left join fetch c.items where c.id = :id")
    Optional<CartEntity> findByIdWithItems(@Param("id") Long id);

    @Query("select c from CartEntity c where c.userId = :userId and c.status = :status")
    Optional<CartEntity> findActiveByUserId(@Param("userId") Long userId,
                                            @Param("status") CartStatus status);

    @Query("select c from CartEntity c left join fetch c.items where c.userId = :userId and c.status = :status")
    Optional<CartEntity> findByUserIdWithItems(@Param("userId") Long userId,
                                               @Param("status") CartStatus status);

    @Query("select c from CartEntity c left join fetch c.items where c.id = :cartId and c.status = :status")
    Optional<CartEntity> findByIdAndStatusWithItems(@Param("cartId") Long cartId,
                                                    @Param("status") CartStatus status);
    
    @Query("select distinct c from CartEntity c left join fetch c.items")
    List<CartEntity> findAllWithItems();

    @org.springframework.data.jpa.repository.Query("select c from CartEntity c where c.status = com.example.shoppingcart.domain.model.enums.CartStatus.ACTIVE and c.updatedAt < :threshold")
    java.util.List<com.example.shoppingcart.infraestructure.adapter.persistence.entity.CartEntity> findAbandonedSince(java.time.LocalDateTime threshold);

}
