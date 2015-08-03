/**
 * $Id: ServiceImpl.java,v 1.1 2012/09/04 05:42:47 xianchao.sun Exp $
 */
package com.consultation.app.service;

import java.io.File;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.consultation.app.http.MultiPartStringRequest;
import com.consultation.app.util.ClientUtil;

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
    
    public void getPatientInfo(RequestQueue mQueue, final Map<String, String> parmas, Response.Listener<String> listener,
        Response.ErrorListener errorListener) {
        httpRequest(mQueue, ClientUtil.GET_PATIENT_INFO_URL, parmas, listener, errorListener);
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

    public void sendImageHttp(RequestQueue mQueue, final Map<String, File> files,
        final Map<String, String> params, final Listener<String> responseListener, final ErrorListener errorListener) {
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