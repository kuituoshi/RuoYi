package com.ruoyi.framework.interceptor;

import com.ruoyi.common.annotation.MFA;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.json.JSON;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.core.domain.MyResult;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * google验证拦截器
 *
 * @author ruoyi
 */
@Component
public abstract class MFAInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(MFA.class))
            {
                boolean reuse = method.getAnnotation(MFA.class).reuse();
                boolean optional = method.getAnnotation(MFA.class).optional();
                MyResult<?> myResult = this.verifyCode(reuse, optional);

                if (myResult.isError())
                {
                    AjaxResult ajaxResult = AjaxResult.error(myResult.getMessage());
                    ServletUtils.renderString(response, JSON.marshal(ajaxResult));
                    return false;
                }
            }
            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * 身份验证的具体验证方式
     *
     * @param reuse 验证码是否可以重用
     * @param optional 如果没绑定是否可以不验证
     * @return
     * @throws Exception
     */
    public abstract MyResult<?> verifyCode(boolean reuse, boolean optional) throws Exception;
}
