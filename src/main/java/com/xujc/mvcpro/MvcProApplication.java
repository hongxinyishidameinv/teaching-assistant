package com.xujc.mvcpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MvcProApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcProApplication.class, args);
    }

}
