package com.xiaoying.h5core.apwebview;

import android.graphics.Bitmap;

public interface APWebHistoryItem {
    public Bitmap getFavicon();

    public String getOriginalUrl();

    public String getTitle();

    public String getUrl();
}
