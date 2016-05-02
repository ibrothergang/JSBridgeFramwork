package com.xiaoying.h5core.core;

import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoying.h5api.api.H5WebProvider;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.FileUtil;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5api.util.tar.TarEntry;
import com.xiaoying.h5api.util.tar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class H5WebProviderImpl implements H5WebProvider {

    public static final String TAG = "H5WebProviderImpl";

    private Map<String, byte[]> resourceMap;

    public H5WebProviderImpl(Bundle params) {
        resourceMap = new HashMap<String, byte[]>();
        boolean mapLocal = H5Utils.getBoolean(params, H5Container.ENABLE_MAPLOCAL, false);
        if (!mapLocal) {
            return;
        }
        parsePackage(params);
    }

    @Override
    public InputStream getWebResource(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        int inPageHashStart = url.indexOf("#");
        if (inPageHashStart != -1) {
            url = url.substring(0, inPageHashStart);
        }

        int queryParamStart = url.indexOf("?");
        if (queryParamStart != -1) {
            url = url.substring(0, queryParamStart);
        }

        if (resourceMap.containsKey(url)) {
            H5Log.d(TAG, "resource found " + url);
            byte[] data = resourceMap.get(url);
            return new ByteArrayInputStream(data);
        }
        return null;
    }

    private void parsePackage(Bundle params) {
        String appId = H5Utils.getString(params, H5Container.APP_ID);
        String installPath = H5Utils.getString(params, H5Container.INSTALL_PATH);
        String tarPath = installPath + "/" + appId + ".tar";
        if (!FileUtil.exists(tarPath)) {
            H5Log.e(TAG, "tar path not exists!");
            return;
        }

        long start = System.currentTimeMillis();
        String installHost = H5Utils.getString(params, H5Container.INSTALL_HOST);
        try {
            FileInputStream fis = new FileInputStream(tarPath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            TarInputStream tis = new TarInputStream(bis);
            TarEntry te = null;
            while ((te = tis.getNextEntry()) != null) {
                String entryName = te.getName();
                // H5Log.d(TAG, "tar entry " + entryName);
                if (te.isDirectory() || TextUtils.isEmpty(entryName)) {
                    continue;
                }

                String entryPath = installHost + "/" + entryName;
                byte buffer[] = new byte[2048];
                int count;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((count = tis.read(buffer)) != -1) {
                    bos.write(buffer, 0, count);
                }
                byte[] data = bos.toByteArray();
                resourceMap.put(entryPath, data);
            }
            tis.close();
        } catch (Exception e) {
            H5Log.e("parse tar package exception", e);
            return;
        }
        long elapse = System.currentTimeMillis() - start;
        H5Log.d(TAG, "parse tar package elapse " + elapse);
        return;
    }
}
