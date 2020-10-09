package dev.flanker.banking.infra.ioc;

public interface ApplicationBuilder {
    ApplicationBuilder addPackageScan(String pkg);

    ApplicationBuilder addConfiguration(Class<?> cfg);

    ApplicationBuilder addPropertySource(String src);

    ApplicationBuilder addBeanPostProcessor(Class<? extends BeanPostProcessor> bpp);

    ApplicationBuilder addBeanProxyPostProcessor(Class<? extends BeanProxyPostProcessor> bpp);

    ApplicationBuilder setArgs(String[] args);

    ApplicationContext build();
}
