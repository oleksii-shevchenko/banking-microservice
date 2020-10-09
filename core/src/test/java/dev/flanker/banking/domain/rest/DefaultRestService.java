package dev.flanker.banking.domain.rest;

import dev.flanker.banking.infra.annotations.Value;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import java.time.LocalDate;

@Singleton
public class DefaultRestService implements RestService {
    @Value("2020-09-09")
    private LocalDate date;

    @PreDestroy
    public void t() {
        System.out.println(date);
    }
}
