package com.micro.arch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class Provider {
    static main(args) {
        SpringApplication.run Provider, args
    }
}
