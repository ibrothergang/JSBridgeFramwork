package com.xiaoying.h5core.core;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Log;

import org.json.JSONException;
import org.json.JSONObject;

public class H5DefaultPlugin implements H5Plugin {

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (H5_PAGE_SHOULD_LOAD_URL.equals(action)) {
            loadUrl(intent);
        } else if (H5_PAGE_SHOULD_LOAD_DATA.equals(action)) {
            loadData(intent);
        } else if (H5_TOOLBAR_MENU_BT.equals(action)) {
            JSONObject param = intent.getParam();
            JSONObject event = new JSONObject();
            try {
                event.put("data", param);
            } catch (JSONException e) {
                H5Log.e("H5DefaultPlugin", "exception", e);
            }

            H5Page h5Page = (H5Page) intent.getTarget();
            h5Page.getBridge().sendToWeb("toolbarMenuClick", event, null);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5_PAGE_SHOULD_LOAD_URL);
        filter.addAction(H5_PAGE_SHOULD_LOAD_DATA);
        filter.addAction(H5_TOOLBAR_MENU_BT);
    }

    @Override
    public void onRelease() {

    }

    private void loadUrl(H5Intent intent) {
        H5IntentImpl load = new H5IntentImpl(H5Container.H5_PAGE_DO_LOAD_URL);
        JSONObject param = intent.getParam();
        load.setParam(param);
        load.setTarget(intent.getTarget());
        H5Container.getMesseger().sendIntent(load);
    }

    private void loadData(H5Intent intent) {
        H5IntentImpl load = new H5IntentImpl(H5_PAGE_LOAD_DATA);
        JSONObject param = intent.getParam();
        load.setParam(param);
        load.setTarget(intent.getTarget());
        H5Container.getMesseger().sendIntent(load);
    }

}
