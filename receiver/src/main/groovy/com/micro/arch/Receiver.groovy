package com.micro.arch

import groovy.util.logging.Log
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.env.MissingRequiredPropertiesException
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@Log
@SpringBootApplication
@ConfigurationProperties
@RestController
class Receiver {

    def routingKey
    def exchange

    @Autowired
    def RabbitAutoConfiguration rabbitConfiguration

    @Bean
    public messageConverter() {
        return new Jackson2JsonMessageConverter()
    }

    static main(args) {
        SpringApplication.run Receiver, args
    }

    @RequestMapping(name = '/receive', method = RequestMethod.POST)
    def receive(@RequestBody payload) {
        if (!payload.callback) {
            throw new MissingRequiredPropertiesException('Need to inform the callback URL')
        }
        payload.start_time = System.nanoTime()

        def result = this.sendMessage(payload)
        return ['message': result]
    }

    def sendMessage(payload) {

        def template = rabbitConfiguration.rabbitTemplate()
        template.setMessageConverter(this.messageConverter())

        def init = System.nanoTime()

        template.exchange = this.exchange
        template.convertAndSend(this.routingKey, payload)

        def finish = System.nanoTime()
        def time = (finish - init) / 1000000
        log.info "Message sent $time ms"

        return 'Your solicitation is being processed, wait the response!'
    }
}
