package dev.flanker.banking.infra.ioc.environment;

import dev.flanker.banking.infra.ioc.Environment;

public interface PropertyResolver {
    String resolve(Environment environment, String placeholderOrValue);

    <T> T resolve(Environment environment, String placeholderOrValue, Class<T> propertyType);
}
