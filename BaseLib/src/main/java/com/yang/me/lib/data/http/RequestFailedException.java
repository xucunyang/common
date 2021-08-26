package com.yang.me.lib.data.http;

import java.io.IOException;

/**
 * Created by lvfeng on 2019/4/15
 */
public class RequestFailedException extends IOException {
    private int code;

    public RequestFailedException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
