package dev.flanker.banking.domain;

import dev.flanker.banking.infra.annotations.Bean;
import dev.flanker.banking.infra.ioc.ApplicationContext;
import java.net.http.HttpClient;

public class Config {
    public static void main(String[] args) {
        ApplicationContext.create(Config.class).start();
    }

    @Bean(name = "client")
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
