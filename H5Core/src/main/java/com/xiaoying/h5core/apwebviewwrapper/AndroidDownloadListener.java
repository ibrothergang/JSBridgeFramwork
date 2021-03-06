package com.xiaoying.h5core.apwebviewwrapper;


import com.xiaoying.h5core.apwebview.APDownloadListener;

import android.webkit.DownloadListener;

class AndroidDownloadListener implements DownloadListener {

    private APDownloadListener mListener;

    AndroidDownloadListener(APDownloadListener listener) {
        mListener = listener;
    }

    @Override
    public void onDownloadStart(String url, String userAgent,
                                String contentDisposition, String mimetype, long contentLength) {
        if (mListener != null) {
            mListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
        }
    }


}
