package com.xiaoying.h5container;

import com.xiaoying.h5api.api.H5Bundle;
import com.xiaoying.h5api.api.H5Context;
import com.xiaoying.h5api.api.H5Param;
import com.xiaoying.h5api.api.H5Service;
import com.xiaoying.h5core.env.H5Container;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by android_mc on 16/4/29.
 */
public class MainActivity extends Activity {
    private EditText page;
    private Button open;
    private H5Service h5Service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        h5Service = H5Container.getService();
        open = (Button) findViewById(R.id.open);

        page = (EditText) findViewById(R.id.startpage);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pages = TextUtils.isEmpty(page.getText().toString()) ?
                        "http://119.29.187.147/McServer/index.html" : page.getText().toString();
                if (h5Service != null) {
                    H5Context h5Context = new H5Context(getApplicationContext());
                    H5Bundle h5Bundle = new H5Bundle();
                    Bundle bundle = new Bundle();
                    bundle.putString(H5Param.URL, pages);
                    h5Bundle.setParams(bundle);
                    h5Service.startPage(h5Context, h5Bundle);
                }
            }
        });
    }
}
