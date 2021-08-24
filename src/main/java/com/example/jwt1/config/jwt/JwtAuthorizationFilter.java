package com.example.jwt1.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt1.auth.PrincipalDetails;
import com.example.jwt1.model.User;
import com.example.jwt1.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // 인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 타게 됨.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilterInternal(request, response, chain); // 이거 지워야 됨!
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

//        String jwtHeader = request.getHeader("Authorization");
        String jwtHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("jwtHeader: "+ jwtHeader);

        // JWT 토큰을 검증ㅇ해서 정상적인 사용자인지 확인
        // header가 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

//        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        String jwtToken = jwtHeader.substring("Bearer ".length());

        VerifyResult result = JWTUtil.verify(jwtToken);

        if (result.isSuccess()) {
            User userEntity = userRepository.findByUsername(result.getUsername());

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            throw new AuthenticationException("Token is not vaild");
        }

    }
}
