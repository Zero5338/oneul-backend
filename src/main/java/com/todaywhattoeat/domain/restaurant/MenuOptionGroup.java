package com.todaywhattoeat.domain.restaurant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴의 옵션 그룹 (예: "사이즈", "맵기").
 * 하나의 그룹 안에 여러 MenuOption(예: "곱빼기 +1000원")이 속함.
 */
@Entity
@Table(name = "menu_option_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private String name; // "사이즈", "맵기" 등

    @Column(nullable = false)
    private boolean required; // 필수 선택 여부

    // 단일선택(라디오)인지 다중선택(체크박스)인지 구분.
    // 프론트에서 이 값 보고 라디오/체크박스 중 뭘 렌더링할지 결정하고,
    // 백엔드에서도 주문 시 옵션 조합이 유효한지(단일인데 여러 개 선택 등) 검증할 때 씀.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SelectionType selectionType;

    @Builder
    public MenuOptionGroup(Menu menu, String name, boolean required, SelectionType selectionType) {
        this.menu = menu;
        this.name = name;
        this.required = required;
        this.selectionType = selectionType;
    }
}
