package com.example.jwt1.config;

import com.example.jwt1.config.jwt.JwtAuthenticationFilter;
import com.example.jwt1.config.jwt.JwtAuthorizationFilter;
import com.example.jwt1.filter.MyFilter1;
import com.example.jwt1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new MyFilter1(), BasicAuthenticationFilter.class);     // 일반필터보다 Security 필터(addFilterAfter라도)가 앞선다
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// stateless, jwt 토큰방식을 쓸 때 사용하는 설정
                .and()
                .addFilter(corsFilter)      // 다른 도메인에서 ajax으로 접근하는 것을 막는 것. @CrossOrigin(인증x), 시큐리티 필터에 등록인증(O)
                .formLogin().disable()
                .httpBasic().disable()      // httpBasic() -> Authrization : id, pw 노출이 되는 인증방식
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))                    // 인증  jwt 필터 등록
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))     // 인가 jwt 필터 등록
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                ;
    }
}
