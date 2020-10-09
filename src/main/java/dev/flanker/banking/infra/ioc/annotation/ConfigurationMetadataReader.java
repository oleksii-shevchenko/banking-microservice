package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.domain.ConfigurationMetadata;

public interface ConfigurationMetadataReader {
    ConfigurationMetadata read(Class<?> configuration);

    static ConfigurationMetadataReader getInstance() {
        return ConfigurationClassMetadataReader.getInstance();
    }
}
