package com.easybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact", "/error").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    // UserDetailsService
    // - Spring Security 프레임 내 인터페이스. 사용자 계정을 처리하는데 도움을 줌
    // - 새로운 사용자를 생성하거나, 로그인 작업 중 기존 사용자를 가져올 수 있음
    @Bean
    public UserDetailsService userDetailsService() {
        // 운영 환경에서 절대 이렇게 직접적으로 사용자 정보를 저장하면 안되고 DB에 저장하여 가져와야함
        // 비밀번호 앞에 {noop} 접두사를 붙이면 Encoder를 사용하지 않고 평문으로 비밀번호를 사용하겠다는 뜻
        // {noop} 더이상 사용하지 않는 NoOpPasswordEncoder라서 평문 테스트할때 사용함
        UserDetails user = User
                .withUsername("user")
                .password("{noop}EazyBytes@12345")
                .authorities("read").build();
        UserDetails admin = User
                .withUsername("admin")
                .password("{bcrypt}$2a$12$QghCYcYMQkauzbQi4XYj8O5TAFAfqZei16OivC8sNDTD.SRZU7/I6") // EazyBytes@54321
                .authorities("admin").build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // SpringSecurity에서 권장하는 기본 인코더는 BCrypt이다. 기본 옵션을 선택하는게 좋다.
        // 유연성있는 코드 작성법이기 때문에 PasswordEncoderFactories 객체를 사용하는것이 좋음
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

        // 이런식으로 직접 써도 되지만 나중에 기본적으로 권장되는 것이 바뀔경우 코드를 수정해야 하므로 위처럼 쓰자.
//        return new BCryptPasswordEncoder();
    }

    // 사용자가 쉬운 비밀번호를 사용하지 않게, 혹은 데이터가 유출되지 않았는지 체크하는 인터페이스
    // 업계에서 노출된 REST API 중 하나를 호출한다. "https://api.pwnedpasswords.com/range/"

    /**
     * From Spring Security 6.3
     */
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        // user나 admin의 password를 12345, 54321 이렇게 쉽게 설정하니까 오류가 생김
        // 심지어 User@12345, Admin@54321 이것도 쉽다고 나옴.
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
