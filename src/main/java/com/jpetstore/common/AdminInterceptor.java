package com.jpetstore.common;

import com.jpetstore.domain.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession();
        Account user = (Account) session.getAttribute("user");

        if (user == null) {
            // If it's an API request, return JSON
            if (path.startsWith("/api/")) {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(Result.unauthorized("请先登录")));
                out.flush();
                out.close();
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        if (!user.isAdmin()) {
            if (path.startsWith("/api/")) {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString(Result.forbidden("需要管理员权限")));
                out.flush();
                out.close();
            } else {
                response.sendRedirect("/");
            }
            return false;
        }

        return true;
    }
}
