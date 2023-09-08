package com.example.concurrency.service;

import com.example.concurrency.entity.Inventory;
import com.example.concurrency.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach  // 테스트 돌리기 전 id=1 인 아이템 한 개를 재고 100개로 집어넣기.
    public void insert() {
        Inventory inventory = new Inventory(1L, "추석 항공권", 100L, 1L);
        inventoryRepository.saveAndFlush(inventory);
    }

    @AfterEach // 테스트 실행 후 DB 비우기
    public void delete() {
        inventoryRepository.deleteAll();
    }

    @Test
    @DisplayName("재고가 100개인 id=1 인 아이템의 재고를 -1 하면 99개가 남는다.")
    public void 동시성문제없는재고감소상황() {
        // 서비스 레이어에서 1번 아이템 재고 1개 감소시키기.
        inventoryService.decrease(1L, 1L);

        Inventory inventory = inventoryRepository.findById(1L).orElseThrow();

        assertEquals(99L, inventory.getCount());
    }

    @Test
    @DisplayName("멀티스레드를 활용해서 동시에 100명이 1개씩 주문을 한 상황")
    public void 동시에100명이주문하는상황() throws InterruptedException {
        int threadCount = 100; // 100개 요청 동시에 넣기
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 동시 요청을 도와주는 자바 유틸리티
        CountDownLatch countDownLatch = new CountDownLatch(threadCount); // 먼저 끝난 쓰레드가 대기하도록 교통정리

        for (int i = 0; i < 100; i++) {  // 반복문으로 100 회 요청
            executorService.submit(() -> { // 개별 쓰레드가 호출할 요청
                try {
                    inventoryService.decrease(1L, 1L); // 동시에 1번 아이템 1개 감소 요청 넣기
                } finally {
                    countDownLatch.countDown(); // 요청 들어간 스레드는 대기 상태로 전환
                }
            });
        }

        countDownLatch.await();  // 모든 쓰레드가 동작을 마치면 병렬처리 종료

        // 재고가 100개 인데 재고 -1 요청을 100번 넣었기 때문에 재고 = 0 이어야 한다.
        Inventory inventory = inventoryRepository.findById(1L).orElseThrow();

        // id=1 물건 재고량=0 일 것이라고 단언
        assertEquals(0, inventory.getCount());
    }
}
