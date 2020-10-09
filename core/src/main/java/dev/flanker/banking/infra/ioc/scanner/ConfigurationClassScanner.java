package dev.flanker.banking.infra.ioc.scanner;

import java.util.Set;

public interface ConfigurationClassScanner {
    Set<Class<?>> scan();
}
