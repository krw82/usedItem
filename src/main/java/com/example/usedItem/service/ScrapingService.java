package com.example.usedItem.service; // Use your actual package name

/**
 * 스크래핑 작업에 대한 계약을 정의합니다.
 */
public interface ScrapingService {

    /**
     * 지원되는 사이트에서 활성 키워드에 대한 예약된 스크래핑 작업을 수행합니다.
     * 이 메서드는 일반적으로 스케줄러에 의해 트리거됩니다.
     */
    void performScraping();

    /**
     * 주어진 키워드와 일치하는 항목에 대해 특정 대상 사이트를 스크랩합니다.
     *
     * @param siteCode    대상 사이트를 식별하는 코드입니다 (예: "BUNJANG", "JOONGGO").
     * @param keywordText 검색할 키워드입니다.
     * @throws IOException 네트워크 요청 또는 파싱 중에 오류가 발생한 경우.
     *                     @// TODO: 외부에서 직접 스크래핑이 필요한 경우 List<ScrapedItem>을 반환하는 것을
     *                     고려하십시오.
     *                     // 그러나 현재 디자인은 내부적으로 저장을 처리하는 performScraping을 사용합니다.
     */
    // List<ScrapedItem> scrapeSite(String siteCode, String keywordText) throws
    // IOException; // performScraping에서 내부적으로만 사용되는 경우 private으로 유지하거나 제거하십시오.
}
