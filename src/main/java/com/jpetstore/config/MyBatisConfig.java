package com.jpetstore.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.jpetstore.mapper")
public class MyBatisConfig {
}