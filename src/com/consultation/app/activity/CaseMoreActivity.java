package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.consultation.app.util.ActivityList;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class CaseMoreActivity extends Activity {

    private LinearLayout layout1, layout2, layout3;

    private RelativeLayout layoutAll;

    private TextView textView1, textView2, textView3;

    private String patientId, expertId, departmentId, caseId, expertName, patientName, consultType, titles, problem, content;

    private String btn1, btn2, viewingCount, flag;

    private Button viewingCountButton;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_more_select_layout);
        mQueue=Volley.newRequestQueue(CaseMoreActivity.this);
        editor=new SharePreferencesEditor(CaseMoreActivity.this);
        patientId=getIntent().getStringExtra("patientId");
        expertId=getIntent().getStringExtra("expertId");
        departmentId=getIntent().getStringExtra("departmentId");
        caseId=getIntent().getStringExtra("caseId");
        expertName=getIntent().getStringExtra("expertName");
        patientName=getIntent().getStringExtra("patientName");
        consultType=getIntent().getStringExtra("consultType");
        titles=getIntent().getStringExtra("titles");
        problem=getIntent().getStringExtra("problem");
        content=getIntent().getStringExtra("content");
        btn1=getIntent().getStringExtra("btn1");
        btn2=getIntent().getStringExtra("btn2");
        viewingCount=getIntent().getStringExtra("viewingCount");
        flag=getIntent().getStringExtra("flag");
        initView();
    }

    private void initView() {
        viewingCountButton=(Button)findViewById(R.id.case_more_select_2_tip_text);
        viewingCountButton.setTextSize(15);
        if(flag.equals("2")) {
            viewingCountButton.setVisibility(View.VISIBLE);
            viewingCountButton.setText(viewingCount);
        }
        textView1=(TextView)findViewById(R.id.case_more_select_1_text);
        textView1.setTextSize(18);
        textView1.setText(btn1);
        textView2=(TextView)findViewById(R.id.case_more_select_2_text);
        textView2.setTextSize(18);
        textView2.setText(btn2);
        textView3=(TextView)findViewById(R.id.case_more_select_cancel_text);
        textView3.setTextSize(18);
        layout1=(LinearLayout)findViewById(R.id.case_more_select_1_layout);
        layout1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(flag.equals("1")) {
                    // 修改
                    Intent intent=new Intent(CaseMoreActivity.this, CreateCaseActivity.class);
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("departmentId", departmentId);
                    intent.putExtra("expertId", expertId);
                    intent.putExtra("expertName", expertName);
                    intent.putExtra("patientId", patientId);
                    intent.putExtra("patientName", patientName);
                    intent.putExtra("consultType", consultType);
                    intent.putExtra("titles", titles);
                    intent.putExtra("problem", problem);
                    intent.putExtra("content", content);
                    startActivity(intent);
                    CaseMoreActivity.this.finish();
                } else if(flag.equals("2")) {
                    // 转住院
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreActivity.this);
                    OpenApiService.getInstance(CaseMoreActivity.this).getToSurgeryCase(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        ActivityList.getInstance().closeActivity("CaseInfoActivity");
                                        CaseMoreActivity.this.finish();
                                    } else if(responses.getInt("rtnCode") == 10004){
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else if(flag.equals("3")) {
                    // 受理
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreActivity.this);
                    OpenApiService.getInstance(CaseMoreActivity.this).getReceivedCase(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        ActivityList.getInstance().closeActivity("CaseInfoActivity");
                                        CaseMoreActivity.this.finish();
                                    } else if(responses.getInt("rtnCode") == 10004){
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else if(flag.equals("4")) {
                    // 转住院
                    AlertDialog.Builder builder=new AlertDialog.Builder(CaseMoreActivity.this);
                    builder.setMessage("确定转手术或住院吗？申请后以往咨询").setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Map<String, String> parmas=new HashMap<String, String>();
                                parmas.put("caseId", caseId);
                                parmas.put("accessToken", ClientUtil.getToken());
                                parmas.put("uid", editor.get("uid", ""));
                                CommonUtil.showLoadingDialog(CaseMoreActivity.this);
                                OpenApiService.getInstance(CaseMoreActivity.this).getToSurgeryCase(mQueue, parmas,
                                    new Response.Listener<String>() {

                                        @Override
                                        public void onResponse(String arg0) {
                                            CommonUtil.closeLodingDialog();
                                            try {
                                                JSONObject responses=new JSONObject(arg0);
                                                if(responses.getInt("rtnCode") == 1) {
                                                    Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"),
                                                        Toast.LENGTH_SHORT).show();
                                                    ActivityList.getInstance().closeActivity("CaseInfoActivity");
                                                    CaseMoreActivity.this.finish();
                                                } else if(responses.getInt("rtnCode") == 10004){
                                                    Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                                        @Override
                                                        public void onSuccess(String rspContent, int statusCode) {
                                                        }

                                                        @Override
                                                        public void onFailure(ConsultationCallbackException exp) {
                                                        }
                                                    });
                                                    startActivity(new Intent(CaseMoreActivity.this, LoginActivity.class));
                                                } else {
                                                    Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"),
                                                        Toast.LENGTH_SHORT).show();
                                                }
                                            } catch(JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError arg0) {
                                            CommonUtil.closeLodingDialog();
                                            Toast.makeText(CaseMoreActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).create().show();
                }
            }
        });
        layout1.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout2=(LinearLayout)findViewById(R.id.case_more_select_2_layout);
        layout2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(flag.equals("1")) {
                    // 删除
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreActivity.this);
                    OpenApiService.getInstance(CaseMoreActivity.this).getDeleteCase(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        ActivityList.getInstance().closeActivity("CaseInfoActivity");
                                        CaseMoreActivity.this.finish();
                                    } else if(responses.getInt("rtnCode") == 10004){
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else if(flag.equals("2")) {
                    // 评论
                    Intent intent=new Intent(CaseMoreActivity.this, DiscussionCaseActivity.class);
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("consultType", consultType);
                    startActivity(intent);
                    CaseMoreActivity.this.finish();
                } else if(flag.equals("3")) {
                    // 拒绝
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreActivity.this);
                    OpenApiService.getInstance(CaseMoreActivity.this).getRejectedCase(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        ActivityList.getInstance().closeActivity("CaseInfoActivity");
                                        CaseMoreActivity.this.finish();
                                    } else if(responses.getInt("rtnCode") == 10004){
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else if(flag.equals("4")) {
                    // 完成
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreActivity.this);
                    OpenApiService.getInstance(CaseMoreActivity.this).getDiscussionCaseFinsh(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        ActivityList.getInstance().closeActivity("DiscussionCaseActivity");
                                        ActivityList.getInstance().closeActivity("CaseInfoActivity");
                                        CaseMoreActivity.this.finish();
                                    } else if(responses.getInt("rtnCode") == 10004){
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
        layout2.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout3=(LinearLayout)findViewById(R.id.case_more_select_cancel_layout);
        layout3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout3.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());

        layoutAll=(RelativeLayout)findViewById(R.id.case_more_all_layout);
        layoutAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
