package com.xiaoying.h5core.plugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoying.h5api.api.H5Context;
import com.xiaoying.h5api.api.H5CoreNode;
import com.xiaoying.h5api.api.H5Data;
import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5Session;
import com.xiaoying.h5core.core.H5ParamParser;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5core.ui.H5Activity;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5UrlHelper;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class H5SessionPlugin implements H5Plugin {

    public static final String TAG = "H5SessionPlugin";

    private H5Session h5Session;

    public H5SessionPlugin(H5Session session) {
        this.h5Session = session;
    }

    @Override
    public void onRelease() {
        h5Session = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(GET_SESSION_DATA);
        filter.addAction(SET_SESSION_DATA);
        filter.addAction(EXIT_SESSION);
        filter.addAction(POP_WINDOW);
        filter.addAction(POP_TO);
        filter.addAction(PUSH_WINDOW);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SET_SESSION_DATA.equals(action)) {
            try {
                setSessionData(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        } else if (GET_SESSION_DATA.equals(action)) {
            try {
                getSessionData(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        } else if (EXIT_SESSION.equals(action)) {
            exitSession(intent);
        } else if (POP_TO.equals(action)) {
            try {
                popTo(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        } else if (POP_WINDOW.equals(action)) {
            popWindow(intent);
        } else if (PUSH_WINDOW.equals(action)) {
            pushWindow(intent);
        }
        return true;
    }

    private void getSessionData(H5Intent intent) throws JSONException {
        H5Data sessionData = h5Session.getData();

        JSONObject param = intent.getParam();
        if (param == null || sessionData == null) {
            return;
        }
        JSONArray jaKeys = H5Utils.getJSONArray(param, "keys", null);
        if (jaKeys == null || jaKeys.length() == 0) {
            return;
        }

        JSONObject resultData = new JSONObject();
        JSONObject values = new JSONObject();
        for (int index = 0; index < jaKeys.length(); ++index) {
            String key = jaKeys.getString(index);
            String value = sessionData.get(key);
            values.put(key, value);
        }
        resultData.put("data", values);
        intent.sendBack(resultData);
    }

    private void setSessionData(H5Intent intent) throws JSONException {
        H5Data sessionData = h5Session.getData();

        JSONObject param = intent.getParam();
        if (param == null || sessionData == null) {
            return;
        }

        JSONObject joData = H5Utils.getJSONObject(param, "data", null);
        if (joData == null || joData.length() == 0) {
            return;
        }

        Iterator entrys = joData.keys();
        while (entrys.hasNext()) {
            String key = (String) entrys.next();
            String value = joData.getString(key);
            sessionData.set(key, value);
        }
    }

    private void exitSession(H5Intent intent) {
        h5Session.exitSession();
    }

    private void popWindow(H5Intent intent) {
        JSONObject param = intent.getParam();
        if (param != null) {
            JSONObject data = H5Utils.getJSONObject(param, "data", null);
            H5Data sessionData = h5Session.getData();
            String dataStr = data.toString();
            sessionData.set(H5Container.H5_SESSION_POP_PARAM, dataStr);
        }

        H5Page page = h5Session.getTopPage();
        if (page != null) {
            page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
        }
    }

    private void popTo(H5Intent intent) throws JSONException {
        boolean succeed = doPopTo(intent);
        if (!succeed) {
            JSONObject result = new JSONObject();
            // 10: invalid index or URL
            result.put("error", "10");
            intent.sendBack(result);
        }
    }

    private boolean doPopTo(H5Intent intent) {
        JSONObject param = intent.getParam();
        int index = Integer.MAX_VALUE;
        // index field from parameter
        if (param != null && !param.isNull("index")) {
            index = H5Utils.getInt(param, "index", index);
        }

        // index field with high priority
        if (index == Integer.MAX_VALUE) {
            String url = H5Utils.getString(param, "url", null);
            index = getUrlIndex(url, false);
        }

        if (index == Integer.MAX_VALUE) {
            String urlPattern = H5Utils.getString(param, "urlPattern", null);
            index = getUrlIndex(urlPattern, true);
        }

        // can't find the right page
        if (index == Integer.MAX_VALUE) {
            H5Log.e(TAG, "can't find page index");
            return false;
        }

        // reverse index to positive number
        // +0 +1 +2 +3 +4 +5
        // -5 -4 -3 -2 -1
        Stack<H5Page> sessionPages = h5Session.getPages();
        int listSize = sessionPages.size();
        if (index < 0) {
            index = listSize - 1 + index;
        }

        // can't pop to current page
        if (index < 0 || index >= listSize - 1) {
            H5Log.e(TAG, "invalid page index");
            return false;
        }

        JSONObject data = H5Utils.getJSONObject(param, "data", null);
        if (data != null && data.length() != 0) {
            H5Data sessionData = h5Session.getData();
            String dataStr = data.toString();
            sessionData.set(H5Container.H5_SESSION_POP_PARAM, dataStr);
        }

        int location = listSize - 1;
        for (; location > index; --location) {
            H5Page page = sessionPages.get(location);
            page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
        }
        return true;
    }

    private int getUrlIndex(String target, boolean isRegular) {
        int index = Integer.MAX_VALUE;
        Stack<H5Page> sessionPages = h5Session.getPages();
        if (TextUtils.isEmpty(target) || sessionPages == null
                || sessionPages.isEmpty()) {
            return index;
        }
        int size = sessionPages.size();
        for (int idx = (size - 1); idx >= 0; --idx) {
            H5Page page = sessionPages.get(idx);
            String pageUrl = page.getUrl();
            if (TextUtils.isEmpty(pageUrl)) {
                continue;
            }

            if (!isRegular) {
                if (target.equals(pageUrl)) {
                    index = idx;
                    break;
                }
            } else {
                Pattern pattern = Pattern.compile(target);
                Matcher matcher = pattern.matcher(pageUrl);
                if (matcher.find()) {
                    index = idx;
                    break;
                }
            }
        }
        return index;
    }

    private void pushWindow(H5Intent intent) {
        JSONObject callParam = intent.getParam();
        H5CoreNode target = intent.getTarget();
        if (!(target instanceof H5Page)) {
            H5Log.w(TAG, "invalid target!");
            return;
        }

        H5Page page = (H5Page) target;
        String currentUrl = page.getUrl();
        Bundle bundle = page.getParams();
        Bundle pushParams = new Bundle();
        pushParams.putAll(bundle);

        JSONObject param = H5Utils.getJSONObject(callParam, "param", null);
        if (param != null && param.length() != 0) {
            Bundle newParam = new Bundle();
            H5Utils.toBundle(newParam, param);
            H5ParamParser parser = new H5ParamParser();
            newParam = parser.parse(newParam, false);
            Set<String> keySet = newParam.keySet();
            // clean old(long name & short name) parameters
            for (String key : keySet) {
                parser.remove(pushParams, key);
            }
            pushParams.putAll(newParam);
        }

        String url = H5Utils.getString(callParam, H5Param.LONG_URL, null);
        if (TextUtils.isEmpty(url)) {
            H5Log.e("can't get url parameter!");
            return;
        }

        url = getAbsoluteUrl(currentUrl, url);

        H5Log.d(TAG, "pushWindow url " + url);
        pushParams.putString(H5Param.LONG_URL, url);

        H5Context h5Context = page.getContext();
        Intent pushIntent = new Intent(h5Context.getContext(), H5Activity.class);
        pushIntent.putExtras(pushParams);
        H5Environment.startActivity(h5Context, pushIntent);
    }

    private String getAbsoluteUrl(String currentUrl, String url) {
        Uri uri = H5UrlHelper.parseUrl(url);
        if (uri == null) {
            return url;
        }

        String scheme = uri.getScheme();
        if (!TextUtils.isEmpty(scheme)) {
            return url;
        }

        String absUrl = null;
        if (url.startsWith("/")) {
            String installHost = h5Session.getData().get(H5Container.INSTALL_HOST);
            absUrl = installHost + url;
        } else {
            if (TextUtils.isEmpty(currentUrl)) {
                return null;
            }
            int index = currentUrl.lastIndexOf("/");
            if (index == -1) {
                return null;
            }
            String prefix = currentUrl.substring(0, index);
            absUrl = prefix + "/" + url;
        }
        return absUrl;
    }
}
