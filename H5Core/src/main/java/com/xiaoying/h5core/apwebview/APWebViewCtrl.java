package com.xiaoying.h5core.apwebview;

import android.graphics.Bitmap;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.webkit.ValueCallback;

import java.util.Map;

public interface APWebViewCtrl {
    public void addJavascriptInterface(Object object, String name);

    public void removeJavascriptInterface(String name);

    public void setWebContentsDebuggingEnabled(boolean enabled);

    public void flingScroll(int vx, int vy);

    public boolean zoomIn();

    public boolean zoomOut();

    public void setHorizontalScrollbarOverlay(boolean overlay);

    public void setVerticalScrollbarOverlay(boolean overlay);

    public boolean overlayHorizontalScrollbar();

    public boolean overlayVerticalScrollbar();

    public SslCertificate getCertificate();

    public void savePassword(String host, String username, String password);

    public void setHttpAuthUsernamePassword(String host, String realm,
                                            String username, String password);

    public String[] getHttpAuthUsernamePassword(String host, String realm);

    public void destroy();

    public void setNetworkAvailable(boolean networkUp);

    public APWebBackForwardList saveState(Bundle outState);

    public APWebBackForwardList restoreState(Bundle inState);

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders);

    public void loadUrl(String url);

    public void postUrl(String url, byte[] postData);

    public void loadData(String data, String mimeType, String encoding);

    public void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding, String historyUrl);

    public void evaluateJavascript(String script, ValueCallback<String> resultCallback);

    public void stopLoading();

    public void reload();

    public boolean canGoBack();

    public void goBack();

    public boolean canGoForward();

    public void goForward();

    public boolean canGoBackOrForward(int steps);

    public void goBackOrForward(int steps);

    public boolean pageUp(boolean top);

    public boolean pageDown(boolean bottom);

    public void setInitialScale(int scaleInPercent);

    public void invokeZoomPicker();

    public String getUrl();

    public String getOriginalUrl();

    public String getTitle();

    public Bitmap getFavicon();

    public int getProgress();

    public int getContentHeight();

    public int getContentWidth();

    public void onPause();

    public void onResume();

    public boolean isPaused();

    public void freeMemory();

    public void clearCache(boolean includeDiskFiles);

    public void clearFormData();

    public void clearHistory();

    public void clearSslPreferences();

    public APWebBackForwardList copyBackForwardList();

    public void setWebViewClient(APWebViewClient client);

    public void setDownloadListener(APDownloadListener listener);

    public void setWebChromeClient(APWebChromeClient client);

    public APWebSettings getSettings();

    public APHitTestResult getHitTestResult();

    /**
     * @param deltaX
     * @param deltaY
     * @param scrollX
     * @param scrollY
     * @param scrollRangeX
     * @param scrollRangeY
     * @param maxOverScrollX
     * @param maxOverScrollY
     * @param isTouchEvent
     * @return
     */
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                int maxOverScrollY, boolean isTouchEvent);
}