package com.xiaoying.h5core.apwebview;

public interface APWebBackForwardList {
    public int getCurrentIndex();

    public APWebHistoryItem getCurrentItem();

    public APWebHistoryItem getItemAtIndex(int index);

    public int getSize();
}
