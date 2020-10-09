package dev.flanker.banking.infra.ioc.util;

import java.lang.reflect.Method;
import java.util.Collection;

public final class ReflectionUtils {
    public static void invokeEmpty(Object obj, Collection<Method> methods) {
        methods.forEach(method -> {
            try {
                method.invoke(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
