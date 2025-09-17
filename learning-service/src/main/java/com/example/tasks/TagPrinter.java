package com.example.tasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TagPrinter {

    @Value("#{'$tags'.split('-')}")
    private String[] tags;

    public void printTags(){
        Arrays.stream(tags).forEach(System.out::println);
    }
}