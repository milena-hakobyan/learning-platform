package com.example.tasks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@Component
public class EnvAndBeansPrinter implements CommandLineRunner {

    private final Environment environment;
    private final ApplicationContext context;

    public EnvAndBeansPrinter(Environment environment, ApplicationContext context) {
        this.environment = environment;
        this.context = context;
    }

    @Override
    public void run(String... args) {
//        System.out.println("==== ENVIRONMENT PROPERTIES ====");
//        for (var propertySource : ((org.springframework.core.env.AbstractEnvironment) environment).getPropertySources()) {
//            if (propertySource.getSource() instanceof java.util.Map<?, ?> map) {
//                map.forEach((k, v) -> System.out.printf("%s = %s%n", k, v));
//            }
//        }
//
//        System.out.println("\n==== BEAN DEFINITIONS ====");
//        String[] beanNames = context.getBeanDefinitionNames();
//
//        for (String name : beanNames) {
//            System.out.println(name);
//        }
    }
}
