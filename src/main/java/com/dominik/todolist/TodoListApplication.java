package com.dominik.todolist;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TodoListApplication  {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoListApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
        LOGGER.info("Todo List API application has started.");
    }
}