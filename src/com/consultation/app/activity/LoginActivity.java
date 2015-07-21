package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;


public class LoginActivity extends Activity {
    
    private TextView title_text;
    
    private TextView userName_text;
    
    private TextView pwd_text;
    
    private TextView verification_code_text;
    
    private ImageView verification_image;
    
    private Button login_btn;
    
    private Button register_btn;
    
    private LinearLayout back_layout,code_layout;
    
    private TextView back_text;
    
    private static ConsultationCallbackHandler handler;
    
    private EditText account,password,code;
    
    private TextView forgetPwd,noAcount;
    
    private int loginCount = 0;
    
    private RequestQueue mQueue;
    
    private SharePreferencesEditor editor;
    
    private ImageLoader mImageLoader;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mQueue = Volley.newRequestQueue(LoginActivity.this);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        editor = new SharePreferencesEditor(LoginActivity.this);
        init();
    }
    
    public static void setHandler(ConsultationCallbackHandler h){
        handler = h;
    }

    private void init() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("用户登陆");
        title_text.setTextSize(20);
        
        forgetPwd = (TextView)findViewById(R.id.login_forget_pwd_text);
        forgetPwd.setTextSize(17);
        forgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
        forgetPwd.getPaint().setAntiAlias(true);
        forgetPwd.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //去忘记密码
                startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class));
            }
        });
        
        noAcount = (TextView)findViewById(R.id.login_no_acount_text);
        noAcount.setTextSize(16);
        
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
        
        userName_text = (TextView)findViewById(R.id.login_username_text);
        userName_text.setTextSize(18);
    
        pwd_text = (TextView)findViewById(R.id.login_pwd_text);
        pwd_text.setTextSize(18);
        
        verification_code_text = (TextView)findViewById(R.id.login_code_text);
        verification_code_text.setTextSize(18);
    
        verification_image = (ImageView)findViewById(R.id.login_code_imageView);
        
        account = (EditText)findViewById(R.id.login_username_input_edit);
        account.setTextSize(18);
        password = (EditText)findViewById(R.id.login_pwd_input_edit);
        password.setTextSize(18);
        code = (EditText)findViewById(R.id.login_code_input_edit);
        code.setTextSize(18);   
        
        code_layout = (LinearLayout)findViewById(R.id.login_code_layout);
        
        verification_image.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ImageListener listener = ImageLoader.getImageListener(verification_image, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                mImageLoader.get(ClientUtil.GET_LOGIN_IMAGE_URL+"??mobile_ph="+account.getText().toString()+"&ts="+System.currentTimeMillis(), listener);
            }
        });
    
        login_btn = (Button)findViewById(R.id.login_btn_login);
        login_btn.setTextSize(20);
        
        register_btn = (Button)findViewById(R.id.login_btn_register);
        register_btn.setTextSize(20);
        
        login_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //登陆
//                if(account.getText().toString() == null || "".equals(account.getText().toString())){
//                    Toast.makeText(LoginActivity.this, "手机号不能位空,请输入手机号!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(!AccountUtil.isMobileNum(account.getText().toString())){
//                    Toast.makeText(LoginActivity.this, "手机号输入有误,请输入正确的手机号!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(password.getText().toString() == null || "".equals(password.getText().toString())){
//                    Toast.makeText(LoginActivity.this, "密码不能位空,请输入密码!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(password.getText().toString().length()<6){
//                    Toast.makeText(LoginActivity.this, "密码格式不正确,请输入6-20位字母或数字的密码!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(loginCount > 3){
//                    if(code.getText().toString() == null || "".equals(code.getText().toString())){
//                        Toast.makeText(LoginActivity.this, "验证码不能位空,请输入验证码!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("mobile_ph", "13501320010");
                parmas.put("pwd", "111111");
//                parmas.put("mobile_ph", account.getText().toString());
//                parmas.put("pwd", password.getText().toString());
//                if(loginCount > 3){
//                    parmas.put("smsVerifyCode", code.getText().toString());
//                }
                CommonUtil.showLoadingDialog(LoginActivity.this);
                OpenApiService.getInstance(LoginActivity.this).getLogin(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                editor.put("uid", responses.getString("uid"));
                                editor.put("userType", responses.getString("userTp"));
                                ClientUtil.setToken(responses.getString("accessToken"));
                                handler.onSuccess("登陆成功", ConsultionStatusCode.USER_LOGIN_SUCCESS);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                loginCount++;
                                if(loginCount == 3){
                                    code_layout.setVisibility(View.VISIBLE);
                                    Map<String, String> imageParmas = new HashMap<String, String>();
                                    imageParmas.put("mobile_ph", account.getText().toString());
                                    imageParmas.put("ts", System.currentTimeMillis()+"");
                                    ImageListener listener = ImageLoader.getImageListener(verification_image, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                                    mImageLoader.get(ClientUtil.GET_LOGIN_IMAGE_URL+"??mobile_ph="+account.getText().toString()+"&ts="+System.currentTimeMillis(), listener);
                                }
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(LoginActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        login_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
        register_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //注册
                RegisterActivity.setHandler(new ConsultationCallbackHandler() {
                    
                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        handler.onSuccess("注册", ConsultionStatusCode.SUCCESS);
                        finish();
                    }
                    
                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                    }
                });
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        register_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
    }
}
