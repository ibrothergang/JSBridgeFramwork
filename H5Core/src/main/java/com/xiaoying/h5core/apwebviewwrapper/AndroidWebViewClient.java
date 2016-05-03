package com.xiaoying.h5core.apwebviewwrapper;

import com.xiaoying.h5core.apwebview.APWebViewClient;
import com.xiaoying.h5core.apwebview.APWebViewCtrl;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class AndroidWebViewClient extends WebViewClient {
    private APWebViewCtrl mAPWebView;
    private APWebViewClient mAPWebViewClient;

    AndroidWebViewClient(APWebViewCtrl apWebView, APWebViewClient apWebViewClient) {
        mAPWebView = apWebView;
        mAPWebViewClient = apWebViewClient;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (mAPWebViewClient != null) {
            return mAPWebViewClient.shouldOverrideUrlLoading(mAPWebView, url);
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onPageStarted(mAPWebView, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onPageFinished(mAPWebView, url);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onLoadResource(mAPWebView, url);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (mAPWebViewClient != null) {
            WebResourceResponse rsp = mAPWebViewClient.shouldInterceptRequest(mAPWebView, url);
            if (rsp != null) {
                //TODO cairq
                return rsp;
                //return new WebResourceResponse(rsp.getMimeType(), rsp.getEncoding(), rsp.getData());
            }
        }
        return null;
    }

    //TODO cair uc not implement
//    @Override
//    public void onTooManyRedirects(WebView view, Message message, Message message1){
//
//    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onReceivedError(mAPWebView, errorCode, description, failingUrl);
        }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend,
                                   Message resend) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onFormResubmission(mAPWebView, dontResend, resend);
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url,
                                       boolean isReload) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.doUpdateVisitedHistory(mAPWebView, url, isReload);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onReceivedSslError(mAPWebView, new AndroidSslErrorHandler(handler),
                    new AndroidSslError(0, null, error));
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onReceivedHttpAuthRequest(mAPWebView, new AndroidHttpAuthHandler(handler), host, realm);
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (mAPWebViewClient != null) {
            return mAPWebViewClient.shouldOverrideKeyEvent(mAPWebView, event);
        }
        return false;
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (mAPWebView != null) {
            mAPWebViewClient.onUnhandledKeyEvent(mAPWebView, event);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (mAPWebViewClient != null) {
            mAPWebViewClient.onScaleChanged(mAPWebView, oldScale, newScale);
        }
    }

    //TODO cairq uc not implement
//    @Override
//    public void onReceivedLoginRequest(WebView view, String s, String s2, String s3) {
//
//    }
}
