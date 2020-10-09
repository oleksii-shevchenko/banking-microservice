package dev.flanker.banking.infra.ioc.environment;

import java.util.concurrent.ConcurrentMap;

public interface PropertiesLoader {
    ConcurrentMap<String, String> load();
}
