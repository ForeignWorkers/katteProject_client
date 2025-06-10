package me.soldesk.katte_project_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form
                        .loginPage("/html/Loginpage/Login.html")
                        .defaultSuccessUrl("/html/Mainpage/Mainpage.html", true)
                        .permitAll()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                     // 메인 접근 허용
                                "/html/Mainpage/Mainpage.html",   // 리다이렉트 대상 허용
                                "/html/Loginpage/Login.html",     // 로그인 페이지 허용
                                "/html/Signuppage/Signup.html",
                                "/css/**", "/js/**",
                                "/signUp",
                                "/signUp_pro"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}