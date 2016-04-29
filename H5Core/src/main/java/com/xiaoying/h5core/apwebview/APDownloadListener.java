package com.xiaoying.h5core.apwebview;

/**
 * similar role of that DownloadListener plays in WebView
 *
 * @author xide.wf
 */
public interface APDownloadListener {
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
}
