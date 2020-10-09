package dev.flanker.banking.infra.ioc.util;

import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public final class BeanUtils {
    public static void invokeEmpty(Object obj, Collection<Method> methods) {
        methods.forEach(method -> {
            try {
                method.setAccessible(true);
                method.invoke(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String getBeanIdOrNull(Annotation[] parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof Named) {
                return ((Named) parameterAnnotation).value();
            }
        }
        return null;
    }

    public static void invokePostConstruct(Object object, String methodName) {
        try {
            var method = object.getClass().getMethod(methodName);
            if (isValidPostConstructMethod(method)) {
                method.invoke(object);
            } else {
                throw new RuntimeException("Invalid post construct method");
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokePreDestroy(Object object, String methodName) {
        try {
            var method = object.getClass().getMethod(methodName);
            if (isValidPreDestroyMethod(method)) {
                method.invoke(object);
            } else {
                throw new RuntimeException("Invalid pre destroy method");
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidPreDestroyMethod(Method method) {
        return method.getReturnType().equals(void.class)
                && method.getParameterCount() == 0
                && !Modifier.isStatic(method.getModifiers());
    }

    public static boolean isValidPostConstructMethod(Method method) {
        return method.getReturnType().equals(void.class)
                && method.getParameterCount() == 0
                && !Modifier.isStatic(method.getModifiers());
    }
}
