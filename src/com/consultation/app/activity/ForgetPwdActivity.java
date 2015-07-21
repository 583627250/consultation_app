package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.CommonUtil;


@SuppressLint("HandlerLeak")
public class ForgetPwdActivity extends Activity {
    
    private LinearLayout back_layout;
    
    private TextView title_text,back_text,phone_text,verification_text,pwd_text;

    private EditText phone_edit,verification_edit,pwd_edit;
    
    private Button getVerification_btn,commit_btm;
    
    private int times;
    
    private RequestQueue mQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_layout);
        mQueue = Volley.newRequestQueue(ForgetPwdActivity.this);
        initDate();
        initView();
    }

    private void initView() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("重置密码");
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
        
        phone_text = (TextView)findViewById(R.id.forget_password_phone_text);
        phone_text.setTextSize(18);
        
        verification_text = (TextView)findViewById(R.id.forget_password_code_text);
        verification_text.setTextSize(18);
        
        pwd_text = (TextView)findViewById(R.id.forget_password_pwd_text);
        pwd_text.setTextSize(18);
        
        phone_edit = (EditText)findViewById(R.id.forget_password_phone_input_edit);
        phone_edit.setTextSize(18);
        
        verification_edit = (EditText)findViewById(R.id.forget_password_code_input_edit);
        verification_edit.setTextSize(18);
        
        pwd_edit = (EditText)findViewById(R.id.forget_password_pwd_input_edit);
        pwd_edit.setTextSize(18);
        
        getVerification_btn = (Button)findViewById(R.id.forget_password_phone_get_btn);
        getVerification_btn.setTextSize(14);
        getVerification_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //获取验证码
                if(null == phone_edit.getText().toString() || "".equals(phone_edit.getText().toString())){
                    Toast.makeText(ForgetPwdActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountUtil.isMobileNum(phone_edit.getText().toString())){
                    Toast.makeText(ForgetPwdActivity.this, "手机号输入有误，请重输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("mobile_ph", phone_edit.getText().toString());
                CommonUtil.showLoadingDialog(ForgetPwdActivity.this);
                OpenApiService.getInstance(ForgetPwdActivity.this).getRegisterVerification(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                OpenApiService.getInstance(ForgetPwdActivity.this).getRegisterMobileUsable(mQueue, parmas, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String arg0) {
                                        CommonUtil.closeLodingDialog();
                                        try {
                                            JSONObject responses = new JSONObject(arg0);
                                            if(responses.getInt("rtnCode") == 1){
                                                getVerification_btn.setEnabled(false);
                                                Toast.makeText(ForgetPwdActivity.this, "验证码请求成功", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(ForgetPwdActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(ForgetPwdActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(ForgetPwdActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        getVerification_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
    
        commit_btm = (Button)findViewById(R.id.forget_password_btn_reset);
        commit_btm.setTextSize(22);
        commit_btm.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                CommonUtil.showLoadingDialog(ForgetPwdActivity.this);
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("mobile_ph", phone_edit.getText().toString());
                parmas.put("pwd", pwd_edit.getText().toString());
                parmas.put("smsVerifyCode", verification_edit.getText().toString());
                CommonUtil.showLoadingDialog(ForgetPwdActivity.this);
                OpenApiService.getInstance(ForgetPwdActivity.this).getRegister(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                Toast.makeText(ForgetPwdActivity.this, "密码修改成功，请重新登陆", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(ForgetPwdActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(ForgetPwdActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                
                
                
                
                
                
                
                Toast.makeText(ForgetPwdActivity.this, "密码修改成功，请重新登陆!", Toast.LENGTH_LONG).show();
                finish();
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

    private void initDate() {
        
    }
}
