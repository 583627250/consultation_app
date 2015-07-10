package com.consultation.app.exception;

public class ConsultationCallbackException extends Exception {

    private static final long serialVersionUID=1L;

    private final int errorCode; // http错误码，用于确定接口调用大错误类型

    private final String errorMessage; // 错误信息

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ConsultationCallbackException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode=errorCode;
        this.errorMessage=errorMessage;
    }
}
