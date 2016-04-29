package com.xiaoying.h5core.apwebview;

import android.graphics.Bitmap;

/**
 * similar role to WebHistoryItem in android sdk
 *
 * @author xide.wf
 * @note please take care to implement clone()
 */
public interface APWebHistoryItem {
    public Bitmap getFavicon();

    public String getOriginalUrl();

    public String getTitle();

    public String getUrl();
}
