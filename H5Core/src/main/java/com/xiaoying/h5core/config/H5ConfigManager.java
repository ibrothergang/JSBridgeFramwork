package com.xiaoying.h5core.config;

import com.xiaoying.h5api.util.H5Environment;

import android.os.Bundle;

public class H5ConfigManager {
    public static final String TAG = "H5ConfigManager";

    private static Bundle globleConfig;
    private static boolean LOCAL_PROXY_GLOBAL_ENABLED = true;

    public static boolean proxyGlobalEnabled() {
        return LOCAL_PROXY_GLOBAL_ENABLED;
    }

    public static Bundle getGlobleConfig() {
        synchronized (H5Environment.class) {
            if (globleConfig == null) {
                globleConfig = new Bundle();
            }
        }
        return globleConfig;
    }
}
