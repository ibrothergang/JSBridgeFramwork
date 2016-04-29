package com.xiaoying.h5core.apwebview;

import android.content.Context;

/**
 * similar role of that WebSettings plays in WebView
 *
 * @author xide.wf
 */
public interface APWebSettings {
    /**
     * Default cache usage mode. If the navigation type doesn't impose any
     * specific behavior, use cached resources when they are available
     * and not expired, otherwise load resources from the network.
     * Use with {@link #setCacheMode}.
     */
    public static final int LOAD_DEFAULT = -1;
    /**
     * Normal cache usage mode. Use with {@link #setCacheMode}.
     *
     * @deprecated This value is obsolete, as from API level
     * {@link android.os.Build.VERSION_CODES#HONEYCOMB} and onwards it has the
     * same effect as {@link #LOAD_DEFAULT}.
     */
    @Deprecated
    public static final int LOAD_NORMAL = 0;
    /**
     * Use cached resources when they are available, even if they have expired.
     * Otherwise load resources from the network.
     * Use with {@link #setCacheMode}.
     */
    public static final int LOAD_CACHE_ELSE_NETWORK = 1;
    /**
     * Don't use the cache, load from the network.
     * Use with {@link #setCacheMode}.
     */
    public static final int LOAD_NO_CACHE = 2;
    /**
     * Don't use the network, load from the cache.
     * Use with {@link #setCacheMode}.
     */
    public static final int LOAD_CACHE_ONLY = 3;

    public void setSupportZoom(boolean support);

    public boolean supportZoom();

    public boolean getMediaPlaybackRequiresUserGesture();

    public void setMediaPlaybackRequiresUserGesture(boolean require);

    public boolean getBuiltInZoomControls();

    public void setBuiltInZoomControls(boolean enabled);

    public boolean getDisplayZoomControls();

    public void setDisplayZoomControls(boolean enabled);

    public boolean getAllowFileAccess();

    public void setAllowFileAccess(boolean allow);

    public boolean getAllowContentAccess();

    public void setAllowContentAccess(boolean allow);

    public boolean getLoadWithOverviewMode();

    public void setLoadWithOverviewMode(boolean overview);

    public boolean getSaveFormData();

    public void setSaveFormData(boolean save);

    public boolean getSavePassword();

    public void setSavePassword(boolean save);

    public int getTextZoom();

    public void setTextZoom(int textZoom);

    public TextSize getTextSize();

    public void setTextSize(TextSize t);

    public ZoomDensity getDefaultZoom();

    public boolean getUseWideViewPort();

    public void setUseWideViewPort(boolean use);

    public void setSupportMultipleWindows(boolean support);

    public boolean supportMultipleWindows();

    public LayoutAlgorithm getLayoutAlgorithm();

    public void setLayoutAlgorithm(LayoutAlgorithm l);

    public String getStandardFontFamily();

    /* font related interfaces */
    public void setStandardFontFamily(String font);

    public String getFixedFontFamily();

    public void setFixedFontFamily(String font);

    public String getSansSerifFontFamily();

    public void setSansSerifFontFamily(String font);

    public String getSerifFontFamily();

    public void setSerifFontFamily(String font);

    public String getCursiveFontFamily();

    public void setCursiveFontFamily(String font);

    public String getFantasyFontFamily();

    public void setFantasyFontFamily(String font);

    public int getMinimumFontSize();

    public void setMinimumFontSize(int size);

    public int getMinimumLogicalFontSize();

    public void setMinimumLogicalFontSize(int size);

    public int getDefaultFontSize();

    public void setDefaultFontSize(int size);

    public int getDefaultFixedFontSize();

    public void setDefaultFixedFontSize(int size);

    public boolean getLoadsImagesAutomatically();

    public void setLoadsImagesAutomatically(boolean flag);

    public boolean getBlockNetworkImage();

    public void setBlockNetworkImage(boolean flag);

    public boolean getJavaScriptEnabled();

    public void setJavaScriptEnabled(boolean flag);

    public void setGeolocationDatabasePath(String databasePath);

    /* cache/data storage interfaces */
    public void setAppCacheEnabled(boolean flag);

    public void setAppCachePath(String appCachePath);

    public boolean getDatabaseEnabled();

    public void setDatabaseEnabled(boolean flag);

    public boolean getDomStorageEnabled();

    public void setDomStorageEnabled(boolean flag);

    public String getDatabasePath();

    public void setDatabasePath(String databasePath);

    public void setGeolocationEnabled(boolean flag);

    public boolean getAllowUniversalAccessFromFileURLs();

    public void setAllowUniversalAccessFromFileURLs(boolean flag);

    public boolean getAllowFileAccessFromFileURLs();

    public void setAllowFileAccessFromFileURLs(boolean flag);

    public PluginState getPluginState();

    public void setPluginState(PluginState state);

    public boolean getJavaScriptCanOpenWindowsAutomatically();

    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag);

    public String getDefaultTextEncodingName();

    public void setDefaultTextEncodingName(String encoding);

    public String getUserAgentString();

    public void setUserAgentString(String ua);

    public String getDefaultUserAgent(Context context);

    public void setNeedInitialFocus(boolean flag);

    public int getCacheMode();

    public void setCacheMode(int mode);

    public enum LayoutAlgorithm {
        NORMAL,
        /**
         * @deprecated This algorithm is now obsolete.
         */
        @Deprecated
        SINGLE_COLUMN,
        /**
         * @deprecated This algorithm is now obsolete.
         */
        @Deprecated
        NARROW_COLUMNS,
        TEXT_AUTOSIZING
    }

    /**
     * Enum for specifying the text size.
     * <ul>
     * <li>SMALLEST is 50%</li>
     * <li>SMALLER is 75%</li>
     * <li>NORMAL is 100%</li>
     * <li>LARGER is 150%</li>
     * <li>LARGEST is 200%</li>
     * </ul>
     *
     * @deprecated Use {@link APWebSettings#setTextZoom(int)} and {@link APWebSettings#getTextZoom()} instead.
     */
    public enum TextSize {
        SMALLEST(50),
        SMALLER(75),
        NORMAL(100),
        LARGER(150),
        LARGEST(200);

        int value;

        TextSize(int size) {
            value = size;
        }
    }

    public enum ZoomDensity {
        FAR(150),      // 240dpi
        MEDIUM(100),    // 160dpi
        CLOSE(75);     // 120dpi

        int value;

        ZoomDensity(int size) {
            value = size;
        }

        /**
         * @hide Only for use by WebViewProvider implementations
         */
        public int getValue() {
            return value;
        }
    }

    public enum RenderPriority {
        NORMAL,
        HIGH,
        LOW
    }

    /**
     * The plugin state effects how plugins are treated on a page. ON means
     * that any object will be loaded even if a plugin does not exist to handle
     * the content. ON_DEMAND means that if there is a plugin installed that
     * can handle the content, a placeholder is shown until the user clicks on
     * the placeholder. Once clicked, the plugin will be enabled on the page.
     * OFF means that all plugins will be turned off and any fallback content
     * will be used.
     */
    public enum PluginState {
        ON,
        ON_DEMAND,
        OFF
    }
}
