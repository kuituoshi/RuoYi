package com.ruoyi.framework.interceptor.impl;
import com.ruoyi.common.constant.ConfigKeyConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.RedisKeyConstants;
import com.ruoyi.common.core.domain.MyResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.*;
import com.ruoyi.framework.interceptor.MFAInterceptor;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 谷歌验证的具体实现方法
 *
 * @author ruoyi
 */
@Component
public class GoogleMFAInterceptor extends MFAInterceptor
{
    public static final String mfaCodeName = "verificationCode";

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private RedisCache redisCache;

    @Override
    public MyResult<?> verifyCode(boolean reuse, boolean optional)
    {
        /**全局参数为关闭验证时，则跳过验证 */
        String authCodeSwitch = sysConfigService.selectConfigByKey(ConfigKeyConstants.GLOBAL_MFA_SWITCH);
        if (Constants.SYS_CONFIG_GLOBAL_SWITCH_CLOSE.equals(authCodeSwitch))
            return MyResult.success("验证通过");

        String mfaCode = ServletUtils.getParameter(mfaCodeName);
        SysUser sysUser = ShiroUtils.getSysUser();

        /**可选验证时如果没绑定验证码时则跳过验证 */
        if (optional && sysUser != null && StringUtils.isEmpty(sysUser.getMfa()))
        {
            return MyResult.success("验证通过");
        }

        if (StringUtils.isEmpty(mfaCode) || sysUser == null || StringUtils.isEmpty(sysUser.getMfa()))
        {
            // 判断是否传了验证码，是否存在该用户，该用户是否已经保存的身份宝
            return MyResult.error("验证码不存在或者未绑定");
        }

        if (!reuse){
            String cacheCode = redisCache.getCacheObject(RedisKeyConstants.USER_MFA_USED_CODE + sysUser.getUserId());
            if (mfaCode.equals(cacheCode))
                return MyResult.error("验证码不能重复使用");
        }

        boolean b = GoogleAuthenticator.authCode(mfaCode, sysUser.getMfa());
        if (!b)
            return MyResult.error("验证失败");

        // 验证成功了缓存，用作重复使用的判断
        redisCache.setCacheObject(RedisKeyConstants.USER_MFA_USED_CODE + sysUser.getUserId(), mfaCode, 30, TimeUnit.SECONDS);
        return MyResult.success("验证通过");
    }
}
