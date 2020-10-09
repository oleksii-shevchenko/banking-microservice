package dev.flanker.banking.domain;

import dev.flanker.banking.domain.rest.RestService;
import dev.flanker.banking.domain.sql.Repository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named("firstService")
@Singleton
public class FirstServiceImpl implements FirstService {
    private final RestService restService;

    private final Repository repository;

    public FirstServiceImpl(RestService restService, Repository repository) {
        this.restService = restService;
        this.repository = repository;
    }

    @PostConstruct
    public void setup() {
        System.out.println("Setup " + FirstServiceImpl.class.getName());
    }

    @Override
    public String doWork(String req) {
        return null;
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutdown " + FirstServiceImpl.class.getName());
    }
}
