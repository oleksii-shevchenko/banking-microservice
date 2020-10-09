package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.environment.ConcurrentHashMapEnvironment;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public interface Environment {
    boolean containsProperty(String key);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T resolveProperty(String placeholderOrValue, Class<T> propertyType);

    static Environment createEnvironment(ConcurrentMap<String, String> properties) {
        return new ConcurrentHashMapEnvironment(properties);
    }
}
