package com.xiaoying.h5core.apwebview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.http.SslCertificate;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;

import com.xiaoying.h5core.R;
import com.xiaoying.h5core.web.H5WebView;
import com.xiaoying.h5core.web.H5WebViewRenderPolicy;

import java.util.Map;

public class APWebView extends FrameLayout implements APWebViewCtrl {
    protected GlueWebView webView;
    protected Bundle webViewCfg;

    public APWebView(Context context) {
        this(context, null, 0, null);
    }

    public APWebView(Context context, final Bundle webViewCfg) {
        this(context, null, 0, webViewCfg);
    }

    public APWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public APWebView(Context context, AttributeSet attrs, Bundle webViewCfg) {
        this(context, attrs, 0, webViewCfg);
    }

    public APWebView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public APWebView(Context context, AttributeSet attrs, int defStyle, final Bundle webViewCfg) {
        super(context, attrs);
        this.webViewCfg = webViewCfg;
        String bizType = null;
        if (webViewCfg != null) {
            bizType = webViewCfg.getString(H5WebView.CfgConstants.KEY_BIZ_TYPE);
        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.apWebView);
            if (TextUtils.isEmpty(bizType)) {
                bizType = a.getString(R.styleable.apWebView_bizType);
            }
        }

        webView = GlueWebViewFactory.instance().createWebView(bizType, context);
        if (webView == null) {
            throw new IllegalStateException("couldn't instantiate WebView instance for bizType:" + bizType);
        }

        final boolean meetApiLevel11 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        if (H5WebViewRenderPolicy.shouldDisableHardwareRenderInLayer() && meetApiLevel11) {
            final View underlyingWebView = webView.getUnderlyingWebView();
            if (underlyingWebView != null
                    && webView.getType().equals(WebViewType.SYSTEM_BUILD_IN)) {
                try {
                    underlyingWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                } catch (Exception globalException) {
                    globalException.printStackTrace();
                }
            }
        }

        addView(webView, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webView.onCompositedToParent(this);
    }

    public APWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        this(context, attrs, defStyle, null);
    }

    public final Bundle getWebViewCfg() {
        return webViewCfg;
    }

    @Override
    public void addJavascriptInterface(Object o, String s) {
        webView.addJavascriptInterface(o, s);
    }

    @Override
    public void removeJavascriptInterface(String s) {
        webView.removeJavascriptInterface(s);
    }

    @Override
    public void setWebContentsDebuggingEnabled(boolean b) {
        webView.setWebContentsDebuggingEnabled(b);
    }

    @Override
    public void flingScroll(int i, int i2) {
        webView.flingScroll(i, i2);
    }

    @Override
    public boolean zoomIn() {
        return webView.zoomIn();
    }

    @Override
    public boolean zoomOut() {
        return webView.zoomOut();
    }

    @Override
    public void setHorizontalScrollbarOverlay(boolean b) {
        webView.setHorizontalScrollbarOverlay(b);
    }

    @Override
    public void setVerticalScrollbarOverlay(boolean b) {
        webView.setVerticalScrollbarOverlay(b);
    }

    @Override
    public boolean overlayHorizontalScrollbar() {
        return webView.overlayHorizontalScrollbar();
    }

    @Override
    public boolean overlayVerticalScrollbar() {
        return webView.overlayVerticalScrollbar();
    }

    @Override
    public SslCertificate getCertificate() {
        return webView.getCertificate();
    }

    @Override
    public void savePassword(String s, String s2, String s3) {
        webView.savePassword(s, s2, s3);
    }

    @Override
    public void setHttpAuthUsernamePassword(String s, String s2, String s3, String s4) {
        webView.setHttpAuthUsernamePassword(s, s2, s3, s4);
    }

    @Override
    public String[] getHttpAuthUsernamePassword(String s, String s2) {
        return webView.getHttpAuthUsernamePassword(s, s2);
    }

    @Override
    public void destroy() {
        webView.destroy();
    }

    @Override
    public void setNetworkAvailable(boolean b) {
        webView.setNetworkAvailable(b);
    }

    @Override
    public APWebBackForwardList saveState(Bundle bundle) {
        return webView.saveState(bundle);
    }

    @Override
    public APWebBackForwardList restoreState(Bundle bundle) {
        return webView.restoreState(bundle);
    }

    @Override
    public void loadUrl(String s, Map<String, String> stringStringMap) {
        webView.loadUrl(s, stringStringMap);
    }

    @Override
    public void loadUrl(String s) {
        webView.loadUrl(s);
    }

    @Override
    public void postUrl(String s, byte[] bytes) {
        webView.postUrl(s, bytes);
    }

    @Override
    public void loadData(String s, String s2, String s3) {
        webView.loadData(s, s2, s3);
    }

    @Override
    public void loadDataWithBaseURL(String s, String s2, String s3, String s4, String s5) {
        webView.loadDataWithBaseURL(s, s2, s3, s4, s5);
    }

    @Override
    public void evaluateJavascript(String s, ValueCallback<String> stringValueCallback) {
        webView.evaluateJavascript(s, stringValueCallback);
    }

    @Override
    public void stopLoading() {
        webView.stopLoading();
    }

    @Override
    public void reload() {
        webView.reload();
    }

    @Override
    public boolean canGoBack() {
        return webView.canGoBack();
    }

    @Override
    public void goBack() {
        webView.goBack();
    }

    @Override
    public boolean canGoForward() {
        return webView.canGoForward();
    }

    @Override
    public void goForward() {
        webView.goForward();
    }

    @Override
    public boolean canGoBackOrForward(int i) {
        return webView.canGoForward();
    }

    @Override
    public void goBackOrForward(int i) {
        webView.goBackOrForward(i);
    }

    @Override
    public boolean pageUp(boolean b) {
        return webView.pageUp(b);
    }

    @Override
    public boolean pageDown(boolean b) {
        return webView.pageDown(b);
    }

    @Override
    public void setInitialScale(int i) {
        webView.setInitialScale(i);
    }

    @Override
    public void invokeZoomPicker() {
        webView.invokeZoomPicker();
    }

    @Override
    public String getUrl() {
        return webView.getUrl();
    }

    @Override
    public String getOriginalUrl() {
        return webView.getOriginalUrl();
    }

    @Override
    public String getTitle() {
        return webView.getTitle();
    }

    @Override
    public Bitmap getFavicon() {
        return webView.getFavicon();
    }

    @Override
    public int getProgress() {
        return webView.getProgress();
    }

    @Override
    public int getContentHeight() {
        return webView.getContentHeight();
    }

    @Override
    public int getContentWidth() {
        return webView.getContentWidth();
    }

    @Override
    public void onPause() {
        webView.onPause();
    }

    @Override
    public void onResume() {
        webView.onResume();
    }

    @Override
    public boolean isPaused() {
        return webView.isPaused();
    }

    @Override
    public void freeMemory() {
        webView.freeMemory();
    }

    @Override
    public void clearCache(boolean b) {
        webView.clearCache(b);
    }

    @Override
    public void clearFormData() {
        webView.clearFormData();
    }

    @Override
    public void clearHistory() {
        webView.clearHistory();
    }

    @Override
    public void clearSslPreferences() {
        webView.clearSslPreferences();
    }

    @Override
    public APWebBackForwardList copyBackForwardList() {
        return webView.copyBackForwardList();
    }

    @Override
    public void setWebViewClient(APWebViewClient apWebViewClient) {
        webView.setWebViewClient(apWebViewClient);
    }

    @Override
    public void setDownloadListener(APDownloadListener apDownloadListener) {
        webView.setDownloadListener(apDownloadListener);
    }

    @Override
    public void setWebChromeClient(APWebChromeClient apWebChromeClient) {
        webView.setWebChromeClient(apWebChromeClient);
    }

    @Override
    public APWebSettings getSettings() {
        return webView.getSettings();
    }

    public View getUnderlyingWebView() {
        return webView.getUnderlyingWebView();
    }

    @Override
    public APHitTestResult getHitTestResult() {
        return webView.getHitTestResult();
    }

    public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                int scrollY, int scrollRangeX, int scrollRangeY,
                                int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    public int getVersion() {
        return webView.getVersion();
    }

    public WebViewType getType() {
        return webView.getType();
    }

    public static final class TagBuilder {
        public static final String build(APWebView webView) {
            if (webView == null) {
                return "(Null webview)";
            }
            return "(WebView type:" + webView.getType() + ", version: "
                    + Version.getMajor(webView.getVersion()) + "."
                    + Version.getMinor(webView.getVersion()) + ")";
        }
    }

}
