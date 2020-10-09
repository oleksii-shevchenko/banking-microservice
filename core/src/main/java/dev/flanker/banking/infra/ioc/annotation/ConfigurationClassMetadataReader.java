package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.annotations.PropertySource;
import dev.flanker.banking.infra.annotations.Scan;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.BeanProxyPostProcessor;
import dev.flanker.banking.infra.ioc.domain.ConfigurationMetadata;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import static org.reflections.ReflectionUtils.getAllMethods;

public class ConfigurationClassMetadataReader implements ConfigurationMetadataReader {
    private static final ConfigurationClassMetadataReader INSTANCE = new ConfigurationClassMetadataReader();

    public static ConfigurationClassMetadataReader getInstance() {
        return INSTANCE;
    }

    @Override
    public ConfigurationMetadata read(Class<?> configuration) {
        return new ConfigurationMetadata(
                readPropertySources(configuration),
                readPackages(configuration),
                readBeanPostProcessors(configuration),
                readBeanProxyPostProcessors(configuration)
        );
    }

    private Set<String> readPropertySources(Class<?> configuration) {
        if (configuration.isAnnotationPresent(PropertySource.class)) {
            var propertySource = configuration.getAnnotation(PropertySource.class);
            return Set.of(propertySource.sources());
        }
        return Collections.emptySet();
    }

    private Set<String> readPackages(Class<?> configuration) {
        if (configuration.isAnnotationPresent(Scan.class)) {
            var scans = configuration.getAnnotation(Scan.class);
            return Set.of(scans.packages());
        }
        return Collections.emptySet();
    }

    private Set<Method> readBeanPostProcessors(Class<?> configuration) {
        return getAllMethods(configuration, method -> BeanPostProcessor.class.isAssignableFrom(method.getReturnType())
                && Modifier.isStatic(method.getModifiers()));
    }

    private Set<Method> readBeanProxyPostProcessors(Class<?> configuration) {
        return getAllMethods(configuration, method -> BeanProxyPostProcessor.class.isAssignableFrom(method.getReturnType())
                && Modifier.isStatic(method.getModifiers()));
    }
}
