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
import com.consultation.app.model.PatientTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class SelectPatientResultActivity extends Activity {

    private LinearLayout back_layout, info_layout;

    private TextView back_text, title_text;

    private PatientTo patient=new PatientTo();

    private RequestQueue mQueue;

    private String nameString;

    private Context mContext;

    private TextView noData, name, sex, phone, birthday, address, blance, code;

    private Button codeBtn, OKBtn;

    private EditText code_edit;

    private SharePreferencesEditor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_select_patient_result_layout);
        nameString=getIntent().getStringExtra("nameString");
        mContext=this;
        initDate();
        initView();
    }

    private void initDate() {
        editor=new SharePreferencesEditor(mContext);
        mQueue=Volley.newRequestQueue(SelectPatientResultActivity.this);
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("userTp", editor.get("userType", ""));
        parmas.put("mobile_ph", nameString);
        System.out.println(parmas);
        CommonUtil.showLoadingDialog(mContext);
        OpenApiService.getInstance(mContext).getPatientInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONObject infos=responses.getJSONObject("user");
                        name.setText("姓名:" + infos.getString("real_name"));
                        if(infos.getString("sex").equals("0")) {
                            sex.setText("性别:女");
                        } else {
                            sex.setText("性别:男");
                        }
                        phone.setText("手机号:" + infos.getString("mobile_ph"));
                        patient.setId(infos.getString("id"));
                        patient.setMobile_ph(infos.getString("mobile_ph"));
                        birthday.setText("出生日期:" + infos.getString("birth_year") + "-" + infos.getString("birth_month") + "-"
                            + infos.getString("birth_day"));
                        address.setText("地址:" + infos.getString("area_province") + infos.getString("area_city")
                            + infos.getString("area_county"));
                        blance.setText("余额:" + infos.getString("current_balance"));
                        info_layout.setVisibility(View.VISIBLE);
                    } else {
                        noData.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("患者信息");
        title_text.setTextSize(20);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        info_layout=(LinearLayout)findViewById(R.id.search_patient_result_layout_all);
        info_layout.setVisibility(View.GONE);

        noData=(TextView)findViewById(R.id.search_patient_result_no_text);
        noData.setTextSize(20);

        name=(TextView)findViewById(R.id.search_patient_result_name_text);
        name.setTextSize(18);

        sex=(TextView)findViewById(R.id.search_patient_result_sex_text);
        sex.setTextSize(18);

        phone=(TextView)findViewById(R.id.search_patient_result_phone_text);
        phone.setTextSize(18);

        birthday=(TextView)findViewById(R.id.search_patient_result_birthday_text);
        birthday.setTextSize(18);

        address=(TextView)findViewById(R.id.search_patient_result_address_text);
        address.setTextSize(18);

        blance=(TextView)findViewById(R.id.search_patient_result_balance_text);
        blance.setTextSize(18);

        code=(TextView)findViewById(R.id.search_patient_result_code_text);
        code.setTextSize(18);

        code_edit=(EditText)findViewById(R.id.search_patient_result_code_input_edit);
        code_edit.setTextSize(18);

//        info_layout.setVisibility(View.VISIBLE);

        codeBtn=(Button)findViewById(R.id.search_patient_result_phone_get_btn);
        codeBtn.setTextSize(14);
        codeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 获取验证码
                final Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("mobile_ph", patient.getMobile_ph());
                CommonUtil.showLoadingDialog(mContext);
                OpenApiService.getInstance(mContext).getRegisterVerification(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "验证码发送错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        codeBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());

        OKBtn=(Button)findViewById(R.id.search_patient_result_btn_determine);
        OKBtn.setTextSize(22);
        OKBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == code_edit.getText().toString() || "".equals(code_edit.getText().toString())) {
                    Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG).show();
                    return;
                }
                final Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("patient_id", patient.getId());
                parmas.put("sms_code", code_edit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(mContext);
                OpenApiService.getInstance(mContext).getRegisterVerification(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putString("patientName", patient.getReal_name());
                                bundle.putString("patientId", patient.getId());
                                intent.putExtras(bundle);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(mContext, "验证码错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        OKBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
    }
}
