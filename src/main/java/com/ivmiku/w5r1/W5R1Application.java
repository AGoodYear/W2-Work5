package com.ivmiku.w5r1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.ivmiku.w5r1.mapper")
@EnableAsync
@EnableTransactionManagement
public class W5R1Application {

    public static void main(String[] args) {
        SpringApplication.run(W5R1Application.class, args);
    }

}
