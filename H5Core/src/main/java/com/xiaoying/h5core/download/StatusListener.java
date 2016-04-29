package com.xiaoying.h5core.download;

import com.xiaoying.h5core.download.Downloader.Status;

public interface StatusListener {
    public void onStatus(String url, Status status);
}
