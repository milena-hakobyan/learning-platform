package com.example.config;

import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;


/**
 * Custom LoadBalancer configuration for USER-SERVICE.
 *
 * By default, Spring Cloud LoadBalancer uses RoundRobinLoadBalancer.
 * Here we override it with RandomLoadBalancer, so that every call
 * to USER-SERVICE (through Feign)
 * will be routed to a randomly selected instance registered in Eureka.
 */
@Configuration
public class LoadBalancerConfig {

    @Bean
    ReactorServiceInstanceLoadBalancer randomLoadBalancer(Environment environment,
                                                          LoadBalancerClientFactory clientFactory) {
        String serviceId = "USER-SERVICE";
        return new RandomLoadBalancer(clientFactory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class), serviceId);
    }

}
