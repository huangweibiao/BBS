package com.bbs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BBS论坛系统启动类
 * 采用Spring Boot 3.5.11 + MyBatis-Plus + MySQL 8 + Redis架构
 */
@SpringBootApplication
@MapperScan("com.bbs.mapper")
@EnableScheduling
public class BBSApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BBSApplication.class, args);
    }
}