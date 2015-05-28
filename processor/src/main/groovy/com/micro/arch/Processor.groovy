package com.micro.arch

import groovy.util.logging.Log
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties

@Log
@SpringBootApplication
@ConfigurationProperties
class Processor {

    def messageConverter = new Jackson2JsonMessageConverter()
    def routingKey

    @Autowired BrainFuckService bfService
    @Autowired ConnectionFactory cachingConnectionFactory
    @Autowired SimpleRabbitListenerContainerFactory containerFactory
    @Autowired RabbitTemplate rabbitTemplate

    @RabbitListener(queues = 'toProcess')
    def receiveMessage(message) {
        def jsonMessage = this.messageConverter.fromMessage(message)
        def newMessage = this.process(jsonMessage)
        this.sendMessage(newMessage)
    }

    def void sendMessage(payload) {
        rabbitTemplate.setMessageConverter(this.messageConverter)
        rabbitTemplate.convertAndSend(this.routingKey, payload)
    }

    def process(jsonMessage) {
        def strMessage = jsonMessage.message

        log.info("Converting message [${strMessage}]")
        jsonMessage.message = bfService.execute(strMessage)
        log.info("Message converted: [${jsonMessage.message}]")

        return jsonMessage
    }

    static main(args) {
        SpringApplication.run Processor, args
    }
}
