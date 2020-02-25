package com.rubiks.backendoasis.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private int code;
    private String message;
}
