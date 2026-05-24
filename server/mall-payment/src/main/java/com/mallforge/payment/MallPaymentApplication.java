package com.mallforge.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.mallforge.payment.mapper")
@ComponentScan(basePackages = { "com.mallforge.payment", "com.mallforge.framework", "com.mallforge.common" })
public class MallPaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallPaymentApplication.class, args);
    }
}
