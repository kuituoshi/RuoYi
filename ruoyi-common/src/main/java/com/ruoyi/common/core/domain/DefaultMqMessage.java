package com.ruoyi.common.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * RocketMq 的默认消息体
 * @param <T>
 */
public class DefaultMqMessage<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 消息ID*/
    private String messageId;
    /** 消息产生时间*/
    private LocalDateTime dateTime;
    /** 消息描述 */
    private String describe;
    /** 消息数据 */
    private T content;

    {
        // 自动初始化时间
        dateTime = LocalDateTime.now();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
