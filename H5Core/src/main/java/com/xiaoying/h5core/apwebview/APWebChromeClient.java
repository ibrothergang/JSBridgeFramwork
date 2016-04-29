package com.xiaoying.h5core.apwebview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;

/**
 * similar role of that WebChromeClient plays in WebView
 *
 * @author xide.wf
 */
public interface APWebChromeClient {
    public void onProgressChanged(APWebViewCtrl view, int newProgress);

    public void onReceivedTitle(APWebViewCtrl view, String title);

    public void onReceivedIcon(APWebViewCtrl view, Bitmap icon);

    public void onReceivedTouchIconUrl(APWebViewCtrl view, String url,
                                       boolean precomposed);

    public void onShowCustomView(View view, CustomViewCallback callback);

    public void onHideCustomView();

    public boolean onCreateWindow(APWebViewCtrl view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg);

    public void onRequestFocus(APWebViewCtrl view);

    public void onCloseWindow(APWebViewCtrl window);

    public boolean onJsAlert(APWebViewCtrl view, String url, String message,
                             APJsResult result);

    public boolean onJsConfirm(APWebViewCtrl view, String url, String message,
                               APJsResult result);

    public boolean onJsPrompt(APWebViewCtrl view, String url, String message,
                              String defaultValue, APJsPromptResult result);

    public boolean onJsBeforeUnload(APWebViewCtrl view, String url, String message,
                                    APJsResult result);

    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback);

    public void onGeolocationPermissionsHidePrompt();

    public boolean onConsoleMessage(ConsoleMessage consoleMessage);

    public Bitmap getDefaultVideoPoster();

    public View getVideoLoadingProgressView();

    public void getVisitedHistory(ValueCallback<String[]> callback);

    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture);

    public interface CustomViewCallback {
        /**
         * Invoked when the host application dismisses the
         * custom view.
         */
        public void onCustomViewHidden();
    }
}
