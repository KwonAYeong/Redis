package com.example.concurrency.facade;

import com.example.concurrency.service.OptimisticInventoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OptimisticInventoryFacade {

    // 파사드 클래스의 역할은 낙관적락 서비스가 기각되었을 때 호출이 반영될 때까지 지속적으로 재시도 하도록 만드는 것이다.
    private OptimisticInventoryService optimisticInventoryService;

    public void decrease(Long id, Long count) throws InterruptedException {

        // 그냥 호출하기만 하면 안되고, 성공할 때까지 반복적으로 호출을 시도해야한다.
        while (true) {
            try {
                optimisticInventoryService.decrease(id, count);  // 서비스의 decrease 호출
                break; // 위 구문에 의한 호출이 버전 정합성이 맞아서 받아들여졌다면 반복 호출 해제
            } catch (Exception e) {
                // 낙관적 락에 의해서 버저닝 정합성이 맞지 않아 예외가 발생했다면
                Thread.sleep(100); // 0.1 초 뒤에 다시 시도
            }
        }
    }
}
