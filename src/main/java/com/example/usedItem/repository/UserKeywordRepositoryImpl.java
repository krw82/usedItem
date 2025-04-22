package com.example.usedItem.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository; // 구현체에도 @Repository 추가 가능

import com.example.usedItem.domain.UserKeyword;

import java.util.List;

// 클래스 이름은 규칙(기존Repository이름 + Impl)을 따르는 것이 일반적입니다.
@Repository // Spring Bean으로 등록
public class UserKeywordRepositoryImpl implements UserKeywordRepositoryCustom {

    @PersistenceContext // EntityManager 주입
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked") // getResultList() 반환 타입 경고 무시
    public List<UserKeyword> findKeywordsByUserIdWithProcedure(String userId) {
        // 1. StoredProcedureQuery 생성 (프로시저 이름, 결과 매핑 클래스 지정)
        StoredProcedureQuery query = em.createStoredProcedureQuery("SP_GET_USER_KEYWORDS", UserKeyword.class);

        // 2. 파라미터 등록 (IN, OUT, INOUT 등 모드 지정)
        query.registerStoredProcedureParameter("p_user_id", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("o_keywords_cursor", Class.class, ParameterMode.REF_CURSOR); // Oracle
                                                                                                            // REF
                                                                                                            // CURSOR 타입
                                                                                                            // 지정

        // 3. IN 파라미터 값 설정
        query.setParameter("p_user_id", userId);

        // 4. 프로시저 실행
        query.execute();

        // 5. 결과 반환 (REF_CURSOR 결과를 List<UserKeyword>로 매핑하여 반환)
        return query.getResultList();
    }
}
