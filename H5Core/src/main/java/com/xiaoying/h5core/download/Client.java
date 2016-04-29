package com.xiaoying.h5core.download;

public interface Client {
    public boolean connect(String url, String localPath);

    public boolean disconnect();

    public void setListener(TransferListener listener);
}
