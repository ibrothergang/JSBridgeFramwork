package com.xiaoying.h5core.core;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.api.H5Param.ParamType;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5core.util.H5UrlHelper;
import com.xiaoying.h5api.util.H5Utils;

import java.util.LinkedList;
import java.util.List;

public class H5ParamParser {
    public static final String TAG = "H5ParamParser";

    private List<H5ParamImpl> paramList;

    public H5ParamParser() {
        paramList = new LinkedList<H5ParamImpl>();
        initList();
    }

    private final void initList() {
        paramList.add(new H5ParamImpl(H5Param.LONG_URL, H5Param.URL,
                ParamType.STRING, ""));
        paramList.add(new H5ParamImpl(H5Param.LONG_DEFAULT_TITLE,
                H5Param.DEFAULT_TITLE, ParamType.STRING, ""));
        paramList.add(new H5ParamImpl(H5Param.LONG_SHOW_TITLEBAR,
                H5Param.SHOW_TITLEBAR, ParamType.BOOLEAN, true));
        paramList.add(new H5ParamImpl(H5Param.LONG_SHOW_TOOLBAR,
                H5Param.SHOW_TOOLBAR, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_SHOW_LOADING,
                H5Param.SHOW_LOADING, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_CLOSE_BUTTON_TEXT,
                H5Param.CLOSE_BUTTON_TEXT, ParamType.STRING, ""));
        paramList.add(new H5ParamImpl(H5Param.LONG_SSO_LOGIN_ENABLE,
                H5Param.SSO_LOGIN_ENABLE, ParamType.BOOLEAN, true));
        paramList.add(new H5ParamImpl(H5Param.LONG_SAFEPAY_ENABLE,
                H5Param.SAFEPAY_ENABLE, ParamType.BOOLEAN, true));
        paramList.add(new H5ParamImpl(H5Param.LONG_SAFEPAY_CONTEXT,
                H5Param.SAFEPAY_CONTEXT, ParamType.STRING, ""));
        paramList.add(new H5ParamImpl(H5Param.LONG_READ_TITLE,
                H5Param.READ_TITLE, ParamType.BOOLEAN, true));
        paramList.add(new H5ParamImpl(H5Param.LONG_BIZ_SCENARIO,
                H5Param.BIZ_SCENARIO, ParamType.STRING, ""));
        paramList.add(new H5ParamImpl(H5Param.LONG_ANTI_PHISHING,
                H5Param.ANTI_PHISHING, ParamType.BOOLEAN, true));
        paramList.add(new H5ParamImpl(H5Param.LONG_BACK_BEHAVIOR,
                H5Param.BACK_BEHAVIOR, ParamType.STRING, "back"));
        paramList.add(new H5ParamImpl(H5Param.LONG_PULL_REFRESH,
                H5Param.PULL_REFRESH, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_CCB_PLUGIN,
                H5Param.CCB_PLUGIN, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_SHOW_PROGRESS,
                H5Param.SHOW_PROGRESS, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_SMART_TOOLBAR,
                H5Param.SMART_TOOLBAR, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_ENABLE_PROXY,
                H5Param.ENABLE_PROXY, ParamType.BOOLEAN, false));
        paramList.add(new H5ParamImpl(H5Param.LONG_CAN_PULL_DOWN,
                H5Param.CAN_PULL_DOWN, ParamType.BOOLEAN, true));
    }

    public Bundle parse(Bundle bundle, boolean fillDefault) {
        if (bundle == null) {
            return bundle;
        }

        for (H5ParamImpl param : paramList) {
            bundle = param.unify(bundle, fillDefault);
        }

        check(bundle);
        return bundle;
    }

    public void remove(Bundle bundle, String key) {
        if (bundle == null || TextUtils.isEmpty(key)) {
            return;
        }
        for (H5ParamImpl param : paramList) {
            String ln = param.getLongName();
            String sn = param.getShortName();
            if (key.equals(ln) || key.equals(sn)) {
                bundle.remove(ln);
                bundle.remove(sn);
                return;
            }
        }
    }

    private void check(Bundle bundle) {
        String url = H5Utils.getString(bundle, H5Param.LONG_URL);
        Uri uri = H5UrlHelper.parseUrl(url);
        if (uri == null || TextUtils.isEmpty(uri.getScheme())) {
            return;
        }

        if (TextUtils.equals("file", uri.getScheme())) {
            return;
        }

        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return;
        }

        boolean isSpecial = H5UrlHelper.isSpecial(uri);
        boolean pullDown = H5Utils.getBoolean(bundle, H5Param.LONG_CAN_PULL_DOWN, false);
        if (!pullDown && !isSpecial) {
            // force set can pull down to show domain
            H5Log.w(TAG, "force to set canPullDown to true");
            bundle.putBoolean(H5Param.LONG_CAN_PULL_DOWN, true);
        }

        boolean pullToRefresh = H5Utils.getBoolean(bundle, H5Param.LONG_PULL_REFRESH, false);
        if (pullToRefresh && !isSpecial) {

            H5Log.d(TAG, "force to set pullRefresh to false");
            bundle.putBoolean(H5Param.LONG_PULL_REFRESH, false);
        }
    }
}
