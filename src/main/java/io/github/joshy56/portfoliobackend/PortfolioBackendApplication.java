package io.github.joshy56.portfoliobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PortfolioBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioBackendApplication.class, args);
    }

    @RestController
    public final class AnyContronller {

        @GetMapping("/world")
        public String helloWorld() {
            return "Hello world!\n";
        }
    }

}
