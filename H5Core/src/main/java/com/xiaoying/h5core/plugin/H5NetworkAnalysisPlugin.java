package com.xiaoying.h5core.plugin;

import android.text.TextUtils;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.util.PingUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class H5NetworkAnalysisPlugin implements H5Plugin {
    private static final String NETWORK_ANALYSIS = "networkAnalysis";

    public H5NetworkAnalysisPlugin() {
    }

    @Override
    public void onRelease() {
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(NETWORK_ANALYSIS);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(final H5Intent intent) {
        String action = intent.getAction();
        if (NETWORK_ANALYSIS.equals(action)) {
            JSONObject param = intent.getParam();
            if (param != null && (!TextUtils.isEmpty(H5Utils.getString(param, "host")))) {
                final String host = H5Utils.getString(param, "host");
                H5Container.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        PingUtil.PingResult result = null;
                        try {
                            result = PingUtil.ping(host);
                        } catch (Exception globalException) {
                            globalException.printStackTrace();
                        }
                        JSONObject resultJson = new JSONObject();
                        try {
                            if (result != null) {
                                resultJson.put("consumedTimeMs", result.consumedTimeMs);
                                resultJson.put("numSendPkt", result.numSendPkt);
                                resultJson.put("numReceivedPkt", result.numReceivedPkt);
                                resultJson.put("loss", result.loss);
                            } else {
                                resultJson.put("error", "ping error");
                            }
                        } catch (JSONException e) {
                            H5Log.e("H5NetworkAnalysisPlugin", "exception", e);
                        }

                        intent.sendBack(resultJson);
                    }
                });
            }
        }
        return true;
    }

}
