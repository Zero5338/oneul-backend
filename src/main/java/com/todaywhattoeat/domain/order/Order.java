package com.todaywhattoeat.domain.order;

import com.todaywhattoeat.domain.restaurant.Restaurant;
import com.todaywhattoeat.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티.
 *
 * 설계 메모:
 * - 장바구니는 별도 DB 테이블로 만들지 않음. 장바구니는 "주문 전 임시 상태"라
 *   프론트(React Query/로컬 상태)에서만 들고 있다가, "주문하기"를 누르는 순간
 *   여기 Order + OrderItem으로 변환해서 저장함.
 *   -> 서버에 굳이 영속화할 필요 없는 임시 상태까지 테이블로 만들지 않는다는
 *      스코프 판단. (여러 기기 간 장바구니 동기화가 필요해지면 그때 추가 고려)
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private String deliveryAddress; // 실제로 배달되지 않으므로 더미 값

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime statusUpdatedAt;

    @Builder
    public Order(User user, Restaurant restaurant, Long totalAmount, String deliveryAddress) {
        this.user = user;
        this.restaurant = restaurant;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
        this.statusUpdatedAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.statusUpdatedAt = LocalDateTime.now();
    }
}
