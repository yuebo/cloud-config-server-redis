package com.eappcat.cloud.config.server.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerRedisApplication.class, args);
    }

}
