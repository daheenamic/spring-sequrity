package com.eazybytes.config;

import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok이 필수 필드가 있는 생성자를 만든다.
public class EazyBankUserDetailsService implements UserDetailsService {

    // Java 클래스 내부에 단일 생성자가 있을 때는 그 생성자 위에 @Autowired를 작성할 필요가 없다.
    // @RequiredArgsConstructor 이게 생성자 1개 만들어줌
    private final CustomerRepository customerRepository;

    /**
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username).orElseThrow(() -> new
                UsernameNotFoundException("User details not found for the user:" + username));
        // GrantedAuthority - 권한을 표현하는 인터페이스
        // SimpleGrantedAuthority - 그걸 구현한 가장 간단한 클래스
        // 만약 List.of("ROLE_ADMIN") 이렇게 하면 Spring Security는 String을 직접 처리 안함.
        // 아래처럼 해야지 Spring Security가 이해 가능. 타입 안정성을 위해서라도 사용.
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole()));
        return new User(customer.getEmail(), customer.getPwd(), authorities);
    }
}
