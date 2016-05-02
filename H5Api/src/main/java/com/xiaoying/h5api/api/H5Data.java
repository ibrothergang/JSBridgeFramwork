package com.xiaoying.h5api.api;

public interface H5Data {

    public void set(String name, String value);

    public String get(String name);

    public String remove(String name);

    public boolean has(String name);

}
