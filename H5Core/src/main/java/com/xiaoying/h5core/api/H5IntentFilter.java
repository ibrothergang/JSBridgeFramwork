package com.xiaoying.h5core.api;

import java.util.Iterator;

public interface H5IntentFilter {

    public void addAction(String action);

    public Iterator<String> actionIterator();
}
