package com.xiaoying.h5core.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;

import com.xiaoying.h5core.R;
import com.xiaoying.h5core.api.H5Page.H5PageHandler;
import com.xiaoying.h5core.api.H5Param;
import com.xiaoying.h5core.api.H5Plugin;
import com.xiaoying.h5core.core.H5PageImpl;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.env.H5Environment;
import com.xiaoying.h5core.util.H5Log;
import com.xiaoying.h5core.util.H5Utils;
import com.xiaoying.h5core.util.KeyboardUtil;
import com.xiaoying.h5core.util.KeyboardUtil.KeyboardListener;
import com.xiaoying.h5core.view.H5FontBar;
import com.xiaoying.h5core.view.H5NavigationBar;
import com.xiaoying.h5core.view.H5TitleBar;
import com.xiaoying.h5core.view.H5ToolBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

public class H5Activity extends Activity {

    public static final String TAG = "H5Activity";
    public final static int FILECHOOSER_RESULTCODE = 1;
    private String cameraFilePath;
    private boolean isRunning;
    private Bundle startParams;
    private ViewGroup h5RootView;
    private H5NavigationBar h5NavBar;
    private H5TitleBar h5TitleBar;
    private View titleBarView;
    private H5ToolBar h5ToolBar;
    private H5FontBar h5FontBar;
    private View toolBarView;
    private H5WebContent h5WebContainer;
    private View h5WebContent;
    private H5PageImpl h5Page;
    private KeyboardUtil KeyboardHelper;
    private PageListener pageListener;
    private boolean newNavStyle;
    private ValueCallback<Uri> uploadMessage;
    private String transactionId;
    private KeyboardListener keyboardListener = new KeyboardListener() {

        @Override
        public void onKeyboardVisible(boolean visible) {
            H5Log.d(TAG, "onKeyboardVisible " + visible);
            if (visible) {
                String publicId = H5Utils.getString(startParams,
                        H5Param.PUBLIC_ID, "");
                String url = h5Page.getUrl();
                JSONObject param = new JSONObject();
                try {
                    param.put(H5Param.PUBLIC_ID, publicId);
                    param.put(H5Param.LONG_URL, url);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }

                h5Page.sendIntent(H5Plugin.KEY_BOARD_BECOME_VISIBLE, param);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        newNavStyle = true;

        H5Environment.setContext(this);
        setContentView(R.layout.h5_activity);
        h5RootView = (ViewGroup) findViewById(R.id.h5_container_root);
        h5Page = new H5PageImpl(this, null);
        h5Page.setHandler(new H5PageHandler() {

            @Override
            public boolean shouldExit() {
                return true;
            }
        });

        if (newNavStyle) {
            initNavBar();
        } else {
            initTitleBar();
        }
        initToolBar();
        initFontBar();
        initWebContent();
        initHelpers();
        h5Page.applyParams();
        applyParams();
    }

    public void setUploadMsg(ValueCallback<Uri> uploadMessage) {
        this.uploadMessage = uploadMessage;
    }

    public void setCameraFilePath(final String cameraFilePath) {
        this.cameraFilePath = cameraFilePath;
    }

    public void setPageListener(PageListener pl) {
        this.pageListener = pl;
    }

    private void initHelpers() {
        KeyboardHelper = new KeyboardUtil(this);
        KeyboardHelper.setListener(keyboardListener);
    }

    private void initNavBar() {
        h5NavBar = new H5NavigationBar(h5Page);
        h5Page.getPluginManager().register(h5NavBar);

        titleBarView = h5NavBar.getContent();
        int height = (int) H5Environment.getResources().getDimension(
                R.dimen.h5_title_height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        h5RootView.addView(titleBarView, 0, lp);
    }

    private void initTitleBar() {
        h5TitleBar = new H5TitleBar(h5Page);
        h5Page.getPluginManager().register(h5TitleBar);

        titleBarView = h5TitleBar.getContent();
        int height = (int) H5Environment.getResources().getDimension(
                R.dimen.h5_title_height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        h5RootView.addView(titleBarView, 0, lp);
    }

    private void initToolBar() {
        h5ToolBar = new H5ToolBar(h5Page);

        h5Page.getPluginManager().register(h5ToolBar);

        toolBarView = h5ToolBar.getContent();
        int height = (int) H5Environment.getResources().getDimension(
                R.dimen.h5_bottom_height);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        h5RootView.addView(toolBarView, h5RootView.getChildCount(), lp);
    }

    private void initFontBar() {
        h5FontBar = new H5FontBar(h5Page);
        h5Page.getPluginManager().register(h5FontBar);
    }

    private void initWebContent() {
        h5WebContainer = new H5WebContent(h5Page);
        h5WebContent = h5WebContainer.getContent();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (toolBarView != null) {
            lp.addRule(RelativeLayout.ABOVE, toolBarView.getId());
        }
        if (titleBarView != null) {
            lp.addRule(RelativeLayout.BELOW, titleBarView.getId());
        }
        h5RootView.addView(h5WebContent, 0, lp);

        h5Page.getPluginManager().register(h5WebContainer);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent intent) {
        boolean backEvent = (intent.getKeyCode() == KeyEvent.KEYCODE_BACK)
                && (intent.getRepeatCount() == 0);
        if (backEvent) {
            h5Page.sendIntent(H5Plugin.H5_PAGE_PHYSICAL_BACK, null);
            return true;
        } else {
            return super.onKeyDown(keyCode, intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        if (!isRunning) {
            isRunning = true;
        } else {
            h5Page.sendIntent(H5Plugin.H5_PAGE_RESUME, null);
        }
        // this operation is a must to start/stop the flash player
        if (Build.VERSION.SDK_INT >= 11 && h5Page.getWebView() != null) {
            h5Page.getWebView().onResume();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPause() {
        super.onPause();

        // this operation is a must to start/stop the flash player
        if (Build.VERSION.SDK_INT >= 11 && h5Page.getWebView() != null) {
            h5Page.getWebView().onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isRunning) {
            return;
        }
        H5Log.d(TAG, "onDestroy H5Activity");
        isRunning = false;
        h5Page.exitPage();
        h5Page.sendIntent(H5Plugin.H5_PAGE_CLOSED, null);
        h5ToolBar = null;
        h5TitleBar = null;
        h5FontBar = null;
        KeyboardHelper.setListener(null);
        KeyboardHelper = null;
        if (!TextUtils.isEmpty(transactionId)) {
            H5Log.d("remove transaction");
//            TaskScheduleService executorService = AlipayApplication.getInstance().getMicroApplicationContext()
//                    .findServiceByInterface(TaskScheduleService.class.getName());
//            if (executorService != null) {
//                try {
//                    executorService.removeTransaction(transactionId);
//                } catch (Exception globalException) {
//                    H5Log.d(globalException.toString());
//                }
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (this.pageListener != null) {
            this.pageListener.onActivityResult(resultCode, intent);
            this.pageListener = null;
        }

        // handling of result of file selection for uploading
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == uploadMessage) {
                H5Log.d(TAG, "error, selection for file uploading done, but upload msg gone!");
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            if (result == null && intent == null && resultCode == Activity.RESULT_OK
                    && cameraFilePath != null) {
                File cameraFile = new File(cameraFilePath);
                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);
                    // Broadcast to the media scanner that we have a new photo
                    // so it will be added into the gallery for the user.
                    sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }
            uploadMessage.onReceiveValue(result);
            uploadMessage = null;
        }
    }

    private void applyParams() {
        startParams = h5Page.getParams();

        // force to set title bar invisible if tool bar visible.
        if (H5Utils.getBoolean(startParams, H5Param.LONG_SHOW_TOOLBAR, false)) {
            H5Log.d(TAG, "force to hide titlebar!");
            startParams.putBoolean(H5Param.LONG_SHOW_TITLEBAR, false);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("H5 start params:");

        Set<String> keys = startParams.keySet();
        for (String key : keys) {
            Object value = startParams.get(key);
            String text = String.format("\n[%s ==> %s]", key, value);
            builder.append(text);
        }

        String paramsStr = builder.toString();
        H5Log.d(TAG, paramsStr);

        for (String key : keys) {
            String intentName = null;
            JSONObject param = new JSONObject();
            if (H5Param.LONG_SHOW_TITLEBAR.equals(key)) {
                boolean value = H5Utils.getBoolean(startParams, key, true);
                if (value == true) {
                    intentName = H5Plugin.SHOW_TITLE_BAR;
                } else {
                    intentName = H5Plugin.HIDE_TITLE_BAR;
                }
            } else if (H5Param.LONG_SHOW_TOOLBAR.equals(key)) {
                boolean value = H5Utils.getBoolean(startParams, key, false);
                if (value == true) {
                    intentName = H5Plugin.SHOW_TOOL_BAR;
                } else {
                    intentName = H5Plugin.HIDE_TOOL_BAR;
                }
            } else if (H5Param.LONG_DEFAULT_TITLE.equals(key)) {
                String title = H5Utils.getString(startParams, key);
                if (TextUtils.isEmpty(title)) {
                    continue;
                }
                intentName = H5Plugin.SET_TITLE;
                try {
                    param.put("title", title);
                    param.put("fromJS", false);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
            } else if (H5Param.LONG_READ_TITLE.equals(key)) {
                boolean readTitle = H5Utils.getBoolean(startParams, key, true);
                intentName = H5Plugin.READ_TITLE;
                try {
                    param.put(key, readTitle);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
            } else if (H5Param.LONG_TOOLBAR_MENU.equals(key)) {
                String toolbarMenu = H5Utils.getString(startParams, key);
                param = H5Utils.parseObject(toolbarMenu);
                intentName = H5Plugin.SET_TOOL_MENU;
            } else if (H5Param.LONG_PULL_REFRESH.equals(key)) {
                boolean refresh = H5Utils.getBoolean(startParams, key, false);
                try {
                    param.put(key, refresh);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
                intentName = H5Plugin.PULL_REFRESH;
            } else if (H5Param.LONG_CAN_PULL_DOWN.equals(key)) {
                boolean down = H5Utils.getBoolean(startParams, key, true);
                try {
                    param.put(key, down);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
                intentName = H5Plugin.CAN_PULL_DOWN;
            } else if (H5Param.LONG_SHOW_PROGRESS.equals(key)) {
                boolean show = H5Utils.getBoolean(startParams, key, false);
                try {
                    param.put(key, show);
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }
                intentName = H5Plugin.SHOW_PROGRESS_BAR;
            } else if (H5Param.LONG_SHOW_OPTION_MENU.equals(key)) {
                final boolean isH5App = TextUtils.equals("20000067",
                        H5Utils.getString(startParams, H5Container.APP_ID));
                boolean show = H5Utils.getBoolean(startParams, key, isH5App);
                intentName = show ? H5Plugin.SHOW_OPTION_MENU : H5Plugin.HIDE_OPTION_MENU;
            } else if ("transaction_id".equals(key)) {
                transactionId = H5Utils.getString(startParams, key);
            }
            if (!TextUtils.isEmpty(intentName)) {
                h5Page.sendIntent(intentName, param);
            }
        }
    }

    public interface PageListener {
        public void onActivityResult(int resultCode, Intent intent);
    }
}
