package com.rubiks.backendoasis.exception;

import lombok.Data;

@Data
public class NoSuchYearException extends RuntimeException {
    private int code = 10002;
    public NoSuchYearException() {
        super("没有此年份的电影！");
    }
}
