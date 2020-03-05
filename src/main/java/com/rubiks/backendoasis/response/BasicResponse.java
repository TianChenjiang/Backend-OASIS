package com.rubiks.backendoasis.response;

import lombok.Data;

import java.util.Dictionary;
import java.util.Map;


@Data
public class BasicResponse<T> {
    private int code;
    private String msg;
    private T data;
    public BasicResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
