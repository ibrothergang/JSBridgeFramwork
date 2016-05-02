package com.xiaoying.h5core.apwebview;

public interface APHttpAuthHandler {
    public void cancel();

    public void proceed(String username, String password);

    public boolean useHttpAuthUsernamePassword();
}
