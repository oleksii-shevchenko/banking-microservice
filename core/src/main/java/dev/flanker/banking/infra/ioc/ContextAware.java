package dev.flanker.banking.infra.ioc;

public interface ContextAware {
    void set(ApplicationContext applicationContext);
}
