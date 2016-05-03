package com.xiaoying.h5core.download;

import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.download.Downloader.Status;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class TaskInfo implements TransferListener {
    public static final String TAG = "TaskImpl";

    private String url;
    private int options;
    private Status status;
    private long totalSize;
    private int progress;
    private Context context;
    private Client client;
    private ProgressListener pl;
    private StatusListener sl;
    private long time;
    private String path;

    public TaskInfo() {
        status = Status.NONE;
        progress = 0;
        options = Downloader.OPT_WIFI_ENABLE;
    }

    public TaskInfo(String url, int options) {
        this();
        this.url = url;
    }

    public TaskInfo(String text) {
        this();
        try {
            JSONObject jo = H5Utils.parseObject(text);
            if (jo == null || jo.length() == 0) {
                return;
            }
            url = jo.getString("url");
            progress = jo.getInt("progress");
            time = jo.getLong("time");
            status = Status.valueOf(jo.getString("status"));
            totalSize = jo.getLong("total");
            path = jo.getString("path");
            options = jo.getInt("options");
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Status getStatus() {
        return this.status;
    }

    protected void setStatus(Status status) {
        if (this.status == status) {
            return;
        }

        H5Log.d(TAG, "setStatus " + status);
        this.status = status;

        if (sl != null) {
            sl.onStatus(url, status);
        }
    }

    public String getUrl() {
        return url;
    }

    public int getOptions() {
        return options;
    }

    protected void setOptions(int options) {
        this.options = options;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskInfo other = (TaskInfo) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        // for save progress
        JSONObject joTask = new JSONObject();

        try {
            joTask.put("url", url);
            joTask.put("status", status);
            joTask.put("progress", progress);
            joTask.put("total", getTotalSize());
            joTask.put("time", System.currentTimeMillis());
            joTask.put("path", path);
            joTask.put("options", options);
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        return joTask.toString();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setProgressListener(ProgressListener l) {
        this.pl = l;
    }

    public void setStatusListener(StatusListener l) {
        this.sl = l;
    }

    @Override
    public void onProgress(int progress) {
        if (this.progress == progress) {
            return;
        }
        this.progress = progress;

        if (pl != null) {
            pl.onProgress(url, progress);
        }
    }

    @Override
    public void onTotalSize(long size) {
        this.setTotalSize(size);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
