package com.coms309.Cynance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@Profile("!test")
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        System.out.println("WebSocketConfig: Registering ServerEndpointExporter");
        return new ServerEndpointExporter();
    }
}
