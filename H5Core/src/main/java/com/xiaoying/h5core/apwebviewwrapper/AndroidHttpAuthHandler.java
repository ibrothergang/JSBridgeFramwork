package com.xiaoying.h5core.apwebviewwrapper;


import android.webkit.HttpAuthHandler;

import com.xiaoying.h5core.apwebview.APHttpAuthHandler;

/**
 * Created by Administrator on 2014/12/9.
 */
class AndroidHttpAuthHandler implements APHttpAuthHandler {
    HttpAuthHandler httpAuthHandler;

    AndroidHttpAuthHandler(HttpAuthHandler httpAuthHandler) {
        this.httpAuthHandler = httpAuthHandler;
    }

    @Override
    public void cancel() {
        if (httpAuthHandler != null) {
            httpAuthHandler.cancel();
        }
    }

    @Override
    public void proceed(String username, String password) {
        if (httpAuthHandler != null) {
            httpAuthHandler.proceed(username, password);
        }
    }

    @Override
    public boolean useHttpAuthUsernamePassword() {
        if (httpAuthHandler != null) {
            return httpAuthHandler.useHttpAuthUsernamePassword();
        } else {
            return false;
        }
    }
}
