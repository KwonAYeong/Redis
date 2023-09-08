package com.example.concurrency.service;

import com.example.concurrency.entity.Inventory;
import com.example.concurrency.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class InventoryService {

    private InventoryRepository inventoryRepository;

    @Transactional
//    public void decrease(Long id, Long count) {  // 아이템 번호와 감소시킬 수량을 적으면
    public synchronized void decrease(Long id, Long count) {  // synchronized 는 한 쓰레드가 호출하는 메서드를 다른 쓰레드가 동시에 호출하는 것을 방지한다.
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(); // 가져와서

        inventory.decrease(count); // 감소시킨 다음

        inventoryRepository.saveAndFlush(inventory);  // DB에 반영
    }
}
