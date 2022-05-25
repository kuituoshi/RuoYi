package com.ruoyi.web.controller.tool;

import com.ruoyi.common.constant.MqTopicConstants;
import com.ruoyi.common.core.domain.DefaultMqMessage;
import com.ruoyi.common.core.rocketmq.RocketMqSender;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQTestController {
    @Autowired
    private RocketMqSender rocketMqSender;

    @GetMapping("/asyncSend/{key}")
    public String asyncSend(@PathVariable("key") String key)
    {
        DefaultMqMessage<Object> mqMessage = new DefaultMqMessage<>();
        mqMessage.setDescribe("发送消息到TOPIC: " + MqTopicConstants.SYSTEM_DEFAULT);
        rocketMqSender.asyncSend(
                MqTopicConstants.SYSTEM_DEFAULT+":payment",
                MessageBuilder.withPayload(mqMessage)
                        .setHeader("KEYS", key)
                        .build()
        );
        return "success async send";
    }

    @GetMapping("/syncSend/{key}")
    public String syncSend(@PathVariable("key") String key)
    {
        DefaultMqMessage<Object> mqMessage = new DefaultMqMessage<>();
        mqMessage.setDescribe("发送消息到TOPIC: " + MqTopicConstants.SYSTEM_DEFAULT);
        rocketMqSender.syncSendOrderly(
                MqTopicConstants.SYSTEM_DEFAULT+":payment",
                MessageBuilder.withPayload(mqMessage)
                        .setHeader("KEYS", key)
                        .build(),
                key
        );
        return "success send";
    }

    @GetMapping("/loop/{type}/{times}")
    public String loop(@PathVariable("times") Integer times,
                       @PathVariable("type") String type)
    {
        DefaultMqMessage<Object> mqMessage = new DefaultMqMessage<>();
        mqMessage.setDescribe("发送消息到TOPIC: " + MqTopicConstants.SYSTEM_DEFAULT);
        if ("sync".equals(type)){
            for (int i = 0; i < times; i++) {
                rocketMqSender.syncSendOrderly(
                        MqTopicConstants.SYSTEM_DEFAULT+":payment",
                        MessageBuilder.withPayload(mqMessage)
                                .setHeader("KEYS", type)
                                .build(),
                        UUID.randomUUID().toString()
                );
            }
        }else{
            for (int i = 0; i < times; i++) {
                rocketMqSender.asyncSend(
                        MqTopicConstants.SYSTEM_DEFAULT+":payment",
                        MessageBuilder.withPayload(mqMessage)
                                .setHeader("KEYS", type)
                                .build()
                );
            }
        }

        return "success send";
    }

}
