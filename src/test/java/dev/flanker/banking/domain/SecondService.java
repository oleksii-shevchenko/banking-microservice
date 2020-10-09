package dev.flanker.banking.domain;

import jakarta.annotation.PostConstruct;

public abstract class SecondService {

    @PostConstruct
    public void init() {
        System.out.println("Setup " + SecondService.class.getName());
    }
}
