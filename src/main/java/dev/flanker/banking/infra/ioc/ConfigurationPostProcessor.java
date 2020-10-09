package dev.flanker.banking.infra.ioc;

public interface ConfigurationPostProcessor {
    void configure(Object configurationObject, Environment environment);
}
