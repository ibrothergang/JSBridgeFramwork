package com.xiaoying.h5core.apwebviewwrapper;

import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.support.annotation.NonNull;

class AndroidSslError extends SslError {
    private SslError sslError;

    AndroidSslError(int error, SslCertificate certificate, SslError sslError) {
        super(error, certificate);
        if (sslError == null) {
            this.sslError = new NullSslError(error, certificate);
        } else {
            this.sslError = sslError;
        }
    }

    @Override
    public SslCertificate getCertificate() {
        return sslError.getCertificate();
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public boolean addError(int error) {
        if (sslError != null) {
            return sslError.addError(error);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasError(int error) {
        return sslError.hasError(error);
    }

    @Override
    public int getPrimaryError() {
        return sslError.getPrimaryError();
    }

    private class NullSslError extends SslError {
        public NullSslError(int error, SslCertificate certificate) {
            super(error, certificate);
        }

        @NonNull
        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public boolean addError(int error) {
            return false;
        }

        @Override
        public boolean hasError(int error) {
            return false;
        }

        @Override
        public int getPrimaryError() {
            return super.getPrimaryError();
        }

        @Override
        public String toString() {
            return "Null SslError instance";
        }
    }
}
