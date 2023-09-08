package com.example.concurrency.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {  // 재고 관리용 테이블

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id; // 물폼 번호

    private String ItemName; // 물품명

    private Long count; // 재고량

    @Version // 버전 필드로 추가
    private Long version; // DB 버전

    // 재고가 0개 미만으로 떨어지지 않도록 검증해주는 메서드
    public void decrease(Long count) {
        if (this.count - count < 0) {
            throw new RuntimeException("재고량이 부족해 판매할 수 없습니다.");
        }
        this.count -= count;
    }


}
