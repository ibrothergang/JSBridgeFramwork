package com.xiaoying.h5core.plugin;

import android.content.Context;
import android.text.ClipboardManager;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class H5ClipboardPlugin implements H5Plugin {

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SET_CLIPBOARD);
        filter.addAction(GET_CLIPBOARD);
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
        if (SET_CLIPBOARD.equals(action)) {
            setClipboard(intent);
        } else if (GET_CLIPBOARD.equals(action)) {
            getClipboard(intent);
        }
        return true;
    }

    public void setClipboard(H5Intent intent) {
        JSONObject callParam = intent.getParam();
        if (callParam == null) {
            return;
        }
        String text = H5Utils.getString(callParam, "text");

        Context context = H5Environment.getContext();
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
    }

    public void getClipboard(H5Intent intent) {
        Context context = H5Environment.getContext();
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence cs = clipboard.getText();
        String text = null;
        if (cs != null) {
            text = cs.toString();
        } else {
            text = "";
        }
        JSONObject data = new JSONObject();
        try {
            data.put("text", text);
        } catch (JSONException e) {
            H5Log.e("H5ClipboardPlugin", "exception", e);
        }
        intent.sendBack(data);
    }

}
