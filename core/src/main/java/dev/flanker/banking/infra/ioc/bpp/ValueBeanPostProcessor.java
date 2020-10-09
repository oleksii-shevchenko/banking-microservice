package dev.flanker.banking.infra.ioc.bpp;

import dev.flanker.banking.infra.annotations.Value;
import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.cpp.ValueConfigurationPostProcessor;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import dev.flanker.banking.infra.ioc.environment.AutoPropertyResolver;
import dev.flanker.banking.infra.ioc.environment.PropertyResolver;
import java.lang.reflect.Field;

import static org.reflections.ReflectionUtils.getAllFields;

public class ValueBeanPostProcessor implements BeanPostProcessor {
    private final PropertyResolver propertyResolver;

    public ValueBeanPostProcessor() {
        this(new AutoPropertyResolver());
    }

    public ValueBeanPostProcessor(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void configure(Object bean, BeanDefinition beanDefinition, ApplicationContext applicationContext) {
        var environment = applicationContext.getEnvironment();
        for (Field field : getAllFields(beanDefinition.beanClass(), field -> field.isAnnotationPresent(Value.class))) {
            var placeholderOrValue = field.getAnnotation(Value.class).value();
            var value = propertyResolver.resolve(environment, placeholderOrValue, field.getType());
            try {
                field.setAccessible(true);
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
