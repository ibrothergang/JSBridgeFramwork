package com.xiaoying.h5core.config;

import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5PluginConfig;
import com.xiaoying.h5api.api.H5PluginManager;
import com.xiaoying.h5api.util.H5Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by android_mc on 16/5/3.
 */
public class H5PluginConfigManager {

    public static final String TAG = "H5PluginConfigManager";
    private static H5PluginConfigManager pluginConfigManager;
    private List<H5PluginConfig> pluginConfigList = Collections.synchronizedList(new ArrayList());

    public static H5PluginConfigManager getInstance() {
        try {
            if (pluginConfigManager == null)
                pluginConfigManager = new H5PluginConfigManager();
            return pluginConfigManager;
        } finally {
        }
    }

    public void addConfig(H5PluginConfig paramH5PluginConfig) {
        if ((paramH5PluginConfig == null) || (paramH5PluginConfig.configInvalid()))
            return;
        H5Log.d("H5PluginConfigManager", "addConfig " + paramH5PluginConfig.bundleName + "/" + paramH5PluginConfig.className + "/" + paramH5PluginConfig.eventList.toString());
        this.pluginConfigList.add(paramH5PluginConfig);
    }

    public H5Plugin createPlugin(String scope, H5PluginManager paramH5PluginManager) {
        if ((this.pluginConfigList == null) || (this.pluginConfigList.isEmpty()) || (paramH5PluginManager == null))
            return null;
        long l1 = System.currentTimeMillis();
        ArrayList<H5PluginConfig> localArrayList = new ArrayList<>();
        Iterator localIterator = this.pluginConfigList.iterator();
        while (true) {
            if (!localIterator.hasNext()) {
                if (!localArrayList.isEmpty())
                    break;
                return null;
            }
            H5PluginConfig localH5PluginConfig = (H5PluginConfig) localIterator.next();
            if (scope.equals(localH5PluginConfig.scope))
                localArrayList.add(localH5PluginConfig);
        }
        H5PluginProxy localH5PluginProxy = new H5PluginProxy(localArrayList, paramH5PluginManager);
        long l2 = System.currentTimeMillis() - l1;
        H5Log.d("H5PluginConfigManager", "createPlugin " + scope + " elapse " + l2);
        return localH5PluginProxy;
    }
}
