package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.infra.ioc.domain.BeanDefinition;
import dev.flanker.banking.infra.ioc.util.BeanUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Closeable;
import java.io.Flushable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotatedClassBeanDefinitionReader implements ClassBeanDefinitionReader {
    private static final AnnotatedClassBeanDefinitionReader INSTANCE = new AnnotatedClassBeanDefinitionReader();

    private static final int FIRST_CHAR_INDEX = 0;

    public static AnnotatedClassBeanDefinitionReader getInstance() {
        return INSTANCE;
    }

    @Override
    public BeanDefinition read(Class<?> source) {
        var builder = BeanDefinition.builder()
                .setBeanId(extractBeanId(source))
                .setBeanInterfaces(getInterfaces(source))
                .setBeanClass(source)
                .setEagerCreated(true);

        var constructor = extractInjectionConstructor(source);

        builder.setBeanConstructor(args -> {
                    try {
                        constructor.setAccessible(true);
                        return constructor.newInstance(args);
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .setBeanConstructorParametersCount(constructor.getParameterCount())
                .setBeanConstructorParametersTypes(constructor.getParameterTypes())
                .setBeanConstructorParametersAnnotations(constructor.getParameterAnnotations())
                .setPostConstructMethods(extractPostConstructMethods(source))
                .setPreDestroyMethods(extractClosingMethods(source));

        return builder.build();
    }

    private String extractBeanId(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(Named.class)) {
            return beanClass.getAnnotation(Named.class).value();
        }
        var className = beanClass.getSimpleName();
        return Character.toLowerCase(className.charAt(FIRST_CHAR_INDEX)) + className.substring(FIRST_CHAR_INDEX + 1);
    }

    private Set<Class<?>> getInterfaces(Class<?> beanClass) {
        var interfaces = new HashSet<Class<?>>();
        getInterfaces(beanClass, interfaces);
        return interfaces;
    }

    private void getInterfaces(Class<?> beanClass, Set<Class<?>> interfaces) {
        if (beanClass == null) {
            return;
        }
        var beanInterfaces = beanClass.getInterfaces();
        Collections.addAll(interfaces, beanInterfaces);
        getInterfaces(beanClass.getSuperclass(), interfaces);
        for (Class<?> beanInterface : beanInterfaces) {
            getInterfaces(beanInterface, interfaces);
        }
    }

    private Constructor<?> extractInjectionConstructor(Class<?> beanClass) {
        for (Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }
        var constructors = beanClass.getDeclaredConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        } else {
            throw new RuntimeException("Multiple constructors");
        }
    }

    private List<String> extractPostConstructMethods(Class<?> beanClass) {
        var initMethods = new ArrayDeque<Method>();
        for (Class<?> beanInterface : getInterfaces(beanClass)) {
            extractPostConstructMethods(beanInterface, initMethods);
        }
        extractPostConstructMethods(beanClass, initMethods);
        return initMethods.stream()
                .map(Method::getName)
                .collect(Collectors.toList());
    }

    private void extractPostConstructMethods(Class<?> beanClass, Deque<Method> initMethods) {
        if (beanClass == null) {
            return;
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                if (BeanUtils.isValidPostConstructMethod(method)) {
                    initMethods.addFirst(method);
                } else {
                    throw new RuntimeException("Invalid post construct method");
                }
            }
        }
        extractPostConstructMethods(beanClass.getSuperclass(), initMethods);
    }

    private List<String> extractClosingMethods(Class<?> beanClass) {
        var closingMethods = new ArrayDeque<Method>();
        extractClosingMethods(beanClass, closingMethods);

        var beanInterfaces = getInterfaces(beanClass);
        for (Class<?> beanInterface : beanInterfaces) {
            extractClosingMethods(beanInterface, closingMethods);
        }

        try {
            extractClosingMethods(
                    beanInterfaces, Closeable.class, Closeable.class.getMethod("close"), closingMethods);
            extractClosingMethods(
                    beanInterfaces, AutoCloseable.class, AutoCloseable.class.getMethod("close"), closingMethods);
            extractClosingMethods(
                    beanInterfaces, Flushable.class, Flushable.class.getMethod("flush"), closingMethods);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return closingMethods.stream()
                .map(Method::getName)
                .collect(Collectors.toList());
    }

    private void extractClosingMethods(Class<?> beanClass, Deque<Method> closingMethods) {
        if (beanClass == null) {
            return;
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                if (BeanUtils.isValidPreDestroyMethod(method)) {
                    closingMethods.addLast(method);
                } else {
                    throw new RuntimeException("Invalid pre destroy method");
                }
            }
        }
        extractClosingMethods(beanClass.getSuperclass(), closingMethods);
    }

    private void extractClosingMethods(Set<Class<?>> interfaces,
                                       Class<?> targetInterface,
                                       Method closeMethod,
                                       Deque<Method> closingMethods) {
        if (interfaces.contains(targetInterface)) {
            closingMethods.addLast(closeMethod);
        }
    }
}
