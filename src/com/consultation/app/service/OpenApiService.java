package com.consultation.app.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.HttpUtil;

public class OpenApiService {

    private static class SingleServiceHolder {

        private static final OpenApiService service=new OpenApiService();
    }

    public static OpenApiService getInstance(Context context) {
        return SingleServiceHolder.service;
    }

    public void heartBeat(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_REFRESHACCESSTOKEN_URL, parmas, listener, errorListener);
    }

    public void getKnowledgeList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_KNOWLEDGE_LIST_URL, parmas, listener, errorListener);
    }

    public void getRegisterVerification(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_REGISTER_VERIFICATION_URL, parmas, listener, errorListener);
    }

    public void getRegisterMobileUsable(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_REGISTER_MOBILEUSABLE_URL, parmas, listener, errorListener);
    }
    
    public void getForgetMobileUsable(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_FORGET_MOBILEUSABLE_URL, parmas, listener, errorListener);
    }
    
    public void getPatientMobileUsable(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_PATIENT_MOBILEUSABLE_URL, parmas, listener, errorListener);
    }

    public void getRegister(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_REGISTER_URL, parmas, listener, errorListener);
    }

    public void getforgetPwd(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_FORGET_PWD_URL, parmas, listener, errorListener);
    }

    public void getLogin(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_LOGIN_URL, parmas, listener, errorListener);
    }

    public void getKnowledgeListByTile(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_KNOWLEDGE_LIST_BY_TITLE_URL, parmas, listener, errorListener);
    }

    public void getSubmitUserInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_SUBMIT_USER_INFO_URL, parmas, listener, errorListener);
    }

    public void getExpertInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_EXPERT_INFO_URL, parmas, listener, errorListener);
    }

    public void getExpertList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_EXPERTLIST_URL, parmas, listener, errorListener);
    }

    public void getPatientCaseList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_PATIENTCASE_LIST_URL, parmas, listener, errorListener);
    }
    
    public void getHelpPatientList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_HELP_PATIENT_LIST_URL, parmas, listener, errorListener);
    }

    public void getPatientCaseListInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_PATIENTCASE_INFO_URL, parmas, listener, errorListener);
    }

    public void getPatientInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_PATIENT_INFO_URL, parmas, listener, errorListener);
    }

    public void getCaseSave(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CASE_SAVE_URL, parmas, listener, errorListener);
    }

    public void getCaseSaveInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CASE_UP_ALL_URL, parmas, listener, errorListener);
    }
    
    public void getContactStatus(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CONTACT_STATUS_URL, parmas, listener, errorListener);
    }
    
    public void getCaseSubmitInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CASE_SUBMIT_ALL_URL, parmas, listener, errorListener);
    }

    public void getIsPatient(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_IS_PATIENT_URL, parmas, listener, errorListener);
    }
    
    public void getChangeExpert(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CHANGE_EXPERT_URL, parmas, listener, errorListener);
    }

    public void getDoctorComment(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_DOCTOR_COMMENT_URL, parmas, listener, errorListener);
    }

    public void getSaveDoctorComment(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_SAVE_DOCTOR_COMMENT_URL, parmas, listener, errorListener);
    }
    
    public void getLastedDiscuss(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_LASTED_DISCUSS_URL, parmas, listener, errorListener);
    }

    public void getInvitationList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_INVITATION_LIST_URL, parmas, listener, errorListener);
    }
    
    public void getDeleteCaseImage(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_DELETE_CASE_IMAGE_URL, parmas, listener, errorListener);
    }

    public void getSendInvitation(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_SEND_INVITATION_URL, parmas, listener, errorListener);
    }

    public void getFeedBackList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_FEEDBACK_LIST_URL, parmas, listener, errorListener);
    }

    public void getDiscussionCaseList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_DISCUSSION_CASE_LIST_URL, parmas, listener, errorListener);
    }
    
    public void getCaseImageList(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CASE_IMAGE_LIST_URL, parmas, listener, errorListener);
    }

    public void getSendDiscussionCase(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_SEND_DISCUSSION_CASE_URL, parmas, listener, errorListener);
    }

    public void getDiscussionCaseFinsh(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_DISCUSSION_CASE_FINSH_URL, parmas, listener, errorListener);
    }

    public void getRejectedCase(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_REJECTED_CASE_URL, parmas, listener, errorListener);
    }
    
    public void getCaseOpinion(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_CASE_OPINION_URL, parmas, listener, errorListener);
    }

    public void getReceivedCase(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_RECEIVED_CASE_URL, parmas, listener, errorListener);
    }

    public void getToSurgeryCase(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_TOSURGERY_CASE_URL, parmas, listener, errorListener);
    }

    public void getDeleteCase(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_DELETE_CASE_URL, parmas, listener, errorListener);
    }

    public void getSendFeedBack(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_SEND_FEEDBACK_URL, parmas, listener, errorListener);
    }

    public void getUserInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_USERINFO_URL, parmas, listener, errorListener);
    }
    
    public void getMyAcountInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_MY_ACOUNT_INFO_URL, parmas, listener, errorListener);
    }
    
    public void getMyAcountRechargeRecord(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_MY_ACOUNT_RECHARGE_RECORD_URL, parmas, listener, errorListener);
    }
    
    public void getMyAcountDrawsRecord(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_MY_ACOUNT_WITHDRAWALS_RECORD_URL, parmas, listener, errorListener);
    }
    
    public void getMyAcountIncomeRecord(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_MY_ACOUNT_INCOME_RECORD_URL, parmas, listener, errorListener);
    }
    
    public void getMyAcountPayRecord(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_MY_ACOUNT_PAY_RECORD_URL, parmas, listener, errorListener);
    }
    
    public void getReadTotalCount(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_READ_TOTAL_COUNT_URL, parmas, listener, errorListener);
    }
    
    public void getExpertAccept(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_EXPERT_ACCEPT_URL, parmas, listener, errorListener);
    }

    public void getHttpsRequest(String url, Context context, Map<String, String> parmas, ConsultationCallbackHandler callbackHandler) {
        HttpPost httpRequest=new HttpPost(url);
        HttpClient httpClient=HttpUtil.getNewHttpClient(context);
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        for(String key: parmas.keySet()) {
            params.add(new BasicNameValuePair(key, parmas.get(key)));
        }
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse httpResponse=httpClient.execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                String strResult=EntityUtils.toString(httpResponse.getEntity());
                callbackHandler.onSuccess(strResult, ConsultionStatusCode.SUCCESS);
            } else {
                callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
            }
        } catch(ClientProtocolException e) {
            e.printStackTrace();
            callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
        } catch(IOException e) {
            e.printStackTrace();
            callbackHandler.onFailure(new ConsultationCallbackException(ConsultionStatusCode.FAIL, "网络请求异常"));
        }
    }

    public void getUploadFiles(String url, Context context, ConsultationCallbackHandler callbackHandler, File[] files,
        Map<String, String> params) {
        HttpUtil.getInstance(context).uploadFiles(url, callbackHandler, files, params);
    }

    private void httpRequest(RequestQueue mQueue, String url, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        StringRequest stringRequest=new StringRequest(Method.POST, url, listener, errorListener) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return parmas;
            }
        };
        mQueue.add(stringRequest);
    }
}