package com.example.usedItem.service;

import com.example.usedItem.domain.UserKeyword;
import java.util.List;

/**
 * 사용자 키워드 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 */
public interface UserKeywordService {

    /**
     * 새 키워드를 등록합니다.
     * 실제 구현에서는 PL/SQL 프로시저를 호출할 수 있습니다.
     */
    void registerKeyword(String userId, String keyword, String targetSite);

    /**
     * 특정 사용자의 키워드 목록을 조회합니다.
     * 실제 구현에서는 PL/SQL 프로시저 (EntityManager 사용)를 호출할 수 있습니다.
     */
    List<UserKeyword> getKeywordsForUser(String userId);
}
