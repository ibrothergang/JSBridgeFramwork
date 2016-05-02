package com.xiaoying.h5api.api;

public interface H5Listener {

    public void onSessionCreated(H5Session session);

    public void onSessionDestroyed(H5Session session);

    public void onPageCreated(H5Page page);

    public void onPageDestroyed(H5Page page);

}
