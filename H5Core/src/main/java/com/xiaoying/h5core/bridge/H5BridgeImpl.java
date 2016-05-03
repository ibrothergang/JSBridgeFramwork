package com.xiaoying.h5core.bridge;

import com.xiaoying.h5api.api.H5Bridge;
import com.xiaoying.h5api.api.H5CallBack;
import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.apwebview.APWebView;
import com.xiaoying.h5core.core.H5IntentImpl;
import com.xiaoying.h5core.env.H5Container;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class H5BridgeImpl implements H5Bridge {

    public static final String TAG = "H5BridgeImpl";

    private static final String INVOKE_JS = "JSBridge._invokeJS(%s)";

    private APWebView webView;

    private Map<String, H5CallBack> callPool;

    private BridgePolicy policy;

    private boolean released;

    public H5BridgeImpl(APWebView webView) {
        this.webView = webView;
        this.released = false;
        this.callPool = new HashMap<String, H5CallBack>();
    }

    public void onRelease() {
        released = true;
        webView = null;
        callPool.clear();
        callPool = null;
        policy = null;
    }

    @Override
    public void sendToNative(H5Intent intent) {
        if (intent == null || released) {
            return;
        }

        postNative(intent);
    }

    private void postNative(final H5Intent intent) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                executeNative(intent);
            }

        });
    }

    private void executeNative(H5Intent intent) {
        String intentId = intent.getId();
        boolean inPool = callPool.containsKey(intentId);
        JSONObject callParam = intent.getParam();

        if (inPool) {
            H5CallBack callback = callPool.remove(intentId);
            callback.onCallBack(callParam);
            return;
        }

        final String action = intent.getAction();
        if (policy != null && policy.shouldBan(action)) {
            H5Log.w(TAG, "JSAPI " + action + " is banned!");
            JSONObject result = new JSONObject();
            try {
                result.put("error", 4);
                result.put("errorMessage", "接口不存在");
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }

            intent.sendBack(result);
            return;
        }

        String paramStr = null;
        if (callParam != null) {
            paramStr = callParam.toString();
        }

        H5Log.d("h5_jsapi_call name={" + action + "} params={" + paramStr + "}");
        H5Container.getMesseger().sendIntent(intent);

        H5Intent.Error error = intent.getError();
        if (error == H5Intent.Error.NONE) {
            return;
        }

        H5Intent call = new H5IntentImpl(H5Plugin.H5_PAGE_JS_CALL);
        JSONObject param = new JSONObject();
        try {
            param.put("error", "" + error.ordinal());
            param.put("funcName", "" + action);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }
        call.setParam(param);
        H5Container.getMesseger().sendIntent(call);
        H5Log.e("error | h5_jsapi_error name={" + action + "} error={" + error
                + "}");
    }

    @Override
    public void sendToWeb(H5Intent intent) {
        if (intent == null || released) {
            return;
        }

        postWeb(intent);
    }

    private void postWeb(final H5Intent intent) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                executeWeb(intent);
            }

        });
    }

    private void executeWeb(H5Intent intent) {
        if (intent == null || webView == null) {
            return;
        }

        String intentId = intent.getId();
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        String type = intent.getType();
        boolean keep = false;
        if (intent instanceof H5IntentImpl) {
            keep = ((H5IntentImpl) intent).isKeep();
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put(H5Container.CLIENT_ID, intentId);
            jo.put(H5Container.FUNC, action);
            jo.put(H5Container.PARAM, param);
            jo.put(H5Container.MSG_TYPE, type);
            jo.put(H5Container.KEEP_CALLBACK, keep);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        String message = jo.toString();
        String joMsg = JSONObject.quote(message);

        String javascript = String.format(INVOKE_JS, joMsg);
        try {
            webView.loadUrl("javascript:" + javascript);
        } catch (Exception e) {
            H5Log.e(TAG, "loadUrl exception", e);
        }
    }

    @Override
    public void sendToWeb(String action, JSONObject param, H5CallBack callback) {
        if (released) {
            return;
        }
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(action);
        intent.setParam(param);
        intent.setType(H5Container.CALL);
        if (callback != null) {
            String clientId = intent.getId();
            callPool.put(clientId, callback);
        }

        sendToWeb(intent);
    }

    @Override
    public void setBridgePolicy(BridgePolicy policy) {
        this.policy = policy;
    }
}
