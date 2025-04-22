package com.example.usedItem.repository;

import java.util.List;

import com.example.usedItem.domain.UserKeyword;

public interface UserKeywordRepositoryCustom {
    /**
     * EntityManager를 사용하여 SP_GET_USER_KEYWORDS 프로시저 호출 (SYS_REFCURSOR 처리)
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 키워드 목록
     */
    List<UserKeyword> findKeywordsByUserIdWithProcedure(String userId);
}
