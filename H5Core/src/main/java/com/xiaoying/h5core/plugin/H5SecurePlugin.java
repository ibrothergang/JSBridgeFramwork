package com.xiaoying.h5core.plugin;

import android.text.TextUtils;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5api.util.RsaUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class H5SecurePlugin implements H5Plugin {

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(RSA);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (RSA.equals(action)) {
            try {
                rsa(intent);
            } catch (JSONException e) {
                H5Log.e("H5SecurePlugin", "exception", e);
            }
        }
        return true;
    }

    private void rsa(H5Intent intent) throws JSONException {
        JSONObject params = intent.getParam();
        String action = H5Utils.getString(params, "action");
        String content = H5Utils.getString(params, "text");
        String key = H5Utils.getString(params, "key");

        if (TextUtils.isEmpty(content)) {
            JSONObject data = new JSONObject();
            data.put("error", 10);
            data.put("errorMessage", "invalid text");
            intent.sendBack(data);
            return;
        }

        if (TextUtils.isEmpty(key)) {
            JSONObject data = new JSONObject();
            data.put("error", 11);
            data.put("errorMessage", "invalid key");
            intent.sendBack(data);
            return;
        }

        String result = null;
        if (TextUtils.equals(action, "encrypt")) {
            result = RsaUtil.encrypt(content, key);
        } else if (TextUtils.equals(action, "decrypt")) {
            result = RsaUtil.decrypt(content, key);
        }

        if (TextUtils.isEmpty(result)) {
            JSONObject data = new JSONObject();
            data.put("error", 11);
            data.put("errorMessage", "ras error!");
            intent.sendBack(data);
            return;
        }

        JSONObject data = new JSONObject();
        data.put("text", result);
        intent.sendBack(data);
    }

}
