package com.xiaoying.h5core.api;

/**
 * the SSO(single sign on) flag holder for H5
 *
 * @author xide.wf
 */
public class H5SsoFlagHolder {
    public static final int DOMAIN_OTHER = -1;
    public static final int DOMAIN_TAOBAO = 0;
    public static final int DOMAIN_LAIWANG = 1;
    public static final int DOMAIN_WEIBO = 2;
    public static final int DOMAIN_NUM = 3;

    public static Boolean[] SsoFlagsArray = new Boolean[]{
            true,
            true,
            true
    };

    public static boolean needSso(final int domainType) {
        if (0 <= domainType && domainType < DOMAIN_NUM) {
            return SsoFlagsArray[domainType].booleanValue();
        } else {
            return false;
        }
    }

    public static void setNeedAutoLogin(final boolean need) {
        for (int i = 0; i < SsoFlagsArray.length; i++) {
            SsoFlagsArray[i] = need;
        }
    }
}
