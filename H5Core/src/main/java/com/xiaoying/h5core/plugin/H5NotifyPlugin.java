package com.xiaoying.h5core.plugin;

import android.text.TextUtils;
import android.widget.Toast;

import com.xiaoying.h5core.R;
import com.xiaoying.h5core.api.H5Intent;
import com.xiaoying.h5core.api.H5IntentFilter;
import com.xiaoying.h5core.api.H5Page;
import com.xiaoying.h5core.api.H5Plugin;
import com.xiaoying.h5core.util.H5Log;
import com.xiaoying.h5core.util.H5Utils;
import com.xiaoying.h5core.view.H5Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xide.wf
 */
public class H5NotifyPlugin implements H5Plugin {

    public static final String TAG = "H5NotifyPlugin";

    private int toast_ok = R.drawable.simple_toast_ok;
    private int toast_fail = R.drawable.simple_toast_false;
    private H5Page h5Page;
    private Timer timer = new Timer();

    public H5NotifyPlugin(H5Page page) {
        this.h5Page = page;
    }

    @Override
    public void onRelease() {
        h5Page = null;
        timer.cancel();
        timer = null;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(TOAST);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (TOAST.equals(action)) {
            toast(intent);
        }
        return true;
    }

    private void toast(final H5Intent intent) {
        JSONObject params = intent.getParam();
        if (params == null || params.length() == 0) {
            return;
        }

        String content = H5Utils.getString(params, "content");
        String type = H5Utils.getString(params, "type");
        int duration = H5Utils.getInt(params, "duration");
        // none / success / fail
        int iconId = getImageId(type);

        int mDuration = Toast.LENGTH_LONG;
        if (duration < 2500) {
            mDuration = Toast.LENGTH_SHORT;
        }
        H5Toast.showToast(h5Page.getContext().getContext(), iconId, content,
                mDuration);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                H5Log.d("H5JSFuncs", "toast show call back");
                JSONObject result = new JSONObject();
                try {
                    result.put("toastCallBack", "true");
                } catch (JSONException e) {
                    H5Log.e(TAG, "exception", e);
                }

                intent.sendBack(result);
            }
        }, mDuration);

        H5Log.d("H5JSFuncs", "toast show");
    }

    private int getImageId(String image) {
        if (TextUtils.equals(image, "success")) {
            return toast_ok;
        } else if (TextUtils.equals(image, "fail")) {
            return toast_fail;
        }
        return 0;
    }

}
