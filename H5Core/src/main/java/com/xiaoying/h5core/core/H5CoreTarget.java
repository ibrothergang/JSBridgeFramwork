package com.xiaoying.h5core.core;

import com.xiaoying.h5core.api.H5CoreNode;
import com.xiaoying.h5core.api.H5Data;
import com.xiaoying.h5core.api.H5Intent;
import com.xiaoying.h5core.api.H5PluginManager;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.manager.H5PluginManagerImpl;
import com.xiaoying.h5core.util.H5Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class H5CoreTarget implements H5CoreNode {

    public static final String TAG = "H5CoreTarget";
    protected H5Data h5Data;
    private H5PluginManager pluginManager;
    private H5CoreNode parent;
    private List<H5CoreNode> children;

    public H5CoreTarget() {
        parent = null;
        children = new ArrayList<H5CoreNode>();
        pluginManager = new H5PluginManagerImpl();
    }

    public H5CoreNode getParent() {
        return this.parent;
    }

    public void setParent(H5CoreNode parent) {
        if (parent == this.parent) {
            return;
        }

        // remove old relationship
        if (this.parent != null) {
            this.parent.removeChild(this);
        }

        this.parent = parent;

        // add new relationship
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    public synchronized boolean addChild(H5CoreNode child) {
        if (child == null) {
            return false;
        }

        for (H5CoreNode target : children) {
            if (target.equals(child)) {
                return false;
            }
        }

        children.add(child);
        child.setParent(this);
        return true;
    }

    public synchronized boolean removeChild(H5CoreNode child) {
        if (child == null) {
            return false;
        }

        Iterator<H5CoreNode> iterator = children.iterator();
        while (iterator.hasNext()) {
            H5CoreNode target = iterator.next();
            if (target.equals(child)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        if (pluginManager != null) {
            try {
                return pluginManager.interceptIntent(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
                return false;
            }

        } else {
            return false;
        }
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        if (pluginManager != null) {
            try {
                return pluginManager.handleIntent(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onRelease() {
        if (pluginManager != null) {
            pluginManager.onRelease();
            pluginManager = null;
        }
        h5Data = null;
    }

    @Override
    public H5PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public H5Data getData() {
        return this.h5Data;
    }

    @Override
    public void setData(H5Data data) {
        this.h5Data = data;
    }

    @Override
    public void sendIntent(String action, JSONObject param) {
        H5Log.d(TAG, "sendIntent action " + action);
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(action);
        intent.setParam(param);
        intent.setTarget(this);
        H5Container.getMesseger().sendIntent(intent);
    }
}
