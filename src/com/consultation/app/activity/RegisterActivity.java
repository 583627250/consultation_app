package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;


@SuppressLint("HandlerLeak")
public class RegisterActivity extends Activity {
    
    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private static ConsultationCallbackHandler handler;
    
    private TextView phone_text,verification_text,pwd_text,repwd_text,
                     tip_text;
    
    private EditText phone_edit,verification_edit,pwd_edit,repwd_edit;
    
    private Button getVerification_btn,commit_btm;
    
    private RequestQueue mQueue;
    
    private int times;
    
    private SharePreferencesEditor editor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        mQueue = Volley.newRequestQueue(RegisterActivity.this);
        editor = new SharePreferencesEditor(RegisterActivity.this);
        init();
    }
    
    public static void setHandler(ConsultationCallbackHandler h){
        handler = h;
    }

    private void init() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("用户注册");
        title_text.setTextSize(20);
    
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {     
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );   
                }
                finish();
            }
        });
        
        phone_text = (TextView)findViewById(R.id.register_phone_text);
        phone_text.setTextSize(18);
        
        verification_text = (TextView)findViewById(R.id.register_code_text);
        verification_text.setTextSize(18);
        
        pwd_text = (TextView)findViewById(R.id.register_pwd_text);
        pwd_text.setTextSize(18);
        
        repwd_text = (TextView)findViewById(R.id.register_repwd_text);
        repwd_text.setTextSize(18);
        
        tip_text = (TextView)findViewById(R.id.register_tip_text);
        tip_text.setTextSize(15);
        

        phone_edit = (EditText)findViewById(R.id.register_phone_input_edit);
        phone_edit.setTextSize(18);
        
        verification_edit = (EditText)findViewById(R.id.register_code_input_edit);
        verification_edit.setTextSize(18);
        
        pwd_edit = (EditText)findViewById(R.id.register_pwd_input_edit);
        pwd_edit.setTextSize(18);
        
        repwd_edit = (EditText)findViewById(R.id.register_repwd_input_edit);
        repwd_edit.setTextSize(18);
        
        
        getVerification_btn = (Button)findViewById(R.id.register_phone_get_btn);
        getVerification_btn.setTextSize(14);
        getVerification_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //获取验证码
                if(null == phone_edit.getText().toString() || "".equals(phone_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountUtil.isPhoneNum(phone_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "手机号输入有误，请重输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("mobile_ph", phone_edit.getText().toString());
                CommonUtil.showLoadingDialog(RegisterActivity.this);
                OpenApiService.getInstance(RegisterActivity.this).getRegisterVerification(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                OpenApiService.getInstance(RegisterActivity.this).getRegisterMobileUsable(mQueue, parmas, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String arg0) {
                                        CommonUtil.closeLodingDialog();
                                        try {
                                            JSONObject responses = new JSONObject(arg0);
                                            if(responses.getInt("rtnCode") == 1){
                                                getVerification_btn.setEnabled(false);
                                                Toast.makeText(RegisterActivity.this, "验证码请求成功", Toast.LENGTH_SHORT).show();
                                                new Thread(new Runnable() {
                                                    
                                                    @Override
                                                    public void run() {
                                                        times = 30;
                                                        while(times >= 0) {
                                                            try {
                                                                Message msg = new Message();
                                                                msg.what = times;
                                                                h.sendMessage(msg);
                                                                Thread.sleep(1000);
                                                                times--;
                                                            } catch(InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }).start();
                                            }
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError arg0) {
                                        CommonUtil.closeLodingDialog();
                                        Toast.makeText(RegisterActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(RegisterActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(RegisterActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        getVerification_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
        
        
        commit_btm = (Button)findViewById(R.id.register_btn_register);
        commit_btm.setTextSize(22);
        commit_btm.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(null == phone_edit.getText().toString() || "".equals(phone_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountUtil.isPhoneNum(phone_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "手机号输入有误，请重输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(null == verification_edit.getText().toString() || "".equals(verification_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(null == pwd_edit.getText().toString() || "".equals(pwd_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pwd_edit.getText().toString().length()<6){
                    Toast.makeText(RegisterActivity.this, "密码格式不正确,请输入6-20位字母或数字的密码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(null == repwd_edit.getText().toString() || "".equals(repwd_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!repwd_edit.getText().toString().equals(pwd_edit.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                CommonUtil.showLoadingDialog(RegisterActivity.this);
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("mobile_ph", phone_edit.getText().toString());
                parmas.put("pwd", pwd_edit.getText().toString());
                parmas.put("smsVerifyCode", verification_edit.getText().toString());
                CommonUtil.showLoadingDialog(RegisterActivity.this);
                OpenApiService.getInstance(RegisterActivity.this).getRegister(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                editor.put("uid", responses.getString("uid"));
                                editor.put("userType", responses.getString("userTp"));
                                ClientUtil.setToken(responses.getString("accessToken"));
                                UserInfoSumbitActivity.setHandler(handler);
                                startActivity(new Intent(RegisterActivity.this, UserInfoSumbitActivity.class));
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(RegisterActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        commit_btm.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
    }
    
    private Handler h = new Handler(){
        
        public void dispatchMessage(Message msg) {
            if(msg.what == 0){
                getVerification_btn.setEnabled(true);
                getVerification_btn.setText("获取验证码");
            }else{
                getVerification_btn.setText(msg.what+"s后重新发送");
            }
        };
    };
}
