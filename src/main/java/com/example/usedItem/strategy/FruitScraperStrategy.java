package com.example.usedItem.strategy;

import java.util.Optional;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.example.usedItem.domain.ScrapedItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FruitScraperStrategy implements SiteScraperStrategy {

    private static final String SITE_CODE = "FRUIT";
    private static final String BASE_URL = "https://fruitsfamily.com";

    @Override
    public String getSiteCode() {
        return SITE_CODE;
    }

    @Override
    public String getProductListSelector() {
        return "div.ProductsListItem";
    }

    @Override
    public Optional<ScrapedItem> parseItem(Element productElement) {
        try {
            // --- FruitsFamily 데이터 추출 로직 ---

            // 1. 상품 링크 및 ID 추출
            Element linkElement = productElement.selectFirst("a.ProductPreview");
            if (linkElement == null) {
                log.warn("[{}] Product link (a.ProductPreview) not found. Skipping item. HTML: {}", SITE_CODE,
                        productElement.html().substring(0, Math.min(productElement.html().length(), 200)));
                return Optional.empty();
            }
            String relativeUrl = linkElement.attr("href");
            if (relativeUrl == null || relativeUrl.isEmpty() || !relativeUrl.startsWith("/product/")) {
                log.warn("[{}] Invalid or missing product href. Skipping item. Href: '{}'", SITE_CODE, relativeUrl);
                return Optional.empty();
            }
            String itemUrl = BASE_URL + relativeUrl;
            String itemSourceId = extractSourceIdFromUrl(relativeUrl);
            if (itemSourceId == null) {
                log.warn("[{}] Could not extract source ID from URL: {}. Skipping item.", SITE_CODE, itemUrl);
                return Optional.empty();
            }

            // 2. 이미지 URL 추출
            Element imgElement = productElement.selectFirst("img.ProductPreview-image");
            String imageUrl = (imgElement != null) ? imgElement.attr("src") : "";
            // src 속성이 비어있을 수 있으므로 추가 체크
            if (imageUrl.isEmpty()) {
                log.warn("[{}] Image source is empty for item URL: {}", SITE_CODE, itemUrl);
                // imageUrl은 null이 아니도록 빈 문자열 유지하거나, 정책에 따라 여기서 건너뛸 수도 있음
            }

            // 3. 브랜드 추출
            Element brandElement = productElement.selectFirst("h6.ProductsListItem-brand");
            String brand = (brandElement != null) ? brandElement.text() : "Unknown"; // 브랜드 없으면 Unknown 처리

            // 4. 제목 추출
            Element titleElement = productElement.selectFirst("h7.ProductsListItem-title");
            String title = (titleElement != null) ? titleElement.text() : "";
            if (title.isEmpty()) {
                log.warn("[{}] Title is empty for item URL: {}. Skipping item.", SITE_CODE, itemUrl);
                return Optional.empty(); // 제목 없으면 필수 데이터 누락 간주
            }

            // 5. 가격 추출 및 파싱
            Element priceElement = productElement.selectFirst("div.ProductsListItem-price");
            Integer price = null;
            if (priceElement != null) {
                String priceText = priceElement.text().replaceAll("[^0-9]", ""); // "원", "," 제거
                if (!priceText.isEmpty()) {
                    try {
                        price = Integer.parseInt(priceText);
                    } catch (NumberFormatException nfe) {
                        log.warn("[{}] 가격 파싱 실패: '{}', Item: '{}', URL: {}", SITE_CODE, priceElement.text(), title,
                                itemUrl);
                        // 가격 파싱 실패 시 null 유지
                    }
                }
            } else {
                log.warn("[{}] Price element not found for item URL: {}", SITE_CODE, itemUrl);
            }

            // 6. 사이즈 추출
            Element sizeElement = productElement.selectFirst("div.ProductsListItem-size");
            String size = (sizeElement != null) ? sizeElement.text() : "OS"; // 사이즈 없으면 OS 기본값

            // 7. 위치 정보 (HTML 구조상 목록에 없음)
            String location = null;

            // 8. 게시 시간 (HTML 구조상 목록에 없음)
            // LocalDateTime postedAtSource = null;

            // ScrapedItem 객체 생성 (필수값 확인은 빌더 전에 해도 됨)
            ScrapedItem item = ScrapedItem.builder()
                    .sourceSiteCode(SITE_CODE)
                    .itemSourceId(itemSourceId)
                    .title(title)
                    .price(price)
                    .itemUrl(itemUrl)
                    .imageUrl(imageUrl)
                    .location(location) // null 가능
                    // .postedAtSource(postedAtSource) // null 가능
                    .build();
            // Fruits Family는 ScrapedItem 엔티티에 size 필드가 없으므로 빌더에 추가하지 않음
            // 필요하다면 ScrapedItem 엔티티 및 빌더 수정 필요

            log.trace("[{}] 항목 파싱 성공: {}", SITE_CODE, title);
            return Optional.of(item);

        } catch (Exception e) {
            // 예상치 못한 다른 예외 처리
            log.warn("[{}] 개별 항목 파싱 중 예상치 못한 오류 (건너뜀): {}. 요소 HTML: {}", SITE_CODE, e.getMessage(),
                    productElement.html().substring(0, Math.min(productElement.html().length(), 500)), e);
            return Optional.empty();
        }
    }

    // URL에서 상품 고유 ID 추출 (FruitsFamily 용)
    // 예: /product/oq2x/존-로렌스-설리반-belted-denim-shirt -> "oq2x"
    private String extractSourceIdFromUrl(String relativeUrl) {
        if (relativeUrl != null && relativeUrl.startsWith("/product/")) {
            try {
                String[] parts = relativeUrl.split("/");
                if (parts.length > 2 && !parts[2].isEmpty()) {
                    return parts[2]; // "/product/" 다음 부분을 ID로 간주
                }
            } catch (Exception e) {
                log.warn("[{}] URL에서 상품 ID 추출 실패: {}", SITE_CODE, relativeUrl);
            }
        }
        return null;
    }
}