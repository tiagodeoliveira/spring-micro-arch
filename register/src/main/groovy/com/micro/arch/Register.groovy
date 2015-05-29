package com.micro.arch

import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.annotation.Id

/**
 * Created by tiago on 27/05/15.
 */

@Log
@SpringBootApplication
@RefreshScope
@RestController
class Register {

    @Autowired
    MessagesRepository repository

    static main(args) {
        SpringApplication.run Register, args
    }

    @RequestMapping(name = '/register', method = RequestMethod.POST)
    def receive(@RequestBody payload) {
        def start = payload.start_time
        def end = (System.nanoTime() - start) / 1000000
        log.info("Message: [${payload.message}] - In: [$end ms]")
        this.repository.save(new Message(time: "$end ms".toString(), message: payload.message, id: payload.hashCode()))
        return 'Registered'
    }
}

class Message {
    @Id
    def id
    def time
    def message
}

interface MessagesRepository extends MongoRepository<Message, String> {}