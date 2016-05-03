package com.xiaoying.h5core.core;

import com.xiaoying.h5api.api.H5Listener;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.api.H5PluginManager;
import com.xiaoying.h5api.api.H5Scenario;
import com.xiaoying.h5api.api.H5Session;
import com.xiaoying.h5api.api.H5WebProvider;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5core.config.H5PluginConfigManager;
import com.xiaoying.h5core.data.H5MemData;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.plugin.H5SessionPlugin;

import android.os.Bundle;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class H5SessionImpl extends H5CoreTarget implements H5Session, H5WebProvider {

    public static final String TAG = "H5Session";

    private String sessionId;
    private H5Scenario h5Scenario;
    private Stack<H5Page> pages;
    private boolean exited;
    private H5WebProvider provider;

    private List<H5Listener> listener;

    public H5SessionImpl() {
        this.exited = false;
        this.listener = new LinkedList<H5Listener>();
        this.pages = new Stack<H5Page>();
        this.h5Data = new H5MemData();
        initPlugins();
    }

    private void initPlugins() {
        H5PluginManager pluginManager = getPluginManager();
        pluginManager.register(new H5SessionPlugin(this));

        H5Plugin localH5Plugin = H5PluginConfigManager.getInstance().createPlugin("session", pluginManager);
        if (localH5Plugin != null)
            pluginManager.register(localH5Plugin);
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public void setId(String id) {
        this.sessionId = id;
    }

    @Override
    public boolean addPage(H5Page page) {
        if (page == null) {
            return false;
        }

        synchronized (pages) {
            if (pages.isEmpty()) {
                Bundle params = page.getParams();
                this.provider = new H5WebProviderImpl(params);

                for (H5Listener ln : listener) {
                    ln.onSessionCreated(this);
                }
            }

            for (H5Page p : pages) {
                if (p.equals(page)) {
                    return false;
                }
            }

            page.setParent(this);
            pages.add(page);

            for (H5Listener ln : listener) {
                ln.onPageCreated(page);
            }
        }

        return true;
    }

    @Override
    public boolean removePage(H5Page page) {
        if (page == null) {
            return false;
        }

        H5Page removedPage = null;
        synchronized (pages) {
            Iterator<H5Page> iterator = pages.iterator();
            while (iterator.hasNext()) {
                H5Page p = iterator.next();
                if (p.equals(page)) {
                    iterator.remove();
                    removedPage = p;
                    break;
                }
            }

            if (removedPage != null) {
                removedPage.onRelease();
                page.setParent(null);
                for (H5Listener ln : listener) {
                    ln.onPageDestroyed(page);
                }
            }

            if (pages.isEmpty()) {
                H5Container.getService().removeSession(this.getId());

                for (H5Listener ln : listener) {
                    ln.onSessionDestroyed(this);
                }
            }
        }

        return (removedPage != null);
    }

    @Override
    public H5Page getTopPage() {
        synchronized (pages) {
            if (pages.isEmpty()) {
                return null;
            }
            H5Page top = pages.peek();
            return top;
        }
    }

    @Override
    public Stack<H5Page> getPages() {
        return pages;
    }

    @Override
    public H5Scenario getScenario() {
        return h5Scenario;
    }

    @Override
    public void setScenario(H5Scenario scenario) {
        this.h5Scenario = scenario;
    }

    @Override
    public void addListener(H5Listener l) {
        if (l == null) {
            return;
        }
        for (H5Listener ln : listener) {
            if (l.equals(ln)) {
                return;
            }
        }
        this.listener.add(l);
    }

    @Override
    public void removeListener(H5Listener l) {
        if (l == null) {
            return;
        }
        this.listener.remove(l);
    }

    @Override
    public boolean exitSession() {
        if (exited) {
            H5Log.e(TAG, "session already exited!");
            return false;
        }
        exited = true;

        while (!pages.isEmpty()) {
            H5Page page = pages.firstElement();
            page.sendIntent(H5Plugin.H5_PAGE_CLOSE, null);
        }
        return true;
    }

    @Override
    public InputStream getWebResource(String url) {
        if (provider != null) {
            return provider.getWebResource(url);
        }
        return null;
    }
}
