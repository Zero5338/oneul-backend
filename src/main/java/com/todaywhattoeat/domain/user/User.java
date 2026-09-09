package com.todaywhattoeat.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 유저 엔티티.
 *
 * 설계 메모:
 * - walletBalance(가상돈 잔액)는 WalletTransaction(원장/ledger) 합계를 매번 계산하지 않고
 *   User 테이블에 비정규화(denormalize)해서 들고 있음.
 *   이유: 잔액 조회는 매우 잦은 read인데, 매번 트랜잭션 내역을 SUM하면 비효율적.
 *   대신 WalletTransaction 원장은 그대로 남겨서 감사(audit)/이력 조회는 가능하게 유지.
 *   -> 이건 "읽기 빈도가 높은 값은 비정규화하고, 원장은 별도로 남긴다"는
 *      실무에서 흔한 지갑/포인트 시스템 설계 패턴이라 ADR로 남길 가치가 있음.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleId; // 구글 OAuth의 sub(subject) 값

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    // 가상돈 잔액 (비정규화 - 위 설계 메모 참고)
    @Column(nullable = false)
    private Long walletBalance;

    // 낙관적 락(optimistic lock)용 버전 필드.
    // charge()/use()가 "읽고 -> 메모리에서 계산 -> 저장"하는 read-modify-write 패턴이라,
    // 동시에 두 요청이 들어오면 갱신 유실(lost update)이 발생할 수 있음.
    // @Version을 두면 UPDATE 시 JPA가 버전을 같이 체크해서, 충돌 시
    // OptimisticLockException을 던져줌 (서비스 레이어에서 재시도/에러 처리 필요).
    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String googleId, String email, String nickname, String profileImageUrl) {
        this.googleId = googleId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.walletBalance = 100_000L; // 신규 유저 가입 시 가상돈 10만원 무료 지급
        this.createdAt = LocalDateTime.now();
    }

    public void charge(Long amount) {
        this.walletBalance += amount;
    }

    public void use(Long amount) {
        if (this.walletBalance < amount) {
            throw new IllegalStateException("가상돈 잔액이 부족합니다.");
        }
        this.walletBalance -= amount;
    }
}
