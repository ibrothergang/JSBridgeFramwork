package com.xiaoying.h5core.apwebviewwrapper;

import android.webkit.SslErrorHandler;

import com.xiaoying.h5core.apwebview.APSslErrorHandler;

/**
 * Created by Administrator on 2014/12/9.
 */
class AndroidSslErrorHandler implements APSslErrorHandler {
    private SslErrorHandler mSslErrorHandler;

    AndroidSslErrorHandler(SslErrorHandler sslErrorHandler) {
        mSslErrorHandler = sslErrorHandler;
    }

    @Override
    public void cancel() {
        mSslErrorHandler.cancel();
    }

    @Override
    public void proceed() {
        mSslErrorHandler.proceed();
    }
}
