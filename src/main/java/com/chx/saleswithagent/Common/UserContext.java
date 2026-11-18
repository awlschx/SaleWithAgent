package com.chx.saleswithagent.Common;

import com.chx.saleswithagent.Constant.ErrorCode;

/**
 * 当前请求的用户上下文。
 *
 * 由登录拦截器在 preHandle 中写入，在 afterCompletion 中清理。
 * 业务层通过 requireUserId() / getUserId() 获取当前用户。
 */
public final class UserContext {

    private UserContext() {
    }

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /** 拦截器解析 token 后写入当前请求的 userId */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /** 未登录时返回 null，调用方需要自行判空 */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /** 未登录时直接抛业务异常，业务代码优先使用这个 */
    public static long requireUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
            throw new BizException(ErrorCode.COMMON_ERROR, "未登录");
        }
        return userId;
    }

    public static boolean isLoggedIn() {
        return USER_ID.get() != null;
    }

    /** 请求结束必须调用，防止 Tomcat 复用线程时串用户 */
    public static void clear() {
        USER_ID.remove();
    }
}
