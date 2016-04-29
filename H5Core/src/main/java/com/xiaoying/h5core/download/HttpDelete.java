package com.xiaoying.h5core.download;

import org.apache.http.client.methods.HttpPost;

import java.net.URI;

public class HttpDelete extends HttpPost {

    public HttpDelete() {
        super();
    }

    public HttpDelete(String uri) {
        super(uri);
    }

    public HttpDelete(URI uri) {
        super(uri);
    }

    public String getMethod() {
        return "DELETE";
    }

}
