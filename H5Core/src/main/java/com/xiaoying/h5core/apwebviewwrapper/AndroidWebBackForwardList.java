package com.xiaoying.h5core.apwebviewwrapper;


import android.webkit.WebBackForwardList;

import com.xiaoying.h5core.apwebview.APWebBackForwardList;
import com.xiaoying.h5core.apwebview.APWebHistoryItem;

/**
 * Created by Administrator on 2014/12/9.
 */
public class AndroidWebBackForwardList implements APWebBackForwardList {
    private WebBackForwardList mList;

    public AndroidWebBackForwardList(WebBackForwardList list) {
        mList = list;
    }

    public int getCurrentIndex() {
        return mList.getCurrentIndex();
    }

    public APWebHistoryItem getCurrentItem() {
        return DynamicProxy.dynamicProxy(APWebHistoryItem.class, mList.getCurrentItem());
    }

    public APWebHistoryItem getItemAtIndex(int i) {
        return DynamicProxy.dynamicProxy(APWebHistoryItem.class, mList.getItemAtIndex(i));
    }

    public int getSize() {
        return mList.getSize();
    }
}
