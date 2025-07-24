package in.weekend.unitune.configuration;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisTemplateFactory {

    private final LettuceConnectionFactory connectionFactory;
    private final Map<Class<?>, ReactiveRedisTemplate<String, ?>> cache = new ConcurrentHashMap<>();

    public RedisTemplateFactory(
            LettuceConnectionFactory connectionFactory
    ) {
        this.connectionFactory = connectionFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> ReactiveRedisTemplate<String, T> createTemplate(Class<T> clazz) {
        return (ReactiveRedisTemplate<String, T>) cache.computeIfAbsent(clazz, cls -> {

            Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(clazz);

            RedisSerializationContext<String, T> context = RedisSerializationContext
                    .<String, T>newSerializationContext(StringRedisSerializer.UTF_8)
                    .value(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                    .hashKey(StringRedisSerializer.UTF_8)
                    .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                    .build();

            return new ReactiveRedisTemplate<>(connectionFactory, context);
        });
    }
}
