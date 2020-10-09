package dev.flanker.banking.domain;

import dev.flanker.banking.infra.annotations.Bean;
import dev.flanker.banking.infra.annotations.Import;
import dev.flanker.banking.infra.annotations.Value;
import dev.flanker.banking.infra.ioc.ApplicationContext;
import java.net.http.HttpClient;

@Import(configurations = ImportConfig.class)
public class Config {
    public static void main(String[] args) {
        ApplicationContext.create(Config.class).start();
    }

    @Value("${val}")
    private double val;

    @Bean()
    public HttpClient httpClient() {
        System.out.println(val);
        return HttpClient.newHttpClient();
    }
}
