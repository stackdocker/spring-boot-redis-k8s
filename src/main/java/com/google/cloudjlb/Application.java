package com.google.cloudjlb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.Arrays;



@SpringBootApplication
@EnableCaching
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {

      System.out.println("Let's inspect the beans provided by Spring Boot:");

      String[] beanNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beanNames);
      for (String beanName : beanNames) {
        System.out.println(beanName);
      }

    };
  }

  @Autowired
  private Environment env;

  @Bean(name="RedisConnectionFactory")
  public RedisConnectionFactory lettuceConnectionFactory() {
	if (0 != redisHostName.length() && 0 < redisPort) {
		return context.getBean(JedisConnectionFactory.class);
	}
	String redis = env.getProperty("spring.redis.host");
	String sentinelMaster = env.getProperty("spring.redis.sentinel.master");
	String sentinelNodes = env.getProperty("spring.redis.sentinel.nodes");
	
    RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master("master")
        .sentinel("redis-sentinel.default.svc.cluster.local", 26379);
    return new LettuceConnectionFactory(sentinelConfig);
  }

    @Value("${redis.hostname}")
    private String redisHostName;

    @Value("${redis.port}")
    private int redisPort;

    @Autowired
    private ApplicationContext context;

    @Bean(name="RedisConnectionFactory1")
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHostName);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
    redisTemplate.setConnectionFactory(cf);
    return redisTemplate;
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisTemplate) {
    RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);

    cacheManager.setDefaultExpiration(3000);
    return cacheManager;
  }
}