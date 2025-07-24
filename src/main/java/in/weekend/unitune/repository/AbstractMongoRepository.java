package in.weekend.unitune.repository;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractMongoRepository<T> {


    protected final ReactiveMongoTemplate mongoTemplate;
    private final Class<T> clazz;
    private final String collection;

    protected AbstractMongoRepository(ReactiveMongoTemplate mongoTemplate, Class<T> clazz, String collection) {
        this.mongoTemplate = mongoTemplate;
        this.clazz = clazz;
        this.collection = collection;
    }


    public Mono<T> save(T entity) {
        return mongoTemplate.save(entity, collection);
    }


    public Flux<T> findAll() {
        return mongoTemplate.findAll(clazz, collection);
    }

    public Flux<T> find(Query query) {
        return mongoTemplate.find(query, clazz, collection);
    }

}
