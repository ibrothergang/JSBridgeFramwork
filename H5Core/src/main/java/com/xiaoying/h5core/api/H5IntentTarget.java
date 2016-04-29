package com.xiaoying.h5core.api;

import org.json.JSONException;

public interface H5IntentTarget {

    public void onRelease();

    public boolean interceptIntent(H5Intent intent) throws JSONException;

    public boolean handleIntent(H5Intent intent) throws JSONException;

}
