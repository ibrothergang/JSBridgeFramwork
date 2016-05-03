package com.xiaoying.h5core.plugin;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.R;
import com.xiaoying.h5core.view.H5Alert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;

public class H5AlertPlugin implements H5Plugin {

    public static final String TAG = "H5AlertPlugin";
    private H5Alert h5Alert;
    private H5Page h5Page;

    public H5AlertPlugin(H5Page page) {
        this.h5Page = page;
    }

    @Override
    public void onRelease() {
        h5Page = null;
        h5Alert = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_ALERT);
        filter.addAction(ALERT);
        filter.addAction(CONFIRM);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_ALERT.equals(action)) {
            showAlert(intent);
        } else if (ALERT.equals(action)) {
            try {
                alert(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }

        } else if (CONFIRM.equals(action)) {
            try {
                confirm(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        }
        return true;
    }

    private void alert(final H5Intent intent) throws JSONException {
        if (intent == null) {
            return;
        }
        JSONObject params = intent.getParam();
        if (params == null) {
            return;
        }

        String title = H5Utils.getString(params, "title");
        String message = H5Utils.getString(params, "message");
        String button = H5Utils.getString(params, "button");

        if (TextUtils.isEmpty(button)) {
            button = H5Environment.getResources().getString(
                    R.string.default_confirm);
        }
        String[] buttons = new String[]{
                button
        };

        H5Alert.H5AlertListener listener = new H5Alert.H5AlertListener() {
            @Override
            public void onClick(H5Alert alert, int index) {
                intent.sendBack(null);
                h5Alert = null;
            }

            @Override
            public void onCancel(H5Alert alert) {
                intent.sendBack(null);
                h5Alert = null;
            }
        };

        if (h5Alert != null) {
            h5Alert.dismiss();
            h5Alert = null;
        }

        Activity activity = (Activity) h5Page.getContext().getContext();
        h5Alert = new H5Alert(activity).cancelable(false).title(title)
                .message(message).buttons(buttons).listener(listener).show();
    }

    private void confirm(final H5Intent intent) throws JSONException {
        if (intent == null) {
            return;
        }
        JSONObject params = intent.getParam();
        if (params == null) {
            return;
        }
        String title = params.getString("title");
        String message = params.getString("message");

        String confirm = H5Utils.getString(params, "okButton");
        Resources resources = H5Environment.getResources();
        if (TextUtils.isEmpty(confirm)) {
            confirm = resources.getString(R.string.default_confirm);
        }
        String cancel = H5Utils.getString(params, "cancelButton");
        if (TextUtils.isEmpty(cancel)) {
            cancel = resources.getString(R.string.default_cancel);
        }

        String[] buttons = new String[]{
                confirm, cancel
        };
        H5Alert.H5AlertListener listener = new H5Alert.H5AlertListener() {
            @Override
            public void onClick(H5Alert alert, int index) {
                boolean confirmed = (index == H5Alert.INDEX_LEFT);
                JSONObject result = new JSONObject();
                try {
                    result.put("ok", confirmed);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }

                intent.sendBack(result);
                h5Alert = null;
            }

            @Override
            public void onCancel(H5Alert alert) {
                JSONObject result = new JSONObject();
                try {
                    result.put("ok", false);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
                intent.sendBack(result);
                h5Alert = null;
            }
        };

        if (h5Alert != null) {
            h5Alert.dismiss();
            h5Alert = null;
        }

        Activity activity = (Activity) h5Page.getContext().getContext();
        h5Alert = new H5Alert(activity).cancelable(false).title(title)
                .message(message).buttons(buttons).listener(listener).show();
    }

    private void showAlert(final H5Intent intent) {
        final JSONObject param = intent.getParam();
        if (param == null) {
            H5Log.e(TAG, "none params");
            return;
        }
        final String title = H5Utils.getString(param, "title", null);
        final String message = H5Utils.getString(param, "message", null);
        String[] buttonLabels = null;
        try {
            JSONArray buttons = H5Utils.getJSONArray(param, "buttons", null);
            if (buttons.length() > 0) {
                buttonLabels = new String[buttons.length()];
                for (int i = 0; i != buttons.length(); i++) {
                    buttonLabels[i] = buttons.getString(i);
                }
            }
        } catch (Exception e) {
            H5Log.e(TAG, e);
        }

        H5Alert.H5AlertListener listener = new H5Alert.H5AlertListener() {
            @Override
            public void onClick(H5Alert alert, int index) {
                alert.dismiss();
                intent.sendBack("index", index);
                h5Alert = null;
            }

            @Override
            public void onCancel(H5Alert alert) {
                intent.sendBack("index", H5Alert.INDEX_CANCEL);
                h5Alert = null;
            }
        };

        if (h5Alert != null) {
            h5Alert.dismiss();
            h5Alert = null;
        }

        Activity activity = (Activity) h5Page.getContext().getContext();
        h5Alert = new H5Alert(activity).cancelable(false).title(title)
                .message(message).buttons(buttonLabels).listener(listener)
                .show();
    }

}
