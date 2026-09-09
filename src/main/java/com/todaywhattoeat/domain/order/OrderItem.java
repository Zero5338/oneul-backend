package com.todaywhattoeat.domain.order;

import com.todaywhattoeat.domain.restaurant.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문에 담긴 개별 메뉴 항목.
 *
 * 설계 메모:
 * - menuName / unitPrice를 Menu 엔티티에서 매번 조인해서 가져오지 않고
 *   "주문 시점의 스냅샷"으로 이 테이블에 그대로 복사해서 저장함.
 *   이유: 나중에 식당이 메뉴 가격을 바꾸거나 메뉴를 삭제해도,
 *   과거 주문 내역(영수증)은 "주문했던 그 시점의 가격"을 그대로 보여줘야 함.
 *   -> 실제 커머스/주문 시스템에서 흔히 쓰이는 스냅샷 패턴.
 * - 선택한 옵션들은 별도 조인 테이블 대신 간단한 문자열(예: "곱빼기, 맵게")로
 *   저장함. 옵션 조합에 대한 정교한 재조회/통계가 필요해지기 전까지는
 *   테이블을 늘리지 않는 MVP 스코프 판단.
 */
@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private String menuNameSnapshot;

    @Column(nullable = false)
    private Long unitPriceSnapshot; // 옵션 추가금액까지 반영된 단가

    @Column(nullable = false)
    private Integer quantity;

    private String selectedOptionsSummary; // 예: "곱빼기, 맵게"

    @Column(nullable = false)
    private Long itemTotalPrice; // unitPriceSnapshot * quantity

    @Builder
    public OrderItem(Order order, Menu menu, String menuNameSnapshot, Long unitPriceSnapshot,
                      Integer quantity, String selectedOptionsSummary) {
        this.order = order;
        this.menu = menu;
        this.menuNameSnapshot = menuNameSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.quantity = quantity;
        this.selectedOptionsSummary = selectedOptionsSummary;
        this.itemTotalPrice = unitPriceSnapshot * quantity;
    }
}
