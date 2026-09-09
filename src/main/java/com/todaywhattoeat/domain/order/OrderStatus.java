package com.todaywhattoeat.domain.order;

public enum OrderStatus {
    RECEIVED,     // 접수완료
    COOKING,      // 조리중
    DELIVERING,   // 배달출발
    ARRIVED       // 도착 (사실은 안 옴 - 컨셉상 유머 처리는 프론트에서)
}
