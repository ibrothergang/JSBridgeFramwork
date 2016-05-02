package com.xiaoying.h5core.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;

import com.xiaoying.h5core.R;
import com.xiaoying.h5api.api.H5Bridge;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5core.apwebview.APJsPromptResult;
import com.xiaoying.h5core.apwebview.APJsResult;
import com.xiaoying.h5core.apwebview.APWebChromeClient;
import com.xiaoying.h5core.apwebview.APWebViewCtrl;
import com.xiaoying.h5core.core.H5IntentImpl;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.ui.H5Activity;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class H5WebChromeClient implements APWebChromeClient {

    public static final String TAG = "H5WebChromeClient";

    private static final String BRIDGE_MSG_HEADER = "h5container.message: ";

    private H5Page h5Page;

    private String cameraFilePath;

    public H5WebChromeClient(H5Page page) {
        this.h5Page = page;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (consoleMessage == null) {
            return false;
        }
        final String message = consoleMessage.message();
        final int lineNumber = consoleMessage.lineNumber();
        final String sourceID = consoleMessage.sourceId();

        H5Log.dWithDeviceInfo(TAG, "onConsoleMessage [message] " + message + " [lineNumber] "
                + lineNumber + " [sourceID] " + sourceID);

        if (TextUtils.isEmpty(message) || h5Page == null) {
            H5Log.e(TAG, "onConsoleMessage return, message: " + message + " h5Page: " + h5Page);
            return false;
        }

        String msgText = null;
        if (message.startsWith(BRIDGE_MSG_HEADER)) {
            msgText = message.replaceFirst(BRIDGE_MSG_HEADER, "");
        }

        if (TextUtils.isEmpty(msgText)) {
            H5Log.eWithDeviceInfo(TAG, "onConsoleMessage return as empty message");
            return false;
        }

        JSONObject joMessage = H5Utils.parseObject(msgText);
        if (joMessage == null || joMessage.length() == 0) {
            return false;
        }

        String clientId = H5Utils.getString(joMessage, H5Container.CLIENT_ID);
        String name = H5Utils.getString(joMessage, H5Container.FUNC);
        String msgType = H5Utils.getString(joMessage, H5Container.MSG_TYPE);
        boolean keep = H5Utils.getBoolean(joMessage, H5Container.KEEP_CALLBACK, false);

        if (TextUtils.isEmpty(clientId)) {
            H5Log.e(TAG, "invalid client id!");
            return false;
        }

        H5Log.d(TAG, "[name] " + name + " [msgType] " + msgType
                + " [clientId] " + clientId);

        JSONObject joParam = H5Utils.getJSONObject(joMessage,
                H5Container.PARAM, null);

        H5Bridge bridge = h5Page.getBridge();
        H5IntentImpl intent = new H5IntentImpl();
        intent.setAction(name);
        intent.setBridge(bridge);
        intent.setParam(joParam);
        intent.setTarget(h5Page);
        intent.setType(msgType);
        intent.setId(clientId);
        intent.setKeep(keep);

        bridge.sendToNative(intent);
        return true;
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        return null;
    }

    @Override
    public void onProgressChanged(APWebViewCtrl view, int newProgress) {
        H5Log.d(TAG, "onProgressChanged [progress] " + newProgress);
        if (h5Page != null) {
            JSONObject param = new JSONObject();
            try {
                param.put(H5Container.KEY_PROGRESS, newProgress);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }

            h5Page.sendIntent(H5Plugin.H5_PAGE_PROGRESS, param);
        }
    }

    @Override
    public void onReceivedTitle(APWebViewCtrl view, String title) {
        H5Log.d(TAG, "onReceivedTitle [title] " + title);
        if (h5Page != null) {
            // save console
            view.loadUrl("javascript:{window.__JSBridgeConsole__ = window.console}");
            // send intent
            JSONObject param = new JSONObject();
            try {
                param.put(H5Container.KEY_TITLE, title);
            } catch (JSONException e) {
                H5Log.e(TAG, "exception", e);
            }
            h5Page.sendIntent(H5Plugin.H5_PAGE_RECEIVED_TITLE, param);
        }
    }

    @Override
    public void onReceivedIcon(APWebViewCtrl view, Bitmap icon) {
        H5Log.d(TAG, "onReceivedIcon");
    }

    @Override
    public void onReceivedTouchIconUrl(APWebViewCtrl view, String url,
                                       boolean precomposed) {
        H5Log.d(TAG, "onReceivedTouchIconUrl. [url] " + url + " [precomposed] "
                + precomposed);
    }

    @Override
    public boolean onJsBeforeUnload(APWebViewCtrl view, String url, String message,
                                    APJsResult result) {
        H5Log.d(TAG, "onJsBeforeUnload [url] " + url + " [message] " + message);
        return false;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {

    }

    @Override
    public View getVideoLoadingProgressView() {
        H5Log.d(TAG, "getVideoLoadingProgressView");
        return null;
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> valueCallback) {

    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser_l(uploadMsg);
    }

    //The undocumented magic method override
    //Eclipse will swear at you if you try to put @Override here
    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser_l(uploadMsg);
    }

    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        openFileChooser_l(uploadMsg);
    }

    private void openFileChooser_l(ValueCallback<Uri> uploadMsg) {
        if (h5Page.getContentView() != null) {
            Context ctx = h5Page.getContentView().getContext();
            try {
                H5Activity activity = (H5Activity) ctx;
                activity.setUploadMsg(uploadMsg);
                activity.startActivityForResult(createDefaultOpenableIntent(ctx),
                        H5Activity.FILECHOOSER_RESULTCODE);
                activity.setCameraFilePath(cameraFilePath);
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Intent createDefaultOpenableIntent(final Context ctx) {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");

        Intent chooser = createChooserIntent(ctx, createCameraIntent(), createCamcorderIntent(),
                createSoundRecorderIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(final Context ctx, Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, ctx.getString(R.string.file_chooser));
        return chooser;
    }

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                File.separator + "browser-photos");
        cameraDataDir.mkdirs();
        cameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                System.currentTimeMillis() + ".jpg";
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraFilePath)));
        return cameraIntent;
    }

    private Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        H5Log.d(TAG, "onShowCustomView [SDK Version] " + Build.VERSION.SDK_INT);
    }

    @Override
    public void onHideCustomView() {
        H5Log.d(TAG, "onShowCustomView [SDK Version] " + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= 12) {
//            super.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(APWebViewCtrl apWebViewCtrl, boolean b, boolean b2, Message message) {
        return false;
    }

    @Override
    public void onRequestFocus(APWebViewCtrl apWebViewCtrl) {

    }

    @Override
    public void onCloseWindow(APWebViewCtrl apWebViewCtrl) {

    }

    @Override
    public boolean onJsAlert(APWebViewCtrl apWebViewCtrl, String s, String s2, APJsResult apJsResult) {
        return false;
    }

    @Override
    public boolean onJsConfirm(APWebViewCtrl apWebViewCtrl, String s, String s2, APJsResult apJsResult) {
        return false;
    }

    @Override
    public boolean onJsPrompt(APWebViewCtrl apWebViewCtrl, String s, String s2, String s3, APJsPromptResult apJsPromptResult) {
        return false;
    }

    public void onRelease() {
        h5Page = null;
    }
}
