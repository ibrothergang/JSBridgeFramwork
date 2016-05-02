/**
 *
 */

package com.xiaoying.h5core.core;

import com.xiaoying.h5core.api.H5Bridge;
import com.xiaoying.h5core.api.H5CallBack;
import com.xiaoying.h5core.api.H5CoreNode;
import com.xiaoying.h5core.api.H5Intent;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.util.H5Log;

import org.json.JSONException;
import org.json.JSONObject;

public class H5IntentImpl implements H5Intent {

    public static final String TAG = "H5Intent";

    private String action;
    private H5CoreNode target;
    private String intentId;
    private String type;
    private boolean canceled;
    private JSONObject param;
    private H5CallBack callBack;
    private Error error;
    private boolean keep;

    private H5Bridge bridge;

    public H5IntentImpl() {
        this(null);
    }

    public H5IntentImpl(String action) {
        this.error = Error.NONE;
        this.action = action;
        this.intentId = "" + System.currentTimeMillis();
        this.canceled = false;
    }

    @Override
    public final String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public final boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public final void cancel() {
        this.canceled = true;
    }

    @Override
    public final H5CoreNode getTarget() {
        return this.target;
    }

    @Override
    public final void setTarget(H5CoreNode target) {
        this.target = target;
    }

    @Override
    public final String getId() {
        return this.intentId;
    }

    public void setId(String id) {
        this.intentId = id;
    }

    @Override
    public JSONObject getParam() {
        return this.param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if (H5Container.CALL.equals(type)) {
            this.type = H5Container.CALL;
        } else {
            this.type = H5Container.CALL_BACK;
        }
    }

    @Override
    public H5Bridge getBridge() {
        return this.bridge;
    }

    public void setBridge(H5Bridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public H5CallBack getCallBack() {
        return this.callBack;
    }

    public void setCallBack(H5CallBack callBack) {
        this.callBack = callBack;
    }

    private boolean sendBack(JSONObject param, boolean keep) {
        if (bridge == null || !(H5Container.CALL.equals(type))) {
            return false;
        }

        H5IntentImpl back = new H5IntentImpl();
        back.action = this.action;
        back.bridge = this.bridge;
        back.intentId = this.intentId;
        back.keep = keep;
        back.param = param;
        back.type = H5Container.CALL_BACK;

        bridge.sendToWeb(back);
        return true;
    }

    @Override
    public boolean sendBack(JSONObject param) {
        return sendBack(param, keep);
    }

    @Override
    public boolean sendBack(String key, Object value) {
        JSONObject param = new JSONObject();
        try {
            param.put(key, value);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        return sendBack(param);
    }

    @Override
    public boolean keepSend(JSONObject param) {
        return sendBack(param, true);
    }

    @Override
    public boolean keepSend(String key, Object value) {
        JSONObject param = new JSONObject();
        try {
            param.put(key, value);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        return keepSend(param);
    }

    @Override
    public boolean sendError(Error code) {
        this.error = code;
        H5Log.w(TAG, "sendError " + error + " [action] " + action);
        this.cancel();
        JSONObject data = new JSONObject();
        try {
            data.put("errorMessage", getErrorMsg(code));
            data.put("error", code.ordinal());
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }
        return sendBack(data);
    }

    public Error getError() {
        return this.error;
    }

    private String getErrorMsg(Error code) {
        switch (code) {
            case NOT_FOUND:
                return "not implemented!";
            case INVALID_PARAM:
                return "invalid parameter!";
            case UNKNOWN_ERROR:
                return "unknown error!";
            case FORBIDDEN:
                return "forbidden!";
            default:
                return "none error occured!";
        }

    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keepSend) {
        this.keep = keepSend;
    }
}
