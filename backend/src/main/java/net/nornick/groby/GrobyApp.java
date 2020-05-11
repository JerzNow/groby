package net.nornick.groby;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class GrobyApp {
    public static void main(String[] args) {
        SpringApplication.run(GrobyApp.class, args);
    }
}

