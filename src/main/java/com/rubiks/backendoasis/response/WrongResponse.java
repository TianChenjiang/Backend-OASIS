package com.rubiks.backendoasis.response;

import lombok.Data;

@Data
public class WrongResponse {
    private int code;
    private String msg;
    public WrongResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
