package dev.flanker.banking.infra.ioc.annotation;

import java.util.Set;

public interface ConfigurationClassScanner {
    Set<Class<?>> scan();
}
