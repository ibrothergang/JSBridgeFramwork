package com.xiaoying.h5core.apwebview;

/**
 * similar role to HttpAuthHandler in android sdk
 *
 * @author xide.wf
 */
public interface APHttpAuthHandler {
    public void cancel();

    public void proceed(String username, String password);

    public boolean useHttpAuthUsernamePassword();
}
