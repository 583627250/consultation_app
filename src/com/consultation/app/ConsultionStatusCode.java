package com.consultation.app;

public interface ConsultionStatusCode {   

    // 对于整个系统通用的状态
    public static final int SUCCESS=1;

    public static final int FAIL=-1;

    public static final int NO_NETWORK=-2;

    // 选择界面
    public static final int SELECT_HOSPITAL=-10;
    
    public static final int SELECT_DEPARTMENT=-11;
    
    public static final int SELECT_TITLE=-12;

    // 登录相关
    public static final int USER_LOGIN_SUCCESS=-20;
    
    public static final int USER_LOGIN_AUTH_FAIL=-21;

    public static final int USER_LOGIN_CANCEL=-22;

    public static final int USER_NOT_LOGIN=-23;

    // 支付相关
    public static final int PAY_USER_SUCCESS=-30;
    
    public static final int PAY_USER_FAIL=-31;
    
    public static final int PAY_USER_CANCEL=-32;
    
}
