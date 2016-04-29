package com.xiaoying.h5core.plugin;

import com.xiaoying.h5core.api.H5Intent;
import com.xiaoying.h5core.api.H5IntentFilter;
import com.xiaoying.h5core.api.H5Plugin;
import com.xiaoying.h5core.util.H5Log;
import com.xiaoying.h5core.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class H5NetworkPlugin implements H5Plugin {

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(GET_NETWORK_TYPE);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public void onRelease() {

    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (GET_NETWORK_TYPE.equals(action)) {
            try {
                getNetworkType(intent);
            } catch (JSONException e) {
                H5Log.e("H5NetworkPlugin", "exception", e);
            }

        }
        return true;
    }

    private void getNetworkType(H5Intent intent) throws JSONException {
        final String value = H5Utils.getNetworkType();
        final String err_msg = "network_type:" + value;
        JSONObject data = new JSONObject();
        data.put("err_msg", err_msg);
        data.put("networkType", value);
        boolean hasNetwork = !("fail".equals(value));
        data.put("networkAvailable", hasNetwork);
        intent.sendBack(data);
    }
}
