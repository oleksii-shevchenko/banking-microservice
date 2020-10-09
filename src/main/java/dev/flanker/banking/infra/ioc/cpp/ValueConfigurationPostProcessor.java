package dev.flanker.banking.infra.ioc.cpp;

import dev.flanker.banking.infra.annotations.Value;
import dev.flanker.banking.infra.ioc.ConfigurationPostProcessor;
import dev.flanker.banking.infra.ioc.Environment;
import java.lang.reflect.Field;

import static org.reflections.ReflectionUtils.getAllFields;

public class ValueConfigurationPostProcessor implements ConfigurationPostProcessor {
    @Override
    public void configure(Object configurationObject, Environment environment) {
        for (Field field : getAllFields(configurationObject.getClass(), field -> field.isAnnotationPresent(Value.class))) {
            var placeholderOrValue = field.getAnnotation(Value.class).value();
            var value = environment.resolveProperty(placeholderOrValue, field.getType());
            try {
                field.setAccessible(true);
                field.set(configurationObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
