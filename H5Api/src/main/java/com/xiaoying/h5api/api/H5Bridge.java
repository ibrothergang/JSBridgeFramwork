package com.xiaoying.h5api.api;

import org.json.JSONObject;

public interface H5Bridge {

    public void sendToNative(H5Intent intent);

    public void sendToWeb(H5Intent intent);

    public void sendToWeb(String action, JSONObject param, H5CallBack callback);

    public void setBridgePolicy(BridgePolicy policy);

    public static interface BridgePolicy {
        public boolean shouldBan(String api);
    }

}
