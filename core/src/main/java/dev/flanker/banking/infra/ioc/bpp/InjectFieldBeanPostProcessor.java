package dev.flanker.banking.infra.ioc.bpp;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import dev.flanker.banking.infra.ioc.util.BeanUtils;
import jakarta.inject.Inject;
import java.lang.reflect.Field;

import static org.reflections.ReflectionUtils.getFields;

public class InjectFieldBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void configure(Object bean, BeanDefinition beanDefinition, ApplicationContext applicationContext) {
        for (Field field : getFields(beanDefinition.beanClass(), method -> method.isAnnotationPresent(Inject.class))) {
            Object injectedField;
            var beanId = BeanUtils.getBeanIdOrNull(field.getAnnotations());
            if (beanId != null) {
                injectedField = applicationContext.getBean(beanId);
            } else {
                injectedField = applicationContext.getBean(field.getType());
            }

            try {
                field.setAccessible(true);
                field.set(bean, injectedField);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
