package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.CommonUtil;

@SuppressLint("HandlerLeak")
public class UpdateActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout,updateLayout;

    private TextView back_text,versionText,updateTitle,isNew;

    private RequestQueue mQueue;
    
    private String versionCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);
        mQueue=Volley.newRequestQueue(UpdateActivity.this);
        PackageManager pm = getPackageManager();
        PackageInfo pinfo;
        try {
            pinfo=pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = pinfo.versionName;
        } catch(NameNotFoundException e) {
            e.printStackTrace();
        }
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("tp", "android");
        CommonUtil.showLoadingDialog(UpdateActivity.this);
        OpenApiService.getInstance(UpdateActivity.this).getVersionCode(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        Message message = new Message();
                        message.obj=responses.getString("version");
                        handler.sendMessage(message);
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(UpdateActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(UpdateActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(UpdateActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(UpdateActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String code = (String)msg.obj;
            if(!code.equals(versionCode)){
                isNew.setText("最新版本"+code);
                isNew.setTextColor(Color.RED);
            }
        }
    };

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("版本更新");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                finish();
            }
        });
        
        versionText=(TextView)findViewById(R.id.update_version_code_text);
        versionText.setTextSize(18);
        versionText.setText("版本号  "+versionCode);

        updateTitle=(TextView)findViewById(R.id.update_version_text);
        updateTitle.setTextSize(18);

        isNew=(TextView)findViewById(R.id.update_version_isNew_text);
        isNew.setTextSize(16);
        
        updateLayout = (LinearLayout)findViewById(R.id.update_version_layout);
        updateLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                initDate();
            }
        });
    }
}
