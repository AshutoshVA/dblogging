package com.demo.logging;

//import org.apache.log4j.Logger;
//import org.apache.logging.log4j.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DbLoggingApplication {

    private static final Logger log = LoggerFactory.getLogger(DbLoggingApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(DbLoggingApplication.class, args);

        log.warn("Hello World");

    }

}
