package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;


public class UserInfoSumbitActivity extends Activity {
    
    private TextView title_text,back_text;
    
    private LinearLayout back_layout;
    
    private TextView realName,sex,birthday,address;
    
    private EditText realNameEdit,birthdayEdit,addressEdit;
    
    private RadioButton male,female;

    private Button commit_btm;
    
    private static ConsultationCallbackHandler handler;
    
    private int sexValue;
    
    private RequestQueue mQueue;
    
    private String[] addressText;
    
    private SharePreferencesEditor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);
        mQueue = Volley.newRequestQueue(UserInfoSumbitActivity.this);
        editor = new SharePreferencesEditor(UserInfoSumbitActivity.this);
        initData();
        initView();
    }
    
    public static void setHandler(ConsultationCallbackHandler h){
        handler = h;
    }

    private void initView() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("用户信息");
        title_text.setTextSize(20);
    
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setText("取消");
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {     
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );   
                }
                handler.onSuccess("取消用户信息提交", ConsultionStatusCode.FAIL);
                finish();
            }
        });
        
        realName = (TextView)findViewById(R.id.user_info_name_text);
        realName.setTextSize(18);
        
        sex = (TextView)findViewById(R.id.user_info_sex_text);
        sex.setTextSize(18);
        
        birthday = (TextView)findViewById(R.id.user_info_birthday_text);
        birthday.setTextSize(18);
        
        address = (TextView)findViewById(R.id.user_info_addre_text);
        address.setTextSize(18);
        
        realNameEdit = (EditText)findViewById(R.id.user_info_name_input_edit);
        realNameEdit.setTextSize(18);
        
        birthdayEdit = (EditText)findViewById(R.id.user_info_birthday_input_edit);
        birthdayEdit.setTextSize(18);
        birthdayEdit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                BirthdayDateActivity.setHandler(new ConsultationCallbackHandler() {
                    
                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        birthdayEdit.setText(rspContent);
                    }
                    
                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                        
                    }
                });
                startActivity(new Intent(UserInfoSumbitActivity.this, BirthdayDateActivity.class));
            }
        });
        
        addressEdit = (EditText)findViewById(R.id.user_info_addre_input_edit);
        addressEdit.setTextSize(18);
        addressEdit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ProvinceActivity.setHandler(new ConsultationCallbackHandler() {
                    
                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        addressText = rspContent.split(",");
                        addressEdit.setText(rspContent.replace(",", ""));
                    }
                    
                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                        
                    }
                });
                startActivity(new Intent(UserInfoSumbitActivity.this, ProvinceActivity.class));
            }
        });
        
        male = (RadioButton)findViewById(R.id.user_info_sex_radioMale);
        male.setTextSize(18);
        
        female = (RadioButton)findViewById(R.id.user_info_sex_radioFemale);
        female.setTextSize(18);
        
        commit_btm = (Button)findViewById(R.id.user_info_btn);
        commit_btm.setTextSize(22);
        commit_btm.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //获取验证码
                if(male.isChecked()){
                    sexValue = 1;
                }
                if(female.isChecked()){
                    sexValue = 0;
                }
                if("".equals(realNameEdit.getText().toString()) || null ==realNameEdit.getText().toString()){
                    Toast.makeText(UserInfoSumbitActivity.this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if("".equals(birthdayEdit.getText().toString()) || null ==birthdayEdit.getText().toString()){
                    Toast.makeText(UserInfoSumbitActivity.this, "请选择出生日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if("".equals(addressEdit.getText().toString()) || null ==addressEdit.getText().toString()){
                    Toast.makeText(UserInfoSumbitActivity.this, "请选择所在城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("real_name", realName.getText().toString());
                parmas.put("sex", sexValue+"");
                parmas.put("birth_year", birthdayEdit.getText().toString().split("-")[0]);
                parmas.put("birth_month", birthdayEdit.getText().toString().split("-")[1]);
                parmas.put("birth_day", birthdayEdit.getText().toString().split("-")[2]);
                parmas.put("area_province", addressText[0]);
                parmas.put("area_city", addressText[1]);
                parmas.put("area_county", addressText[2]);
                parmas.put("uid", editor.get("uid", ""));
                parmas.put("accessToken", ClientUtil.getToken());
                CommonUtil.showLoadingDialog(UserInfoSumbitActivity.this);
                OpenApiService.getInstance(UserInfoSumbitActivity.this).getSubmitUserInfo(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                handler.onSuccess("用户信息提交成功", ConsultionStatusCode.SUCCESS);
                                finish();
                            }else{
                                Toast.makeText(UserInfoSumbitActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(UserInfoSumbitActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        commit_btm.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
    }

    private void initData() {
        
    }
}
