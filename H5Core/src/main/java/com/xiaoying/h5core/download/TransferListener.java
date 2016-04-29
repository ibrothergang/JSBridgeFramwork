package com.xiaoying.h5core.download;

public interface TransferListener {
    void onProgress(int progress);

    void onTotalSize(long size);
}
