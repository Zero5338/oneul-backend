package com.todaywhattoeat.domain.restaurant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 옵션 그룹에 속한 개별 옵션 (예: "곱빼기 +1000원", "맵게").
 */
@Entity
@Table(name = "menu_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false)
    private MenuOptionGroup optionGroup;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long extraPrice; // 추가 금액 (없으면 0)

    @Builder
    public MenuOption(MenuOptionGroup optionGroup, String name, Long extraPrice) {
        this.optionGroup = optionGroup;
        this.name = name;
        this.extraPrice = extraPrice;
    }
}
