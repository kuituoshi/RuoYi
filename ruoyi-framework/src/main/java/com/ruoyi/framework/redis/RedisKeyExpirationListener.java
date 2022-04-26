package com.ruoyi.framework.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener
{
    public static final Logger log = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    /**
     * 针对 redis 数据失效事件，进行数据处理
     *
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        String prefix = "";
        if(expiredKey.contains("_")){
            prefix = expiredKey.substring(0,expiredKey.indexOf("_"));
        }

        //log.info("收到消息key: {}，前缀为: {}", expiredKey, prefix);
    }
}
