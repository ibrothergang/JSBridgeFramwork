package com.xiaoying.h5core.plugin;

import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.R;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class H5LoadingPlugin implements H5Plugin {

    public static final String TAG = "H5LoadingPlugin";

    private static final int LOADING_TEXT_MAX = 20;
    private Runnable loadingTask;
    private Handler handler;
    private H5Page h5Page;
    private LoadingDialog dialog;
    private Activity activity;

    public H5LoadingPlugin(H5Page page) {
        this.h5Page = page;
        handler = new Handler();

        Context context = h5Page.getContext().getContext();
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onRelease() {
        handler.removeCallbacks(loadingTask);
        loadingTask = null;
        h5Page = null;
    }

    public void showLoading(H5Intent intent) {
        JSONObject param = intent.getParam();
        String title = H5Utils.getString(param, "text");
        int delay = H5Utils.getInt(param, "delay");

        H5Log.d(TAG, "showLoading [title] " + title + " [delay] " + delay);

        if (dialog == null) {
            dialog = new LoadingDialog(activity);
        }

        hideLoading();

        // cut the text to limited size
        if (!TextUtils.isEmpty(title) && title.length() > LOADING_TEXT_MAX) {
            title = title.substring(0, LOADING_TEXT_MAX);
        }

        dialog.setMessage(title);

        loadingTask = new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    try {
                        dialog.show();
                    } catch (RuntimeException e) {
                        // prevent the adding window failed exception when window-session has lost
                        e.printStackTrace();
                    }
                }
            }
        };
        handler.postDelayed(loadingTask, delay);
    }

    public void hideLoading() {
        if (loadingTask != null) {
            handler.removeCallbacks(loadingTask);
            loadingTask = null;
        }

        if (dialog != null && dialog.isShowing() && activity != null && !activity.isFinishing()) {
            H5Log.d("hideLoading");
            try {
                dialog.dismiss();
            } catch (Throwable t) {
                H5Log.e(TAG, "dismiss exception");
            }
        }
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        if (activity != null) {
            filter.addAction(SHOW_LOADING);
            filter.addAction(HIDE_LOADING);
        }
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_LOADING.equals(action)) {
            showLoading(intent);
        } else if (HIDE_LOADING.equals(action)) {
            hideLoading();
        }
        return true;
    }

    class LoadingDialog extends AlertDialog {

        private ProgressBar pbLoading;
        private TextView tvMessage;
        private String messageText;

        protected LoadingDialog(Context context) {
            this(context, R.style.h5_loading_style);
        }

        public LoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.h5_loading, null);
            pbLoading = (ProgressBar) view.findViewById(R.id.h5_loading_progress);
            tvMessage = (TextView) view.findViewById(R.id.h5_loading_message);

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.99f;
            getWindow().setAttributes(lp);
            setView(view);
            pbLoading.setVisibility(View.VISIBLE);
            setCancelable(true);
            setOnCancelListener(null);
            pbLoading.setIndeterminate(false);
            setCanceledOnTouchOutside(false);
            realSetMessage();
            super.onCreate(savedInstanceState);
        }

        public void setMessage(String text) {
            messageText = text;
            if (tvMessage != null) {
                realSetMessage();
            }
        }

        private void realSetMessage() {
            tvMessage.setText(messageText);
            if (TextUtils.isEmpty(messageText)) {
                tvMessage.setVisibility(View.GONE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
            }
        }

    }
}
