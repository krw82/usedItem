package com.example.usedItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.usedItem.domain.UserKeyword;

import java.util.List;

@Repository // Spring Bean으로 등록
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long>, UserKeywordRepositoryCustom { // 커스텀
                                                                                                               // 인터페이스
                                                                                                               // 상속 추가

        /**
         * SP_ADD_USER_KEYWORD 프로시저 호출
         *
         * @param userId     사용자 ID (p_user_id 파라미터)
         * @param keyword    키워드 (p_keyword 파라미터)
         * @param targetSite 대상 사이트 (p_target_site 파라미터)
         */
        @Procedure(name = "SP_ADD_USER_KEYWORD") // DB에 정의된 프로시저 이름과 일치해야 함 (대소문자 주의)
        // 또는 procedureName = "SP_ADD_USER_KEYWORD" 로 명시적 지정 가능
        void addUserKeywordProcedure(@Param("p_user_id") String userId,
                        @Param("p_keyword") String keyword,
                        @Param("p_target_site") String targetSite);

        /**
         * SP_GET_USER_KEYWORDS 프로시저 호출 (SYS_REFCURSOR 반환)
         * 주의: @Procedure 어노테이션만으로 REF_CURSOR를 처리하는 것은 Hibernate 버전에 따라 동작이 불안정할 수 있습니다.
         * EntityManager를 직접 사용하는 방식(UserKeywordRepositoryCustom)이 더 안정적일 수 있습니다.
         * 여기서는 예시로 남겨두지만, 아래의 findKeywordsByUserIdWithProcedure 사용을 권장합니다.
         */
        @Procedure(name = "SP_GET_USER_KEYWORDS", outputParameterName = "o_keywords_cursor")
        List<UserKeyword> getUserKeywordsProcedure(@Param("p_user_id") String userId);

        // --- Spring Data JPA 쿼리 메소드 (참고용) ---
        // PL/SQL을 직접 호출하지 않고, JPA가 SQL을 생성하여 실행합니다.
        // 메소드 이름 규칙에 따라 쿼리를 자동으로 생성합니다.
        List<UserKeyword> findByUserIdOrderByCreatedAtDesc(String userId);
}
