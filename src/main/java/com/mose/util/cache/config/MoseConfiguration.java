package com.mose.util.cache.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.mose.util.cache.common.DesUtil;
import com.mose.util.cache.common.MoseIDUtil;
import com.mose.util.cache.common.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;


/**
 * 工具类前置配置
 */
public class MoseConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(MoseConfiguration.class);



    /**
     * reids 相关配置
     * @param redisTemplate
     * @return
     */
    @ConditionalOnMissingBean(name = {"moseRedisTemplate"})
    @Bean(name ="moseRedisTemplate")
    public RedisTemplate redisTemplate(RedisTemplate redisTemplate){
        logger.debug("redisTemplate  redisSerializer  initialize ...");
        // 1. 创建jackson序列化方式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        // 2. 创建object mapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 允许访问对象的所有属性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 转换json过程中保存类信息
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置value的序列化规则和key的序列化规则
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // jackson2JsonRedisSerializer 就是JSON的序列号规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置hash类型key/value序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 工厂创建redisTemplate对象之后在进行配置
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

//    @ConditionalOnMissingBean
//    @Bean
//    public Retryer retryer(){
//        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
//                // retryIf 重试条件
//                .retryIfResult(Objects::isNull)
//                //设置异常重试源
//                .retryIfExceptionOfType(Exception.class)
//                .retryIfRuntimeException()
////                .retryIfResult(res -> res = false)
//                //设置等待间隔时间
//                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
//                //设置最大重试次数
//                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
//                .build();
//        return retryer;
//    }



    @Value("${spring.application.name:}")
    String prefix;

    @PostConstruct
    public void moseIDUtilInitialize(){
        logger.debug("moseIDUtil initialize...");
        MoseIDUtil.setPrefix(this.prefix);
    }


    @ConditionalOnBean(name = {"moseRedisTemplate"})
    @Bean
    public DesUtil MoseConfiguration(@Qualifier("moseRedisTemplate") RedisTemplate redisTemplate) {
        logger.debug("DesUtil initialize...");
        RedisUtil.setRedisTemplate(redisTemplate);
        DesUtil.refresh();
        return null;
    }

}
