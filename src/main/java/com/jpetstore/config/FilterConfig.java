package com.jpetstore.config;

import com.jpetstore.common.JwtAuthFilter;
import com.jpetstore.common.JwtUtil;
import com.jpetstore.service.AccountService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil("jpetstore-secret-key-change-in-production-2026", 86400000);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtUtil jwtUtil, AccountService accountService) {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter(jwtUtil, accountService));
        bean.addUrlPatterns("/api/*");
        return bean;
    }
}
