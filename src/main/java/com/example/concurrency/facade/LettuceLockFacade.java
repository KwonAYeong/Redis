package com.example.concurrency.facade;

import com.example.concurrency.entity.Inventory;
import com.example.concurrency.repository.LettuceRepository;
import com.example.concurrency.service.InventoryService;
import io.lettuce.core.resource.KqueueProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LettuceLockFacade {

    private LettuceRepository lettuceRepository;

    private InventoryService inventoryService;

    public void decrease(Long key, Long quantity) throws InterruptedException {
        // InventoryService 의 decrease 연산 앞 뒤에, 락을 걸고 해제하는 코드를 추가함.
        while (!lettuceRepository.lock(key)) {
            Thread.sleep(100);
        } // 락 점유 시도, 성공시 false, 실패시 true 가 되어 재점유 시도하도록 반복문 설계

        try {
            inventoryService.decrease(key, quantity);
        } finally {
            lettuceRepository.unlock(key);
        } // 예외 발생 없이 성공적으로 decrease 했다면 락 풀기.

    }
}
