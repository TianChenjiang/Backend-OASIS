package com.rubiks.backendoasis.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Map;


@Data
@NoArgsConstructor
public class BasicResponse<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
    public BasicResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
