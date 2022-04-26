package com.ruoyi.framework.config;

import com.ruoyi.framework.redis.RedisKeyExpirationListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * redis消息监听配置
 * 
 * @author ruoyi
 */
@Configuration
public class RedisMessageConfig
{
    @Value("${spring.redis.database}")
    private String database;

    @Resource
    private RedisKeyExpirationListener redisKeyExpirationListener;


    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 增加消息删除监听
        container.addMessageListener(redisKeyExpirationListener, new PatternTopic("__keyevent@"+database+"__:expired"));
        return container;
    }
}
