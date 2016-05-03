package com.xiaoying.h5core.config;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5PluginConfig;
import com.xiaoying.h5api.api.H5PluginManager;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android_mc on 16/5/3.
 */
public class H5PluginProxy implements H5Plugin {

    public static final String TAG = "H5PluginProxy";
    private Map<String, ProxyInfo> proxyInfoMap;
    private H5PluginManager pluginManager;

    public H5PluginProxy(List<H5PluginConfig> pluginConfigList, H5PluginManager paramH5PluginManager) {
        this.pluginManager = paramH5PluginManager;
        this.proxyInfoMap = new HashMap<>();
        if ((pluginConfigList == null) || (pluginConfigList.isEmpty())) {
            return;
        }
        Iterator pluginConfigIterator = pluginConfigList.iterator();
        while (pluginConfigIterator.hasNext()) {
            H5PluginConfig localH5PluginConfig = (H5PluginConfig) pluginConfigIterator.next();
            H5PluginProxy.ProxyInfo localProxyInfo = new H5PluginProxy.ProxyInfo(this);
            localProxyInfo.registered = false;
            localProxyInfo.plugin = null;
            localProxyInfo.pluginInfo = localH5PluginConfig;
            Iterator eventIterator = localH5PluginConfig.eventList.iterator();
            while (eventIterator.hasNext()) {
                String str = (String) eventIterator.next();
                this.proxyInfoMap.put(str, localProxyInfo);
            }
        }
    }

    public boolean handleIntent(H5Intent paramH5Event) {
        String actions = paramH5Event.getAction();
        H5PluginProxy.ProxyInfo localProxyInfo = this.proxyInfoMap.get(actions);
        if ((localProxyInfo.plugin != null) && (localProxyInfo.registered))
            return false;
        if (localProxyInfo.plugin == null)
            localProxyInfo.plugin = getPlugin(localProxyInfo.pluginInfo);
        boolean isHandle = false;
        if (localProxyInfo.plugin != null) {
            String str2 = localProxyInfo.pluginInfo.className;
            H5Log.d("H5PluginProxy", "[" + actions + "] handle pass " + str2);
            try {
                isHandle = localProxyInfo.plugin.handleIntent(paramH5Event);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            localProxyInfo.registered = true;
        }
        return isHandle;
    }

    private H5Plugin getPlugin(H5PluginConfig pluginConfig) {
        Class<?> classType = H5Utils.getClass(pluginConfig.className);

        try {
            Object object = classType.newInstance();
            if (object instanceof H5Plugin) {
                H5Plugin h5Plugin = (H5Plugin) object;
                pluginManager.register(h5Plugin);
                return h5Plugin;
            }
        } catch (IllegalAccessException e) {
            H5Log.e(TAG, "exception", e);
        } catch (InstantiationException e) {
            H5Log.e(TAG, "exception", e);
        }
        return null;
    }

    public boolean interceptIntent(H5Intent paramH5Event) {
        String actions = paramH5Event.getAction();
        H5PluginProxy.ProxyInfo localProxyInfo = this.proxyInfoMap.get(actions);
        if ((localProxyInfo.plugin != null) && (localProxyInfo.registered))
            return false;
        if (localProxyInfo.plugin == null)
            localProxyInfo.plugin = getPlugin(localProxyInfo.pluginInfo);
        boolean isIntercept = false;
        if (localProxyInfo.plugin != null) {
            String str2 = localProxyInfo.pluginInfo.className;
            H5Log.d("H5PluginProxy", "[" + actions + "] intercept pass " + str2);
            try {
                isIntercept = localProxyInfo.plugin.interceptIntent(paramH5Event);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }

            localProxyInfo.registered = isIntercept;
        }
        return isIntercept;
    }

    public void getFilter(H5IntentFilter paramH5EventFilter) {
        Iterator localIterator = this.proxyInfoMap.keySet().iterator();
        while (true) {
            if (!localIterator.hasNext())
                return;
            paramH5EventFilter.addAction((String) localIterator.next());
        }
    }

    public void onRelease() {
        this.proxyInfoMap.clear();
    }

    class ProxyInfo {
        public H5Plugin plugin;
        public H5PluginConfig pluginInfo;
        public boolean registered;

        ProxyInfo(H5PluginProxy proxy) {

        }
    }
}
