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
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.http.MultiPartStringRequest;
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

    public void getRegister(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_REGISTER_URL, parmas, listener, errorListener);
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

    public void getIsPatient(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_IS_PATIENT_URL, parmas, listener, errorListener);
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
    
    public void getUploadFiles(String url, Context context, Map<String, String> parmas, ConsultationCallbackHandler callbackHandler, File[] files) {
        HttpUtil.getInstance(context).uploadFiles(url, callbackHandler, files);
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

    public void sendImageHttp(RequestQueue mQueue, final Map<String, File> files, final Map<String, String> params,
        final Listener<String> responseListener, final ErrorListener errorListener) {
        MultiPartStringRequest multiPartRequest=
            new MultiPartStringRequest(Request.Method.PUT, "", responseListener, errorListener) {

                @Override
                public Map<String, File> getFileUploads() {
                    return files;
                }

                @Override
                public Map<String, String> getStringUploads() {
                    return params;
                }
            };
        mQueue.add(multiPartRequest);
    }
}