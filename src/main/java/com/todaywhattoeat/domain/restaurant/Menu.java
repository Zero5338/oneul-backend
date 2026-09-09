package com.todaywhattoeat.domain.restaurant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.LAZY가 기본이지만 명시적으로 적어둠.
    // 주문 상세 조회 시 "주문 100개 -> 메뉴 100번 조회" 같은 N+1이
    // 여기서 발생하기 쉬운 지점 (의도적으로 MVP에서는 그대로 둠).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long basePrice;

    private String imageUrl;

    @Column(nullable = false)
    private String category; // 탭 구분용: 메인/사이드/음료 등

    @Builder
    public Menu(Restaurant restaurant, String name, Long basePrice, String imageUrl, String category) {
        this.restaurant = restaurant;
        this.name = name;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
