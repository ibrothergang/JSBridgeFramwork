package com.xiaoying.h5core.web;

import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.R;
import com.xiaoying.h5core.core.H5PageImpl;
import com.xiaoying.h5core.env.H5Container;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class H5JSInjector {
    public static final String TAG = "H5JSInjector";
    public static final boolean DBG = false;

    private Object injectorLock;
    private boolean enableScanJs;
    private HashMap<String, String> bridgeParams;
    private H5WebView webView;

    public H5JSInjector(H5PageImpl page) {
        this.webView = page.getWebView();
        this.injectorLock = new Object();
        this.enableScanJs = false;
        this.bridgeParams = new HashMap<String, String>();
        JSONObject params = H5Utils.toJSONObject(page.getParams());
        String scenario = H5Utils.getString(params, H5Param.LONG_BIZ_SCENARIO);
        if (H5Container.SCAN_APP.equals(scenario)) {
            enableScanJS(true);
        }
        setParamsToWebPage("startupParams", params.toString());
    }

    private void enableScanJS(boolean enable) {
        this.enableScanJs = enable;
    }

    public void reset() {
    }

    public boolean inject(boolean finished) {
        if (webView == null && finished) {
            H5Log.e(TAG, "invalid webview parameter!");
            return false;
        }

        synchronized (injectorLock) {
            long time = System.currentTimeMillis();
            loadBridge(webView);
            loadWeinre(webView);
            loadH5PerformanceMonitor(webView);
            if (finished) {
                // for this script depends on document event
                // on some platform may cause event not received bug.
                loadShare(webView);
            }

            if (finished && enableScanJs) {
                loadScan(webView);
            }
            loadDebug(webView);
            long delta = System.currentTimeMillis() - time;
            H5Log.d(TAG, "inject js total elapsed " + delta);
        }
        return true;
    }

    public void setParamsToWebPage(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            H5Log.e(TAG, "invalid js parameters!");
            return;
        }
        synchronized (injectorLock) {
            bridgeParams.put(key, value);
            H5Log.d(TAG, "setParamsToWebPage [key] " + key + " [value] " + value);
            webView.loadUrl("javascript:if(typeof JSBridge === 'object'){JSBridge."
                    + key + "=" + value + "'}");
        }
    }

    private void loadBridge(H5WebView webView) {
        long enterTime = System.currentTimeMillis();
        String bridgeStr = null;
        if (DBG) {
            try {
                bridgeStr = readFile("/sdcard/bridge_min.js");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            bridgeStr = H5Utils.readRaw(R.raw.bridge_min);
        }
        if (TextUtils.isEmpty(bridgeStr)) {
            bridgeStr = "";
            H5Log.d(TAG, "no bridge data defined!");
            return;
        }

        String startupStr = "JSBridge.startupParams=\'{startupParams}\'";
        String paramsStr = "";
        for (String key : bridgeParams.keySet()) {
            String value = bridgeParams.get(key);
            paramsStr += ";JSBridge." + key + "=" + value + ";";
        }

        // replace startup parameters
        if (!TextUtils.isEmpty(paramsStr)) {
            bridgeStr = bridgeStr.replace(startupStr, paramsStr);
        } else {
            H5Log.d(TAG, "no params data defined!");
        }

        H5Log.d(TAG, "bridgeStr " + bridgeStr);
        webView.loadUrl("javascript:" + bridgeStr);
        H5Log.d(TAG, "bridge data injected!");

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load bridge delta time " + deltaTime);
    }

    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        reader.close();
        return stringBuilder.toString();
    }

    private void loadWeinre(H5WebView webView) {
        if (!H5Utils.isDebugable()) {
            H5Log.d(TAG, "weinre only work for debug package.");
            return;
        }
        Context context = webView.getContext();
        boolean enabled = H5Utils.getConfigBoolean(context, "weinre_enable");
        if (!enabled) {
            H5Log.d(TAG, "weinre feature not enabled");
            return;
        }

        long enterTime = System.currentTimeMillis();
        String server = H5Utils.getConfigString(context, "weinre_server");
        String portStr = H5Utils.getConfigString(context, "weinre_port");
        int port = 0;
        try {
            port = Integer.valueOf(portStr);
        } catch (Exception e) {
            H5Log.e(TAG, "load weinre exception", e);
            return;
        }
        if (TextUtils.isEmpty(server) || port <= 0) {
            H5Log.w(TAG, "invalid weinre settings!");
            return;
        }
        String url = "http://" + server + ":" + port
                + "/target/target-script-min.js:clientIP";
        String data = "(function(){var js=document.createElement('script');js.src='"
                + url + "';document.body.appendChild(js);})();";
        webView.loadUrl("javascript:" + data);
        H5Log.d(TAG, "weinre data injected!");

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load weinre delta time " + deltaTime);
    }

    private void loadDebug(H5WebView webView) {
        long enterTime = System.currentTimeMillis();

        String h5_DynamicScript = H5Environment.getConfig("h5_DynamicScript");
        // h5_DynamicScript =
        if (TextUtils.isEmpty(h5_DynamicScript)) {
            H5Log.d(TAG, "no config found for dynamic script");
            return;
        }
        String appendjs = "var jsref=document.createElement('script'); jsref.setAttribute(\"type\",\"text/javascript\");jsref.setAttribute(\"src\", \""
                + h5_DynamicScript
                + "\");document.getElementsByTagName(\"head\")[0].appendChild(jsref)";
        webView.loadUrl("javascript:" + appendjs);

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load debug delta time " + deltaTime);
    }

    private void loadShare(H5WebView webView) {
        String shareStr = H5Utils.readRaw(R.raw.share_min);
        if (TextUtils.isEmpty(shareStr)) {
            return;
        }
        webView.loadUrl("javascript:" + shareStr);
    }

    private void loadScan(H5WebView webView) {
        String scanStr = H5Utils.readRaw(R.raw.scan_min);
        if (TextUtils.isEmpty(scanStr)) {
            return;
        }
        webView.loadUrl("javascript:" + scanStr);
    }

    private void loadH5PerformanceMonitor(H5WebView webView) {
        long enterTime = System.currentTimeMillis();
        String bridgeStr = null;
        bridgeStr = H5Utils.readRaw(R.raw.h5performance);
        if (TextUtils.isEmpty(bridgeStr)) {
            bridgeStr = "";
            H5Log.d(TAG, "no H5PerformanceMonitor data defined!");
            return;
        }
        H5Log.d(TAG, "H5PerformanceMonitor " + bridgeStr);
        webView.loadUrl("javascript:" + bridgeStr);
        H5Log.d(TAG, "H5PerformanceMonitor data injected!");

        long deltaTime = System.currentTimeMillis() - enterTime;
        H5Log.d(TAG, "load H5PerformanceMonitor delta time " + deltaTime);
    }

}
