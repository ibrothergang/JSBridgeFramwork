package com.xiaoying.h5core.web;

import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5WebProvider;
import com.xiaoying.h5api.util.FileUtil;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5UrlHelper;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.apwebview.APHttpAuthHandler;
import com.xiaoying.h5core.apwebview.APSslErrorHandler;
import com.xiaoying.h5core.apwebview.APWebBackForwardList;
import com.xiaoying.h5core.apwebview.APWebViewClient;
import com.xiaoying.h5core.apwebview.APWebViewCtrl;
import com.xiaoying.h5core.core.H5PageImpl;
import com.xiaoying.h5core.env.H5Container;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceResponse;
import android.webkit.WebViewClient;

import java.io.InputStream;

public class H5WebViewClient implements APWebViewClient {

    public static final String TAG = "H5WebViewClient";

    private H5PageImpl h5Page;
    private String checkingUrl;
    private String loadingUrl;
    private String pageUrl;
    private H5WebProvider provider;
    private long totalBytes;
    private int uid;
    private long startTime;
    private int lastPageIndex;
    private boolean pageUpdated;
    private long delta;
    private boolean hasPerformanceToReport;

    public H5WebViewClient(H5PageImpl page) {
        this.h5Page = page;
        this.uid = H5Utils.getUid(H5Environment.getContext());
        this.pageUpdated = false;
        this.lastPageIndex = -1;
        this.pageUrl = H5Utils.getString(page.getParams(), H5Param.LONG_URL);
        hasPerformanceToReport = false;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setCheckingUrl(String url) {
        checkingUrl = url;
    }

    public void setWebProvider(H5WebProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldOverrideUrlLoading(APWebViewCtrl view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading " + url);
        if (h5Page == null || TextUtils.isEmpty(url)) {
            return true;
        }

        checkingUrl = null;
        // reset checkingUrl
        JSONObject param = new JSONObject();
        try {
            param.put(H5Param.LONG_URL, url);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        if (view != null) {
            try {
                final H5WebView h5WebView = (H5WebView) view;
                final Bundle config = h5WebView.getWebViewCfg();
                if (config != null && !config.getBoolean(H5WebView.CfgConstants.KEY_NEED_VERIFY_URL, true)) {
                    try {
                        param.put(H5WebView.CfgConstants.KEY_NEED_VERIFY_URL, false);
                    } catch (JSONException e) {
                        H5Log.e(TAG, "exception", e);
                    }
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        h5Page.sendIntent(H5Plugin.H5_PAGE_SHOULD_LOAD_URL, param);

        // checkingUrl should be set by H5_PAGE_SHOULD_LOAD_URL
        if (url.equals(checkingUrl)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onReceivedError(APWebViewCtrl webView, int errorCode,
                                String description, String failingUrl) {
        String info = "onReceivedError errorCode " + errorCode
                + " description " + description + " failingUrl " + failingUrl;
        H5Log.dWithDeviceInfo(TAG, info);

        // If this is a "Protocol Not Supported" error, then revert to the previous
        // page. If there was no previous page, then punt. The application's config
        // is likely incorrect (start page set to sms: or something like that)
        if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }

        if (h5Page != null) {
            JSONObject param = new JSONObject();
            try {
                param.put("type", "genericError");
                param.put("errorCode", errorCode);
                param.put(H5Param.LONG_URL, failingUrl);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Plugin.H5_PAGE_ERROR, param);
        }
    }

    @Override
    public void onFormResubmission(APWebViewCtrl apWebViewCtrl, Message message, Message message2) {

    }

    @Override
    public void onReceivedSslError(APWebViewCtrl view, APSslErrorHandler handler,
                                   SslError error) {
        int errorCode = error.getPrimaryError();
        H5Log.dWithDeviceInfo(TAG, "onReceivedSslError " + errorCode);

        if (h5Page != null) {
            JSONObject param = new JSONObject();
            try {
                param.put("type", "sslError");
                param.put("errorCode", errorCode);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Plugin.H5_PAGE_ERROR, param);
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(APWebViewCtrl apWebViewCtrl, APHttpAuthHandler apHttpAuthHandler, String s,
                                          String s2) {

    }

    @Override
    public boolean shouldOverrideKeyEvent(APWebViewCtrl apWebViewCtrl, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onUnhandledKeyEvent(APWebViewCtrl apWebViewCtrl, KeyEvent keyEvent) {

    }

    @Override
    public void onScaleChanged(APWebViewCtrl apWebViewCtrl, float v, float v2) {

    }

    @Override
    public void onReceivedLoginRequest(APWebViewCtrl apWebViewCtrl, String s, String s2, String s3) {

    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WebResourceResponse shouldInterceptRequest(APWebViewCtrl view, String url) {
        Log.d(TAG, "shouldInterceptRequest " + url);
        if (!TextUtils.isEmpty(url) && url.startsWith("file://")) {
            if (h5Page != null) {
                Bundle params = h5Page.getParams();
                String installPath = H5Utils.getString(params, H5Container.INSTALL_PATH);
                String filePath = H5UrlHelper.getPath(url);
                if (!FileUtil.childOf(filePath, installPath)) {
                    return new WebResourceResponse(null, null, null);
                }
            }
        } else if (provider != null) {
            InputStream is = provider.getWebResource(url);
            if (is != null) {
                String filePath = H5UrlHelper.getPath(url);
                String fileName = FileUtil.getFileName(filePath);
                String mimeType = FileUtil.getMimeType(fileName);
                WebResourceResponse wrr = new WebResourceResponse(mimeType, "UTF-8", is);
                return wrr;
            }
        }
        return null;
    }

    @Override
    public void onTooManyRedirects(APWebViewCtrl apWebViewCtrl, Message message, Message message2) {

    }

    @Override
    public void onLoadResource(APWebViewCtrl view, String url) {
        Log.d(TAG, "onLoadResource " + url);
        if (h5Page != null && h5Page.getUrl() != null && h5Page.getUrl().startsWith("file://")
                && !url.startsWith("file://")) {
            H5Log.d(TAG, "trigger taobao auto login when onLoadResource");
            JSONObject param = new JSONObject();
            try {
                param.put("url", url);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Container.H5_PAGE_LOAD_RESOURCE, param);
        }
    }

    @Override
    public void onPageStarted(APWebViewCtrl view, String url, Bitmap favicon) {
        Log.d(TAG, "onPageStarted " + url);
        if (hasPerformanceToReport) {
            reportH5Performance();
        }
        hasPerformanceToReport = true;
        pageUpdated = false;
        if (h5Page != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                // some special payment url processed here
                JSONObject param = new JSONObject();
                try {
                    param.put(H5Param.LONG_URL, url);
                    param.put("webview", view);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
//                h5Page.sendIntent(WalletPayPlugin.SPECIAL_CASH_PAY, param);
            }

            loadingUrl = url;
            H5Log.d("onPageStarted url={" + url + "} ");
            int webViewIndex = 0;
            if (view instanceof H5WebView) {
                webViewIndex = ((H5WebView) view).getWebViewIndex();
            }

            totalBytes = getTotalRxBytes();
            JSONObject param1 = new JSONObject();
            try {
                param1.put(H5Param.LONG_URL, url);
                param1.put("webViewIndex", webViewIndex);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Plugin.H5_PAGE_STARTED, param1);
            H5Log.d("h5_page_start url={" + url + "}");
            startTime = System.currentTimeMillis();
        }

    }

    @Override
    public void doUpdateVisitedHistory(APWebViewCtrl view, String url,
                                       boolean isReload) {
        Log.d(TAG, "doUpdateVisitedHistory " + url + " isReload " + isReload);
        pageUpdated = true;
        if (h5Page != null) {
            pageUrl = url;
            JSONObject param = new JSONObject();
            try {
                param.put(H5Param.LONG_URL, url);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Plugin.H5_PAGE_UPDATED, param);
        }
    }

    @Override
    public void onPageFinished(APWebViewCtrl view, String url) {
        Log.d(TAG, "onPageFinished " + url);
        if (h5Page != null && view != null) {
            long pageSize = getTotalRxBytes() - totalBytes;
            JSONObject param = new JSONObject();
            try {
                param.put(H5Param.LONG_URL, url);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            String title = view.getTitle();

            if (url != null && title != null) {
                boolean titlePartOfUrl = url.contains(title)
                        || (title.contains(".html") || title.contains(".htm"));
                try {
                    if (!titlePartOfUrl) {
                        // if title is sub-part of url is not
                        // empty, then fallback to use it
                        param.put(H5Container.KEY_TITLE, title);
                    } else {
                        // otherwise pass null as title to indicate that the title should be ignored
                        param.put(H5Container.KEY_TITLE, null);
                    }
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
            }
            try {
                param.put("pageSize", pageSize);
                APWebBackForwardList list = view.copyBackForwardList();
                if (list != null) {
                    int historySize = list.getSize();
                    int pageIndex = list.getCurrentIndex();
                    if (pageIndex != lastPageIndex || !TextUtils.equals(view.getOriginalUrl(), url)) {
                        pageUpdated = true;
                        lastPageIndex = pageIndex;
                    }

                    param.put("pageIndex", pageIndex);
                    param.put("historySize", historySize);
                }
                param.put(H5Container.KEY_PAGE_UPDATED, pageUpdated);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Plugin.H5_PAGE_FINISHED, param);
            delta = System.currentTimeMillis() - startTime;
            H5Log.d("h5_page_finish url={" + url + "} cost={" + delta + "}");
        }

        if (h5Page != null) {
            String colorStr = null;
            if (h5Page.getParams() != null
                    && (colorStr = h5Page.getParams().getString(H5Param.LONG_BACKGROUND_COLOR)) != null) {
                JSONObject param = new JSONObject();
                try {
                    long color = Long.parseLong(colorStr);
                    color = color ^ 0xFF000000;
                    try {
                        param.put(H5Param.LONG_BACKGROUND_COLOR, (int) color);
                    } catch (JSONException e) {
                        H5Log.e(TAG, "exception", e);
                    }
                    h5Page.sendIntent(H5Plugin.H5_PAGE_BACKGROUND, param);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private long getTotalRxBytes() {
        long begUidRx = 0;
        try {
            begUidRx = TrafficStats.getUidRxBytes(uid);
        } catch (Exception e) {
        }
        return begUidRx;
    }

    public void onRelease() {
        h5Page = null;
    }

    public void reportH5Performance() {
        if (h5Page == null) {
            return;
        }
        H5Log.d(TAG, "reportPerformance");
//        String performance = "start=" + startTime + "^" + "finishLoad=" + delta;
//        JSONArray array = h5Page.getH5performance();
//        try{
//        if (array != null && array.size() > 0) {
//            String jsErrorKey = "jsErrors";
//            int jsErrorCount = 0;
//            for (int i = 0; i < array.size(); i++) {
//                JSONObject obj = array.getJSONObject(i);
//                if (TextUtils.equals(obj.getString("name"), jsErrorKey)) {
//                    jsErrorCount++;
//                } else if (TextUtils.equals(obj.getString("name"), "pageLoad")) {
//                    performance = performance + "^" + obj.getString("name") + "="
//                            + (obj.getLongValue("value") - startTime);
//                } else if (TextUtils.equals(obj.getString("name"), "domReady ")) {
//                    performance = performance + "^" + obj.getString("name") + "="
//                            + (obj.getLongValue("value") - startTime);
//                } else {
//                    performance = performance + "^" + obj.getString("name") + "=" + obj.getString("value");
//                }
//            }
//            performance = performance + "^" + jsErrorKey + "=" + jsErrorCount;
//        }
//        }catch (JSONException e){
//            H5Log.e(TAG,"exception",e);
//        }
//        Behavor behavor = new Behavor();
//        behavor.setSeedID("H5_PAGE_PERFORMANCE");
//        behavor.setParam1(loadingUrl);
//        String publicId = H5Utils.getString(h5Page.getParams(), H5Param.PUBLIC_ID);
//        String appId = H5Utils.getString(h5Page.getParams(), H5Container.APP_ID);
//        if (StringUtils.isNotEmpty(publicId)) {
//            behavor.setParam2(publicId);
//        } else if (StringUtils.isNotEmpty(appId)) {
//            behavor.setParam2(appId);
//        }
//        // TODO IP
//        // behavor.setParam3(extParam3);
//        // BehaviourIdEnum.
//        LoggerFactory.getBehavorLogger().openPage(behavor);

    }
}
