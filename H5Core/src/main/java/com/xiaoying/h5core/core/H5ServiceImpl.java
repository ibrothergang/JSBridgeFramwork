package com.xiaoying.h5core.core;

import com.xiaoying.h5api.api.H5Bundle;
import com.xiaoying.h5api.api.H5Context;
import com.xiaoying.h5api.api.H5Listener;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5PluginConfig;
import com.xiaoying.h5api.api.H5PluginManager;
import com.xiaoying.h5api.api.H5Service;
import com.xiaoying.h5api.api.H5Session;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5core.config.H5PluginConfigManager;
import com.xiaoying.h5core.data.H5SecureData;
import com.xiaoying.h5core.plugin.H5ClipboardPlugin;
import com.xiaoying.h5core.plugin.H5CookiePlugin;
import com.xiaoying.h5core.plugin.H5DownloadPlugin;
import com.xiaoying.h5core.plugin.H5NetworkAnalysisPlugin;
import com.xiaoying.h5core.plugin.H5NetworkPlugin;
import com.xiaoying.h5core.plugin.H5SecurePlugin;
import com.xiaoying.h5core.plugin.H5ServicePlugin;
import com.xiaoying.h5core.plugin.H5SystemPlugin;
import com.xiaoying.h5core.ui.H5Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class H5ServiceImpl extends H5CoreTarget implements H5Service {

    public static final String TAG = "H5Service";

    private static int sessionId = 0;

    private Stack<H5Session> sessions;

    private Map<String, List<H5Listener>> ssListeners;

    public H5ServiceImpl() {
        ssListeners = new HashMap<String, List<H5Listener>>();
        h5Data = new H5SecureData();
        sessions = new Stack<H5Session>();

        initPlugins();
    }

    public static synchronized final String obtainSessionId() {
        int currentSessionId = sessionId++;
        return "h5session" + currentSessionId;
    }


    @Override
    public void addPluginConfig(H5PluginConfig paramH5PluginConfig) {
        H5PluginConfigManager.getInstance().addConfig(paramH5PluginConfig);
    }

    private void initPlugins() {
        H5PluginManager pluginManager = getPluginManager();

        pluginManager.register(new H5ServicePlugin());
        pluginManager.register(new H5NetworkPlugin());
        pluginManager.register(new H5SystemPlugin());
        pluginManager.register(new H5SecurePlugin());
        pluginManager.register(new H5CookiePlugin());
        pluginManager.register(new H5ClipboardPlugin());
        pluginManager.register(new H5DownloadPlugin());
        pluginManager.register(new H5DefaultPlugin());
        pluginManager.register(new H5NetworkAnalysisPlugin());

        H5Plugin localH5Plugin = H5PluginConfigManager.getInstance().createPlugin("service", pluginManager);
        if (localH5Plugin != null)
            pluginManager.register(localH5Plugin);
    }

    @Override
    public void onRelease() {
        super.onRelease();
    }

    @Override
    public H5Page createPage(H5Context h5Context, H5Bundle bundle) {
        if (h5Context == null || h5Context.getContext() == null) {
            H5Log.e(TAG, "invalid h5 context!");
            return null;
        }

        if (!(h5Context.getContext() instanceof Activity)) {
            H5Log.e(TAG, "not activity context!");
            return null;
        }

        Bundle params = null;
        if (bundle != null) {
            params = bundle.getParams();
            String sessionId = H5Environment.getSessionId(h5Context, params);
            H5Log.d(TAG, "createPage for session " + sessionId);
            List<H5Listener> listeners = bundle.getListeners();
            if (listeners != null && !listeners.isEmpty()) {
                ssListeners.put(sessionId, listeners);
            }
        }

        Activity activity = (Activity) h5Context.getContext();
        H5Page page = new H5PageImpl(activity, params);
        return page;
    }

    @Override
    public boolean startPage(H5Context h5Context, H5Bundle bundle) {
        Context context = null;
        if (h5Context != null && h5Context.getContext() != null) {
            context = h5Context.getContext();
        } else {
            context = H5Environment.getContext();
        }
        Intent intent = new Intent(context, H5Activity.class);

        if (bundle != null) {
            Bundle params = bundle.getParams();
            String sessionId = H5Environment.getSessionId(h5Context, params);
            H5Log.d(TAG, "startPage for session " + sessionId);
            List<H5Listener> listeners = bundle.getListeners();
            if (listeners != null && !listeners.isEmpty()) {
                ssListeners.put(sessionId, listeners);
            }
            intent.putExtras(params);
        }

        H5Environment.startActivity(h5Context, intent);
        return true;
    }

    public boolean addSession(H5Session session) {
        if (session == null) {
            return false;
        }

        synchronized (sessions) {
            for (H5Session s : sessions) {
                if (s.equals(session)) {
                    return false;
                }
            }

            session.setParent(this);
            sessions.add(session);
        }
        return true;
    }

    public H5Session getSession(String sessionId) {
        H5Session h5Session = null;
        synchronized (sessions) {
            for (H5Session session : sessions) {
                String id = session.getId();
                if (sessionId.equals(id)) {
                    h5Session = session;
                    break;
                }
            }
        }

        // create session if not found
        if (h5Session == null) {
            h5Session = new H5SessionImpl();
            h5Session.setId(sessionId);
            addSession(h5Session);
        }

        if (ssListeners.containsKey(sessionId)) {
            List<H5Listener> listeners = ssListeners.remove(sessionId);
            for (H5Listener l : listeners) {
                h5Session.addListener(l);
            }
        }
        return h5Session;
    }

    public boolean removeSession(String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            return false;
        }

        synchronized (sessions) {
            Iterator<H5Session> iterator = sessions.iterator();
            while (iterator.hasNext()) {
                H5Session s = iterator.next();
                if (sessionId.equals(s.getId())) {
                    ssListeners.remove(sessionId);
                    iterator.remove();
                    s.setParent(null);
                    s.onRelease();
                    return true;
                }
            }
        }
        return false;
    }

    public H5Session getTopSession() {
        synchronized (sessions) {
            if (sessions.isEmpty()) {
                return null;
            }
            H5Session top = sessions.peek();
            return top;
        }
    }

    @Override
    public boolean exitService() {
        for (H5Session session : sessions) {
            session.exitSession();
        }
        return true;
    }

    ;

}
