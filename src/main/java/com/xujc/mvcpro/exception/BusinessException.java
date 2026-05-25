package com.xujc.mvcpro.exception;

import com.xujc.mvcpro.common.ResponseCode;

public class BusinessException extends RuntimeException {
    
    private ResponseCode responseCode;
    private String message;
    
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.message = responseCode.getMessage();
    }
    
    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.message = message;
    }
    
    public BusinessException(String message) {
        super(message);
        this.responseCode = ResponseCode.INTERNAL_ERROR;
        this.message = message;
    }
    
    public ResponseCode getResponseCode() {
        return responseCode;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
