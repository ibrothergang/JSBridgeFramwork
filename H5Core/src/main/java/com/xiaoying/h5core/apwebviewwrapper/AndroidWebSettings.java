package com.xiaoying.h5core.apwebviewwrapper;

import com.xiaoying.h5core.apwebview.APWebSettings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

final class AndroidWebSettings implements APWebSettings {
    private WebSettings webSettings;

    AndroidWebSettings(WebSettings androidWebSettings) {
        webSettings = androidWebSettings;
    }

    @Override
    public void setSupportZoom(boolean support) {
        webSettings.setSupportZoom(support);
    }

    @Override
    public boolean supportZoom() {
        return webSettings.supportZoom();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean getMediaPlaybackRequiresUserGesture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return webSettings.getMediaPlaybackRequiresUserGesture();
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(require);
        }
    }

    @Override
    public boolean getBuiltInZoomControls() {
        return webSettings.getBuiltInZoomControls();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setBuiltInZoomControls(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(enabled);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean getDisplayZoomControls() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return webSettings.getDisplayZoomControls();
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setDisplayZoomControls(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(enabled);
        }
    }

    @Override
    public boolean getAllowFileAccess() {
        return webSettings.getAllowFileAccess();
    }

    @Override
    public void setAllowFileAccess(boolean allow) {
        webSettings.setAllowFileAccess(allow);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean getAllowContentAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return webSettings.getAllowContentAccess();
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setAllowContentAccess(boolean allow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setAllowContentAccess(allow);
        }
    }

    @Override
    public boolean getLoadWithOverviewMode() {
        return webSettings.getLoadWithOverviewMode();
    }

    @Override
    public void setLoadWithOverviewMode(boolean overview) {
        webSettings.setLoadWithOverviewMode(overview);
    }

    @Override
    public boolean getSaveFormData() {
        return webSettings.getSaveFormData();
    }

    @Override
    public void setSaveFormData(boolean save) {
        webSettings.setSaveFormData(save);
    }

    @Override
    public boolean getSavePassword() {
        return webSettings.getSavePassword();
    }

    @Override
    public void setSavePassword(boolean save) {
        webSettings.setSaveFormData(save);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public int getTextZoom() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return webSettings.getTextZoom();
        } else {
            return 10;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void setTextZoom(int textZoom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            webSettings.setTextZoom(textZoom);
        }
    }

    @Override
    public TextSize getTextSize() {
        return TextSize.valueOf(webSettings.getTextSize().name());
    }

    @Override
    public void setTextSize(TextSize t) {
        WebSettings.TextSize textSize = null;
        switch (t) {
            case LARGER:
                textSize = WebSettings.TextSize.LARGER;
                break;
            case LARGEST:
                textSize = WebSettings.TextSize.LARGEST;
                break;
            case NORMAL:
                textSize = WebSettings.TextSize.NORMAL;
                break;
            case SMALLER:
                textSize = WebSettings.TextSize.SMALLER;
                break;
            case SMALLEST:
                textSize = WebSettings.TextSize.SMALLEST;
                break;
            default:
                break;
        }
        if (textSize != null) {
            webSettings.setTextSize(textSize);
        }
    }

    @Override
    public ZoomDensity getDefaultZoom() {
        return ZoomDensity.valueOf(webSettings.getDefaultZoom().name());
    }

    @Override
    public boolean getUseWideViewPort() {
        return webSettings.getUseWideViewPort();
    }

    @Override
    public void setUseWideViewPort(boolean use) {
        webSettings.setUseWideViewPort(use);
    }

    @Override
    public void setSupportMultipleWindows(boolean support) {
        webSettings.setSupportMultipleWindows(support);
    }

    @Override
    public boolean supportMultipleWindows() {
        return webSettings.supportMultipleWindows();
    }

    @Override
    public LayoutAlgorithm getLayoutAlgorithm() {
        return LayoutAlgorithm.valueOf(webSettings.getLayoutAlgorithm().name());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setLayoutAlgorithm(LayoutAlgorithm l) {
        WebSettings.LayoutAlgorithm androidL = null;
        switch (l) {
            case NARROW_COLUMNS:
                androidL = WebSettings.LayoutAlgorithm.NARROW_COLUMNS;
                break;
            case NORMAL:
                androidL = WebSettings.LayoutAlgorithm.NORMAL;
                break;
            case SINGLE_COLUMN:
                androidL = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
                break;
            case TEXT_AUTOSIZING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    androidL = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;
                }
                break;
            default:
                break;
        }
        if (androidL != null) {
            webSettings.setLayoutAlgorithm(androidL);
        }

    }

    @Override
    public String getStandardFontFamily() {
        return webSettings.getStandardFontFamily();
    }

    @Override
    public void setStandardFontFamily(String font) {
        webSettings.setStandardFontFamily(font);
    }

    @Override
    public String getFixedFontFamily() {
        return webSettings.getFixedFontFamily();
    }

    @Override
    public void setFixedFontFamily(String font) {
        webSettings.setFixedFontFamily(font);
    }

    @Override
    public String getSansSerifFontFamily() {
        return webSettings.getSansSerifFontFamily();
    }

    @Override
    public void setSansSerifFontFamily(String font) {
        webSettings.setSansSerifFontFamily(font);
    }

    @Override
    public String getSerifFontFamily() {
        return webSettings.getSerifFontFamily();
    }

    @Override
    public void setSerifFontFamily(String font) {
        webSettings.setSerifFontFamily(font);
    }

    @Override
    public String getCursiveFontFamily() {
        return webSettings.getCursiveFontFamily();
    }

    @Override
    public void setCursiveFontFamily(String font) {
        webSettings.setCursiveFontFamily(font);
    }

    @Override
    public String getFantasyFontFamily() {
        return webSettings.getFantasyFontFamily();
    }

    @Override
    public void setFantasyFontFamily(String font) {
        webSettings.setFantasyFontFamily(font);
    }

    @Override
    public int getMinimumFontSize() {
        return webSettings.getMinimumFontSize();
    }

    @Override
    public void setMinimumFontSize(int size) {
        webSettings.setMinimumFontSize(size);
    }

    @Override
    public int getMinimumLogicalFontSize() {
        return webSettings.getMinimumLogicalFontSize();
    }

    @Override
    public void setMinimumLogicalFontSize(int size) {
        webSettings.setMinimumLogicalFontSize(size);
    }

    @Override
    public int getDefaultFontSize() {
        return webSettings.getDefaultFontSize();
    }

    @Override
    public void setDefaultFontSize(int size) {
        webSettings.setDefaultFontSize(size);
    }

    @Override
    public int getDefaultFixedFontSize() {
        return webSettings.getDefaultFixedFontSize();
    }

    @Override
    public void setDefaultFixedFontSize(int size) {
        webSettings.setDefaultFixedFontSize(size);
    }

    @Override
    public boolean getLoadsImagesAutomatically() {
        return webSettings.getLoadsImagesAutomatically();
    }

    @Override
    public void setLoadsImagesAutomatically(boolean flag) {
        webSettings.setLoadsImagesAutomatically(flag);
    }

    @Override
    public boolean getBlockNetworkImage() {
        return webSettings.getBlockNetworkImage();
    }

    @Override
    public void setBlockNetworkImage(boolean flag) {
        webSettings.setBlockNetworkImage(flag);
    }

    @Override
    public boolean getJavaScriptEnabled() {
        return webSettings.getJavaScriptEnabled();
    }

    @Override
    public void setJavaScriptEnabled(boolean flag) {
        webSettings.setJavaScriptEnabled(flag);
    }

    @Override
    public void setGeolocationDatabasePath(String databasePath) {
        webSettings.setGeolocationDatabasePath(databasePath);
    }

    @Override
    public void setAppCacheEnabled(boolean flag) {
        webSettings.setAppCacheEnabled(flag);
    }

    @Override
    public void setAppCachePath(String appCachePath) {
        webSettings.setAppCachePath(appCachePath);
    }

    @Override
    public boolean getDatabaseEnabled() {
        return webSettings.getDatabaseEnabled();
    }

    @Override
    public void setDatabaseEnabled(boolean flag) {
        webSettings.setDatabaseEnabled(flag);
    }

    @Override
    public boolean getDomStorageEnabled() {
        return webSettings.getDomStorageEnabled();
    }

    @Override
    public void setDomStorageEnabled(boolean flag) {
        webSettings.setDomStorageEnabled(flag);
    }

    @Override
    public String getDatabasePath() {
        return webSettings.getDatabasePath();
    }

    @Override
    public void setDatabasePath(String databasePath) {
        webSettings.setDatabasePath(databasePath);
    }

    @Override
    public void setGeolocationEnabled(boolean flag) {
        webSettings.setGeolocationEnabled(flag);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean getAllowUniversalAccessFromFileURLs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return webSettings.getAllowUniversalAccessFromFileURLs();
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(flag);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean getAllowFileAccessFromFileURLs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return webSettings.getAllowFileAccessFromFileURLs();
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setAllowFileAccessFromFileURLs(boolean flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(flag);
        }
    }

    @Override
    public PluginState getPluginState() {
        return PluginState.valueOf(webSettings.getPluginState().name());
    }

    @Override
    public void setPluginState(PluginState state) {
        switch (state) {
            case OFF:
                webSettings
                        .setPluginState(WebSettings.PluginState.OFF);
                break;
            case ON:
                webSettings
                        .setPluginState(WebSettings.PluginState.ON);
                break;
            case ON_DEMAND:
                webSettings
                        .setPluginState(WebSettings.PluginState.ON_DEMAND);
            default:
                break;
        }
    }

    @Override
    public boolean getJavaScriptCanOpenWindowsAutomatically() {
        return webSettings.getJavaScriptCanOpenWindowsAutomatically();
    }

    @Override
    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        webSettings.setJavaScriptCanOpenWindowsAutomatically(flag);
    }

    @Override
    public String getDefaultTextEncodingName() {
        return webSettings.getDefaultTextEncodingName();
    }

    @Override
    public void setDefaultTextEncodingName(String encoding) {
        webSettings.setDefaultTextEncodingName(encoding);
    }

    @Override
    public String getUserAgentString() {
        return webSettings.getUserAgentString();
    }

    @Override
    public void setUserAgentString(String ua) {
        webSettings.setUserAgentString(ua);
    }

    @Override
    public String getDefaultUserAgent(Context context) {
        // TODO cairq
        return null;
    }

    @Override
    public void setNeedInitialFocus(boolean flag) {
        webSettings.setNeedInitialFocus(flag);
    }

    @Override
    public int getCacheMode() {
        return webSettings.getCacheMode();
    }

    @Override
    public void setCacheMode(int mode) {
        webSettings.setCacheMode(mode);
    }
}
