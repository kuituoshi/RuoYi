package com.ruoyi.common.core.domain;

import java.io.Serializable;

/**
 * 通用返回的结果类
 * @param <T>
 */
public class MyResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer state;
    private String message;
    private T data;

    /**
     * 只能内部构造
     * @param state 使用枚举定义状态
     * @param message 消息
     * @param data 范型数据
     */
    private MyResult(State state, String message, T data)
    {
        this.state = state.value();
        this.message = message;
        this.data = data;
    }

    /**
     * 空构造方法，谨慎使用
     *
     */
    public MyResult() {}

    /**
     * 内部状态枚举
     */
    private enum State
    {
        /** 成功 */
        SUCCESS(0),
        /** 错误 */
        ERROR(1),
        /** 警告 */
        WARN(2);
        private final int value;

        State(int value)
        {
            this.value = value;
        }

        public int value()
        {
            return this.value;
        }
    }

    /**
     * 判断是否是成功状态
     */
    public boolean isSuccess()
    {
        return State.SUCCESS.value() == state;
    }

    /**
     * 判断是否是失败状态
     */
    public boolean isError()
    {
        return State.ERROR.value() == state;
    }

    /**
     * 判断是否是警告状态
     */
    public boolean isWarn()
    {
        return State.WARN.value() == state;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 设置成功消息，设置同时设置状态为成功
     * @param message 消息内容
     */
    public void setSuccessMessage(String message){
        this.message = message;
        this.state = State.SUCCESS.value();
    }

    /**
     * 设置失败消息，设置同时设置状态为失败
     * @param message 消息内容
     */
    public void setErrorMessage(String message){
        this.message = message;
        this.state = State.ERROR.value();
    }

    /**
     * 设置警告消息，设置同时设置状态为警告
     * @param message 消息内容
     */
    public void setWarnMessage(String message){
        this.message = message;
        this.state = State.WARN.value();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    /**
     * 构造一个成功状态的 MyResult对象
     * @param message 消息
     * @param data 数据
     * @return MyResult对象
     */
    public static <T> MyResult<T> success(String message, T data)
    {
        return new MyResult<>(State.SUCCESS, message, data);
    }

    /**
     * 构造一个失败状态的 MyResult对象
     * @param message 消息
     * @param data 数据
     * @return MyResult对象
     */
    public static <T> MyResult<T> error(String message, T data)
    {
        return new MyResult<>(State.ERROR, message, data);
    }

    /**
     * 构造一个警告状态的 MyResult对象
     * @param message 消息
     * @param data 数据
     * @return MyResult对象
     */
    public static <T> MyResult<T> warn(String message, T data)
    {
        return new MyResult<>(State.WARN, message, data);
    }

    /**
     * 构造一个成功状态的不含数据的 MyResult对象
     * @param message 消息
     * @return MyResult对象
     */
    public static <T> MyResult<T> success(String message)
    {
        return success(message, null);
    }

    /**
     * 构造一个失败状态的不含数据的 MyResult对象
     * @param message 消息
     * @return MyResult对象
     */
    public static <T> MyResult<T> error(String message)
    {
        return error(message, null);
    }

    /**
     * 构造一个警告状态的不含数据的 MyResult对象
     * @param message 消息
     * @return MyResult对象
     */
    public static <T> MyResult<T> warn(String message)
    {
        return warn(message, null);
    }
}
