package dev.flanker.banking.infra.ioc;

import dev.flanker.banking.infra.ioc.annotation.ClassBeanDefinitionReader;
import dev.flanker.banking.infra.ioc.annotation.ObjectBeanDefinitionReader;

public interface ApplicationContextBuilder {
    ApplicationContextBuilder addPackageScan(String pkg);

    ApplicationContextBuilder addConfiguration(Class<?> cfg);

    ApplicationContextBuilder setAutoConfigurationLoad(boolean flg);

    ApplicationContextBuilder addPropertySource(String src);

    ApplicationContextBuilder addBeanPostProcessor(Class<? extends BeanPostProcessor> bpp);

    ApplicationContextBuilder addBeanProxyPostProcessor(Class<? extends BeanProxyPostProcessor> bpp);

    ApplicationContextBuilder setArgs(String[] args);

    ApplicationContextBuilder setClassBeanDefinitionReader(Class<? extends ClassBeanDefinitionReader> cbdr);

    ApplicationContextBuilder setObjectBeanDefinitionReader(Class<? extends ObjectBeanDefinitionReader> obdr);

    ApplicationContext build() throws Exception;
}
