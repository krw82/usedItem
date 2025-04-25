package com.example.usedItem.strategy;

import java.util.Optional;

import com.example.usedItem.domain.ScrapedItem;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface SiteScraperStrategy {

    String getSiteCode();

    /**
     * 해당 사이트에서 상품 목록을 감싸는 요소의 CSS 선택자를 반환합니다.
     *
     * @return 상품 목록 요소 선택자
     */
    String getProductListSelector();

    /**
     * 상품 목록에서 개별 상품 요소를 파싱하여 ScrapedItem 객체를 생성합니다.
     * 파싱 실패 시 Optional.empty() 반환 가능.
     *
     * @param productElements 파싱할 개별 상품 HTML 요소
     * @return 파싱된 ScrapedItem Optional 객체
     */
    Optional<ScrapedItem> parseItem(Element productElements);

}
