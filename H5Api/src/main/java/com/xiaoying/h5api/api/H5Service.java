package com.xiaoying.h5api.api;

public interface H5Service extends H5CoreNode {


    public abstract void addPluginConfig(H5PluginConfig paramH5PluginConfig);

    public H5Page createPage(H5Context h5Context, H5Bundle params);

    public boolean startPage(H5Context h5Context, H5Bundle params);

    public boolean exitService();

    public boolean addSession(H5Session session);

    public H5Session getSession(String sessionId);

    public boolean removeSession(String sessionId);

    public H5Session getTopSession();

}
