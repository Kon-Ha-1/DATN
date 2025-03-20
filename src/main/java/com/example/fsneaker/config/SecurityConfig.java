package com.example.fsneaker.config;

import com.example.fsneaker.dto.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/trangchuadmin").hasAnyRole("MANAGER","STAFF")
                        .requestMatchers("/quan-ly-tai-khoan").hasRole("ADMIN")
                        .requestMatchers("/trangkhachhang").hasRole("CUSTOMER")
                        .requestMatchers("/thanh-toan").authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true) //Xóa session hiện tại
                        .deleteCookies("JSESSIONID") //Xóa cookie phiên mặc định của Spring Security
                        .addLogoutHandler(((request, response, authentication) -> {
                            //Dọn dẹp thêm dữ liệu nếu cần
                            //Ví dụ: Xóa cache, hoặc dữ liệu lưu trữ tạm thời khác
                            SecurityContextHolder.clearContext();;//Xóa SecurityContext
                        }))
                        .permitAll()).csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
