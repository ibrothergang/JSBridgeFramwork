package com.xiaoying.h5core.core;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.xiaoying.h5api.api.H5Bridge;
import com.xiaoying.h5api.api.H5Context;
import com.xiaoying.h5api.api.H5Data;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5PluginManager;
import com.xiaoying.h5api.api.H5Scenario;
import com.xiaoying.h5api.api.H5Service;
import com.xiaoying.h5api.api.H5Session;
import com.xiaoying.h5api.util.FileUtil;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5UrlHelper;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.bridge.H5BridgeImpl;
import com.xiaoying.h5core.config.H5PluginConfigManager;
import com.xiaoying.h5core.data.H5MemData;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.plugin.H5ActionSheetPlugin;
import com.xiaoying.h5core.plugin.H5AlertPlugin;
import com.xiaoying.h5core.plugin.H5InjectPlugin;
import com.xiaoying.h5core.plugin.H5LoadingPlugin;
import com.xiaoying.h5core.plugin.H5LongClickPlugin;
import com.xiaoying.h5core.plugin.H5NotifyPlugin;
import com.xiaoying.h5core.plugin.H5PagePlugin;
import com.xiaoying.h5core.plugin.H5ShakePlugin;
import com.xiaoying.h5core.ui.H5Activity;
import com.xiaoying.h5core.web.H5WebChromeClient;
import com.xiaoying.h5core.web.H5WebView;
import com.xiaoying.h5core.web.H5WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

public class H5PageImpl extends H5CoreTarget implements H5Page {

    public static final String TAG = "H5PageImpl";

    private Activity activity;
    private H5SessionImpl h5Session;
    private Bundle startParams;
    private H5WebView h5WebView;
    private H5BridgeImpl h5Bridge;
    private H5PageHandler h5PageHandler;
    private H5Context h5Context;

    private boolean exited;

    private H5WebChromeClient h5ChromeClient;
    private H5WebViewClient h5ViewClient;

    private JSONArray h5performance;

    public H5PageImpl(Activity activity, Bundle params) {
        H5Environment.setContext(activity);
        this.h5Context = new H5Context(activity);
        this.activity = activity;
        this.exited = false;

        String hostName = H5Utils.getClassName(activity);
        H5Log.d(TAG, "h5 page host in activity " + hostName);

        this.startParams = params;
        // if parameter not set, use activity default
        if (startParams == null) {
            startParams = activity.getIntent().getExtras();
        }

        // may user parameter not set
        if (startParams == null) {
            startParams = new Bundle();
        }

        // parse magic options and unify parameters
        parseMagicOptions(startParams);
        H5ParamParser parser = new H5ParamParser();
        startParams = parser.parse(startParams, true);

        this.h5Data = new H5MemData();

        String bizType = H5Utils.getString(startParams, H5Param.LONG_BIZ_TYPE, "");
        if (TextUtils.isEmpty(bizType)) {
            // if bizType not available, falling back to use publicId
            bizType = H5Utils.getString(params, H5Param.PUBLIC_ID, "");
            if (TextUtils.isEmpty(bizType)) {
                // if PUBLIC_ID not available, falling back to use appId
                bizType = H5Utils.getString(params, H5Container.APP_ID);
            }
        }
        Bundle webViewCfg = new Bundle();
        webViewCfg.putString(H5WebView.CfgConstants.KEY_BIZ_TYPE, bizType);
        h5WebView = new H5WebView(activity, webViewCfg);
        H5Log.d("h5_create_webview appId={" + "} params={" + "}");
        boolean allowAccessFromFileURL = whetherAllowAccessFromFileURL();
        H5Log.d(TAG, "alow webview access from file URL" + allowAccessFromFileURL);
        h5WebView.init(allowAccessFromFileURL);

        h5Bridge = new H5BridgeImpl(h5WebView);

        h5ChromeClient = new H5WebChromeClient(this);
        h5WebView.setWebChromeClient(h5ChromeClient);

        h5ViewClient = new H5WebViewClient(this);
        h5WebView.setWebViewClient(h5ViewClient);

        initPlugins();

        initSession();
        h5ViewClient.setWebProvider(h5Session);

        if (!(activity instanceof H5Activity)) {
            applyParams();
        }
    }

    private boolean whetherAllowAccessFromFileURL() {
        String urlStr = H5Utils.getString(startParams, H5Param.LONG_URL);
        Uri uri = H5UrlHelper.parseUrl(urlStr);
        if (uri == null) {
            return false;
        }

        String scheme = uri.getScheme();
        if (!"file".equals(scheme)) {
            return false;
        }

        String filePath = uri.getPath();
        String rootPath = H5Utils.getApplicaitonDir() + "/files/apps";
        boolean underRoot = FileUtil.childOf(filePath, rootPath);
        String installPath = H5Utils.getString(startParams, H5Container.INSTALL_PATH);
        boolean underStall = FileUtil.childOf(filePath, installPath);
        if (underStall && underRoot) {
            return true;
        }
        H5Log.d(TAG, "NOT ALLOWED to load file scheme " + urlStr);
        return false;
    }

    private void parseMagicOptions(Bundle params) {
        if (params == null) {
            H5Log.w(TAG, "invalid magic parameter!");
            return;
        }

        String urlStr = H5Utils.getString(params, H5Param.URL);
        if (TextUtils.isEmpty(urlStr)) {
            urlStr = H5Utils.getString(params, H5Param.LONG_URL);
        }

        if (TextUtils.isEmpty(urlStr)) {
            H5Log.e(TAG, "no url found in magic parameter");
            return;
        }

        String decodedOptions = null;
        Uri uri = H5UrlHelper.parseUrl(urlStr);
        String optionsStr = H5UrlHelper.getParam(uri, "__webview_options__", null);
        if (TextUtils.isEmpty(optionsStr)) {
            H5Log.w(TAG, "no magic options found");
            return;
        }

        H5Log.d(TAG, "found magic options " + optionsStr);

        try {
            decodedOptions = URLDecoder.decode(optionsStr, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
        }
        if (TextUtils.isEmpty(decodedOptions)) {
            H5Log.e(TAG, "faild to decode magic options");
            return;
        }

        H5ParamParser parser = new H5ParamParser();
        try {
            String[] pairs = decodedOptions.split("&");
            for (String pair : pairs) {
                String[] values = pair.split("=");
                if (values.length < 2) {
                    continue;
                }
                String key = URLDecoder.decode(values[0], "UTF-8");
                String value = URLDecoder.decode(values[1], "UTF-8");

                // clean old(long name & short name) parameters
                parser.remove(params, key);
                // put new parameter to bundle.
                params.putString(key, (String) value);
                H5Log.d(TAG, "decode magic option [key] " + key + " [value] "
                        + value);
            }
        } catch (Exception e) {
            H5Log.e(TAG, "failed to decode magic option.", e);
            return;
        }
    }

    @Override
    public void onRelease() {
        h5ViewClient.onRelease();
        h5ViewClient = null;
        h5ChromeClient.onRelease();
        h5ChromeClient = null;
        h5Bridge.onRelease();
        h5Bridge = null;
        startParams = null;
        activity = null;
        h5Session = null;
        h5WebView.onRelease();
        h5WebView = null;
        h5Context = null;
        super.onRelease();
    }

    @Override
    public H5Context getContext() {
        return this.h5Context;
    }

    @Override
    public H5Session getSession() {
        return this.h5Session;
    }

    @Override
    public String getUrl() {
        if (h5ViewClient != null) {
            return h5ViewClient.getPageUrl();
        } else {
            return "";
        }
    }

    @Override
    public Bundle getParams() {
        return this.startParams;
    }

    @Override
    public String getTitle() {
        return h5WebView == null ? "" : h5WebView.getTitle();
    }

    @Override
    public H5Bridge getBridge() {
        return h5Bridge;
    }

    public H5WebView getWebView() {
        return h5WebView;
    }

    public H5WebViewClient getViewClient() {
        return h5ViewClient;
    }

    @Override
    public boolean exitPage() {
        if (h5ViewClient != null) {
            h5ViewClient.reportH5Performance();
        }
        if (exited) {
            H5Log.e(TAG, "page already exited!");
            return false;
        }

        if (h5WebView != null) {
            // remove TTS api added in AccessibilityInjector so as to unbind the service connection
            // from TTS service to avoid ServiceConnectionLeaked
            h5WebView.getSettings().setJavaScriptEnabled(false);
        }

        this.exited = true;

        if (h5PageHandler != null && !h5PageHandler.shouldExit()) {
            H5Log.w(TAG, "page exit intercepted!");
            return false;
        }

        if (activity != null) {
            activity.finish();
        }

        return h5Session.removePage(this);
    }

    private void initPlugins() {
        final H5PluginManager pm = getPluginManager();
        pm.register(new H5AlertPlugin(this));
        pm.register(new H5LoadingPlugin(this));
        pm.register(new H5NotifyPlugin(this));
        pm.register(new H5ActionSheetPlugin(this));
        pm.register(new H5ShakePlugin());
        pm.register(new H5InjectPlugin(this));
        pm.register(new H5LongClickPlugin(this));
        pm.register(new H5PagePlugin(this));
        H5Plugin localH5Plugin = H5PluginConfigManager.getInstance().createPlugin("page", pm);
        if (localH5Plugin != null)
            pm.register(localH5Plugin);
    }

    private void initSession() {
        String sessionId = H5Utils.getString(startParams, H5Param.SESSION_ID);
        H5Service service = H5Container.getService();
        h5Session = (H5SessionImpl) service.getSession(sessionId);

        H5Scenario h5Scenario = h5Session.getScenario();
        String scenarioName = H5Utils.getString(startParams,
                H5Param.LONG_BIZ_SCENARIO);
        if (!TextUtils.isEmpty(scenarioName) && h5Scenario == null) {
            H5Log.d(TAG, "set session scenario " + scenarioName);
            h5Scenario = new H5ScenarioImpl(scenarioName);
            h5Session.setScenario(h5Scenario);
        }
    }

    public void applyParams() {
        h5Session.addPage(this);

        Set<String> keys = startParams.keySet();
        for (String key : keys) {
            String intentName = null;
            JSONObject param = new JSONObject();
            if (H5Param.LONG_URL.equals(key)) {
                String url = H5Utils.getString(startParams, key);
                if (!TextUtils.isEmpty(url)) {
                    intentName = H5Plugin.H5_PAGE_LOAD_URL;
                    try {
                        param.put(H5Param.LONG_URL, url);
                        String publicId = H5Utils.getString(startParams,
                                H5Param.PUBLIC_ID, "");
                        param.put(H5Param.PUBLIC_ID, publicId);
                    } catch (JSONException e) {
                        H5Log.e(TAG, "exception", e);
                    }
                }
            } else if (H5Param.LONG_SHOW_LOADING.equals(key)) {
                boolean value = H5Utils.getBoolean(startParams, key, false);
                if (value == true) {
                    intentName = H5Plugin.SHOW_LOADING;
                }
            } else if (H5Param.LONG_BACK_BEHAVIOR.equals(key)) {
                String behavior = H5Utils.getString(startParams, key);
                intentName = H5Plugin.H5_PAGE_BACK_BEHAVIOR;
                try {
                    param.put(H5Param.LONG_BACK_BEHAVIOR, behavior);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);

                }
            } else if (H5Param.LONG_CCB_PLUGIN.equals(key)) {
                boolean enable = H5Utils.getBoolean(startParams, key, false);
                if (enable) {
                    intentName = key;
                    try {
                        param.put(key, true);
                    } catch (JSONException e) {
                        H5Log.e(TAG, "exception", e);

                    }
                }
            } else if (H5Param.LONG_BACKGROUND_COLOR.equals(key)) {
                String colorStr = startParams.getString(H5Param.LONG_BACKGROUND_COLOR);
                if (colorStr != null) {
                    try {
                        long color = Long.parseLong(colorStr);
                        color = color ^ 0xFF000000;
                        try {
                            param.put(H5Param.LONG_BACKGROUND_COLOR, (int) color);
                        } catch (JSONException e) {
                            H5Log.e(TAG, "exception", e);

                        }
                        intentName = H5Plugin.H5_PAGE_BACKGROUND;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        continue;
                    }
                } else {
                    continue;
                }
            }

            if (!TextUtils.isEmpty(intentName)) {
                sendIntent(intentName, param);
            }
        }

        initTextSize();
    }

    private void initTextSize() {
        H5Scenario h5Scenario = h5Session.getScenario();
        if (h5Scenario == null) {
            return;
        }
        H5Data scenarioData = h5Scenario.getData();
        String sizeStr = scenarioData.get(H5Container.FONT_SIZE);
        if (TextUtils.isEmpty(sizeStr)) {
            return;
        }
        try {
            int size = Integer.parseInt(sizeStr);
            setTextSize(size);
        } catch (Exception e) {
            H5Log.e("failed to parse scenario font size.", e);
        }
    }

    @Override
    public void setHandler(H5PageHandler handler) {
        this.h5PageHandler = handler;
    }

    @Override
    public View getContentView() {
        return h5WebView;
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding, String historyUrl) {
        JSONObject param = new JSONObject();
        try {
            param.put("baseUrl", baseUrl);
            param.put("data", data);
            param.put("mimeType", mimeType);
            param.put("encoding", encoding);
            param.put("historyUrl", historyUrl);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);

        }
        sendIntent(H5Plugin.H5_PAGE_SHOULD_LOAD_DATA, param);
    }

    @Override
    public void loadUrl(String url) {
        JSONObject param = new JSONObject();
        try {
            param.put(H5Param.LONG_URL, url);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);

        }
        sendIntent(H5Plugin.H5_PAGE_LOAD_URL, param);
    }

    @Override
    public void setTextSize(int textSize) {
        h5WebView.setTextSize(textSize);
    }

    public JSONArray getH5performance() {
        return h5performance;
    }

    public void setH5performance(JSONArray h5performance) {
        this.h5performance = h5performance;
    }

}
