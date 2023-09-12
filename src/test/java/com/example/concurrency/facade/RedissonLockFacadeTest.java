package com.example.concurrency.facade;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedissonLockFacadeTest {

    @Autowired
    private RedissonLockFacade redissonLockFacade;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach // 테스트 돌리기 전 아이템 1개 / 1번 ID로 / 100개 재고로 집어넣기
    public void insert(){
        Inventory inventory = new Inventory(1L, "추석 항공권", 100L, 1L);
        inventoryRepository.saveAndFlush(inventory);
    }

    @AfterEach // 테스트 실행 후 디비 다 비워버리기
    public void delete() {
        inventoryRepository.deleteAll();
    }

    @Test
    @DisplayName("멀티쓰레드로 100명이 동시에 구매요청을 넣는 경우")
    public void 동시에100명이주문하는상황() throws Exception {
        int threadCount = 100; // 100개 요청 동시에 넣기
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 동시 요청을 도와주는 자바 유틸리티
        CountDownLatch countDownLatch = new CountDownLatch(threadCount); // 먼저 끝난 쓰레드가 대기하도록 교통정리

        for(int i = 0; i < 100; i++){ // 반복문으로 100회 요청
            executorService.submit(() -> { // 개별 쓰레드가 호출할 요청
                try {
                    redissonLockFacade.decrease(1L, 1L); // 동시에 1번 아이템 1개 감소 요청넣기
                } catch(Exception e){}finally{
                    countDownLatch.countDown(); // 요청 들어간 쓰레드는 대기상태로 전환
                }
            });
        }

        countDownLatch.await(); // 모든 쓰레드가 동작을 마치면 병렬처리 종료

        // 재고가 100개인데 1개 감소 요청을 100번 넣었기 때문에 0개가 남아야 함
        Inventory inventory = inventoryRepository.findById(1L).orElseThrow();

        // 1번 아이템의 재고량이 0개일것이라고 단언
        assertEquals(0, inventory.getCount());
    }


}