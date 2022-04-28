package com.ruoyi.common.constant;

/**
 * redis缓存Key常量
 *
 */
public class RedisKeyConstants {
    /**
     * 用户创建MFA时临时存储的密钥，用于验证输入的验证码
     */
    public static final String USER_TEMP_MFA_SECRET = "user_temp_mfa_secret";

    /**
     * 用户已使用过的MFA验证码
     */
    public static final String USER_MFA_USED_CODE = "user_mfa_used_code";
}
