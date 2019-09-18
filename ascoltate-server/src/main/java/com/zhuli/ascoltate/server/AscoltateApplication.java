package com.zhuli.ascoltate.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
public class AscoltateApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(AscoltateApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
