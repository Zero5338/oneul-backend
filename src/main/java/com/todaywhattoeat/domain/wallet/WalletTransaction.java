package com.todaywhattoeat.domain.wallet;

import com.todaywhattoeat.domain.order.Order;
import com.todaywhattoeat.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 가상돈 원장(ledger).
 *
 * User.walletBalance는 "현재 잔액"만 빠르게 읽기 위한 비정규화 값이고,
 * 실제 충전/사용 이력은 전부 이 테이블에 남긴다.
 *
 * idempotencyKey: 결제 웹훅이 네트워크 문제로 재전송됐을 때
 * 같은 결제 건이 중복으로 적립되는 걸 막기 위한 유니크 키.
 * (PG사가 보내는 결제 승인 건 고유 ID를 그대로 사용)
 */
@Entity
@Table(name = "wallet_transactions",
        uniqueConstraints = @UniqueConstraint(columnNames = "idempotencyKey"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private Long amount;

    // USE 타입일 때만 연결됨 (충전/가입보너스는 null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order relatedOrder;

    // CHARGE 타입일 때만 값이 있음. 웹훅 중복 처리 방지용.
    private String idempotencyKey;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public WalletTransaction(User user, TransactionType type, Long amount,
                              Order relatedOrder, String idempotencyKey) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.relatedOrder = relatedOrder;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now();
    }
}
