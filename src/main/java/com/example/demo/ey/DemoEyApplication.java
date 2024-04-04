package com.example.demo.ey;

import jakarta.annotation.PostConstruct;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoEyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoEyApplication.class, args);
    }

    @PostConstruct
    public void init() {
        SpringDocUtils.getConfig().replaceWithClass(org.springframework.data.domain.Pageable.class, org.springdoc.core.converters.models.Pageable.class);
    }

}
