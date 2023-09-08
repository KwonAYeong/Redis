package com.example.concurrency.repository;

import com.example.concurrency.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 비관적 락은 @Lock 어노테이션을 이용해 지정할 수 있다.
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.id = :id")
    Inventory findByIdPessimistic(Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select i from Inventory i where i.id = :id")
    Inventory findByIdOptimistic(Long id);
}
