package com.example.concurrency.service;

import com.example.concurrency.entity.Inventory;
import com.example.concurrency.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PessimisticInventoryService {

    private InventoryRepository inventoryRepository;

    @Transactional
    public void decrease(Long id, Long count) {  // 아이템 번호와 감소시킬 수량을 적으면
        Inventory inventory = inventoryRepository.findByIdPessimistic(id); // 비관적 락을 활용한 상태

        inventory.decrease(count); // 감소시킨 다음

        inventoryRepository.saveAndFlush(inventory);  // DB에 반영
    }
}
