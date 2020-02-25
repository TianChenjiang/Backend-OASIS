package com.rubiks.backendoasis.response;

import lombok.Data;

@Data
public class SuccessResponse extends Response {
    private int code;
    private String msg;

    public SuccessResponse() {
        this.code = 200;
        this.msg = "Success";
    }
}
