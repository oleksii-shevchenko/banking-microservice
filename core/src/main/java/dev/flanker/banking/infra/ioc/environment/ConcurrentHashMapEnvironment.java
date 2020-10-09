package dev.flanker.banking.infra.ioc.environment;

import dev.flanker.banking.infra.ioc.Environment;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashMapEnvironment implements Environment {
    private final ConcurrentMap<String, String> properties;

    public ConcurrentHashMapEnvironment(ConcurrentMap<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
