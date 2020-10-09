package dev.flanker.banking.infra.ioc.environment;

import dev.flanker.banking.infra.ioc.Environment;

public interface PropertyPlaceholderResolver {
    String resolve(Environment environment, String placeholderOrValue);

    Object resolve(Environment environment, String placeholderOrValue, Class<?> propertyType);
}
