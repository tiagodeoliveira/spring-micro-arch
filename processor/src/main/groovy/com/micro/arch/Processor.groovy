package com.micro.arch

import groovy.util.logging.Log
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import reactor.Environment
import reactor.bus.EventBus
import reactor.bus.Event

import reactor.spring.context.annotation.Selector

@Log
@SpringBootApplication
@ConfigurationProperties
class Processor {

    def messageConverter = new Jackson2JsonMessageConverter()
    def routingKey

    @Autowired BrainFuckService bfService
    @Autowired ConnectionFactory cachingConnectionFactory
    @Autowired SimpleRabbitListenerContainerFactory containerFactory
    @Autowired RabbitAutoConfiguration rabbitConfiguration

    @RabbitListener(queues = 'toProcess')
    def receiveMessage(message) {
        log.info("Message received $message")
        def jsonMessage = this.messageConverter.fromMessage(message)
        def newMessage = this.process(jsonMessage)
        this.sendMessage(newMessage)
    }

    def void sendMessage(payload) {
        def template = rabbitConfiguration.rabbitTemplate()
        template.setMessageConverter(this.messageConverter)
        template.convertAndSend(this.routingKey, payload)
        log.info("Sending message $payload")
    }

    def process(jsonMessage) {
        log.info("Processing message $jsonMessage")
        def strMessage = jsonMessage.message
        jsonMessage.message = bfService.execute(strMessage)
        return jsonMessage
    }

    static main(args) {
        SpringApplication.run Processor, args
    }
}
