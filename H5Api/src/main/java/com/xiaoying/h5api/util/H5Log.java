package com.xiaoying.h5api.util;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class H5Log {
    public static final String TAG = "H5Log";
    public final static String CURRENT_DEVICE_SPEC = Build.MANUFACTURER + "-"
            + Build.MODEL + "-" + Build.CPU_ABI + "-api" + Build.VERSION.SDK_INT;

    private static LogListener logListener;

    public static void setListener(LogListener listener) {
        synchronized (LogListener.class) {
            logListener = listener;
        }
    }

    public static void d(String log) {
        d(TAG, log);
    }

    public static void d(String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        sendLog(tag, log);
        Log.d(tag, log);
    }

    public static void dWithDeviceInfo(String tag, String log) {
        final String appended = " on device: " + H5Log.CURRENT_DEVICE_SPEC;
        d(tag, (log == null ? appended : log + appended));
    }

    public static void w(String log) {
        w(TAG, log);
    }

    public static void w(String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        sendLog(tag, log);
        Log.w(tag, log);
    }

    public static void e(String log) {
        e(TAG, log, null);
    }

    public static void e(String tag, String log) {
        e(tag, log, null);
    }

    public static void eWithDeviceInfo(String tag, String log) {
        final String appended = " on device: " + H5Log.CURRENT_DEVICE_SPEC;
        e(tag, (log == null ? appended : log + appended), null);
    }

    public static void e(String log, Exception e) {
        e(TAG, log, e);
    }

    public static void e(String tag, String log, Exception e) {
        sendLog(tag, log);
        Log.e(tag, log, e);
    }

    private static void sendLog(String tag, String log) {
        if (!H5Utils.isDebugable()) {
            return;
        }

        synchronized (LogListener.class) {
            if (logListener != null) {
                logListener.onLog(tag, log);
            }
        }
    }

    public interface LogListener {
        public void onLog(String tag, String log);
    }
}
