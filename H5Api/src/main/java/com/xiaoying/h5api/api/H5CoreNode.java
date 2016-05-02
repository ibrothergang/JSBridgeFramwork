package com.xiaoying.h5api.api;

import org.json.JSONObject;

public interface H5CoreNode extends H5DataProvider, H5IntentTarget {

    public H5CoreNode getParent();

    public void setParent(H5CoreNode parent);

    public boolean addChild(H5CoreNode child);

    public boolean removeChild(H5CoreNode child);

    public H5PluginManager getPluginManager();

    public void sendIntent(String action, JSONObject param);

}
