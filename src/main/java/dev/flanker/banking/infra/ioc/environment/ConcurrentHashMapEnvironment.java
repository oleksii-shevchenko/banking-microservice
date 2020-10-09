package dev.flanker.banking.infra.ioc.environment;

import dev.flanker.banking.infra.ioc.Environment;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashMapEnvironment implements Environment {
    private final ConcurrentMap<String, String> properties;

    private final PropertyResolver propertyResolver;

    public ConcurrentHashMapEnvironment(ConcurrentMap<String, String> properties) {
        this(properties, new AutoPropertyResolver());
    }

    public ConcurrentHashMapEnvironment(ConcurrentMap<String, String> properties, PropertyResolver propertyResolver) {
        this.properties = new ConcurrentHashMap<>(properties);
        this.propertyResolver = propertyResolver;
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

    @Override
    public <T> T resolveProperty(String placeholderOrValue, Class<T> propertyType) {
        return propertyResolver.resolve(this, placeholderOrValue, propertyType);
    }
}
