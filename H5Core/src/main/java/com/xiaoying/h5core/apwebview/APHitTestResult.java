package com.xiaoying.h5core.apwebview;

public interface APHitTestResult {
    /**
     * Default HitTestResult, where the target is unknown.
     */
    public static final int UNKNOWN_TYPE = 0;
    /**
     * @deprecated This type is no longer used.
     */
    @Deprecated
    public static final int ANCHOR_TYPE = 1;
    /**
     * HitTestResult for hitting a phone number.
     */
    public static final int PHONE_TYPE = 2;
    /**
     * HitTestResult for hitting a map address.
     */
    public static final int GEO_TYPE = 3;
    /**
     * HitTestResult for hitting an email address.
     */
    public static final int EMAIL_TYPE = 4;
    /**
     * HitTestResult for hitting an HTML::img tag.
     */
    public static final int IMAGE_TYPE = 5;
    /**
     * @deprecated This type is no longer used.
     */
    @Deprecated
    public static final int IMAGE_ANCHOR_TYPE = 6;
    /**
     * HitTestResult for hitting a HTML::a tag with src=http.
     */
    public static final int SRC_ANCHOR_TYPE = 7;
    /**
     * HitTestResult for hitting a HTML::a tag with src=http + HTML::img.
     */
    public static final int SRC_IMAGE_ANCHOR_TYPE = 8;
    /**
     * HitTestResult for hitting an edit text area.
     */
    public static final int EDIT_TEXT_TYPE = 9;

    public String getExtra();

    public int getType();
}
