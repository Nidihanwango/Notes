package com.example.ppt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan(basePackages = "com.example.ppt.mapper")
public class PptApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PptApplication.class, args);
    }

}
