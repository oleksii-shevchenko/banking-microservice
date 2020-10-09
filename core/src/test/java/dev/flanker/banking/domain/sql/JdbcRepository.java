package dev.flanker.banking.domain.sql;

import jakarta.inject.Singleton;

@Singleton
public class JdbcRepository implements Repository {
    @Override
    public void close() throws Exception {
        Thread.sleep(10_000);
        System.out.println("Close");
    }
}
