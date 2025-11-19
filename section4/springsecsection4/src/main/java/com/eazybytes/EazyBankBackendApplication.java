package com.eazybytes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableWebSecurity // Spring Boot를 사용하는 경우에는 명시하지 않아도 된다. build.gradle에서 주입한거만으로도 적용이 됨.
public class EazyBankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EazyBankBackendApplication.class, args);
    }
}
