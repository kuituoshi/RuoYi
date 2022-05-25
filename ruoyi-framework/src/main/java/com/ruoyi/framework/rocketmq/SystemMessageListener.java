package com.ruoyi.framework.rocketmq;

import com.ruoyi.common.constant.MqTopicConstants;
import com.ruoyi.common.core.domain.DefaultMqMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 系统消息监听
 */
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}",
        topic = MqTopicConstants.SYSTEM_DEFAULT,
        maxReconsumeTimes = 3)
public class SystemMessageListener implements RocketMQListener<DefaultMqMessage<?>> {
    private static final Logger log = LoggerFactory.getLogger(SystemMessageListener.class);

    @Override
    public void onMessage(DefaultMqMessage<?> mqMessage) {
        log.info("默认组接受到消息: {}", mqMessage.getDescribe());
    }
}

