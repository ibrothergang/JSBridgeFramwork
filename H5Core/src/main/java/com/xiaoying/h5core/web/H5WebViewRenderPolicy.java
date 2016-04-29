package com.xiaoying.h5core.web;

import android.os.Build;

/**
 * some rules about whether need or not to enable hardware render on specified devices
 *
 * @author xide.wf
 */
public class H5WebViewRenderPolicy {
    public static boolean shouldDisableHardwareRenderInLayer() {
        // case 1: samsung GS4 on android 4.3 is know to cause crashes at libPowerStretch.so:0x2d4c
        // use GT-I95xx to match more GS4 series devices though GT-I9500 is the typical device
        final boolean isSamsungGs4 = Build.MODEL != null
                && Build.MODEL.contains("GT-I95")
                && Build.MANUFACTURER != null
                && Build.MANUFACTURER.equals("samsung");
        final boolean isJbMr2 = Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2;
        if (isSamsungGs4 && isJbMr2) {
            return true;
        }

        return false;
    }
}
