package com.xiaoying.h5core.plugin;

import com.xiaoying.h5api.api.H5Data;
import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.env.H5Container;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import java.util.Iterator;

public class H5ServicePlugin implements H5Plugin {

    public H5ServicePlugin() {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SET_SHARE_DATA);
        filter.addAction(GET_SHARE_DATA);
        filter.addAction(REMOVE_SHARE_DATA);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (GET_SHARE_DATA.equals(action)) {
            try {
                getSharedData(intent);
            } catch (JSONException e) {
                H5Log.e("H5ServicePlugin", "exception", e);
            }
        } else if (SET_SHARE_DATA.equals(action)) {
            setSharedData(intent);
        } else if (REMOVE_SHARE_DATA.equals(action)) {
            try {
                removeShareData(intent);
            } catch (JSONException e) {
                H5Log.e("H5ServicePlugin", "exception", e);
            }
        }
        return true;
    }

    private void setSharedData(H5Intent intent) {
        JSONObject param = intent.getParam();
        JSONObject data = H5Utils.getJSONObject(param, "data", null);
        if (data == null || data.length() == 0) {
            return;
        }

        H5Data shareData = H5Container.getService().getData();

        Iterator entrys = data.keys();
        while (entrys.hasNext()) {
            String key = (String) entrys.next();
            String value = H5Utils.getString(data, key);
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                shareData.set(key, value);
            }
        }
    }

    private void getSharedData(H5Intent intent) throws JSONException {
        JSONObject param = intent.getParam();
        JSONObject data = new JSONObject();
        JSONArray keys = H5Utils.getJSONArray(param, "keys", null);

        H5Data shareData = H5Container.getService().getData();

        if (keys != null && keys.length() != 0) {
            int size = keys.length();
            for (int index = 0; index < size; ++index) {
                Object obj = keys.get(index);
                if (!(obj instanceof String)) {
                    continue;
                }
                String key = (String) obj;
                String value = shareData.get(key);
                data.put(key, value);
            }
        }
        JSONObject result = new JSONObject();
        result.put("data", data);
        intent.sendBack(result);
    }

    private void removeShareData(H5Intent intent) throws JSONException {
        JSONObject param = intent.getParam();
        JSONArray keys = H5Utils.getJSONArray(param, "keys", null);

        H5Data shareData = H5Container.getService().getData();

        if (keys != null && keys.length() != 0) {
            int size = keys.length();
            for (int index = 0; index < size; ++index) {
                Object obj = keys.get(index);
                if (!(obj instanceof String)) {
                    continue;
                }
                String key = (String) obj;
                shareData.remove(key);
            }
        }
    }

}
