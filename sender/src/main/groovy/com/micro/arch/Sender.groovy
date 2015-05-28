package com.micro.arch

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import groovy.util.logging.Log
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Log
@SpringBootApplication
@ConfigurationProperties
@RefreshScope
@EnableHystrix
class Sender {
    @Autowired
    ConnectionFactory cachingConnectionFactory
    @Autowired
    SimpleRabbitListenerContainerFactory containerFactory
    @Autowired
    SenderHTTP httpSender

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter()
    }

    static main(args) {
        SpringApplication.run Sender, args
    }

    @RabbitListener(queues = 'toDeliver')
    def receive(message) {
        def jsonMessage = this.messageConverter().fromMessage(message)
        this.httpSender.sendHTTPMessage(jsonMessage)
    }

}

@Log
@Service
@RefreshScope
class SenderHTTP {

    @HystrixCommand(fallbackMethod = 'sendHTTPFallback')
    def sendHTTPMessage(jsonMessage) {
        def url = jsonMessage.callback
        log.info("Sending callback to $url - message: [$jsonMessage]")
        def client = new RestTemplate()
        def response = client.postForLocation(url, jsonMessage, [])
        log.info("Notification sent. Response [${response.toString()}]")
    }

    def sendHTTPFallback(jsonMessage) {
        def start = jsonMessage.start_time
        def finish = (System.nanoTime() - start) / 1000000
        log.info("Impossible to send the response to register service! Time [${finish}] Message [${jsonMessage}]")
    }
}