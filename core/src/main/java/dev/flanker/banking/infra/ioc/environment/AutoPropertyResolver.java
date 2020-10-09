package dev.flanker.banking.infra.ioc.environment;

import dev.flanker.banking.infra.ioc.Environment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.regex.Pattern;

import static org.reflections.ReflectionUtils.getMethods;

public class AutoPropertyResolver implements PropertyResolver {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("^\\$\\{(?<key>[\\w.]+)(:(?<val>[^[:$}]]+))?}$");

    private static final Map<Class<?>, Class<?>> WRAPPERS = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            double.class, Double.class,
            float.class, Float.class,
            int.class, Integer.class,
            long.class, Long.class,
            short.class, Short.class
    );

    @Override
    public String resolve(Environment environment, String placeholderOrValue) {
        var matcher = PLACEHOLDER_PATTERN.matcher(placeholderOrValue);
        if (matcher.find()) {
            var key = matcher.group("key");
            var defaultValue = matcher.group("val");
            return environment.getProperty(key, defaultValue);
        } else {
            return placeholderOrValue;
        }
    }

    @Override
    public <T> T resolve(Environment environment, String placeholderOrValue, Class<T> propertyType) {
        if (propertyType.equals(String.class)) {
            return (T) resolve(environment, placeholderOrValue);
        }
        var value = resolve(environment, placeholderOrValue);
        var factoryMethod = resolveFactoryMethod(propertyType);
        try {
            return (T) factoryMethod.invoke(null, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Method resolveFactoryMethod(Class<?> propertyType) {
        var factoryClass = propertyType.isPrimitive() ? WRAPPERS.get(propertyType) : propertyType;

        var parseMethod = getMethods(factoryClass, method -> method.getName().startsWith("parse") &&
                method.getReturnType().equals(propertyType) &&
                method.getParameterCount() == 1 &&
                CharSequence.class.isAssignableFrom(method.getParameterTypes()[0]) &&
                Modifier.isStatic(method.getModifiers()) &&
                Modifier.isPublic(method.getModifiers())
        );

        if (parseMethod.size() == 1) {
            return parseMethod.iterator().next();
        }

        var valueOfMethod = getMethods(factoryClass, method -> method.getName().equals("valueOf") &&
                method.getReturnType().equals(propertyType) &&
                method.getParameterCount() == 1 &&
                String.class.isAssignableFrom(method.getParameterTypes()[0]) &&
                Modifier.isStatic(method.getModifiers()) &&
                Modifier.isPublic(method.getModifiers())
        );

        if (valueOfMethod.size() == 1) {
            return valueOfMethod.iterator().next();
        }

        throw new RuntimeException("Factory method not found!");
    }

}
