package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.annotations.Import;
import java.util.HashSet;
import java.util.Set;

public class ImportAnnotationConfigurationClassScanner implements ConfigurationClassScanner {
    private final Class<?> rootConfiguration;

    public ImportAnnotationConfigurationClassScanner(Class<?> rootConfiguration) {
        this.rootConfiguration = rootConfiguration;
    }

    @Override
    public Set<Class<?>> scan() {
        return scan(rootConfiguration, new HashSet<>());
    }

    private Set<Class<?>> scan(Class<?> configuration, Set<Class<?>> configurationSet) {
        configurationSet.add(configuration);
        if (configuration.isAnnotationPresent(Import.class)) {
            var importAnnotation = configuration.getAnnotation(Import.class);
            for (Class<?> imported : importAnnotation.configurations()) {
                scan(imported, configurationSet);
            }
        }
        return configurationSet;
    }
}
