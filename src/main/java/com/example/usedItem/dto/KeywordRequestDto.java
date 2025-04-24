package com.example.usedItem.dto;

import com.example.usedItem.domain.Keyword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // Setter는 Controller에서 @RequestBody 매핑 시 필요할 수 있음
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRequestDto {

    @NotBlank(message = "키워드를 입력해주세요.")
    @Size(max = 255, message = "키워드는 255자 이하로 입력해주세요.")
    private String keywordText;

    @NotBlank(message = "대상 사이트 코드를 입력해주세요.")
    @Size(max = 20, message = "사이트 코드는 20자 이하로 입력해주세요.")
    private String targetSiteCode; // 예: "BUNJANG", "JOONGGO"

    private Boolean active = true; // 기본값 true

    private Integer minPrice;

    private Integer maxPrice;

    public Keyword toEntity() {
        return Keyword.builder()
                .keywordText(this.keywordText)
                .targetSiteCode(this.targetSiteCode)
                .active(this.active)
                .minPrice(this.minPrice)
                .maxPrice(this.maxPrice)
                .build();
    }

}