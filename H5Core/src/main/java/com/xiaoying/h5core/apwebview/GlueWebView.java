package com.xiaoying.h5core.apwebview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public abstract class GlueWebView extends FrameLayout implements APWebViewCtrl {
    public GlueWebView(Context context) {
        super(context);
    }

    public GlueWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GlueWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * to notify this glue web view has been added into parent layout/WebViewCtrl
     *
     * @param parent
     */
    public abstract void onCompositedToParent(APWebViewCtrl parent);

    public abstract View getUnderlyingWebView();

    public abstract int getVersion();

    public abstract WebViewType getType();

    public abstract boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                         int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                         int maxOverScrollY, boolean isTouchEvent);

    public static final class TagBuilder {
        public static final String build(GlueWebView webView) {
            if (webView == null) {
                return "(Null webview)";
            }
            final String version = String.format("%s.%s", Version.getMajor(webView.getVersion()),
                    Version.getMinor(webView.getVersion()));
            return "(WebView type:" + webView.getType() + ", version: "
                    + version + ")";
        }
    }
}
