package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class CaseMoreNewActivity extends Activity {

    private LinearLayout layout1, layout2, layout3, layout4;

    private RelativeLayout layoutAll;

    private TextView textView1, textView2, textView3, textView4;

    private String btn1, btn2, btn3, caseId;

    private int btnCount;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_more_select_new_layout);
        mQueue=Volley.newRequestQueue(CaseMoreNewActivity.this);
        editor=new SharePreferencesEditor(CaseMoreNewActivity.this);
        caseId=getIntent().getStringExtra("caseId");
        btn1=getIntent().getStringExtra("btn1");
        btn2=getIntent().getStringExtra("btn2");
        btn3=getIntent().getStringExtra("btn3");
        btnCount=getIntent().getIntExtra("btnCount", 0);
        initView();
    }

    private void initView() {
        textView1=(TextView)findViewById(R.id.case_more_new_select_1_text);
        textView1.setTextSize(18);
        textView1.setText(btn1);
        textView2=(TextView)findViewById(R.id.case_more_new_select_2_text);
        textView2.setTextSize(18);
        textView2.setText(btn2);
        textView3=(TextView)findViewById(R.id.case_more_new_select_3_text);
        textView3.setTextSize(18);
        textView3.setText(btn3);
        textView4=(TextView)findViewById(R.id.case_more_new_select_cancel_text);
        textView4.setTextSize(18);
        if(btnCount == 2) {
            layout2.setVisibility(View.GONE);
        }

        layout1=(LinearLayout)findViewById(R.id.case_more_new_select_1_layout);
        layout1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 上传图片
                startActivityForResult(new Intent(CaseMoreNewActivity.this, AddPatientPicActivity.class), 1);
            }
        });
        layout1.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout2=(LinearLayout)findViewById(R.id.case_more_new_select_2_layout);
        layout2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 转手术或住院
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("caseId", caseId);
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseMoreNewActivity.this);
                OpenApiService.getInstance(CaseMoreNewActivity.this).getToSurgeryCase(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Intent intent=new Intent();
                                    intent.putExtra("status", "2");
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseMoreNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseMoreNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        layout2.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout3=(LinearLayout)findViewById(R.id.case_more_new_select_3_layout);
        layout3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 完成
                Intent intent=new Intent(CaseMoreNewActivity.this, DialogNewActivity.class);
                intent.putExtra("flag", 0);
                intent.putExtra("titleText", "确认完成？");
                startActivityForResult(intent, 0);
            }
        });
        layout3.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout4=(LinearLayout)findViewById(R.id.case_more_new_select_cancel_layout);
        layout4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout4.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());

        layoutAll=(RelativeLayout)findViewById(R.id.case_more_new_all_layout);
        layoutAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 0:
                    // 完成
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreNewActivity.this);
                    OpenApiService.getInstance(CaseMoreNewActivity.this).getDiscussionCaseFinsh(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Intent intent=new Intent();
                                        intent.putExtra("status", "3");
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreNewActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError arg0) {
                                CommonUtil.closeLodingDialog();
                                Toast.makeText(CaseMoreNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    break;
                case 1:
                    if(data != null){
                        Intent intent=new Intent();
                        intent.putExtra("status", "1");
                        intent.putExtra("path", data.getStringExtra("bitmap"));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
