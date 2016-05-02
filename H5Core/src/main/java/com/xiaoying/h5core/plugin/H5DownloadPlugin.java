package com.xiaoying.h5core.plugin;

import android.text.TextUtils;

import com.xiaoying.h5core.api.H5Intent;
import com.xiaoying.h5core.api.H5IntentFilter;
import com.xiaoying.h5core.api.H5Plugin;
import com.xiaoying.h5core.download.Downloader;
import com.xiaoying.h5core.download.Downloader.Status;
import com.xiaoying.h5core.download.DownloaderImpl;
import com.xiaoying.h5core.download.ProgressListener;
import com.xiaoying.h5core.download.StatusListener;
import com.xiaoying.h5core.util.FileUtil;
import com.xiaoying.h5core.util.H5Log;
import com.xiaoying.h5core.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class H5DownloadPlugin implements StatusListener, ProgressListener,
        H5Plugin {
    public static final String TAG = "H5DownloadPlugin";

    private Downloader downloader;
    private Set<String> tasks;
    private H5Intent intent;

    public H5DownloadPlugin() {
        tasks = new HashSet<String>();
    }

    private void initDownload(H5Intent intent) {
        if (downloader == null) {
            this.intent = intent;
            downloader = DownloaderImpl.getInstance();
            downloader.setProgressListener(this);
            downloader.setStatusListener(this);
        }
    }

    public void startDownload(H5Intent intent) throws JSONException {
        initDownload(intent);
        final JSONObject param = intent.getParam();
        String url = H5Utils.getString(param, "url");
        if (TextUtils.isEmpty(url)) {
            H5Log.w(TAG, "invalid url " + url);
            return;
        }
        int options = Downloader.OPT_WIFI_ENABLE;
        if (H5Utils.getBoolean(param, "mobile", false)) {
            options |= Downloader.OPT_MOBILE_ENABLE;
        }

        JSONObject result = new JSONObject();
        tasks.add(url);
        if (downloader.add(url, options)) {
            result.put("success", true);
        } else {
            tasks.remove(url);
            result.put("success", false);
        }
        intent.sendBack(result);
    }

    public void stopDownload(H5Intent intent) throws JSONException {
        initDownload(intent);

        final JSONObject param = intent.getParam();
        String url = H5Utils.getString(param, "url");
        if (TextUtils.isEmpty(url)) {
            H5Log.w(TAG, "invlaid url " + url);
            return;
        }

        JSONObject result = new JSONObject();
        if (downloader.cancel(url)) {
            result.put("success", true);
        } else {
            result.put("success", false);
        }
        intent.sendBack(result);
    }

    public void getDownloadInfo(H5Intent intent) throws JSONException {
        initDownload(intent);

        final JSONObject param = intent.getParam();
        String url = H5Utils.getString(param, "url");
        if (TextUtils.isEmpty(url)) {
            H5Log.w(TAG, "invlaid url " + url);
            return;
        }

        String path = downloader.getFile(url);
        JSONObject info = new JSONObject();
        Status status = Status.NONE;
        int progress = 0;
        if (FileUtil.exists(path)) {
            status = Status.SUCCEED;
            progress = 100;
        } else {
            status = downloader.getStatus(url);
            progress = downloader.getProgress(url);
        }
        info.put("status", getStatus(status));
        info.put("progress", progress);
        info.put("url", url);
        info.put("path", path);
        intent.sendBack(info);
    }

    @Override
    public void onProgress(String url, int progress) {
        if (TextUtils.isEmpty(url) || !tasks.contains(url)) {
            return;
        }

        JSONObject data = new JSONObject();
        try {
            data.put("url", url);
            data.put("status", getStatus(Status.DOWNLOADING));
            data.put("progress", progress);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        if (intent != null) {
            intent.getBridge().sendToWeb("downloadEvent", data, null);
        }
    }

    @Override
    public void onStatus(String url, Status status) {
        if (TextUtils.isEmpty(url) || !tasks.contains(url)) {
            return;
        }

        JSONObject data = new JSONObject();
        try {
            data.put("url", url);
            data.put("progress", downloader.getProgress(url));
            data.put("status", getStatus(status));
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }
        if (intent != null) {
            intent.getBridge().sendToWeb("downloadEvent", data, null);
        }
        if (status == Status.SUCCEED || status == Status.FAILED) {
            tasks.remove(url);
        }
    }

    private String getStatus(Status status) {
        switch (status) {
            case PENDDING:
                return "pendding";
            case DOWNLOADING:
                return "downloading";
            case PAUSED:
                return "paused";
            case FAILED:
                return "failed";
            case SUCCEED:
                return "success";

            default:
                return "none";
        }
    }

    @Override
    public void onRelease() {
        tasks = null;
        downloader = null;
        intent = null;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        final String action = intent.getAction();
        if (H5_START_DOWNLOAD.equals(action)) {
            try {
                startDownload(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        } else if (H5_STOP_DOWNLOAD.equals(action)) {
            try {
                stopDownload(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        } else if (H5_GET_DOWNLOAD_INFO.equals(action)) {
            try {
                getDownloadInfo(intent);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
        }
        return true;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(H5_START_DOWNLOAD);
        filter.addAction(H5_STOP_DOWNLOAD);
        filter.addAction(H5_GET_DOWNLOAD_INFO);
    }
}
