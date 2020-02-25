package com.rubiks.backendoasis.response;

import lombok.Data;

@Data
public class WrongResponse {
    private int code;
    private String msg;
    public WrongResponse() {
        this.code = 500;
        this.msg = "Internal Server Error";
    }
}
