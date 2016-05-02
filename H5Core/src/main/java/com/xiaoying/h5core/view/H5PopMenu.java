package com.xiaoying.h5core.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;

import com.xiaoying.h5core.R;
import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

interface TitleProvider {
    public String getTitle();
}

public abstract class H5PopMenu {

    public static final String TAG = "H5PopMenu";

    List<MenuItem> menuList;

    PopupWindow popupWindow;
    H5Page h5Page;
    TitleProvider titleProvider;
    boolean menuUpdated;
    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            Object tag = v.getTag();
            if (!(tag instanceof Integer)) {
                return;
            }
            int position = (Integer) tag;

            try {
                MenuItem item = menuList.get(position);
                JSONObject data = new JSONObject();
                data.put(H5Container.MENU_NAME, item.name);
                data.put(H5Container.MENU_TAG, item.tag);
                String title = h5Page.getTitle();
                if (titleProvider != null) {
                    title = titleProvider.getTitle();
                }
                data.put(H5Container.KEY_TITLE, title);
                data.put(H5Param.LONG_URL, h5Page.getUrl());
                h5Page.sendIntent(H5Plugin.H5_TOOLBAR_MENU_BT, data);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        }

    };

    public H5PopMenu(H5Page page) {
        this.h5Page = page;
        initMenu();
        menuUpdated = true;
    }

    public void setTitleProvider(TitleProvider p) {
        this.titleProvider = p;
    }

    public abstract void initMenu();

    public abstract void showMenu(View anchor);

    public void setMenu(H5Intent intent, boolean temp) throws JSONException {
        JSONObject param = intent.getParam();
        JSONArray menus = H5Utils.getJSONArray(param, "menus", null);
        boolean overrideSystemDefault = H5Utils.getBoolean(param, "override", false);
        if (overrideSystemDefault && menuList != null) {
            menuList.clear();
        } else if (menus == null || menus.length() == 0) {
            initMenu();
        }

        int realIndex = 0;
        for (int index = 0; menus != null && index < menus.length(); index++) {
            JSONObject jsonobject = menus.getJSONObject(index);
            String name = jsonobject.getString(H5Container.MENU_NAME);
            String tag = jsonobject.getString(H5Container.MENU_TAG);
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(tag)) {
                H5Log.w(TAG, "invalid tag: " + tag + " name: " + name);
                continue;
            }

            if (hasMenu(name, tag)) {
                H5Log.w(TAG, "existed tag: " + tag + " name: " + name);
                continue;
            }

            // at most 5 custom menu
            if (realIndex > 4 && temp) {
                continue;
            }

            if (name.length() > 4) {
                name = name.substring(0, 4);
            }

            Drawable icon = getDrawable(tag);
            MenuItem popupItem = new MenuItem(name, tag, icon, temp);
            popupItem.temp = temp;
            if (H5Container.MENU_COMPLAIN.equals(tag)) {
                menuList.add(popupItem);
            } else {
                menuList.add(realIndex++, popupItem);
            }
            menuUpdated = true;
        }
    }

    private boolean hasMenu(String name, String tag) {
        for (MenuItem item : menuList) {
            if (item.name.equals(name) || item.tag.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private Drawable getDrawable(String tag) {
        Resources resources = H5Environment.getResources();
        if (H5Container.MENU_COMPLAIN.equals(tag)) {
            return resources.getDrawable(R.drawable.h5_nav_complain);
        } else if (H5Container.MENU_SHARE.equals(tag)) {
            return resources.getDrawable(R.drawable.h5_nav_share);
        } else {
            return resources.getDrawable(R.drawable.h5_nav_default);
        }
    }

    public void resetMenu() {
        for (int index = menuList.size() - 1; index >= 0; index--) {
            MenuItem menu = menuList.get(index);
            if (menu.temp) {
                menuList.remove(index);
            }
        }
        menuUpdated = true;
    }

    class MenuItem {
        protected String name;
        protected String tag;
        protected Drawable icon;
        protected boolean temp;
        protected int weight;

        public MenuItem(String name, String tag, Drawable icon, boolean temp) {
            this.name = name;
            this.tag = tag;
            this.icon = icon;
            this.temp = temp;
        }
    }
}
