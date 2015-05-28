package com.micro.arch

import groovy.util.logging.Log
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by tiago on 27/05/15.
 */

@Log
@SpringBootApplication
@EnableDiscoveryClient
@RestController
class Register {

    static main(args) {
        SpringApplication.run Register, args
    }

    @RequestMapping(name = '/register', method = RequestMethod.POST)
    def receive(@RequestBody payload) {
        def start = payload.start_time
        def end = (System.nanoTime() - start) / 1000000
        log.info("Message: [${payload.message}] - In: [$end ms]")
        return 'Registered'
    }
}
