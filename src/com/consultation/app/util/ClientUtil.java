package com.consultation.app.util;

import android.text.TextUtils;


public class ClientUtil {

    static {
    	SERVICE_DOMAIN="http://kydoo.honrystar.com";
    }
    
    private static String SERVICE_DOMAIN;
    
    public static String GET_KNOWLEDGE_LIST_URL = SERVICE_DOMAIN +"/api/knowledge/list.json";

    public static String GET_KNOWLEDGE_BYID_URL = SERVICE_DOMAIN +"/h5/knowledge/content.shtml";
    
    public static String GET_REGISTER_VERIFICATION_URL = SERVICE_DOMAIN +"/api/user/smsVerifyCode.json";
    
    public static String GET_REGISTER_MOBILEUSABLE_URL = SERVICE_DOMAIN +"/api/user/mobileUsable.json";
    
    public static String GET_REGISTER_URL = SERVICE_DOMAIN +"/api/user/register.json";
    
    public static String GET_LOGIN_URL = SERVICE_DOMAIN +"/api/user/login.json";
    
    public static String GET_LOGIN_IMAGE_URL = SERVICE_DOMAIN +"/ImageServlet";
    
    public static String GET_KNOWLEDGE_LIST_BY_TITLE_URL = SERVICE_DOMAIN +"/ImageServlet";
    
    public static String GET_SUBMIT_USER_INFO_URL = SERVICE_DOMAIN +"/api/user/saveUserInfo.json";
   
    public static String GET_EXPERTLIST_URL = SERVICE_DOMAIN +"/api/doctor/expertList.json";
    
    public static String GET_EXPERT_INFO_URL = SERVICE_DOMAIN +"/api/doctor/getById.json";
    
    public static String GET_PATIENTCASE_LIST_URL = SERVICE_DOMAIN +"/api/patientCase/list.json";

    
    
    private static String token;

    public static String getToken() {
        return ClientUtil.token == null ? "" : ClientUtil.token;
    }

    public static void setToken(String token) {
        if(!TextUtils.isEmpty(token)) {
            ClientUtil.token=token;
        } else {
            ClientUtil.token="";
        }
    }

    public static boolean isLogin() {
        boolean islogin=false;
        if(ClientUtil.token != null && ClientUtil.token.length() > 20) {
            islogin=true;
        }
        return islogin;
    }
}
