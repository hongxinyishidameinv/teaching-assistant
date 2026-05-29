package com.xujc.mvcpro.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private int code;
    private String message;
    private Object data;

    public static ApiResponse ok() {
        return new ApiResponse(true, ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }

    public static ApiResponse ok(Object data) {
        return new ApiResponse(true, ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(true, ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static ApiResponse error(ResponseCode responseCode) {
        return new ApiResponse(false, responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static ApiResponse error(ResponseCode responseCode, String message) {
        return new ApiResponse(false, responseCode.getCode(), message, null);
    }

    public static ApiResponse error(int code, String message) {
        return new ApiResponse(false, code, message, null);
    }
}