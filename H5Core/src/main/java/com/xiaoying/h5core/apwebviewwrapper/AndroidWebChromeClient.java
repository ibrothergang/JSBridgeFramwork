package com.xiaoying.h5core.apwebviewwrapper;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.xiaoying.h5core.apwebview.APJsPromptResult;
import com.xiaoying.h5core.apwebview.APJsResult;
import com.xiaoying.h5core.apwebview.APWebChromeClient;
import com.xiaoying.h5core.apwebview.APWebViewCtrl;

/**
 * Created by Administrator on 2014/12/9.
 */
class AndroidWebChromeClient extends WebChromeClient {
    private APWebViewCtrl mAPView;
    private APWebChromeClient mClient;

    AndroidWebChromeClient(APWebViewCtrl apWebView,
                           APWebChromeClient apWebChromeClient) {
        mAPView = apWebView;
        mClient = apWebChromeClient;
    }

    @Override
    public void onProgressChanged(WebView view, int i) {
        if (mClient != null) {
            mClient.onProgressChanged(mAPView, i);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String s) {
        if (mClient != null) {
            mClient.onReceivedTitle(mAPView, s);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap bitmap) {
        if (mClient != null) {
            mClient.onReceivedIcon(mAPView, bitmap);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String s, boolean b) {
        if (mClient != null) {
            mClient.onReceivedTouchIconUrl(mAPView, s, b);
        }
    }

    @Override
    public void onShowCustomView(View view,
                                 final CustomViewCallback customViewCallback) {
        if (mClient != null) {
            mClient.onShowCustomView(view, DynamicProxy.dynamicProxy(
                    APWebChromeClient.CustomViewCallback.class, customViewCallback));
        }
    }

    @Override
    public void onHideCustomView() {
        if (mClient != null) {
            mClient.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean b, boolean b1,
                                  Message message) {
        return mClient.onCreateWindow(mAPView, b, b1, message);
    }

    @Override
    public void onRequestFocus(WebView webView) {
        if (mClient != null) {
            mClient.onRequestFocus(mAPView);
        }
    }

    @Override
    public void onCloseWindow(WebView webView) {
        if (mClient != null) {
            mClient.onCloseWindow(mAPView);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String s, String s1,
                             JsResult jsResult) {
        return mClient.onJsAlert(mAPView, s, s1,
                DynamicProxy.dynamicProxy(APJsResult.class, jsResult));
    }

    @Override
    public boolean onJsConfirm(WebView view, String s, String s1,
                               JsResult jsResult) {
        return mClient.onJsConfirm(mAPView, s, s1,
                DynamicProxy.dynamicProxy(APJsResult.class, jsResult));
    }

    @Override
    public boolean onJsPrompt(WebView view, String s, String s1, String s2,
                              JsPromptResult jsPromptResult) {
        return mClient.onJsPrompt(mAPView, s, s1, s2, DynamicProxy
                .dynamicProxy(APJsPromptResult.class, jsPromptResult));
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String s, String s1,
                                    JsResult jsResult) {
        return mClient.onJsBeforeUnload(mAPView, s, s1,
                DynamicProxy.dynamicProxy(APJsResult.class, jsResult));
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String s,
                                                   GeolocationPermissions.Callback callback) {
        if (mClient != null) {
            mClient.onGeolocationPermissionsShowPrompt(s, DynamicProxy
                    .dynamicProxy(
                            GeolocationPermissions.Callback.class,
                            callback));
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (mClient != null) {
            mClient.onGeolocationPermissionsHidePrompt();
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        if (mClient != null) {
            return mClient.onConsoleMessage(new ConsoleMessage(cm
                    .message(), cm.sourceId(), cm.lineNumber(),
                    ConsoleMessage.MessageLevel.valueOf(cm
                            .messageLevel().name())));
        }
        return false;
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mClient != null) {
            return mClient.getDefaultVideoPoster();
        }
        return null;
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (mClient != null) {
            return mClient.getVideoLoadingProgressView();
        }
        return null;
    }

    @Override
    public void getVisitedHistory(final ValueCallback<String[]> valueCallback) {
        // backup code
        // mClient.getVisitedHistory(new
        // android.webkit.ValueCallback<String[]>(){
        // @Override
        // public void onReceiveValue(String[] t){
        // valueCallback.onReceiveValue(t);
        // }
        // });
        mClient.getVisitedHistory(DynamicProxy.dynamicProxy(
                ValueCallback.class, valueCallback));
    }

    public void openFileChooser(final ValueCallback<Uri> uploadMsg) {
        if (mClient != null) {
            mClient.openFileChooser(uploadMsg, null, null);
        }
    }

    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        if (mClient != null) {
            mClient.openFileChooser(uploadMsg, acceptType, null);
        }
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if (mClient != null) {
            mClient.openFileChooser(uploadMsg, acceptType, capture);
        }
    }

    // //FIXME: not provide setupAutoFill() yet this version
    // //@Override
    // public void setupAutoFill(Message message) {
    // mClient.setupAutoFill(message);
    // }
}
