package com.xiaoying.h5api.util;

import com.xiaoying.h5api.api.H5Context;
import com.xiaoying.h5api.api.H5Param;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

public class H5Environment {

    public static final String TAG = "H5Environment";

    private static Context context;

    public static Context getContext() {
        if (context == null) {
            //TODO
        }
        return context;
    }

    public static void setContext(Context ctx) {
        if (context == null && ctx != null) {
            context = ctx.getApplicationContext();
        }
    }

    public static String getConfig(String configName) {
        //TODO
//        ConfigService configService = getMicroContext().findServiceByInterface(
//                ConfigService.class.getName());
//        if (configService != null) {
//            return configService.getConfig(configName);
//        } else {
        H5Log.e(TAG, "can't get config service");
        return null;
//        }
    }

    public static Resources getResources() {
//
        return context.getResources();
    }

    public static String getSessionId(H5Context h5Context, Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        String sessionId = H5Utils.getString(bundle, H5Param.SESSION_ID);
        if (!TextUtils.isEmpty(sessionId)) {
            return sessionId;
        }

//        if (h5Context instanceof WalletContext) {
//            MicroApplication app = ((WalletContext) h5Context)
//                    .getMicroApplication();
//            if (app != null && !TextUtils.isEmpty(app.getAppId())) {
//                String appId = app.getAppId();
//                bundle.putString(H5Container.APP_ID, appId);
//                sessionId = "h5session_" + appId;
//            }
//        }

        if (TextUtils.isEmpty(sessionId)) {
            sessionId = "h5session_default";
        }

        bundle.putString(H5Param.SESSION_ID, sessionId);
        return sessionId;
    }

//    public static MicroApplication getMicroApplication(H5Context h5Context) {
//        if (h5Context == null) {
//            return null;
//        }
//        Context context = h5Context.getContext();
//        MicroApplication app = null;
//        if (context instanceof BaseActivity) {
//            app = ((BaseActivity) context).getActivityApplication();
//        } else if (context instanceof BaseFragmentActivity) {
//            app = ((BaseFragmentActivity) context).getActivityApplication();
//        }
//
//        if (app == null && (h5Context instanceof WalletContext)) {
//            app = ((WalletContext) h5Context).getMicroApplication();
//        }
//
//        return app;
//    }

    public static void startActivity(H5Context h5Context, Intent intent) {
        if (intent == null) {
            H5Log.w(TAG, "invalid intent parameter");
            return;
        }

        try {
            Context context = null;
            if (h5Context != null && h5Context.getContext() != null) {
                context = h5Context.getContext();
            } else {
                context = getContext();
            }

            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            H5Log.e(TAG, "startActivity exception", e);
        }
    }

    public static void startActivityForResult(H5Context h5Context,
                                              Intent intent, int requestCode) {
        if (intent == null) {
            H5Log.w(TAG, "invalid intent parameter");
            return;
        }

        try {
            Context context = null;
            if (h5Context != null && h5Context.getContext() != null) {
                context = h5Context.getContext();
            } else {
                context = getContext();
            }

            if (!(context instanceof Activity)) {
                H5Log.w(TAG, "context must be instanceof Activity!");
                return;
            }
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            H5Log.e(TAG, "startActivityForResult exception", e);
        }
    }

    public static void startActivityForResult(Activity activity,
                                              Intent intent, int requestCode) {
        if (intent == null || activity == null) {
            H5Log.w(TAG, "invalid intent parameter");
            return;
        }

        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            H5Log.e(TAG, "startActivityForResult exception", e);
        }
    }
}
