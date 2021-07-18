package com.example.jwt1.filter;


import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("my filter1~");
//        var writer = servletResponse.getWriter();
//        writer.print("hello~~");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
