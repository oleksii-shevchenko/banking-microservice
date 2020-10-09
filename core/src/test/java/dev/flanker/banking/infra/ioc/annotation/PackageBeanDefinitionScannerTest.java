package dev.flanker.banking.infra.ioc.annotation;

import dev.flanker.banking.domain.FirstServiceImpl;
import dev.flanker.banking.domain.rest.RestService;
import dev.flanker.banking.domain.sql.Repository;
import dev.flanker.banking.infra.ioc.impl.CachingApplicationContext;
import dev.flanker.banking.infra.ioc.impl.DefaultBeanDefinitionContainer;
import dev.flanker.banking.infra.ioc.impl.DefaultContextAwareBeanFactory;
import dev.flanker.banking.infra.ioc.scanner.PackageBeanDefinitionScanner;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

class PackageBeanDefinitionScannerTest {
    private static final String PACKAGE = "dev.flanker.banking.domain";

    private final AnnotatedClassBeanDefinitionReader beanDefinitionReader = new AnnotatedClassBeanDefinitionReader();

    private final PackageBeanDefinitionScanner beanDefinitionScanner = new PackageBeanDefinitionScanner(
            PACKAGE, beanDefinitionReader);

    @Test
    public void okTest() {
        System.out.println(Arrays.asList(FirstServiceImpl.class));

        var containerBuilder = DefaultBeanDefinitionContainer.builder();

        var container = beanDefinitionScanner.scan(containerBuilder).build();

        System.out.println(container.getBeanDefinition(Repository.class));

        var context = new CachingApplicationContext(
                container,
                new DefaultContextAwareBeanFactory(Collections.emptySet(), Collections.emptyList()),
                null,
                new ConcurrentHashMap<>()
        );

        context.start();

        System.out.println(context.getBean(RestService.class));
    }

}