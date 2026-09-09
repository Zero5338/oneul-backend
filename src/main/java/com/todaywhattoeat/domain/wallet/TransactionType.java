package com.todaywhattoeat.domain.wallet;

public enum TransactionType {
    CHARGE, // 충전 (결제 테스트모드)
    USE,    // 사용 (주문 시 차감)
    SIGNUP_BONUS // 신규가입 무료 지급
}
