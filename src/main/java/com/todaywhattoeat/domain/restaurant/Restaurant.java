package com.todaywhattoeat.domain.restaurant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantCategory category; // 한식/중식/양식/야식 등

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description; // 설명 텍스트가 길어질 수 있어 TEXT로 지정

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 참고: Restaurant -> Menu는 여기서 @OneToMany로 양방향 매핑하지 않음.
    // 식당 목록 조회 시 메뉴까지 같이 즉시로딩(EAGER)하거나
    // 무심코 각 식당마다 메뉴를 따로 조회(N+1)하게 되기 쉬움.
    // MVP에서는 일단 이대로 두고(N+1 발생 가능 지점), 이후 개선 라운드에서
    // fetch join / @EntityGraph로 최적화하는 걸 의도적으로 남겨둠.

    @Builder
    public Restaurant(String name, RestaurantCategory category, String imageUrl, String description) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
