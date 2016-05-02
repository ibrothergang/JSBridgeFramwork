package com.xiaoying.h5core.apwebview;

public interface APDownloadListener {
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
}
