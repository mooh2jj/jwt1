package com.example.jwt1.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("my filter1~");
//        var writer = servletResponse.getWriter();
//        writer.print("hello~~");

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        // 토큰 : cos 만들어줌. id, pw 정상적으로 들어와서 로그인이 완료되면 토큰을 만들어주고 그걸 응답을 해준다.
        // 요청할 때마 header에 Authorization에 value값으로 토큰을 가지고 올거임
        // 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증만 하면 됨.(RSA, HS256)
        if (req.getMethod().equals("POST")) {
            var headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth: "+ headerAuth);

            if (headerAuth.equals("cos")) {
                filterChain.doFilter(req, res);

            } else {
                PrintWriter printWriter = res.getWriter();
                printWriter.println("인증 안됨");
            }
        }

        System.out.println("필터1");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
