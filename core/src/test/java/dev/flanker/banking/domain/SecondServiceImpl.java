package dev.flanker.banking.domain;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class SecondServiceImpl extends SecondService {
    private final FirstService firstService;

    public SecondServiceImpl() {
        this(null);
    }

    @Inject
    public SecondServiceImpl(FirstService firstService) {
        this.firstService = firstService;
    }
}
