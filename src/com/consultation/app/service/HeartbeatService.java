package com.consultation.app.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.IBinder;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class HeartbeatService extends android.app.Service {

    private boolean threadDisable;
    
    private RequestQueue mQueue;

    private SharePreferencesEditor editor;
    
    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }
    
    private OpenApiService service=new OpenApiService();

    public void onCreate() {
        super.onCreate();
        mQueue = Volley.newRequestQueue(getApplicationContext());
        editor = new SharePreferencesEditor(getApplicationContext());
        new Thread(new Runnable() {

            public void run() {
                while(!threadDisable) {
                    try {
                        Map<String, String> parmas=new HashMap<String, String>();
                        parmas.put("uid", editor.get("uid", ""));
                        parmas.put("refreshToken", editor.get("refreshToken", ""));
                        if(!editor.get("refreshToken", "").equals("")){
                            service.heartBeat(mQueue, parmas, new Response.Listener<String>(){

                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);
                                    try {
                                        JSONObject responses=new JSONObject(response);
                                        if(responses.getInt("rtnCode") == 1) {
                                            ClientUtil.setToken(responses.getString("accessToken"));
                                        }else{
                                            editor.put("refreshToken", "");
                                            ClientUtil.setToken("");
                                        }
                                    } catch(JSONException e) {
                                        e.printStackTrace();
                                    }
                                }}, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError arg0) {
                                    }
                                });
                        }
                        Thread.sleep(1000 * 60 * 60 * 1);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    public void onDestroy() {
        super.onDestroy();
        /** 服务停止时，终止 */
        this.threadDisable=true;
    }
}
