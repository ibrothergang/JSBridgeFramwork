package com.xiaoying.h5core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaoying.h5core.R;
import com.xiaoying.h5api.api.H5Intent;
import com.xiaoying.h5api.api.H5IntentFilter;
import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.api.H5Plugin;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class H5TitleBar implements H5Plugin {
    public static final String TAG = "H5TitleBar";
    private static final long CACHE_PERIOD = 1000 * 60 * 60 * 12L;
    private H5Page h5Page;

    private View content;
    private View optionMenu;
    private TextView tvTitle;
    private TextView tvSubtitle;

    private Button btText;
    private ImageButton btImage;

    private View dotView;
    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            String eventName = null;
            if (view.equals(btImage) || view.equals(btText)) {
                eventName = H5Plugin.H5_TITLEBAR_OPTIONS;
                if (dotView != null && dotView.getVisibility() == View.VISIBLE) {
                    dotView.setVisibility(View.GONE);
                }
            } else if (view.equals(tvSubtitle)) {
                eventName = H5Plugin.H5_TITLEBAR_SUBTITLE;
            } else if (view.equals(tvTitle)) {
                eventName = H5Plugin.H5_TITLEBAR_TITLE;
            }

            if (!TextUtils.isEmpty(eventName)) {
                h5Page.sendIntent(eventName, null);
            }
        }
    };

    @SuppressLint("InflateParams")
    public H5TitleBar(H5Page page) {
        h5Page = page;
        Context context = h5Page.getContext().getContext();
        content = LayoutInflater.from(context).inflate(R.layout.h5_title_bar,
                null);
        optionMenu = content.findViewById(R.id.h5_nav_options);
        hideOptionMenu();

        tvTitle = (TextView) content.findViewById(R.id.tv_h5_title);
        tvTitle.setOnClickListener(listener);

        tvSubtitle = (TextView) content.findViewById(R.id.tv_h5_subtitle);
        tvSubtitle.setVisibility(View.GONE);
        tvSubtitle.setOnClickListener(listener);

        btText = (Button) content.findViewById(R.id.bt_h5_text);

        btImage = (ImageButton) content.findViewById(R.id.bt_h5_image);

        dotView = content.findViewById(R.id.bt_h5_dot);

        btText.setOnClickListener(listener);
        btImage.setOnClickListener(listener);

        hideOptionMenu();
    }

    public View getContent() {
        return content;
    }

    @Override
    public void onRelease() {
        h5Page = null;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (SHOW_TITLE_BAR.equals(action)) {
            showTitleBar();
        } else if (HIDE_TITLE_BAR.equals(action)) {
            hideTitleBar();
        } else if (SHOW_OPTION_MENU.equals(action)) {
            showOptionMenu();
        } else if (SET_OPTION_MENU.equals(action)) {
            setOptionMenu(intent);
        } else if (HIDE_OPTION_MENU.equals(action)) {
            hideOptionMenu();
        } else if (SET_TITLE.equals(action)) {
            setTitle(intent);
        } else if (H5_SHOW_TIPS.equals(action)) {
            H5Tip.showTip(h5Page.getContext().getContext(),
                    (ViewGroup) content, H5Utils.getString(intent.getParam(),
                            H5Container.KEY_TIP_CONTENT));
        }
        return true;
    }

    private void setTitle(H5Intent intent) {
        JSONObject param = intent.getParam();
        try {
            if (param == null || param.length() == 0 || param.get(H5Container.KEY_TITLE) == null) {
                return;
            }
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }

        String title = H5Utils.getString(param, "title");
        String subtitle = H5Utils.getString(param, "subtitle");

        if (title != null) {
            tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(subtitle)) {
            tvSubtitle.setVisibility(View.VISIBLE);
            if (subtitle.length() > 5) {
                subtitle = subtitle.substring(0, 4) + "...";
            }
            tvSubtitle.setText(subtitle);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }
    }

    private void showTitleBar() {
        content.setVisibility(View.VISIBLE);
    }

    private void hideTitleBar() {
        content.setVisibility(View.GONE);
    }

    private void setOptionMenu(H5Intent intent) {
        JSONObject param = intent.getParam();
        if (param == null || param.length() == 0) {
            return;
        }

        String text = H5Utils.getString(param, "title");
        String iconUrl = H5Utils.getString(param, "icon");

        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(iconUrl)) {
            return;
        }

        showOptionMenu();
        if (!TextUtils.isEmpty(text)) {
            btText.setText(text);
            btText.setVisibility(View.VISIBLE);
            btImage.setVisibility(View.GONE);
            btImage.setImageDrawable(null);
        } else if (!TextUtils.isEmpty(iconUrl)) {
            btText.setVisibility(View.GONE);
            btText.setText(null);
            btImage.setVisibility(View.VISIBLE);
            loadImageAsync(iconUrl);
        }
    }

    private void showOptionMenu() {
        optionMenu.setVisibility(View.VISIBLE);
    }

    private void hideOptionMenu() {
        optionMenu.setVisibility(View.GONE);
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_TITLE_BAR);
        filter.addAction(HIDE_TITLE_BAR);
        filter.addAction(SHOW_OPTION_MENU);
        filter.addAction(HIDE_OPTION_MENU);
        filter.addAction(SET_OPTION_MENU);
        filter.addAction(SET_TITLE);
        filter.addAction(H5_SHOW_TIPS);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    private void setImageBackgroundOfButton(final Bitmap bitmap) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                btImage.setImageBitmap(bitmap);
                btImage.getParent().requestLayout();
            }
        });
    }

    public void loadImageAsync(String imageUrl) {
        //TODO implement load Image
//        imageLoaderService.startLoad(null, null, imageUrl, new ImageLoaderListener() {
//            @Override
//            public void onPostLoad(String arg0, Bitmap image) {
//                setImageBackgroundOfButton(image);
//            }
//
//            @Override
//            public void onFailed(String arg0, int arg1, String arg2) {
//            }
//
//            @Override
//            public void onCancelled(String arg0) {
//            }
//
//            @Override
//            public void onPreLoad(String arg0) {
//            }
//
//            @Override
//            public void onProgressUpdate(String arg0, double arg1) {
//            }
//
//        }, -1, -1, new ImageCacheListener() {
//            @Override
//            public long getCachePeriod(HttpUrlRequest httpUrlRequest,
//                                       HttpUrlResponse httpUrlResponse) {
//                return CACHE_PERIOD;
//            }
//        });
    }

}
