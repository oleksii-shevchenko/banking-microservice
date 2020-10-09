package dev.flanker.banking.domain;

import dev.flanker.banking.infra.annotations.Bean;
import dev.flanker.banking.infra.ioc.BeanPostProcessor;
import java.net.http.HttpClient;
import java.util.function.Supplier;

public class ImportConfig {
    @Bean
    public HttpClient client() {
        return HttpClient.newHttpClient();
    }

    public static BeanPostProcessor bpp() {
        return (o, d, a) -> System.out.println("bpp " + o);
    }
}
