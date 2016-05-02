package com.xiaoying.h5api.api;

import java.util.Iterator;

public interface H5IntentFilter {

    public void addAction(String action);

    public Iterator<String> actionIterator();
}
