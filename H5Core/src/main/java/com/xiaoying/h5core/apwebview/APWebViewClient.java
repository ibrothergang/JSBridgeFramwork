package com.xiaoying.h5core.apwebview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebResourceResponse;

/**
 * similar role of that WebViewClient plays in WebView
 *
 * @author xide.wf
 */
public interface APWebViewClient {
    /**
     * Generic error
     */
    public static final int ERROR_UNKNOWN = -1;
    /**
     * Server or proxy hostname lookup failed
     */
    public static final int ERROR_HOST_LOOKUP = -2;
    /**
     * Unsupported authentication scheme (not basic or digest)
     */
    public static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;
    /**
     * User authentication failed on server
     */
    public static final int ERROR_AUTHENTICATION = -4;
    /**
     * User authentication failed on proxy
     */
    public static final int ERROR_PROXY_AUTHENTICATION = -5;
    /**
     * Failed to connect to the server
     */
    public static final int ERROR_CONNECT = -6;
    // These ints must match up to the hidden values in EventHandler.
    /**
     * Failed to read or write to the server
     */
    public static final int ERROR_IO = -7;
    /**
     * Connection timed out
     */
    public static final int ERROR_TIMEOUT = -8;
    /**
     * Too many redirects
     */
    public static final int ERROR_REDIRECT_LOOP = -9;
    /**
     * Unsupported URI scheme
     */
    public static final int ERROR_UNSUPPORTED_SCHEME = -10;
    /**
     * Failed to perform SSL handshake
     */
    public static final int ERROR_FAILED_SSL_HANDSHAKE = -11;
    /**
     * Malformed URL
     */
    public static final int ERROR_BAD_URL = -12;
    /**
     * Generic file error
     */
    public static final int ERROR_FILE = -13;
    /**
     * File not found
     */
    public static final int ERROR_FILE_NOT_FOUND = -14;
    /**
     * Too many requests during this load
     */
    public static final int ERROR_TOO_MANY_REQUESTS = -15;

    public boolean shouldOverrideUrlLoading(APWebViewCtrl view, String url);

    public void onPageStarted(APWebViewCtrl view, String url, Bitmap favicon);

    public void onPageFinished(APWebViewCtrl view, String url);

    public void onLoadResource(APWebViewCtrl view, String url);

    public WebResourceResponse shouldInterceptRequest(APWebViewCtrl view,
                                                      String url);

    public void onTooManyRedirects(APWebViewCtrl view, Message cancelMsg,
                                   Message continueMsg);

    public void onReceivedError(APWebViewCtrl view, int errorCode,
                                String description, String failingUrl);

    public void onFormResubmission(APWebViewCtrl view, Message dontResend,
                                   Message resend);

    public void doUpdateVisitedHistory(APWebViewCtrl view, String url,
                                       boolean isReload);

    public void onReceivedSslError(APWebViewCtrl view, APSslErrorHandler handler,
                                   SslError error);

    public void onReceivedHttpAuthRequest(APWebViewCtrl view,
                                          APHttpAuthHandler handler, String host, String realm);

    public boolean shouldOverrideKeyEvent(APWebViewCtrl view, KeyEvent event);

    public void onUnhandledKeyEvent(APWebViewCtrl view, KeyEvent event);

    public void onScaleChanged(APWebViewCtrl view, float oldScale, float newScale);

    public void onReceivedLoginRequest(APWebViewCtrl view, String realm,
                                       String account, String args);
}
