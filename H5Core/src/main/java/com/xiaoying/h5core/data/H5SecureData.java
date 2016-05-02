package com.xiaoying.h5core.data;

import com.xiaoying.h5api.api.H5Data;

import java.util.HashMap;
import java.util.Map;

public class H5SecureData implements H5Data {

    // TODO implement real secure data

    private Map<String, String> map;

    public H5SecureData() {
        map = new HashMap<String, String>();
    }

    @Override
    public void set(String name, String value) {
        map.put(name, value);
    }

    @Override
    public String get(String name) {
        return map.get(name);
    }

    @Override
    public String remove(String name) {
        String value = map.remove(name);
        return value;
    }

    @Override
    public boolean has(String name) {
        return map.containsKey(name);
    }
}
