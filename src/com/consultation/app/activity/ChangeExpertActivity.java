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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint("HandlerLeak")
public class ChangeExpertActivity extends Activity {

    private String caseId, consult_tp, mobile_ph;

    private TextView title_text, back_text;

    private LinearLayout back_layout, expert_layout, expert_layout_line, code_layout_line, code_layout;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private TextView expert_text, code_text, consulatioan_text;

    private EditText expert_edit, code_edit;

    private RadioButton radioButton1, radioButton3;

    private Button submitBtn, getCodeBtn;

    private int times;

    private String expertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_expert_layout);
        caseId=getIntent().getStringExtra("caseId");
        consult_tp=getIntent().getStringExtra("consult_tp");
        mobile_ph=getIntent().getStringExtra("mobile_ph");
        editor=new SharePreferencesEditor(ChangeExpertActivity.this);
        mQueue=Volley.newRequestQueue(ChangeExpertActivity.this);
        initData();
        initView();
    }

    private void initData() {

    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例复用");
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
        
        expert_layout_line=(LinearLayout)findViewById(R.id.change_expert_select_expert_layout_line);
        expert_layout=(LinearLayout)findViewById(R.id.change_expert_select_expert_layout);
        code_layout_line=(LinearLayout)findViewById(R.id.change_expert_code_layout_line);
        code_layout=(LinearLayout)findViewById(R.id.change_expert_code_layout);

        expert_text=(TextView)findViewById(R.id.change_expert_select_expert_text);
        expert_text.setTextSize(18);
        consulatioan_text=(TextView)findViewById(R.id.change_expert_select_consultation_text);
        consulatioan_text.setTextSize(18);
        code_text=(TextView)findViewById(R.id.change_expert_code_text);
        code_text.setTextSize(18);

        expert_edit=(EditText)findViewById(R.id.change_expert_select_expert_input_edit);
        expert_edit.setTextSize(18);
        expert_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 选择专家
                startActivityForResult(new Intent(ChangeExpertActivity.this, SelectExpertActivity.class), 0);
            }
        });
        code_edit=(EditText)findViewById(R.id.change_expert_code_input_edit);
        code_edit.setTextSize(18);

        radioButton1=(RadioButton)findViewById(R.id.change_expert_select_consultation_explicit);
        radioButton1.setTextSize(18);
        radioButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    expert_layout_line.setVisibility(View.VISIBLE);
                    expert_layout.setVisibility(View.VISIBLE);
                    code_layout.setVisibility(View.VISIBLE);
                    code_layout_line.setVisibility(View.VISIBLE);
                }
            }
        });
        radioButton3=(RadioButton)findViewById(R.id.change_expert_select_consultation_public);
        radioButton3.setTextSize(18);
        if(consult_tp.equals("公开讨论")) {
            radioButton3.setChecked(true);
            radioButton1.setChecked(false);
        } else if(consult_tp.equals("专家咨询")) {
            radioButton1.setChecked(true);
            radioButton3.setChecked(false);
        }
        if(radioButton3.isChecked()) {
            expert_layout_line.setVisibility(View.GONE);
            expert_layout.setVisibility(View.GONE);
            code_layout.setVisibility(View.GONE);
            code_layout_line.setVisibility(View.GONE);
        }
        radioButton3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    expert_layout.setVisibility(View.GONE);
                    expert_layout_line.setVisibility(View.GONE);
                    code_layout.setVisibility(View.GONE);
                    code_layout_line.setVisibility(View.GONE);
                }
            }
        });

        submitBtn=(Button)findViewById(R.id.change_expert_btn_submit);
        submitBtn.setTextSize(20);
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("id", caseId);
                if(radioButton1.isChecked()) {
                    if(null == expert_edit.getText().toString() || "".equals(expert_edit.getText().toString())) {
                        Toast.makeText(ChangeExpertActivity.this, "请选择专家", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(null == code_edit.getText().toString() || "".equals(code_edit.getText().toString())) {
                        Toast.makeText(ChangeExpertActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parmas.put("expert_userid", expertId);
                    parmas.put("sms_code", code_edit.getText().toString());
                }
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(ChangeExpertActivity.this);
                OpenApiService.getInstance(ChangeExpertActivity.this).getChangeExpert(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Intent intent=new Intent();
                                    Bundle bundle=new Bundle();
                                    intent.putExtras(bundle);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(ChangeExpertActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(ChangeExpertActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(ChangeExpertActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(ChangeExpertActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        submitBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        getCodeBtn=(Button)findViewById(R.id.change_expert_phone_get_btn);
        getCodeBtn.setTextSize(14);
        getCodeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 获取验证码
                final Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("mobile_ph", mobile_ph);
                CommonUtil.showLoadingDialog(ChangeExpertActivity.this);
                OpenApiService.getInstance(ChangeExpertActivity.this).getPatientMobileUsable(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    getCodeBtn.setEnabled(false);
                                    Toast.makeText(ChangeExpertActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            times=30;
                                            while(times >= 0) {
                                                try {
                                                    Message msg=new Message();
                                                    msg.what=times;
                                                    h.sendMessage(msg);
                                                    Thread.sleep(1000);
                                                    times--;
                                                } catch(InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).start();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(ChangeExpertActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(ChangeExpertActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(ChangeExpertActivity.this, "验证码发送错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(ChangeExpertActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        getCodeBtn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            switch(requestCode) {
                case 0:
                    expert_edit.setText(data.getExtras().getString("expertName"));
                    expertId=data.getExtras().getString("expertId");
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler h=new Handler() {

        public void dispatchMessage(Message msg) {
            if(msg.what == 0) {
                getCodeBtn.setEnabled(true);
                getCodeBtn.setText("获取验证码");
            } else {
                getCodeBtn.setText(msg.what + "s后重新发送");
            }
        };
    };
}
