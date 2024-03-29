package com.example.jwt1.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt1.auth.PrincipalDetails;
import com.example.jwt1.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
// login 요청해서 username, password 전송하면 post
// UsernamePasswordAuthenticationFilter 동작을 함.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도중");

        // 1) username, password 받아서
        try {
//            BufferedReader br = request.getReader();
//
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                System.out.println(input);
//            }

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // authenticationManager 로그인 시도: authenticate() => PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨.
            // DB에 있는 username과 password가 일치하면 로그인되고, 로그인되면 session영역에 저장됨.
            Authentication authentication
                    = authenticationManager.authenticate(authenticationToken);
            // authenication 객체가 session영역에 저장됨 => 로그인되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();                // authenication 정보를 가져옴.
            System.out.println("principalDetails:(로그인 완료됨) " + principalDetails.getUser().getUsername());    // 나오면 인증(로그인)된 거!
//            System.out.println(request.getInputStream().toString());
            // authenication 리턴의 이유는 권한 관리를 security가 대신해주기 때문에 편하려고 하는 거
            // 굳이 jwt 토큰을 사용하면서 세션을 만들 이유가 없음. 근데 단지 권한 처리때문에 session을 넣어줌.


            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("=========================================");
        // 2) 정상인지 로그인 시도 -> authenicationManager로 로그인 시도를 하면!
        // PrincipalDateailsService가 호출 loadUserByUsername()함수 실행됨.

        // 3) PrincipalDetails를 세션에 담고 (권한 관리를 위해서)

        // 4) JWT토큰을 만들어서 응답해주면 됨.

        return null;
    }

    // attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실햄됨.
    // jwt 토큰을 만들어서 request 요청한 사용자에게 jwt토큰을 response해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다는 뜻임");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // RSA방식 x Hash암호방식
        var jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * 5))        // 토큰 만료시간
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));

        response.addHeader("Authorization", "Bearer "+jwtToken);        // 이제 jwt를 httpHeader Authorization에 넣고 응답해줌
    }
}
