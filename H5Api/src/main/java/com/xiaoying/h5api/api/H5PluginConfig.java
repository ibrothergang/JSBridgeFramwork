package com.xiaoying.h5api.api;

import android.text.TextUtils;

import com.xiaoying.h5api.util.H5Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by android_mc on 16/4/29.
 */
public class H5PluginConfig {
    public static final boolean DEFAULT_LAZY_INIT = true;
    public static final String TAG = "H5PluginConfig";
    public String bundleName;
    public String className;
    public List<String> eventList;
    public String scope;

    public H5PluginConfig() {
        init();
    }

    public H5PluginConfig(String bundleName, String className, String scope, String apis) {
        init();
        this.bundleName = bundleName;
        this.className = className;
        this.scope = scope;
        setEvents(apis);
    }

    private void init() {
        this.eventList = new ArrayList();
    }

    public boolean configInvalid() {
        return (TextUtils.isEmpty(this.bundleName)) || (TextUtils.isEmpty(this.className)) || (this.eventList == null) || (this.eventList.isEmpty());
    }

    public void setEvents(String apis) {
        if (TextUtils.isEmpty(apis)) {
            return;
        }
        Iterator localIterator = Arrays.asList(apis.split("\\|")).iterator();
        while (localIterator.hasNext()) {
            String str = ((String) localIterator.next()).trim();
            if (TextUtils.isEmpty(str)) {
                H5Log.d("H5PluginConfig", "invalid empty event");
            } else {
                H5Log.d("H5PluginConfig", "add event config " + str);
                this.eventList.add(str);
            }
        }
    }
}
