package com.xiaoying.h5core.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoying.h5core.R;
import com.xiaoying.h5core.api.H5Intent;
import com.xiaoying.h5core.api.H5IntentFilter;
import com.xiaoying.h5core.api.H5Page;
import com.xiaoying.h5core.api.H5Param;
import com.xiaoying.h5core.api.H5Plugin;
import com.xiaoying.h5core.env.H5Container;
import com.xiaoying.h5core.env.H5Environment;
import com.xiaoying.h5core.util.H5Log;
import com.xiaoying.h5core.util.H5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class H5NavigationBar implements H5Plugin, TitleProvider {

    public static final String TAG = "H5NavigationBar";

    private static final long CACHE_PERIOD = 1000 * 60 * 60 * 12L;
    private H5Page h5Page;

    ;
    private H5RelativeLayout contentView;
    private TextView tvBack;
    private View btClose;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private View h5NavOptions;
    private View h5Title;
    private TextView btText;
    private ImageButton btIcon;
    private View btMenu;
    private View btDotView;
    private ImageView dotImage;
    private TextView dotText;
    private boolean readTitle;
    private boolean pageStarted;
    private H5NavMenu h5NavMenu;
    private Map<String, String> backText;
    private String defaultTitle;
    private boolean ignorePageTitleFromCallbacks;
    private H5MeasureListener layoutListener = new H5MeasureListener() {

        @Override
        public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (btClose == null || h5NavOptions == null || tvBack == null) {
                H5Log.w(TAG, "invalid layout elements!");
                return;
            }

            int left = H5Utils.dip2px(12) + tvBack.getMeasuredWidth();
            if (btClose.getVisibility() == View.VISIBLE) {
                left = left + H5Utils.dip2px(6) + btClose.getMeasuredWidth();
            }
            left += H5Utils.dip2px(16);

            int right = 0;
            if (h5NavOptions.getVisibility() == View.VISIBLE) {
                right = right + h5NavOptions.getMeasuredWidth();
            }
            right = right + H5Utils.dip2px(16);

            int paddingLeft = left > right ? left : right;
            int paddingRight = paddingLeft;

            int parentWidth = ((ViewGroup) h5Title.getParent()).getWidth();
            int perfectWidth = parentWidth - paddingLeft - paddingRight;
            int maxWidth = parentWidth - left - right;

            int textWidth = (int) tvTitle.getPaint().measureText(tvTitle.getText().toString());
            if (tvSubtitle.getVisibility() == View.VISIBLE) {
                textWidth += (int) tvSubtitle.getPaint()
                        .measureText(tvSubtitle.getText().toString()) + H5Utils.dip2px(40);
            }
            if (textWidth > perfectWidth && textWidth <= maxWidth) {
                paddingRight = paddingRight - (textWidth - perfectWidth);
            } else if (textWidth > maxWidth) {
                paddingRight = right;
            }

            H5Log.d(TAG, "title padding left " + paddingLeft + " right " + paddingRight);
            h5Title.setPadding(paddingLeft, 0, paddingRight, 0);
        }

    };
    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (h5Page == null) {
                // page already released
                return;
            }

            String eventName = null;
            if (view.equals(tvBack)) {
                eventName = H5Plugin.H5_TOOLBAR_BACK;
            } else if (view.equals(btClose)) {
                eventName = H5Plugin.H5_TOOLBAR_CLOSE;
            } else if (view.equals(btIcon) || view.equals(btText)) {
                eventName = H5Plugin.H5_TITLEBAR_OPTIONS;
            } else if (view.equals(tvSubtitle)) {
                eventName = H5Plugin.H5_TITLEBAR_SUBTITLE;
            } else if (view.equals(tvTitle)) {
                eventName = H5Plugin.H5_TITLEBAR_TITLE;
            } else if (view.equals(btMenu)) {
                h5NavMenu.showMenu(contentView);
            }

            // hide red dot
            if (view.equals(btIcon) || view.equals(btText)
                    || view.equals(btMenu)) {
                btDotView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(eventName)) {
                h5Page.sendIntent(eventName, null);
            }
        }
    };

    public H5NavigationBar(H5Page page) {
        this.ignorePageTitleFromCallbacks = false;
        this.pageStarted = false;
        backText = new HashMap<String, String>();
        h5Page = page;
        Context context = page.getContext().getContext();
        h5NavMenu = new H5NavMenu(page);
        h5NavMenu.setTitleProvider(this);
        contentView = (H5RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.h5_navigation_bar, null);

        tvBack = (TextView) contentView.findViewById(R.id.tv_nav_back);
        h5Title = contentView.findViewById(R.id.ll_h5_title);

        btClose = contentView.findViewById(R.id.h5_nav_close);

        tvTitle = (TextView) contentView.findViewById(R.id.tv_h5_title);
        tvTitle.setOnClickListener(listener);

        tvSubtitle = (TextView) contentView.findViewById(R.id.tv_h5_subtitle);
        tvSubtitle.setVisibility(View.GONE);
        tvSubtitle.setOnClickListener(listener);

        h5NavOptions = contentView.findViewById(R.id.h5_nav_options);

        btText = (TextView) contentView.findViewById(R.id.bt_h5_text);
        btIcon = (ImageButton) contentView.findViewById(R.id.bt_h5_image);
        btMenu = contentView.findViewById(R.id.bt_h5_options);
        btDotView = contentView.findViewById(R.id.bt_h5_dot);

        dotImage = (ImageView) contentView.findViewById(R.id.bt_h5_dot_bg);
        dotText = (TextView) contentView.findViewById(R.id.bt_h5_dot_number);

        tvBack.setOnClickListener(listener);
        btClose.setOnClickListener(listener);
        btText.setOnClickListener(listener);
        btIcon.setOnClickListener(listener);
        btMenu.setOnClickListener(listener);

        // initial state
        showNavOptions(true);
        showClose(false);

        contentView.setLayoutListener(layoutListener);
    }

    public View getContent() {
        return contentView;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        filter.addAction(SHOW_TITLE_BAR);
        filter.addAction(HIDE_TITLE_BAR);
        filter.addAction(SHOW_OPTION_MENU);
        filter.addAction(HIDE_OPTION_MENU);
        filter.addAction(SET_OPTION_MENU);
        filter.addAction(SET_TITLE);
        filter.addAction(READ_TITLE);
        filter.addAction(SET_TOOL_MENU);
        filter.addAction(H5_SHOW_TIPS);
        filter.addAction(H5_PAGE_STARTED);
        filter.addAction(H5_PAGE_FINISHED);
        filter.addAction(H5_PAGE_SHOW_CLOSE);
        filter.addAction(H5_PAGE_RECEIVED_TITLE);
        filter.addAction(H5Container.H5_PAGE_SET_BACK_TEXT);
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        if (H5_PAGE_RECEIVED_TITLE.equals(action)) {
            setPageTitle(param);
        } else if (H5_PAGE_STARTED.equals(action)) {
            pageStarted = true;
            ignorePageTitleFromCallbacks = false;
            tvSubtitle.setText("");
            tvSubtitle.setVisibility(View.GONE);

            // reset option menu
            setOptionType(OptionType.MENU);
            h5NavMenu.resetMenu();

            // reset back button text
            String url = H5Utils.getString(param, H5Param.LONG_URL);
            String text = backText.get(url);
            if (TextUtils.isEmpty(text)) {
                text = H5Environment.getResources().getString(R.string.h5_backward);
            }
            tvBack.setText(text);
        } else if (H5_PAGE_FINISHED.equals(action)) {
            boolean pageUpdated = H5Utils.getBoolean(param, "pageUpdated", false);
            if (pageUpdated) {
                setPageTitle(param);
            }
            CharSequence currentTitle = tvTitle.getText();
            if (!TextUtils.isEmpty(defaultTitle) && TextUtils.isEmpty(currentTitle)) {
                tvTitle.setText(defaultTitle);
            }
        }
        return false;
    }

    private void setPageTitle(JSONObject param) {
        try {
            if (param.get(H5Container.KEY_TITLE) == null) {
                H5Log.d(TAG, "case 1, page title ignored!");
                return;
            }
        } catch (JSONException e) {
            H5Log.e(TAG, "exception", e);
        }


        if (readTitle && !ignorePageTitleFromCallbacks) {
            String title = H5Utils.getString(param, H5Container.KEY_TITLE);
            // if need to set title empty, should use string with blank(" ")
            tvTitle.setText(title);
        } else {
            H5Log.d(TAG, "case 2, page title ignored!");
        }
    }

    @Override
    public void onRelease() {
        h5Page = null;
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        JSONObject param = intent.getParam();
        if (SHOW_TITLE_BAR.equals(action)) {
            showTitleBar(true);
        } else if (HIDE_TITLE_BAR.equals(action)) {
            showTitleBar(false);
        } else if (SHOW_OPTION_MENU.equals(action)) {
            showNavOptions(true);
        } else if (SET_OPTION_MENU.equals(action)) {
            setOptionMenu(intent);
        } else if (HIDE_OPTION_MENU.equals(action)) {
            showNavOptions(false);
        } else if (SET_TITLE.equals(action)) {
            setTitle(intent);
        } else if (READ_TITLE.equals(action)) {
            readTitle = H5Utils
                    .getBoolean(param, H5Param.LONG_READ_TITLE, true);
        } else if (H5_SHOW_TIPS.equals(action)) {
            H5Tip.showTip(h5Page.getContext().getContext(),
                    (ViewGroup) contentView, H5Utils.getString(
                            intent.getParam(), H5Container.KEY_TIP_CONTENT));
        } else if (H5_PAGE_SHOW_CLOSE.equals(action)) {
            boolean show = H5Utils.getBoolean(param, "show", false);
            showClose(show);
        } else if (SET_TOOL_MENU.equals(action)) {
            try{
                h5NavMenu.setMenu(intent, pageStarted);
            }catch (JSONException e){
                H5Log.e(TAG,"exception",e);
            }

        } else if (H5Container.H5_PAGE_SET_BACK_TEXT.equals(action)) {
            setBackText(intent);
        } else {
            return false;
        }
        return true;
    }

    private void setBackText(H5Intent intent) {
        JSONObject param = intent.getParam();
        String text = H5Utils.getString(param, "text");
        String url = H5Utils.getString(param, H5Param.LONG_URL);
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(url)) {
            return;
        }
        if (text.length() > 3) {
            text = text.substring(0, 3);
        }
        tvBack.setText(text);
        backText.put(url, text);
    }

    private void setOptionType(OptionType type) {
        boolean icon = false;
        boolean text = false;
        boolean menu = false;
        if (type == OptionType.ICON) {
            icon = true;
        } else if (type == OptionType.TEXT) {
            text = true;
        } else if (type == OptionType.MENU) {
            menu = true;
        }

        btText.setVisibility(text ? View.VISIBLE : View.INVISIBLE);
        btIcon.setVisibility(icon ? View.VISIBLE : View.INVISIBLE);
        btMenu.setVisibility(menu ? View.VISIBLE : View.INVISIBLE);
    }

    private void showClose(boolean show) {
        if (show) {
            btClose.setVisibility(View.VISIBLE);
        } else {
            btClose.setVisibility(View.GONE);
        }
        contentView.requestLayout();
    }

    private void showTitleBar(boolean show) {
        if (show) {
            contentView.setVisibility(View.VISIBLE);
        } else {
            contentView.setVisibility(View.GONE);
        }
    }

    private void showNavOptions(boolean show) {
        if (show) {
            h5NavOptions.setVisibility(View.VISIBLE);
        } else {
            h5NavOptions.setVisibility(View.GONE);
        }
    }

    private void setOptionMenu(H5Intent intent) {
        JSONObject param = intent.getParam();

        String text = H5Utils.getString(param, "title");
        String iconUrl = H5Utils.getString(param, "icon");
        String redDot = H5Utils.getString(param, "redDot");

        if (!TextUtils.isEmpty(text)) {
            text = text.trim();
            if (text.length() > 4) {
                text = text.substring(0, 4);
            }
            btText.setText(text);
            setOptionType(OptionType.TEXT);
        } else if (!TextUtils.isEmpty(iconUrl)) {
            btIcon.setImageBitmap(null);
            setOptionType(OptionType.ICON);
            loadImageAsync(iconUrl);
        }

        if (!TextUtils.isEmpty(redDot)) {
            int dotNum = -1;
            try {
                dotNum = Integer.parseInt(redDot);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int visibility = dotNum >= 0 ? View.VISIBLE : View.GONE;
            btDotView.setVisibility(visibility);

            if (dotNum == 0) {
                dotImage.setVisibility(View.VISIBLE);
                dotText.setVisibility(View.GONE);
            } else if (dotNum > 0) {
                dotText.setVisibility(View.VISIBLE);
                dotImage.setVisibility(View.GONE);
                dotText.setText(dotNum + "");
            }
        }
    }

    public void loadImageAsync(String imageUrl) {
        //TODO loadImage
//        imageLoaderService.startLoad(null, null, imageUrl, new ImageLoaderListener() {
//            @Override
//            public void onPostLoad(String arg0, Bitmap image) {
//                setOptionImage(image);
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

    private void setOptionImage(final Bitmap bitmap) {
        H5Utils.runOnMain(new Runnable() {

            @Override
            public void run() {
                btIcon.setImageBitmap(bitmap);
            }
        });
    }

    public byte[] getImage(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(imageUrl);
        } catch (Exception e) {
            H5Log.e(TAG, "get url exception.", e);
            return null;
        }
        HttpURLConnection conn = null;

        ByteArrayOutputStream bos = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);

            int resCode = conn.getResponseCode();
            if (resCode != 200) {
                H5Log.w(TAG, "get image response " + resCode);
                return null;
            }

            InputStream is = conn.getInputStream();

            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            // limit icon size to 20k
            while ((len = is.read(buffer)) != -1 && bos.size() <= 20480) {
                bos.write(buffer, 0, len);
            }
            is.close();
        } catch (Exception e) {
            H5Log.e(TAG, "get image exception.", e);
        }
        if (bos != null) {
            return bos.toByteArray();
        } else {
            return null;
        }
    }

    @Override
    public String getTitle() {
        if (tvTitle != null) {
            return String.valueOf(tvTitle.getText());
        }
        return null;
    }

    private void setTitle(H5Intent intent) {
        JSONObject param = intent.getParam();
        if (param == null || param.length() == 0) {
            return;
        }

        // if title set by javascript, page title from webview client is ignored
        // (onReceivedTitle, onPageFinished);
        if (H5Utils.getBoolean(param, "fromJS", true)) {
            ignorePageTitleFromCallbacks = true;
        }
        String title = H5Utils.getString(param, "title");
        String subtitle = H5Utils.getString(param, "subtitle");

        if (!TextUtils.isEmpty(title)) {
            if (!pageStarted) {
                defaultTitle = title;
            }
            tvTitle.setText(title.trim());
        }

        if (!TextUtils.isEmpty(subtitle)) {
            tvSubtitle.setVisibility(View.VISIBLE);
            if (subtitle.length() > 7) {
                subtitle = subtitle.substring(0, 4) + "...";
            }
            tvSubtitle.setText(subtitle);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }

        contentView.requestLayout();
        contentView.invalidate();
    }

    private static enum OptionType {
        ICON, TEXT, MENU,
    }

}
