package com.xiaoying.h5core.apwebviewwrapper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslCertificate;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.xiaoying.h5core.apwebview.APDownloadListener;
import com.xiaoying.h5core.apwebview.APHitTestResult;
import com.xiaoying.h5core.apwebview.APWebBackForwardList;
import com.xiaoying.h5core.apwebview.APWebChromeClient;
import com.xiaoying.h5core.apwebview.APWebSettings;
import com.xiaoying.h5core.apwebview.APWebViewClient;
import com.xiaoying.h5core.apwebview.APWebViewCtrl;
import com.xiaoying.h5core.apwebview.GlueWebView;
import com.xiaoying.h5core.apwebview.Version;
import com.xiaoying.h5core.apwebview.WebViewType;
import com.xiaoying.h5core.util.H5Log;

import java.util.Map;

/**
 * glue webview implement basing android build-in webview
 *
 * @author xide.wf
 */
public class AndroidWebView extends GlueWebView {
    private static final String TAG = "AndroidWebView";
    private APWebSettings webSettings;
    private APWebViewCtrl parentWebViewCtrl;
    private WebView webView;

    public AndroidWebView(Context context) {
        this(context, null);
    }

    public AndroidWebView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public AndroidWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        webView = new InternalWebView(context, attrs);
        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // pass the long click events to parent layout
                return false;
            }
        });
        addView(webView, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webSettings = new AndroidWebSettings(webView.getSettings());
    }

    @Override
    public void onCompositedToParent(APWebViewCtrl apWebViewCtrl) {
        parentWebViewCtrl = apWebViewCtrl;
        if (parentWebViewCtrl == null) {
            throw new IllegalArgumentException("parentWebViewCtrl is null, fatal error");
        }
    }

    @Override
    public int getVersion() {
        return Version.build(1, 0);
    }

    @Override
    public WebViewType getType() {
        return WebViewType.SYSTEM_BUILD_IN;
    }

    @Override
    public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6,
                                int i7, int i8, boolean b) {
        return parentWebViewCtrl.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, b);
    }

    /**
     * Called by {@link #overScrollBy(int, int, int, int, int, int, int, int, boolean)} to
     * respond to the results of an over-scroll operation.
     *
     * @param scrollX  New X scroll value in pixels
     * @param scrollY  New Y scroll value in pixels
     * @param clampedX True if scrollX was clamped to an over-scroll boundary
     * @param clampedY True if scrollY was clamped to an over-scroll boundary
     */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {
        webView.addJavascriptInterface(obj, interfaceName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void removeJavascriptInterface(String name) {
        if (Build.VERSION.SDK_INT >= 11) {
            webView.removeJavascriptInterface(name);
        }
    }

    @Override
    public void setWebContentsDebuggingEnabled(boolean b) {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setWebContentsDebuggingEnabled(b);
        }
    }

    @Override
    public void flingScroll(int vx, int vy) {
        webView.flingScroll(vx, vy);
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
    public void setVerticalScrollbarOverlay(boolean overlay) {
        webView.setVerticalScrollbarOverlay(overlay);
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
    public void setHttpAuthUsernamePassword(String host, String realm,
                                            String username, String password) {
        webView.setHttpAuthUsernamePassword(host, realm, username, password);
    }

    @Override
    public String[] getHttpAuthUsernamePassword(String host, String realm) {
        return webView.getHttpAuthUsernamePassword(host, realm);
    }

    @Override
    public void destroy() {
        webView.destroy();
    }

    @Override
    public void setNetworkAvailable(boolean networkUp) {
        webView.setNetworkAvailable(networkUp);
    }

    @Override
    public APWebBackForwardList saveState(Bundle bundle) {
        WebBackForwardList list = webView.saveState(bundle);
        if (list != null) {
            return new AndroidWebBackForwardList(list);
        }
        return null;
    }

    @Override
    public APWebBackForwardList restoreState(Bundle bundle) {
        WebBackForwardList list = webView.restoreState(bundle);
        if (list != null) {
            return new AndroidWebBackForwardList(list);
        }
        return null;
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        webView.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void postUrl(String url, byte[] postData) {
        webView.postUrl(url, postData);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        webView.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding, String historyUrl) {
        webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding,
                historyUrl);
    }

    @Override
    public void evaluateJavascript(String s,
                                   ValueCallback<String> stringValueCallback) {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.evaluateJavascript(s, stringValueCallback);
        }
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
    public boolean canGoBackOrForward(int steps) {
        return webView.canGoBackOrForward(steps);
    }

    @Override
    public void goBackOrForward(int steps) {
        webView.goBackOrForward(steps);
    }

    @Override
    public boolean pageUp(boolean top) {
        return webView.pageUp(top);
    }

    @Override
    public boolean pageDown(boolean bottom) {
        return webView.pageDown(bottom);
    }

    @Override
    public void setInitialScale(int scaleInPercent) {
        webView.setInitialScale(scaleInPercent);
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
        H5Log.d(TAG, "getContentWidth() is currently not supported yet");
        return 0;
    }

    @Override
    public void onPause() {
        if (Build.VERSION.SDK_INT >= 11) {
            webView.onPause();
        }
    }

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= 11) {
            webView.onResume();
        }
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void freeMemory() {
        webView.freeMemory();
    }

    @Override
    public void clearCache(boolean includeDiskFiles) {
        webView.clearCache(includeDiskFiles);
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
        WebBackForwardList list = webView.copyBackForwardList();
        if (list != null) {
            return new AndroidWebBackForwardList(list);
        }
        return null;
    }

    @Override
    public void setWebViewClient(APWebViewClient apWebViewClient) {
        webView.setWebViewClient(new AndroidWebViewClient(parentWebViewCtrl,
                apWebViewClient));
    }

    @Override
    public void setDownloadListener(APDownloadListener apDownloadListener) {
        webView.setDownloadListener(new AndroidDownloadListener(
                apDownloadListener));
    }

    @Override
    public void setWebChromeClient(APWebChromeClient apWebChromeClient) {
        webView.setWebChromeClient(new AndroidWebChromeClient(
                parentWebViewCtrl, apWebChromeClient));
    }

    @Override
    public APWebSettings getSettings() {
        return webSettings;
    }

    @Override
    public APHitTestResult getHitTestResult() {
        WebView.HitTestResult hitTestResult = webView.getHitTestResult();
        if (hitTestResult == null) {
            return null;
        } else {
            return new AndroidHitTestResult(hitTestResult);
        }
    }

    @Override
    public View getUnderlyingWebView() {
        return webView;
    }

    private final class InternalWebView extends WebView {
        public InternalWebView(Context context) {
            super(context);
        }

        public InternalWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public InternalWebView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                       int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                       int maxOverScrollY, boolean isTouchEvent) {
            if (deltaY < 0 && scrollY == 0) {
                return AndroidWebView.this.parentWebViewCtrl.overScrollBy(deltaX, deltaY, scrollX,
                        scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                        isTouchEvent);
            } else {
                return super.overScrollBy(deltaX, deltaY, scrollX,
                        scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                        isTouchEvent);
            }
        }
    }

    private class AndroidHitTestResult implements APHitTestResult {
        WebView.HitTestResult droidHitTestResult;

        AndroidHitTestResult(WebView.HitTestResult hitTestResult) {
            droidHitTestResult = hitTestResult;
        }

        @Override
        public String getExtra() {
            return droidHitTestResult.getExtra();
        }

        @Override
        public int getType() {
            return droidHitTestResult.getType();
        }
    }
}
