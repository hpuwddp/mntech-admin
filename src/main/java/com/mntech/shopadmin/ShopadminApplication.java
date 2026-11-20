package com.mntech.shopadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mntech.shopadmin.dao")
public class ShopadminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopadminApplication.class, args);
    }

}
