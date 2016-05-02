package com.xiaoying.h5core.util;

import android.net.Uri;
import android.text.TextUtils;

import com.xiaoying.h5api.util.H5Log;

public class H5UrlHelper {
    public static final String TAG = "UrlHelper";

    public static Uri parseUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            H5Log.e(TAG, "parse url exception.", e);
        }
        return uri;
    }

    public static final String getHost(String url) {
        Uri uri = parseUrl(url);
        String host = null;
        if (uri != null) {
            host = uri.getHost();
        }
        return host;
    }

    public static final String getPath(String url) {
        Uri uri = parseUrl(url);
        String path = null;
        if (uri != null) {
            path = uri.getPath();
        }
        return path;
    }

    public static String getParam(Uri uri, String key, String defaultValue) {
        if (uri == null) {
            return defaultValue;
        }
        String value = null;
        try {
            value = uri.getQueryParameter(key);
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public static boolean isSpecial(Uri uri) {
        if (uri == null) {
            return false;
        }

        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        //TODO Increase the special domain of judgment
        return false;
    }
}
