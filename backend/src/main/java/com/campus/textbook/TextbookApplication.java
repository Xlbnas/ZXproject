package com.campus.textbook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园教材循环利用平台 启动类
 */
@SpringBootApplication
@MapperScan("com.campus.textbook.mapper")
public class TextbookApplication {
    public static void main(String[] args) {
        SpringApplication.run(TextbookApplication.class, args);
    }
}
