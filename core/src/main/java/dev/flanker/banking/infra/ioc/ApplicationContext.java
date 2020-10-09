package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.impl.DefaultApplicationBuilder;
import java.util.Collection;

public interface ApplicationContext extends AutoCloseable {
    void start();

    Object getBean(String beanId);

    <T> T getBean(String beanId, Class<T> beanClass);

    <T> T getBean(Class<T> beanClass);

    <T> Collection<? extends T> getBeans(Class<T> beanClass);

    Environment getEnvironment();

    @Override
    void close();

    static ApplicationContext create(Class<?> rootConfiguration) {
        return new DefaultApplicationBuilder()
                .addConfiguration(rootConfiguration)
                .addPackageScan(rootConfiguration.getPackageName())
                .build();
    }

    static ApplicationBuilder builder() {
        return new DefaultApplicationBuilder();
    }
}
