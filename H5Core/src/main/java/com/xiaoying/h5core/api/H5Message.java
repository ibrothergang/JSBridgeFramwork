package com.xiaoying.h5core.api;

import org.json.JSONObject;

public interface H5Message {

    public String getId();

    public void setId(String id);

    public H5CoreNode getTarget();

    public void setTarget(H5CoreNode source);

    public void cancel();

    public boolean isCanceled();

    public JSONObject getParam();

    public void setParam(JSONObject data);

}
