package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.annotations.Bean;
import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.reflections.ReflectionUtils;

import static org.reflections.ReflectionUtils.getAllMethods;

public class ConfigurationObjectBeanDefinitionReader implements ObjectBeanDefinitionReader {
    private static final ConfigurationObjectBeanDefinitionReader INSTANCE = new ConfigurationObjectBeanDefinitionReader();

    private static final String EMPTY_STRING = "";

    public static ConfigurationObjectBeanDefinitionReader getInstance() {
        return INSTANCE;
    }

    @Override
    public List<BeanDefinition> read(Object source) {
        var configurationClass = source.getClass();
        var beanDefinitions = new ArrayList<BeanDefinition>();
        for (Method method : getAllMethods(configurationClass, method -> method.isAnnotationPresent(Bean.class))) {
            beanDefinitions.add(readBeanDefinition(source, configurationClass, method));
        }
        return beanDefinitions;
    }

    private BeanDefinition readBeanDefinition(Object configuration, Class<?> configurationClass, Method factoryMethod) {
        return BeanDefinition.builder()
                .setBeanId(extractBeanId(factoryMethod))
                .setBeanInterfaces(getInterfaces(factoryMethod))
                .setBeanClass(factoryMethod.getReturnType())
                .setEagerCreated(true)
                .setBeanConstructorParametersCount(factoryMethod.getParameterCount())
                .setBeanConstructorParametersTypes(factoryMethod.getParameterTypes())
                .setBeanConstructorParametersAnnotations(factoryMethod.getParameterAnnotations())
                .setBeanConstructor(args -> {
                    try {
                        factoryMethod.setAccessible(true);
                        return factoryMethod.invoke(configuration, args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .setPostConstructMethods(extractInitMethods(factoryMethod))
                .setPreDestroyMethods(extractClosingMethods(factoryMethod))
                .build();
    }

    private String extractBeanId(Method method) {
        var name = method.getAnnotation(Bean.class).name();
        return !EMPTY_STRING.equals(name) ? name : method.getName();
    }

    private Set<Class<?>> getInterfaces(Method factoryMethod) {
        var returnType = factoryMethod.getReturnType();
        if (returnType.isInterface()) {
            return Set.of(returnType);
        }
        return ReflectionUtils.getAllSuperTypes(returnType, Class::isInterface);
    }

    private List<String> extractInitMethods(Method factoryMethod) {
        return Arrays.asList(factoryMethod.getAnnotation(Bean.class).postConstruct());
    }

    private List<String> extractClosingMethods(Method factoryMethod) {
        return Arrays.asList(factoryMethod.getAnnotation(Bean.class).preDestroy());
    }
}
