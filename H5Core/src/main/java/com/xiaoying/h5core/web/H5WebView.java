package com.xiaoying.h5core.web;

import com.xiaoying.h5api.util.FileUtil;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5api.util.NetworkUtil;
import com.xiaoying.h5api.util.NetworkUtil.NetworkType;
import com.xiaoying.h5core.apwebview.APWebBackForwardList;
import com.xiaoying.h5core.apwebview.APWebChromeClient;
import com.xiaoying.h5core.apwebview.APWebHistoryItem;
import com.xiaoying.h5core.apwebview.APWebSettings;
import com.xiaoying.h5core.apwebview.APWebView;
import com.xiaoying.h5core.apwebview.APWebViewClient;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.refresh.H5PullableView;
import com.xiaoying.h5core.refresh.OverScrollListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;

import java.util.Map;

@TargetApi(VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class H5WebView extends APWebView implements H5PullableView {

    public static String TAG = "H5WebView";
    private static int WEBVIEW_INDEX = 0;
    private OverScrollListener overScrollListener;
    private int webViewIndex = 0;

    ;
    private boolean released = false;

    public H5WebView(Context context) {
        super(context);
        webViewIndex = WEBVIEW_INDEX++;
        TAG += "(type:" + webView.getVersion() + " version:" + ")";
    }

    public H5WebView(Context context, final Bundle webViewCfg) {
        super(context, webViewCfg);
        webViewIndex = WEBVIEW_INDEX++;
    }

    public H5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        webViewIndex = WEBVIEW_INDEX++;
    }

    public H5WebView(Context context, AttributeSet attrs, final Bundle webViewCfg) {
        super(context, attrs, webViewCfg);
        webViewIndex = WEBVIEW_INDEX++;
    }

    public H5WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        webViewIndex = WEBVIEW_INDEX++;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public H5WebView(Context context, AttributeSet attrs, int defStyle,
                     boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
    }

    public int getWebViewIndex() {
        return webViewIndex;
    }

    public void init(boolean allowAccessFromFileURL) {
        applyCustomSettings(allowAccessFromFileURL);
        if (webView == null) {
            H5Log.e(TAG, "FATAL ERROR, the internal glue webView is null!");
        }
    }

    @TargetApi(VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    private void applyCustomSettings(boolean allowAccessFromFileURL) {
        H5Log.d(TAG, "applyCustomSettings allowAccessFromFileURL " + allowAccessFromFileURL);

        APWebSettings settings = getSettings();

        // set default text encoding
        settings.setDefaultTextEncodingName("utf-8");

        settings.setSupportMultipleWindows(false);

        // JavaScript settings
        try {
            settings.setJavaScriptEnabled(true);
        } catch (NullPointerException e) {
            H5Log.d(TAG, "Ignore the exception in AccessibilityInjector when init");
            e.printStackTrace();
        }
        settings.setDefaultFontSize(16);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        // avoid webview to save user password
        settings.setSavePassword(false);

        // enable plugin state
        settings.setPluginState(APWebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(allowAccessFromFileURL);

        String h5Folder = H5Utils.getApplicaitonDir() + "/app_h5container";
        FileUtil.mkdirs(h5Folder);

        // database
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            settings.setDatabasePath(h5Folder + "/databases");
        }

        // cache related
        settings.setAppCachePath(h5Folder + "/appcache");
        settings.setAppCacheEnabled(true);

        // fix google issue 4641
        // see: https://code.google.com/p/android/issues/detail?id=4641
        settings.getUserAgentString();

        // web cache
        NetworkUtil nu = new NetworkUtil(getContext());
        if (nu.getNetworkType() == NetworkType.NONE) {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        // zoom controls
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);

        // enable overview
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(false);
        }

        settings.setAllowFileAccessFromFileURLs(allowAccessFromFileURL);
        settings.setAllowUniversalAccessFromFileURLs(allowAccessFromFileURL);

        // enable debug code
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT
                && H5Utils.isDebugable()) {
            setWebContentsDebuggingEnabled(true);
        }

        // for security protect
        if (Build.VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1
                && Build.VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }

        // user agent
        try {
            String ua = settings.getUserAgentString();
            String packageName = getContext().getPackageName();
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ua = ua + "  AliApp(AP/" + packageInfo.versionName
                    + ") Client/" + packageInfo.versionName;

            settings.setUserAgentString(ua);
        } catch (NameNotFoundException e) {
            H5Log.e("setUserAgent exception", e);
        }
    }

    @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setTextSize(int size) {
        APWebSettings settings = getSettings();
        if (Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            settings.setTextZoom(size);
        } else {
            APWebSettings.TextSize textSize = getTextSize(size);
            settings.setTextSize(textSize);
        }
    }

    public APWebSettings.TextSize getTextSize(int textZoom) {
        if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_LARGEST) {
            return APWebSettings.TextSize.LARGEST;
        } else if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_LARGER) {
            return APWebSettings.TextSize.LARGER;
        } else if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_NORMAL) {
            return APWebSettings.TextSize.NORMAL;
        } else if (textZoom >= H5Container.WEBVIEW_FONT_SIZE_SMALLER) {
            return APWebSettings.TextSize.SMALLER;
        }
        return APWebSettings.TextSize.NORMAL;
    }

    @TargetApi(VERSION_CODES.KITKAT)
    @Override
    public void loadUrl(String url) {
        Log.d(TAG, "loadUrl " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        final String finalUrl = url;

        // load javascript in UI thread to ensure it works
        // see: http://developer.android.com/guide/webapps/migrating.html#Threads
        H5Utils.runOnMain(
                new Runnable() {
                    @Override
                    public void run() {
                        loadUrl_l(finalUrl);
                    }
                }
        );
    }

    private void loadUrl_l(String url) {
        boolean meetApiLevel19 = Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
        try {
            if (url.startsWith("javascript") && meetApiLevel19) {
                this.evaluateJavascript(url, null);
            } else {
                super.loadUrl(url);
            }
        } catch (Exception e) {
            // load URL execute javascript exception.
            H5Log.e(TAG, "loadUrl exception", e);
            // fall back to the legacy way
            super.loadUrl(url);
        }
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                int scrollY, int scrollRangeX, int scrollRangeY,
                                int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (overScrollListener != null) {
            overScrollListener.onOverScrolled(deltaX, deltaY, scrollX, scrollY);
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }

    @Override
    public void setOverScrollListener(OverScrollListener listener) {
        this.overScrollListener = listener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public void onPause() {
        H5Log.d(TAG, "onPause " + webViewIndex + " do nothing");
    }

    @Override
    public void onResume() {
        H5Log.d(TAG, "onResume " + webViewIndex);
        super.onResume();
    }

    @Override
    public void setWebViewClient(APWebViewClient client) {
        if (client != null) {
            String clientName = H5Utils.getClassName(client);
            H5Log.d(TAG, "setWebViewClient " + clientName);
        }
        super.setWebViewClient(client);
    }

    @Override
    public void setWebChromeClient(APWebChromeClient client) {
        if (client != null) {
            String clientName = H5Utils.getClassName(client);
            H5Log.d(TAG, "setWebChromeClient " + clientName);
        }
        super.setWebChromeClient(client);
    }

    @Override
    public void removeJavascriptInterface(String name) {
        H5Log.d(TAG, "removeJavascriptInterface " + name);
        if (Build.VERSION.SDK_INT >= 11) {
            super.removeJavascriptInterface(name);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        H5Log.d(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        H5Log.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        onRelease();
    }

    public void printBackForwardList() {
        APWebBackForwardList currentList = this.copyBackForwardList();
        int currentSize = currentList.getSize();
        for (int i = 0; i < currentSize; ++i) {
            APWebHistoryItem item = currentList.getItemAtIndex(i);
            String url = item.getUrl();
            H5Log.d(TAG, "The URL at index: " + Integer.toString(i) + " is " + url);
        }
    }

    public void onRelease() {
        if (released) {
            return;
        }
        released = true;
        H5Log.d(TAG, "exit webview!");

        // Load blank page so that JavaScript onunload is called
        this.loadUrl("about:blank");
        this.reload();

        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                destroyWebView();
            }
        }, 1000);
    }

    private void destroyWebView() {
        try {
            // remove from parent to stop drawing it
            ViewParent parent = this.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this);
            }

            this.setVisibility(View.GONE);
            this.clearFocus();
            this.clearAnimation();
            this.setDownloadListener(null);
            this.setWebViewClient(null);
            this.setWebChromeClient(null);
            this.stopLoading();
            this.removeAllViews();
            this.removeAllViewsInLayout();
            this.clearHistory();
            this.clearSslPreferences();
            this.destroyDrawingCache();
            this.freeMemory();

            // MUST removed from parent, or native crash
            this.destroy();
        } catch (Exception e) {
            // on some devices, destroy webview may crash
            H5Log.e(TAG, "destroy webview exception.", e);
        }
    }

    public enum FlingDirection {
        FLING_LEFT, FLING_UP, FLING_RIGHT, FLING_DOWN,
    }

    public static final class CfgConstants {
        public static final String KEY_BIZ_TYPE = "bizType";
        /**
         * whether need to verify the security of the url
         * default is true
         */
        public static final String KEY_NEED_VERIFY_URL = "needVerifyUrl";
    }
}
