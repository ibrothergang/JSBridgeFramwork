package com.xiaoying.h5core.plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView.HitTestResult;
import android.widget.Toast;

import com.xiaoying.h5core.R;
import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5core.apwebview.APHitTestResult;
import com.xiaoying.h5core.core.H5PageImpl;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

public class H5LongClickPlugin implements H5Plugin, OnLongClickListener {

    public static final String TAG = "H5LongClickPlugin";

    private Activity activity;
    private H5PageImpl h5Page;

    public H5LongClickPlugin(H5PageImpl h5Page) {
        this.h5Page = h5Page;
        Context context = h5Page.getContext().getContext();
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
        setLongClickListener(h5Page, this);
    }

    private static void setLongClickListener(final H5PageImpl page,
                                             final OnLongClickListener listener) {
        if (page != null) {
            final View webView = page.getWebView().getUnderlyingWebView();
            if (webView != null) {
                webView.setOnLongClickListener(listener);
            }
        }
    }

    @Override
    public void onRelease() {
        setLongClickListener(h5Page, null);
        h5Page = null;
        activity = null;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        return false;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {

    }

    @Override
    public boolean onLongClick(View view) {
        if (h5Page == null || h5Page.getWebView() == null) {
            H5Log.e(TAG, "h5Page is lost in onLongClick(), fatal error!");
            return false;
        }
        final APHitTestResult result = h5Page.getWebView().getHitTestResult();
        boolean image = result != null
                && (result.getType() == HitTestResult.IMAGE_TYPE || result
                .getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE);
        if (image && !TextUtils.isEmpty(result.getExtra())) {

            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        saveImage(result.getExtra());
                    }
                    if (dialog != null && activity != null && !activity.isFinishing()) {
                        try {
                            dialog.dismiss();
                        } catch (Throwable t) {
                            H5Log.e(TAG, "dismiss exception.");
                        }
                    }
                }
            };
            String savePhone = H5Environment.getResources().getString(
                    R.string.save_to_phone);
            String[] items = new String[]{
                    savePhone
            };
            Dialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.image).setItems(items, listener)
                    .create();
            dialog.setCanceledOnTouchOutside(true);
            if (activity != null && !activity.isFinishing()) {
                dialog.show();
            }
            return true;
        }
        return false;
    }

    private void saveImage(String url) {
        H5Container.getExecutorService().execute(new ImageSaveRunner(url));
    }

    class ImageSaveRunner implements Runnable {
        String url;
        String filePath;

        public ImageSaveRunner(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            final boolean succeed = download();

            H5Utils.runOnMain(new Runnable() {

                @Override
                public void run() {
                    if (activity == null) {
                        return;
                    }
                    String message;
                    if (succeed) {
                        message = H5Environment.getResources().getString(
                                R.string.save_image_to, filePath);
                    } else {
                        message = H5Environment.getResources().getString(
                                R.string.save_image_failed);
                    }
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        private boolean download() {
            //TODO
//            try {
//                HttpGet httpGet = new HttpGet(url);
//                HttpClient client = new DefaultHttpClient();
//                HttpResponse httpResponse;
//                httpResponse = client.execute(httpGet);
//                int statusCode = httpResponse.getStatusLine().getStatusCode();
//                if (statusCode != HttpStatus.SC_OK) {
//                    return false;
//                }
//                InputStream is = httpResponse.getEntity().getContent();
//                String extension = null;
//                try {
//                    extension = url.substring(url.lastIndexOf("."));
//                } catch (IndexOutOfBoundsException e) {
//                    extension = ".jpg";
//                }
//
//                if (extension != null && extension.length() > 4) {
//                    extension = ".jpg";
//                }
//
//               
//                if (!FileUtil.create(filePath)) {
//                    H5Log.w(TAG, "failed to create file " + filePath);
//                    return false;
//                }
//                File imageFile = new File(filePath);
//                FileOutputStream fos = new FileOutputStream(imageFile);
//                int count;
//                byte[] buffer = new byte[1024];
//                while ((count = is.read(buffer)) > 0) {
//                    fos.write(buffer, 0, count);
//                }
//                fos.flush();
//                is.close();
//                fos.close();
//
//                MediaScannerConnection.scanFile(H5Environment.getContext(),
//                        new String[] {
//                            filePath
//                        }, new String[] {
//                            "image/*"
//                        },
//                        null);
//            } catch (Exception e) {
//                H5Log.e(TAG, "save image exception", e);
//                return false;
//            }
            return true;
        }
    }

}
