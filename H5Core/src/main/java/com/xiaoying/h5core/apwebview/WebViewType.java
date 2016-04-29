package com.xiaoying.h5core.apwebview;

/**
 * enum type to identify the type of web view so as to
 * meet some potential biz needs, such as:
 * <p/>
 * 1) performance analysis & evaluate online by specified web view
 * 2) user behavior tracking
 * 2) help biz to adapt to the difference of web view
 *
 * @author xide.wf
 */
public enum WebViewType {
    /* android build-in web view */
    SYSTEM_BUILD_IN,
    /* third-party provided web view */
    /* currently used to identify the UC provided web view implement */
    THIRD_PARTY
}
