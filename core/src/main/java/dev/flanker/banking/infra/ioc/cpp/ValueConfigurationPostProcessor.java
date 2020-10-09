package dev.flanker.banking.infra.ioc.cpp;

import dev.flanker.banking.infra.annotations.Value;
import dev.flanker.banking.infra.ioc.ConfigurationPostProcessor;
import dev.flanker.banking.infra.ioc.Environment;
import dev.flanker.banking.infra.ioc.environment.AutoPropertyResolver;
import dev.flanker.banking.infra.ioc.environment.PropertyResolver;
import java.lang.reflect.Field;

import static org.reflections.ReflectionUtils.getAllFields;

public class ValueConfigurationPostProcessor implements ConfigurationPostProcessor {
    private final PropertyResolver propertyResolver;

    public ValueConfigurationPostProcessor() {
        this(new AutoPropertyResolver());
    }

    public ValueConfigurationPostProcessor(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void configure(Object configurationObject, Environment environment) {
        for (Field field : getAllFields(configurationObject.getClass(), field -> field.isAnnotationPresent(Value.class))) {
            var placeholderOrValue = field.getAnnotation(Value.class).value();
            var value = propertyResolver.resolve(environment, placeholderOrValue, field.getType());
            try {
                field.setAccessible(true);
                field.set(configurationObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
