package com.xiaoying.h5core.apwebview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5core.apwebviewwrapper.AndroidWebView;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class GlueWebViewFactory {
    private static final String TAG = "GlueWebViewFactory";
    private static final boolean BUILD_WITH_WALLET = true;
    private static GlueWebViewFactory sInstance = null;

    private GlueWebViewFactory() {

    }

    public static GlueWebViewFactory instance() {
        if (sInstance == null) {
            sInstance = new GlueWebViewFactory();
        }
        return sInstance;
    }

    private static HashMap<String, WebViewType> buildWebViewRules(
            final String ruleString) {
        HashMap<String, WebViewType> rules = new HashMap<String, WebViewType>();
        if (!TextUtils.isEmpty(ruleString)) {
            try {
                JSONObject ruleInJson = H5Utils.parseObject(ruleString);
                if (ruleInJson != null) {
                    Iterator sets = ruleInJson.keys();
                    while (sets.hasNext()) {
                        try {
                            String key = (String) sets.next();
                            rules.put(key, WebViewType
                                    .valueOf((String) ruleInJson.get(key)));
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception globalException) {
                globalException.printStackTrace();
                H5Log.e(TAG, "parseObject failed! rules in string format: "
                        + ruleString);
            }
        }
        return rules;
    }

    public GlueWebView createWebView(final String bizType, final Context context) {
        if (!BUILD_WITH_WALLET) {
            return null;
        }

//
        UcWebService ucWebService = null;
//        if (ctx != null) {
//            ucWebService = ctx.getExtServiceByInterface(UcWebService.class
//                    .getName());
//        }

        if (!Build.CPU_ABI.contains("armeabi")) {
            // when create webview on architecture other than arm, use system build in webview
            return createWebView_l(ucWebService, context, WebViewType.SYSTEM_BUILD_IN);
        }

        // for UC web view verify only
        if (ucWebService != null
                && TextUtils.equals("uc_dev", bizType)) {
            H5Log.d(TAG, "UC developer verify");
            return createWebView_l(ucWebService, context, WebViewType.THIRD_PARTY);
        }

        WebViewType webViewType = WebViewType.SYSTEM_BUILD_IN;
//        final boolean enableExternalWebView = WalletConfigManager.getSwitchVal(
//                WalletConfigManager.KEY_H5_MULTIPLE_WEBVIEW,
//                WalletConfigManager.KEY_ENABLE_EXTERNAL_WEBVIEW, false);
//
//        final String webViewUsageRule = WalletConfigManager.getConfigVal(
//                WalletConfigManager.KEY_H5_MULTIPLE_WEBVIEW,
//                WalletConfigManager.KEY_WEBVIEW_USAGE_RULE, "");
//
//        H5Log.d(TAG, "enableExternalWebView: " + enableExternalWebView + " webViewUsageRule: "
//                + webViewUsageRule);
//
//        if (enableExternalWebView && ucWebService != null
//                && !TextUtils.isEmpty(bizType)) {
//            final HashMap<String, WebViewType> rulesInMap = buildWebViewRules(webViewUsageRule);
//            if (rulesInMap != null && !rulesInMap.isEmpty()) {
//                WebViewType tmpType = rulesInMap.get(bizType);
//                if (tmpType != null) {
//                    H5Log.d(TAG, "got web view type from config service: " + tmpType);
//                    webViewType = tmpType;
//                }
//            }
//        }
        return createWebView_l(ucWebService, context, webViewType);
    }

    private GlueWebView createWebView_l(final UcWebService ucWebService, final Context context,
                                        WebViewType webViewType) {
        JSONObject param = new JSONObject();
        GlueWebView targetWebView = null;
        if (ucWebService != null && webViewType == WebViewType.THIRD_PARTY) {
            try {
                targetWebView = ucWebService.createWebView(context);
            } catch (Exception globalExcepion) {
                globalExcepion.printStackTrace();
                H5Log.d(TAG, "create uc web view failed, switch to use android web view");
                try {
                    param.put("downgradeToBuildInWebView", true);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }

                targetWebView = new AndroidWebView(context);
            }
        } else if (webViewType == WebViewType.SYSTEM_BUILD_IN) {
            targetWebView = new AndroidWebView(context);
        }

        final String webViewTypeString = GlueWebView.TagBuilder.build(targetWebView);
        try {
            param.put("webViewType", webViewTypeString);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }
        H5Log.dWithDeviceInfo(TAG, "webViewType: " + webViewTypeString);

        if (H5Container.getService() != null
                && H5Container.getService().getTopSession() != null
                && H5Container.getService().getTopSession().getTopPage() != null) {
            H5Page page = H5Container.getService().getTopSession().getTopPage();
            page.sendIntent(H5Container.ACTION_CREATE_WEBVIEW, param);
        }
        return targetWebView;
    }

}
