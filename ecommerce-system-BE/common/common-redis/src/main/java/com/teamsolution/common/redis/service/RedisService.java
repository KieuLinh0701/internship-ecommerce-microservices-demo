package com.teamsolution.common.redis.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate redisTemplate;

  // Stores a string value with a TTL
  public void set(String key, String value, Duration ttl) {
    redisTemplate.opsForValue().set(key, value, ttl);
  }

  // Returns the value for the given key, or empty if not found
  public Optional<String> get(String key) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(key));
  }

  // Atomically retrieves and deletes a key — prevents race conditions on token rotation
  public Optional<String> getAndDelete(String key) {
    String script =
        """
                local val = redis.call('GET', KEYS[1])
                if val then redis.call('DEL', KEYS[1]) end
                return val
                """;
    String result =
        redisTemplate.execute(new DefaultRedisScript<>(script, String.class), List.of(key));
    return Optional.ofNullable(result);
  }

  // Deletes the given key
  public void delete(String key) {
    redisTemplate.delete(key);
  }

  // Returns true if the key exists
  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  // Alias for hasKey
  public boolean exists(String key) {
    return hasKey(key);
  }

  // Sets or refreshes the TTL of an existing key
  public void expire(String key, Duration ttl) {
    redisTemplate.expire(key, ttl);
  }

  // Returns remaining TTL as Duration, or Duration.ZERO if expired or missing
  public Duration getExpire(String key) {
    Long seconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
    return seconds != null && seconds > 0 ? Duration.ofSeconds(seconds) : Duration.ZERO;
  }

  // Adds a value to a Redis Set and refreshes the TTL of the set key
  public void addToSet(String key, String value, Duration ttl) {
    redisTemplate.opsForSet().add(key, value);
    redisTemplate.expire(key, ttl);
  }

  // Removes a value from a Redis Set
  public void removeFromSet(String key, String value) {
    redisTemplate.opsForSet().remove(key, value);
  }

  // Returns all members of a Redis Set, or null if the key does not exist
  public Set<String> getSet(String key) {
    return redisTemplate.opsForSet().members(key);
  }
}
