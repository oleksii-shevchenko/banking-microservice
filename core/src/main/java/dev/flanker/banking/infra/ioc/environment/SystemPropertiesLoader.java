package dev.flanker.banking.infra.ioc.environment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SystemPropertiesLoader implements PropertiesLoader {
    @Override
    public ConcurrentMap<String, String> load() {
        return new ConcurrentHashMap<>(System.getenv());
    }
}
