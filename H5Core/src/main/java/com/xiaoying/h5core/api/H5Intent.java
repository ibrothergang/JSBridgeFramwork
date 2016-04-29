package com.xiaoying.h5core.api;

import org.json.JSONObject;

public interface H5Intent extends H5Message {

    public String getAction();

    public String getType();

    public H5CallBack getCallBack();

    public H5Bridge getBridge();

    // send back one time
    public boolean sendBack(JSONObject data);

    // send back one time
    public boolean sendBack(String k, Object o);

    // send more than one time
    public boolean keepSend(JSONObject data);

    // send more than one time
    public boolean keepSend(String k, Object o);

    public Error getError();

    public boolean sendError(Error code);

    public static enum Error {
        NONE, NOT_FOUND, INVALID_PARAM, UNKNOWN_ERROR, FORBIDDEN
    }

}
