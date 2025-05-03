package com.andredevel.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQInitializer {
    
    private final RabbitAdmin rabbitAdmin;
    
    /**
     * Método que inicializa o RabbitAdmin, criando as exchanges e queues
     */
    @PostConstruct
    public void init() {
        // Cria as exchanges e queues
        rabbitAdmin.initialize();
    }
}
