/**
 *
 */
package com.xiaoying.h5core.apwebview;

import android.content.Context;

/**
 * service to export the capabilities provided by UC web view sdk, such as:
 * <p/>
 * 1) create a webview
 *
 * @author xide.wf
 */
public abstract class UcWebService {
    /**
     * create a web view that ready for use in the similar way as
     * android build-in web view
     *
     * @return the instance of concrete web view
     */
    public abstract GlueWebView createWebView(final Context context) throws IllegalArgumentException;
}
