package net.nornick.groby;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class GrobyApp {
    public static void main(String[] args) {
        SpringApplication.run(GrobyApp.class, args);
        log.debug("Start");
    }
}

