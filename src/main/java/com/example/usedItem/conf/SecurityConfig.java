package com.example.usedItem.conf; // 적절한 config 패키지에 위치

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig { // 기존 Security 설정 클래스가 있다면 거기에 추가

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 현재 가장 널리 사용되는 안전한 해싱 알고리즘 중 하나입니다.
        return new BCryptPasswordEncoder();
    }

    // 여기에 다른 Spring Security 관련 설정을 추가할 수 있습니다.
    // 예: HttpSecurity 설정 (지금 당장은 API 테스트를 위해 비활성화 가능)
    /*
     * @Bean
     * public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     * http
     * .csrf(csrf -> csrf.disable()) // API 서버는 CSRF 비활성화 고려
     * .authorizeHttpRequests(authz -> authz
     * .requestMatchers("/api/users/signup", "/api/users/check-email",
     * "/api/users/check-nickname").permitAll() // 회원가입 및 중복체크는 허용
     * .anyRequest().authenticated() // 나머지는 인증 필요 (나중에 설정)
     * );
     * // .formLogin(withDefaults()); // 기본 폼 로그인 (API 서버에는 부적합)
     * // .httpBasic(withDefaults()); // 기본 HTTP Basic 인증
     *
     * return http.build();
     * }
     */
}