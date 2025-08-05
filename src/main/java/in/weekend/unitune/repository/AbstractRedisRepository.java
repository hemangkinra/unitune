package in.weekend.unitune.repository;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractRedisRepository<T> {

    protected final ReactiveRedisTemplate<String, T> redisTemplate;
    private final Class<T> clazz;
    private final String keyPrefix;

    protected AbstractRedisRepository(ReactiveRedisTemplate<String, T> redisTemplate,
                                      Class<T> clazz,
                                      String keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.clazz = clazz;
        this.keyPrefix = keyPrefix;
    }

    // ========================= Basic CRUD Operations =========================

    /**
     * Save entity without TTL
     */
    public Mono<T> save(String id, T entity) {
        return redisTemplate.opsForValue()
                .set(buildKey(id), entity)
                .map(success -> entity);
    }

    /**
     * Save entity with TTL
     */
    public Mono<T> save(String id, T entity, Duration ttl) {
        return redisTemplate.opsForValue()
                .set(buildKey(id), entity, ttl)
                .map(success -> entity);
    }

    /**
     * Save multiple entities without TTL
     */
    public Mono<Boolean> saveAll(Map<String, T> entities) {
        Map<String, T> keyValueMap = entities.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        entry -> buildKey(entry.getKey()),
                        Map.Entry::getValue
                ));

        return redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    /**
     * Find entity by ID
     */
    public Mono<T> findById(String id) {
        return redisTemplate.opsForValue()
                .get(buildKey(id));
    }

    /**
     * Find multiple entities by IDs
     */
    public Flux<T> findAllById(Collection<String> ids) {
        List<String> keys = ids.stream()
                .map(this::buildKey)
                .toList();

        return redisTemplate.opsForValue()
                .multiGet(keys)
                .flatMapMany(Flux::fromIterable)
                .filter(java.util.Objects::nonNull);
    }

    /**
     * Find all entities with the key prefix
     */
    public Flux<T> findAll() {
        return redisTemplate.keys(keyPrefix + "*")
                .flatMap(key -> redisTemplate.opsForValue().get(key))
                .filter(java.util.Objects::nonNull);
    }

    /**
     * Check if entity exists
     */
    public Mono<Boolean> existsById(String id) {
        return redisTemplate.hasKey(buildKey(id));
    }

    /**
     * Count all entities
     */
    public Mono<Long> count() {
        return redisTemplate.keys(keyPrefix + "*")
                .count();
    }

    /**
     * Delete entity by ID
     */
    public Mono<Boolean> deleteById(String id) {
        return redisTemplate.delete(buildKey(id))
                .map(count -> count > 0);
    }

    /**
     * Delete multiple entities by IDs
     */
    public Mono<Long> deleteAllById(Collection<String> ids) {
        String[] keys = ids.stream()
                .map(this::buildKey)
                .toArray(String[]::new);

        return redisTemplate.delete(keys);
    }

    /**
     * Delete all entities with the key prefix
     */
    public Mono<Long> deleteAll() {
        return redisTemplate.keys(keyPrefix + "*")
                .collectList()
                .flatMap(keys -> keys.isEmpty() ?
                        Mono.just(0L) :
                        redisTemplate.delete(keys.toArray(String[]::new)));
    }

    // ========================= TTL Operations =========================

    /**
     * Set TTL for existing entity
     */
    public Mono<Boolean> setTtl(String id, Duration ttl) {
        return redisTemplate.expire(buildKey(id), ttl);
    }

    /**
     * Get TTL for entity
     */
    public Mono<Duration> getTtl(String id) {
        return redisTemplate.getExpire(buildKey(id));
    }

    /**
     * Remove TTL (make entity persistent)
     */
    public Mono<Boolean> removeTtl(String id) {
        return redisTemplate.persist(buildKey(id));
    }

    // ========================= Hash Operations =========================

    /**
     * Save entity as hash
     */
    public Mono<Boolean> saveAsHash(String id, Map<String, Object> hash) {
        return redisTemplate.opsForHash()
                .putAll(buildKey(id), hash);
    }

    /**
     * Save entity as hash with TTL
     */
    public Mono<Boolean> saveAsHash(String id, Map<String, Object> hash, Duration ttl) {
        return redisTemplate.opsForHash()
                .putAll(buildKey(id), hash)
                .then(redisTemplate.expire(buildKey(id), ttl));
    }

    /**
     * Get hash field
     */
    public Mono<Object> getHashField(String id, String field) {
        return redisTemplate.opsForHash()
                .get(buildKey(id), field);
    }

    /**
     * Set hash field
     */
    public Mono<Boolean> setHashField(String id, String field, Object value) {
        return redisTemplate.opsForHash()
                .put(buildKey(id), field, value);
    }

    /**
     * Delete hash field
     */
    public Mono<Long> deleteHashField(String id, String... fields) {
        return redisTemplate.opsForHash()
                .remove(buildKey(id), (Object[]) fields);
    }

    // ========================= Set Operations =========================

    /**
     * Get all set members
     */
    public Flux<T> getSetMembers(String id) {
        return redisTemplate.opsForSet()
                .members(buildKey(id));
    }

    /**
     * Remove from set
     */
    public Mono<Long> removeFromSet(String id, T... values) {
        return redisTemplate.opsForSet()
                .remove(buildKey(id), (Object[]) values);
    }

    /**
     * Check if member exists in set
     */
    public Mono<Boolean> isSetMember(String id, T value) {
        return redisTemplate.opsForSet()
                .isMember(buildKey(id), value);
    }

    // ========================= List Operations =========================

    /**
     * Add to list (left push)
     */
    public Mono<Long> addToListLeft(String id, T... values) {
        return redisTemplate.opsForList()
                .leftPushAll(buildKey(id), values);
    }

    /**
     * Add to list (right push)
     */
    public Mono<Long> addToListRight(String id, T... values) {
        return redisTemplate.opsForList()
                .rightPushAll(buildKey(id), values);
    }

    /**
     * Get list range
     */
    public Flux<T> getListRange(String id, long start, long end) {
        return redisTemplate.opsForList()
                .range(buildKey(id), start, end);
    }

    /**
     * Pop from list (left)
     */
    public Mono<T> popFromListLeft(String id) {
        return redisTemplate.opsForList()
                .leftPop(buildKey(id));
    }

    /**
     * Pop from list (right)
     */
    public Mono<T> popFromListRight(String id) {
        return redisTemplate.opsForList()
                .rightPop(buildKey(id));
    }

    /**
     * Get list size
     */
    public Mono<Long> getListSize(String id) {
        return redisTemplate.opsForList()
                .size(buildKey(id));
    }

    // ========================= Sorted Set Operations =========================

    /**
     * Add to sorted set
     */
    public Mono<Boolean> addToSortedSet(String id, T value, double score) {
        return redisTemplate.opsForZSet()
                .add(buildKey(id), value, score);
    }

    /**
     * Get sorted set range by rank
     */
    public Flux<T> getSortedSetRange(String id, long start, long end) {
        return redisTemplate.opsForZSet()
                .range(buildKey(id), Range.from(Range.Bound.inclusive(start)).to(Range.Bound.inclusive(end)));
    }

    /**
     * Get sorted set range by score
     */
    public Flux<T> getSortedSetRangeByScore(String id, double min, double max) {
        return redisTemplate.opsForZSet()
                .rangeByScore(buildKey(id), Range.from(Range.Bound.inclusive(min)).to(Range.Bound.inclusive(max)));
    }

    /**
     * Remove from sorted set
     */
    @SafeVarargs
    public final Mono<Long> removeFromSortedSet(String id, T... values) {
        return redisTemplate.opsForZSet()
                .remove(buildKey(id), (Object[]) values);
    }

    /**
     * Get score from sorted set
     */
    public Mono<Double> getSortedSetScore(String id, T value) {
        return redisTemplate.opsForZSet()
                .score(buildKey(id), value);
    }

    // ========================= Utility Methods =========================

    /**
     * Build Redis key with prefix
     */
    protected String buildKey(String id) {
        return keyPrefix + ":" + id;
    }

    /**
     * Scan keys with pattern
     */
    public Flux<String> scanKeys(String pattern) {
        return redisTemplate.scan(ScanOptions.scanOptions()
                .match(keyPrefix + ":" + pattern)
                .build());
    }

    /**
     * Get keys matching pattern
     */
    public Flux<String> getKeys(String pattern) {
        return redisTemplate.keys(keyPrefix + ":" + pattern);
    }

    // ========================= Getters =========================

    protected Class<T> getEntityClass() {
        return clazz;
    }

    protected String getKeyPrefix() {
        return keyPrefix;
    }
}