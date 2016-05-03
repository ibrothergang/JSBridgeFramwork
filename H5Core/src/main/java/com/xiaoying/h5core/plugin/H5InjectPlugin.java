package com.xiaoying.h5core.plugin;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.core.H5PageImpl;
import com.xiaoying.h5core.web.H5JSInjector;

import org.json.JSONObject;

import android.text.TextUtils;

import java.util.Iterator;

public class H5InjectPlugin implements H5Plugin {

    public static final String TAG = "H5InjectPlugin";

    private H5JSInjector injector;
    private H5PageImpl h5Page;

    public H5InjectPlugin(H5PageImpl page) {
        this.h5Page = page;
        injector = new H5JSInjector(h5Page);
    }

    @Override
    public void onRelease() {
        injector = null;
        h5Page = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5_PAGE_STARTED);
        filter.addAction(H5_PAGE_FINISHED);
        filter.addAction(H5_PAGE_RECEIVED_TITLE);
        filter.addAction(H5_PAGE_JS_PARAM);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_RECEIVED_TITLE.equals(action)) {
            injector.inject(false);
        } else if (H5_PAGE_FINISHED.equals(action)) {
            injector.inject(true);
        } else if (H5_PAGE_STARTED.equals(action)) {
            injector.reset();
        }
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_JS_PARAM.equals(action)) {
            JSONObject param = intent.getParam();
            Iterator entrys = param.keys();
            while (entrys.hasNext()) {
                String k = (String) entrys.next();
                String v = H5Utils.getString(param, k);
                if (!TextUtils.isEmpty(k) && !TextUtils.isEmpty(v)) {
                    injector.setParamsToWebPage(k, v);
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
