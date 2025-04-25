package com.example.usedItem.service.Impl; // 실제 구현 패키지 이름으로 변경하세요.

import com.example.usedItem.domain.Keyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.usedItem.domain.ScrapedItem;
import com.example.usedItem.repository.KeywordRepository;
import com.example.usedItem.service.ScrapedItemService;
import com.example.usedItem.service.ScrapingService;
import com.example.usedItem.strategy.SiteScraperStrategy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingServiceImpl implements ScrapingService {

    private final KeywordRepository keywordRepository;
    private final ScrapedItemService scrapedItemService; // 스크랩한 아이템을 저장하는 서비스
    private final SiteScraperStrategy siteScraperStrategy;

    // 예시: 5분마다 실행 (fixedRate = 밀리초)
    @Scheduled(fixedRate = 300000) // 5 * 60 * 1000 = 300,000ms (5분)
    @Transactional(readOnly = true) // 키워드를 가져오기 위한 읽기 전용 트랜잭션
    @Override
    public void performScraping() {
        log.info("스크래핑 작업 시작...");
        // 활성 키워드 가져오기 (KeywordRepository에 findByActiveTrue가 존재하는지 확인)
        List<Keyword> activeKeywords = keywordRepository.findByActiveTrue();

        if (activeKeywords.isEmpty()) {
            log.info("스크랩할 활성 키워드가 없습니다.");
            return;
        }

        log.info("처리할 활성 키워드 {}개를 찾았습니다.", activeKeywords.size());

        for (Keyword keyword : activeKeywords) {
            log.debug("키워드 '{}' (사이트: {})에 대한 스크래핑을 시도합니다.", keyword.getKeywordText(), keyword.getTargetSiteCode());
            try {
                List<ScrapedItem> scrapedItems = scrapeSite(keyword.getTargetSiteCode(), keyword.getKeywordText());
                if (!scrapedItems.isEmpty()) {
                    // 아이템 저장은 별도의 트랜잭션에서 이루어져야 합니다.
                    // ScrapedItemService.saveItemsIfNotExists는 자체 트랜잭션을 처리해야 합니다.
                    scrapedItemService.saveItemsIfNotExists(scrapedItems);
                }
                // 대상 사이트에 과부하를 주지 않도록 작은 지연을 추가하는 것을 고려합니다.
                // Thread.sleep(1000); // 예: 1초 대기
            } catch (IOException e) {
                log.error("I/O error scraping for keyword '{}' (Site: {}): {}", keyword.getKeywordText(),
                        keyword.getTargetSiteCode(), e.getMessage());
            } catch (Exception e) { // Catch broader exceptions, but log specifically
                log.error("Unexpected error scraping for keyword '{}' (Site: {}): {} ", keyword.getKeywordText(),
                        keyword.getTargetSiteCode(), e.getMessage(), e);
            }
        }
        log.info("스크래핑 작업이 완료되었습니다.");
    }

    /**
     * 사이트 코드와 키워드를 기반으로 특정 사이트를 스크랩합니다.
     * 참고: 이 메서드는 네트워크 I/O 및 파싱을 수행합니다.
     *
     * @param siteCode    사이트 코드 (예: "BUNJANG").
     * @param keywordText 검색할 키워드.
     * @return 찾은 ScrapedItem 객체 목록.
     * @throws IOException 연결 또는 파싱에 실패한 경우.
     */
    private List<ScrapedItem> scrapeSite(String siteCode, String keywordText) throws IOException {
        List<ScrapedItem> items = new ArrayList<>();
        String targetUrl = buildTargetUrl(siteCode, keywordText);

        if (targetUrl == null) {
            log.warn("사이트 코드 '{}' 및 키워드 '{}'에 대한 대상 URL을 생성할 수 없습니다. 건너뜁니다.", siteCode, keywordText);
            return items; // URL 없이는 진행할 수 없습니다.
        }

        log.debug("URL에 연결 중: {}", targetUrl);

        // Jsoup를 사용하여 HTML 요청 및 파싱
        // !!! 중요: User-Agent는 실제 브라우저를 모방해야 합니다 !!!
        Document doc = Jsoup.connect(targetUrl)
                .userAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .timeout(15000) // 15 seconds timeout
                .get();

        // --- Site-Specific Parsing Logic ---
        // This part needs to be adapted for EACH site's HTML structure.
        Elements productElements = doc.select(siteScraperStrategy.getProductListSelector());

        if (productElements.isEmpty()) {
            log.warn("URL '{}'에서 사이트 '{}'에 대한 선택자를 사용하여 제품 요소를 찾을 수 없습니다.", targetUrl, siteCode);
            // 디버깅을 위해 HTML의 일부를 여기에 기록하는 것이 유용할 수 있습니다.
            // log.debug("HTML 본문 스니펫: {}", doc.body().html().substring(0,
            // Math.min(doc.body().html().length(), 1000)));
            return items;
        }

        log.debug("사이트 '{}'에 대한 잠재적인 제품 요소 {}개를 찾았습니다.", siteCode, productElements.size());

        for (Element productElement : productElements) {
            try {

                items.add(siteScraperStrategy.parseItem(productElement).orElseThrow());
            } catch (Exception e) {
                // 단일 항목을 파싱하는 동안 예외를 잡아 다른 항목이 계속 진행되도록 합니다.
                log.warn("개별 제품 요소를 파싱하는 중 오류가 발생했습니다(항목 건너뜀): {}. 요소 HTML: {}", e.getMessage(),
                        productElement.outerHtml().substring(0, Math.min(productElement.outerHtml().length(), 500)));
            }
        }
        log.info("사이트: {}, 키워드: '{}', {}개의 항목이 성공적으로 파싱되어 저장 준비가 완료되었습니다.", siteCode, keywordText, items.size());
        return items;

    }

    /**
     * 사이트 코드와 키워드를 기반으로 대상 검색 URL을 구성합니다.
     *
     * @param siteCode    사이트 식별자.
     * @param keywordText 검색어.
     * @return 형식이 지정된 검색 URL, 또는 사이트가 지원되지 않거나 인코딩에 실패한 경우 null.
     */
    private String buildTargetUrl(String siteCode, String keywordText) {
        try {
            String encodedKeyword = URLEncoder.encode(keywordText, StandardCharsets.UTF_8.toString());

            if ("BUNJANG".equalsIgnoreCase(siteCode)) {
                // Verify the correct, current search URL structure for Bunjang mobile/web
                return "https://m.bunjang.co.kr/search/products?q=" + encodedKeyword; // 번개장터 모바일/웹의 올바른 최신 검색 URL 구조를
                                                                                      // 확인하세요.
            } else if ("JOONGGO".equalsIgnoreCase(siteCode)) { // 중고나라
                // 중고나라 모바일/웹의 올바른 최신 검색 URL 구조를 확인하세요.
                return "https://m.joongna.com/search/list?searchAttribute=content&query="
                        + encodedKeyword;

            }
            // 다른 지원되는 사이트에 대한 'else if' 블록을 추가합니다.
            else {
                log.warn("URL 구성에 지원되지 않는 사이트 코드: {}", siteCode);
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("키워드 '{}'를 URL 인코딩하는 데 실패했습니다: {}", keywordText, e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the unique source identifier for an item from its URL.
     * This logic is highly site-specific.
     * URL에서 항목에 대한 고유한 소스 식별자를 추출합니다.
     * 이 로직은 매우 사이트별로 다릅니다.
     *
     * @param itemUrl  항목 세부 정보 페이지의 전체 URL.
     * @param siteCode URL이 속한 사이트의 코드.
     * @return 추출된 소스 ID, 또는 추출에 실패한 경우 null.
     */
    private String extractSourceIdFromUrl(String itemUrl, String siteCode) {
        if (itemUrl == null || itemUrl.isEmpty()) {
            return null;
        }

        try {
            if ("BUNJANG".equalsIgnoreCase(siteCode) && itemUrl.contains("/products/")) {
                // 예시: https://m.bunjang.co.kr/products/12345678?q=keyword
                String pathPart = itemUrl.split("\\?")[0]; // 쿼리 매개변수 제거 바꿨음 고쳐야함
                return pathPart.substring(pathPart.lastIndexOf('/') + 1);
            } else if ("JOONGGO".equalsIgnoreCase(siteCode)) {
                // 예시: https://m.joongna.com/product-detail/6543210
                // 예시: https://cafe.naver.com/joonggonara/123456789 (다른 로직 필요)
                // 중고나라의 URL 패턴을 기반으로 추출 로직을 구현합니다.
                if (itemUrl.contains("joongna.com/product-detail/")) {
                    String pathPart = itemUrl.split("//")[0]; // 바꿨음 고쳐야함
                    return pathPart.substring(pathPart.lastIndexOf('/') + 1);
                } else if (itemUrl.contains("joonggonara") && itemUrl.contains("/")) { // 카페 URL에 대한 기본 검사
                    // 카페 URL에 대한 더 강력한 파싱이 필요합니다.
                    log.warn("중고나라 카페 URL ID 추출이 완전히 구현되지 않았습니다: {}", itemUrl);
                    return null;
                }
                log.warn("URL에 대한 중고나라 ID 추출 방법을 결정할 수 없습니다: {}", itemUrl);
                return null;
            }
            // 다른 사이트에 대한 'else if' 블록을 추가합니다.
            else {
                log.warn("사이트 코드 '{}' 및 URL에 대한 소스 ID 추출 로직이 정의되지 않았습니다: {}", siteCode, itemUrl);
                return null;
            }
        } catch (Exception e) {
            log.warn("Failed to extract source ID from URL '{}' for site '{}': {}", itemUrl, siteCode, e.getMessage());
            return null;
        }
    }
}
