package dev.flanker.banking.infra.ioc.environment;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FilePropertiesLoader implements PropertiesLoader {
    private final Collection<String> propertyFiles;

    public FilePropertiesLoader(Collection<String> propertyFiles) {
        this.propertyFiles = propertyFiles;
    }

    @Override
    public ConcurrentMap<String, String> load() {
        var properties = new ConcurrentHashMap<String, String>();
        for (String propertyFile : propertyFiles) {
            try (var in = getClass().getClassLoader().getResourceAsStream(propertyFile)) {
                var holder = new Properties();
                holder.load(in);
                for (Map.Entry<Object, Object> propertyEntry : holder.entrySet()) {
                    properties.put((String) propertyEntry.getKey(), (String) propertyEntry.getValue());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }
}
