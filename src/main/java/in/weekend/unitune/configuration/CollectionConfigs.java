package in.weekend.unitune.configuration;

import in.weekend.unitune.exceptions.DBException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "unitune.db")
public class CollectionConfigs {

    private Map<String, String> collections;

    public String getCollectionName(Class<?> clazz){
        String className = clazz.getSimpleName();
        if (collections.containsKey(className)) {
            return collections.get(className);
        } else {
            throw new DBException("Collection Not Found");
        }
    }
}
