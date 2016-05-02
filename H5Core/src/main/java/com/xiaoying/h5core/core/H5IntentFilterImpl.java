package com.xiaoying.h5core.core;

import com.xiaoying.h5api.api.H5IntentFilter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class H5IntentFilterImpl implements H5IntentFilter {

    private Set<String> actions;

    public H5IntentFilterImpl() {
        actions = new HashSet<String>();
    }

    public void addAction(String action) {
        actions.add(action);
    }

    public Iterator<String> actionIterator() {
        return actions.iterator();
    }
}
