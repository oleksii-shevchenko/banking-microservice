package dev.flanker.banking.infra.ioc.bpp;

import dev.flanker.banking.infra.ioc.ApplicationContext;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import dev.flanker.banking.infra.ioc.util.BeanUtils;
import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.reflections.ReflectionUtils.getMethods;

public class InjectMethodBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void configure(Object bean, BeanDefinition beanDefinition, ApplicationContext applicationContext) {
        for (Method injectMethod : getMethods(beanDefinition.beanClass(), method -> method.isAnnotationPresent(Inject.class))) {
            var parametersCount = injectMethod.getParameterCount();
            var parameters = new Object[parametersCount];

            var parametersAnnotations = injectMethod.getParameterAnnotations();
            var parametersTypes = injectMethod.getParameterTypes();

            for (int i = 0; i < parametersCount; i++) {
                var beanId = BeanUtils.getBeanIdOrNull(parametersAnnotations[i]);
                if (beanId != null) {
                    parameters[i] = applicationContext.getBean(beanId);
                } else {
                    parameters[i] = applicationContext.getBean(parametersTypes[i]);
                }
            }

            try {
                injectMethod.setAccessible(true);
                injectMethod.invoke(bean, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
