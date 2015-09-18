package com.consultation.app.util;

import android.text.TextUtils;

import com.consultation.app.CaseParams;

public class ClientUtil {

    static {
        SERVICE_DOMAIN="http://kydoo.honrystar.com";
    }

    private static String SERVICE_DOMAIN;

    public static String GET_REFRESHACCESSTOKEN_URL=SERVICE_DOMAIN + "/api/user/refreshAccessToken.json ";
    
    public static String GET_KNOWLEDGE_LIST_URL=SERVICE_DOMAIN + "/api/knowledge/list.json";

    public static String GET_KNOWLEDGE_BYID_URL=SERVICE_DOMAIN + "/h5/knowledge/content.shtml";

    public static String GET_REGISTER_VERIFICATION_URL=SERVICE_DOMAIN + "/api/user/smsVerifyCode.json";

    public static String GET_REGISTER_MOBILEUSABLE_URL=SERVICE_DOMAIN + "/api/user/mobileUsable.json";
    
    public static String GET_FORGET_MOBILEUSABLE_URL=SERVICE_DOMAIN + "/api/user/smsVerifyCode4Forgot.json";
    
    public static String GET_PATIENT_MOBILEUSABLE_URL=SERVICE_DOMAIN + "/api/user/sentPatientSmsVerifyCode.json";
    
    public static String GET_FORGET_PWD_URL=SERVICE_DOMAIN + "/api/user/forgetPwd.json";

    public static String GET_REGISTER_URL=SERVICE_DOMAIN + "/api/user/register.json";

    public static String GET_LOGIN_URL=SERVICE_DOMAIN + "/api/user/login.json";

    public static String GET_LOGIN_IMAGE_URL=SERVICE_DOMAIN + "/ImageServlet";

    public static String GET_KNOWLEDGE_LIST_BY_TITLE_URL=SERVICE_DOMAIN + "/ImageServlet";

    public static String GET_SUBMIT_USER_INFO_URL=SERVICE_DOMAIN + "/api/user/saveUserInfo.json";

    public static String GET_EXPERTLIST_URL=SERVICE_DOMAIN + "/api/doctor/expertList.json";

    public static String GET_EXPERT_INFO_URL=SERVICE_DOMAIN + "/api/doctor/getById.json";

    public static String GET_PATIENTCASE_LIST_URL=SERVICE_DOMAIN + "/api/patientCase/list.json";
    
    public static String GET_HELP_PATIENT_LIST_URL=SERVICE_DOMAIN + "/api/patientCase/listCases4Expert.json";
    
    public static String GET_PATIENTCASE_INFO_URL=SERVICE_DOMAIN + "/api/patientCase/getById2.json";
    
    public static String GET_PATIENT_INFO_URL =SERVICE_DOMAIN + "/api/user/getByMobile.json";
    
    public static String GET_CASE_SAVE_URL =SERVICE_DOMAIN + "/api/patientCase/save.json";
    
    public static String GET_IS_PATIENT_URL =SERVICE_DOMAIN + "/api/user/verifySmsCodeOfPatient.json";
    
    public static String GET_CASE_UP_ALL_URL =SERVICE_DOMAIN + "/api/caseContent/save.json";
    
    public static String GET_CASE_SUBMIT_ALL_URL =SERVICE_DOMAIN + "/api/patientCase/submitById.json";
    
    public static String GET_UPLOAD_IMAGES_URL =SERVICE_DOMAIN + "/api/caseFiles/multiUpload.json";
   
    public static String GET_DOCTOR_COMMENT_URL =SERVICE_DOMAIN + "/api/doctorComment/listByDoctorUserid.json";
    
    public static String GET_SAVE_DOCTOR_COMMENT_URL =SERVICE_DOMAIN + "/api/doctorComment/save.json";
    
    public static String GET_INVITATION_LIST_URL =SERVICE_DOMAIN + "/api/doctorInvitCode/list.json";
    
    public static String GET_SEND_INVITATION_URL =SERVICE_DOMAIN + "/api/doctorInvitCode/invit.json";
    
    public static String GET_FEEDBACK_LIST_URL =SERVICE_DOMAIN + "/api/userFeeback/list.json";
    
    public static String GET_SEND_FEEDBACK_URL =SERVICE_DOMAIN + "/api/userFeeback/save.json";
    
    public static String GET_DISCUSSION_CASE_LIST_URL =SERVICE_DOMAIN + "/api/caseDiscuss/list.json";
    
    public static String GET_CASE_IMAGE_LIST_URL =SERVICE_DOMAIN + "/api/caseFiles/list.json";
    
    public static String GET_SEND_DISCUSSION_CASE_URL =SERVICE_DOMAIN + "/api/caseDiscuss/save.json";
    
    public static String GET_DISCUSSION_CASE_FINSH_URL =SERVICE_DOMAIN + "/api/patientCase/completed.json";
    
    public static String GET_DELETE_CASE_URL =SERVICE_DOMAIN + "/api/patientCase/deleteById.json";
    
    public static String GET_REJECTED_CASE_URL =SERVICE_DOMAIN + "/api/patientCase/rejected.json";
    
    public static String GET_CASE_OPINION_URL =SERVICE_DOMAIN + "/api/patientCase/fillOpinion.json";
    
    public static String GET_RECEIVED_CASE_URL =SERVICE_DOMAIN + "/api/patientCase/received.json";
    
    public static String GET_TOSURGERY_CASE_URL =SERVICE_DOMAIN + "/api/patientCase/toSurgery.json";
    
    public static String GET_JION_DOCTOR_URL =SERVICE_DOMAIN + "/api/doctor/save.json";
    
    public static String GET_USERINFO_URL =SERVICE_DOMAIN + "/api/user/getUserInfo.json";
    
    public static String GET_USER_ICON_URL =SERVICE_DOMAIN + "/api/user/iconUpload.json";
    
    public static String GET_DISCUSSION_CASE_IMAGE_URL =SERVICE_DOMAIN + "/api/caseDiscussFiles/multiUpload.json";
    
    public static String GET_DELETE_CASE_IMAGE_URL =SERVICE_DOMAIN + "/api/caseFiles/deleteById.json";
    
    public static String GET_MY_ACOUNT_INFO_URL =SERVICE_DOMAIN + "/api/userBalance/getDatas.json";
    
    public static String GET_MY_ACOUNT_RECHARGE_RECORD_URL =SERVICE_DOMAIN + "/api/userBalanceTopup/list.json";
    
    public static String GET_MY_ACOUNT_WITHDRAWALS_RECORD_URL =SERVICE_DOMAIN + "/api/userBalancexxx/list.json";
    
    public static String GET_MY_ACOUNT_INCOME_RECORD_URL =SERVICE_DOMAIN + "/api/userBalanceIncome/list.json";
    
    public static String GET_MY_ACOUNT_PAY_RECORD_URL =SERVICE_DOMAIN + "/api/userBalancePayment/list.json";
    
    public static String GET_EXPERT_ACCEPT_URL =SERVICE_DOMAIN + "/api/patientCase/acceptByExpert.json";

    private static CaseParams caseParams=new CaseParams();

    private static String token;

    public static CaseParams getCaseParams() {
        return caseParams;
    }

    public static void setCaseParams(CaseParams paramsMap) {
        for(String key: paramsMap.getKeys()) {
            ClientUtil.caseParams.add(key, paramsMap.getValue(key));
        }
    }

    public static void reSetCaseParams() {
        ClientUtil.caseParams.clear();
    }

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
        if(ClientUtil.token != null && !"".equals(ClientUtil.token)) {
            islogin=true;
        }
        return islogin;
    }
}
