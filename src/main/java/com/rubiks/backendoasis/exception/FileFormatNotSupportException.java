package com.rubiks.backendoasis.exception;

import com.rubiks.backendoasis.response.WrongResponse;
import lombok.Data;

@Data
public class FileFormatNotSupportException extends RuntimeException {
    private int code = 10001;
    public FileFormatNotSupportException() {
        super("该文件类型不支持！");
    }
}
