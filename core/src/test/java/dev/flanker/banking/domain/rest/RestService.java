package dev.flanker.banking.domain.rest;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RestService {
    @PostConstruct
    default void validateResources() {
        System.out.println("Validating Resources...");
    }
}
