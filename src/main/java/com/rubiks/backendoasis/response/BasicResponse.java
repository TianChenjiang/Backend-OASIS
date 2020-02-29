package com.rubiks.backendoasis.response;

import lombok.Data;

@Data
public class BasicResponse<T> {
    private int code;
    private String message;
    private T data;
    public BasicResponse(int code, String message, T data) {
        this.code = 200;
        this.message = "Success";
        this.data = data;
    }
}
