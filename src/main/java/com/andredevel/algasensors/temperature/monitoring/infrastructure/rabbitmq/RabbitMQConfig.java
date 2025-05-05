package com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_PROCESS_TEMPERATURE = "temperature-monitoring.process-temperature.v1.q";
    public static final String DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE = "temperature-monitoring.process-temperature.v1.dlq";
    public static final String QUEUE_ALERTING = "temperature-monitoring.alerting.v1.q";
    
    // Se passar o ObjectMapper como parâmetro, o Spring irá injetar o bean já configurado (Rest api)
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // Necessário para criação exchange e queues
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }   
    
    // Configuração da queue
    @Bean
    public Queue queueProcessTemperature() {
        // mapeia a dlq
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE);
        
        return QueueBuilder.durable(QUEUE_PROCESS_TEMPERATURE).withArguments(args).build();
    }

    // Configuração da dead letter queue
    @Bean
    public Queue deadLetterQueueProcessTemperature() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE).build();
    }

    // Configuração da queue
    @Bean
    public Queue queueAlerting() {
        return QueueBuilder.durable(QUEUE_ALERTING).build();
    }
    
    // Configuração da exchange
    public FanoutExchange exchange() {
        return ExchangeBuilder.fanoutExchange("temperature-processing.temperature-received.v1.e").build();
    }
    
    @Bean
    public Binding bindingProcessTemperature() {
        return BindingBuilder.bind(queueProcessTemperature()).to(exchange());
    }

    @Bean
    public Binding bindingAlerting() {
        return BindingBuilder.bind(queueAlerting()).to(exchange());
    }
    
    
}
