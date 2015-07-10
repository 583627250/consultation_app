package com.consultation.app.util;

import android.text.TextUtils;


public class ClientUtil {

    static {
    	SERVICE_DOMAIN="http://192.168.11.100:7001";
    }
    
    private static String SERVICE_DOMAIN;

    private static String ticket;

    public static String getTicket() {
        return ClientUtil.ticket == null ? "" : ClientUtil.ticket;
    }

    public static void setTicket(String ticket) {
        if(!TextUtils.isEmpty(ticket)) {
            ClientUtil.ticket=ticket;
        } else {
            ClientUtil.ticket="";
        }
    }

    public static boolean isLogin() {
        boolean islogin=false;
        if(ClientUtil.ticket != null && ClientUtil.ticket.length() > 20) {
            islogin=true;
        }
        return islogin;
    }
}
