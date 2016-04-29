package com.xiaoying.h5core.apwebview;

/**
 * similar role to SslErrorHandler in android sdk
 *
 * @author xide.wf
 */
public interface APSslErrorHandler {
    public void cancel();

    public void proceed();
}
