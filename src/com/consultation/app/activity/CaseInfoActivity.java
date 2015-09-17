package com.consultation.app.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.CaseContentTo;
import com.consultation.app.model.CasesTo;
import com.consultation.app.model.DoctorTo;
import com.consultation.app.model.UserStatisticsTo;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ActivityList;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.CaseBroadcastReceiver;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint("HandlerLeak")
public class CaseInfoActivity extends Activity {

    private TextView title_text, back_text, right_text;

    private LinearLayout back_layout;

    private TextView primaryName, primaryDepartment, primaryTitle, primaryHospital, primaryTip, expertName, expertDepartment,
            expertTitle, expertHospital, expertTip, caseStatus, caseStatusText, caseTitle, caseTitleText, caseInfo, caseInfoText,
            caseUnresolved, caseUnresolvedText, caseModel, caseModelText, caseOpinion, caseOpinionText, picText;

    private LinearLayout all,picLayout;

    private EditText opinionEdit;
    
    private Button count;

    private ImageView updateOpinionBtn, saveOpinionBtn, doctorPhoto, expertPhoto;

    private String caseId;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private StringBuffer caseContent=new StringBuffer();

    private CasesTo casesTo;
    
    private boolean haveCase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_info_layout);
        caseId=getIntent().getStringExtra("caseId");
        editor=new SharePreferencesEditor(CaseInfoActivity.this);
        ActivityList.getInstance().setActivitys("CaseInfoActivity", this);
        mQueue=Volley.newRequestQueue(CaseInfoActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    private void initData() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("id", caseId);
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        CommonUtil.showLoadingDialog(CaseInfoActivity.this);
        OpenApiService.getInstance(CaseInfoActivity.this).getPatientCaseListInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        casesTo=new CasesTo();
                        String viewingCount=responses.getString("viewingCount");
                        if(viewingCount.equals("null")) {
                            casesTo.setViewingCount(0);
                        } else {
                            casesTo.setViewingCount(responses.getInt("viewingCount"));
                        }
                        JSONObject info=responses.getJSONObject("patientCase");
                        casesTo.setId(info.getString("id"));
                        casesTo.setStatus(info.getString("status"));
                        casesTo.setDestination(info.getString("destination"));
                        String createTime=info.getString("create_time");
                        if(createTime.equals("null")) {
                            casesTo.setCreate_time(0);
                        } else {
                            casesTo.setCreate_time(Long.parseLong(createTime));
                        }
                        casesTo.setTitle(info.getString("title"));
                        casesTo.setDepart_id(info.getString("depart_id"));
                        casesTo.setDoctor_userid(info.getString("doctor_userid"));
                        casesTo.setPatient_name(info.getString("patient_name"));
                        casesTo.setPatient_id(info.getString("patient_userid"));
                        String consult_fee=info.getString("consult_fee");
                        if(consult_fee.equals("null")) {
                            casesTo.setConsult_fee("0");
                        } else {
                            casesTo.setConsult_fee(consult_fee);
                        }
                        casesTo.setDoctor_name(info.getString("doctor_name"));
                        casesTo.setExpert_userid(info.getString("expert_userid"));
                        casesTo.setExpert_name(info.getString("expert_name"));
                        casesTo.setProblem(info.getString("problem"));
                        casesTo.setConsult_tp(info.getString("consult_tp"));
                        casesTo.setOpinion(info.getString("opinion"));
                        if(!responses.getString("caseContent").equals("null")) {
                            haveCase = true;
                            CaseContentTo caseContentTo=new CaseContentTo();
                            JSONObject caseContentJsonObject=responses.getJSONObject("caseContent");
                            caseContentTo.setCaseId(caseContentJsonObject.getString("case_id"));
                            caseContentTo.setFill_tp(caseContentJsonObject.getString("fill_tp"));
                            caseContentTo.setContent_zs_xml(caseContentJsonObject.getString("content_zs_xml"));
                            if(!caseContentJsonObject.getString("content_zs_xml").equals("null")) {
                                caseContentTo.setContent_zs_xml(caseContentJsonObject.getString("content_zs_xml"));
                            }
                            if(!caseContentJsonObject.getString("content_zs_txt").equals("null")) {
                                caseContent.append(caseContentJsonObject.getString("content_zs_txt")).append("\r\n");
                            }
                            if(!caseContentJsonObject.getString("content_tz_xml").equals("null")) {
                                caseContentTo.setContent_tz_xml(caseContentJsonObject.getString("content_tz_xml"));
                            }
                            if(!caseContentJsonObject.getString("content_tz_txt").equals("null")) {
                                caseContent.append(caseContentJsonObject.getString("content_tz_txt")).append("\r\n");
                            }
                            if(!caseContentJsonObject.getString("content_zljg_xml").equals("null")) {
                                caseContentTo.setContent_zljg_xml(caseContentJsonObject.getString("content_zljg_xml"));
                            }
                            if(!caseContentJsonObject.getString("content_zljg_txt").equals("null")) {
                                caseContent.append(caseContentJsonObject.getString("content_zljg_txt")).append("\r\n");
                            }
                            if(!caseContentJsonObject.getString("content_jws_xml").equals("null")) {
                                caseContentTo.setContent_jws_xml(caseContentJsonObject.getString("content_jws_xml"));
                            }
                            if(!caseContentJsonObject.getString("content_jws_xml").equals("null")) {
                                caseContent.append(caseContentJsonObject.getString("content_jws_xml")).append("\r\n");
                            }
                            if(!caseContentJsonObject.getString("content_jzs_xml").equals("null")) {
                                caseContentTo.setContent_jzs_xml(caseContentJsonObject.getString("content_jzs_xml"));
                            }
                            if(!caseContentJsonObject.getString("content_jzs_txt").equals("null")) {
                                caseContent.append(caseContentJsonObject.getString("content_jzs_txt")).append("\r\n");
                            }
                            if(!caseContentJsonObject.getString("content_jy_xml").equals("null")) {
                                caseContentTo.setContent_jy_xml(caseContentJsonObject.getString("content_jy_xml"));
                            }
                            if(!caseContentJsonObject.getString("content_jy_txt").equals("null")) {
                                caseContent.append(caseContentJsonObject.getString("content_jy_txt"));
                            }
                            casesTo.setCaseContentTo(caseContentTo);
                        }
                        DoctorTo doctorTo=new DoctorTo();
                        JSONObject dObject=responses.getJSONObject("doctor");
                        doctorTo.setId(dObject.getString("id"));
                        doctorTo.setHospital_name(dObject.getString("depart_name"));
                        doctorTo.setDepart_name(dObject.getString("hospital_name"));
                        doctorTo.setTitle(dObject.getString("title"));
                        doctorTo.setGoodat_fields(dObject.getString("goodat_fields"));
                        doctorTo.setApprove_status(dObject.getString("approve_status"));
                        UserTo userTo=new UserTo();
                        JSONObject uObject=dObject.getJSONObject("user");
                        userTo.setUser_name(uObject.getString("real_name"));
                        userTo.setSex(uObject.getString("sex"));
                        userTo.setBirth_year(uObject.getString("birth_year"));
                        userTo.setTp(uObject.getString("tp"));
                        userTo.setIcon_url(uObject.getString("icon_url"));
                        doctorTo.setUser(userTo);
                        UserStatisticsTo statisticsTo=new UserStatisticsTo();
                        JSONObject tObject=dObject.getJSONObject("userTj");
                        statisticsTo.setTotal_consult(tObject.getInt("total_consult"));
                        statisticsTo.setStar_value(tObject.getInt("star_value"));
                        doctorTo.setUserTj(statisticsTo);
                        casesTo.setDoctorTo(doctorTo);
                        if(!casesTo.getConsult_tp().equals("公开讨论")){
                            DoctorTo expertTo=new DoctorTo();
                            JSONObject eObject=responses.getJSONObject("expert");
                            expertTo.setId(eObject.getString("id"));
                            expertTo.setHospital_name(eObject.getString("depart_name"));
                            expertTo.setDepart_name(eObject.getString("hospital_name"));
                            expertTo.setTitle(eObject.getString("title"));
                            expertTo.setGoodat_fields(eObject.getString("goodat_fields"));
                            expertTo.setApprove_status(eObject.getString("approve_status"));
                            UserTo userTo1=new UserTo();
                            JSONObject uObject1=eObject.getJSONObject("user");
                            userTo1.setUser_name(uObject1.getString("real_name"));
                            userTo1.setSex(uObject1.getString("sex"));
                            userTo1.setBirth_year(uObject1.getString("birth_year"));
                            userTo1.setTp(uObject1.getString("tp"));
                            userTo1.setIcon_url(uObject1.getString("icon_url"));
                            expertTo.setUser(userTo1);
                            UserStatisticsTo statisticsTo1=new UserStatisticsTo();
                            JSONObject tObject1=eObject.getJSONObject("userTj");
                            statisticsTo1.setTotal_consult(tObject1.getInt("total_consult"));
                            statisticsTo1.setStar_value(tObject1.getInt("star_value"));
                            expertTo.setUserTj(statisticsTo1);
                            casesTo.setExpertTo(expertTo);
                        }
                        
                        handler.sendEmptyMessage(0);
                    } else if(responses.getInt("rtnCode") == 10004){
                        Toast.makeText(CaseInfoActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(CaseInfoActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(CaseInfoActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(CaseInfoActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if((casesTo.getStatus().equals("新建") || casesTo.getStatus().equals("已驳回") || casesTo.getStatus().equals("拒受理"))
                && editor.get("userType", "").equals("1")) {
                right_text.setVisibility(View.VISIBLE);
                right_text.setText("更多");
            } else if(casesTo.getStatus().equals("讨论中") && !casesTo.getConsult_tp().equals("公开讨论") && !editor.get("userType", "").equals("0")) {
                right_text.setVisibility(View.VISIBLE);
                right_text.setText("更多");
            } else if(casesTo.getStatus().equals("讨论中") && casesTo.getConsult_tp().equals("公开讨论") && !editor.get("userType", "").equals("0")) {
                right_text.setVisibility(View.VISIBLE);
                right_text.setText("讨论");
                count.setVisibility(View.VISIBLE);
                count.setText(casesTo.getViewingCount()+"");
            }else if(casesTo.getStatus().equals("已完成") && editor.get("userType", "").equals("1")
                && !casesTo.getConsult_tp().equals("公开讨论")) {
                right_text.setVisibility(View.VISIBLE);
                right_text.setText("评价");
            } else if(casesTo.getStatus().equals("已完成") && editor.get("userType", "").equals("2")
                && !casesTo.getConsult_tp().equals("公开讨论")) {
                right_text.setVisibility(View.VISIBLE);
                right_text.setText("查看评价");
            } else if(casesTo.getStatus().equals("已审核") && editor.get("userType", "").equals("2")
                && !casesTo.getConsult_tp().equals("公开讨论")) {
                right_text.setVisibility(View.VISIBLE);
                right_text.setText("更多");
            }
            all.setVisibility(View.VISIBLE);
            primaryName.setText(casesTo.getDoctor_name());
            primaryDepartment.setText(casesTo.getDoctorTo().getDepart_name());
            primaryTitle.setText(casesTo.getDoctorTo().getTitle());
            primaryHospital.setText(casesTo.getDoctorTo().getHospital_name());
            if(casesTo.getConsult_tp().equals("公开讨论")){
                LinearLayout expertLayout = (LinearLayout)findViewById(R.id.case_info_expert_layout);
                LinearLayout expertLine = (LinearLayout)findViewById(R.id.case_info_expert_line);
                expertLayout.setVisibility(View.GONE);
                expertLine.setVisibility(View.GONE);
            }else{
                if(!"null".equals(casesTo.getDoctorTo().getUser().getIcon_url())
                        && !"".equals(casesTo.getDoctorTo().getUser().getIcon_url())) {
                    ImageListener listener=ImageLoader.getImageListener(doctorPhoto, R.drawable.photo_primary, R.drawable.photo_primary);
                    mImageLoader.get(casesTo.getDoctorTo().getUser().getIcon_url(), listener);
                }
                if(!"null".equals(casesTo.getExpertTo().getUser().getIcon_url())
                        && !"".equals(casesTo.getExpertTo().getUser().getIcon_url())) {
                        ImageListener listener=ImageLoader.getImageListener(expertPhoto, R.drawable.photo_expert, R.drawable.photo_expert);
                        mImageLoader.get(casesTo.getExpertTo().getUser().getIcon_url(), listener);
                    }
                expertName.setText(casesTo.getExpert_name());
                expertDepartment.setText(casesTo.getExpertTo().getDepart_name());
                expertTitle.setText(casesTo.getExpertTo().getTitle());
                expertHospital.setText(casesTo.getExpertTo().getHospital_name());
            }
            caseStatusText.setText(casesTo.getStatus());
            caseStatus.setText(casesTo.getPatient_name() + "先生/女士");
            caseTitleText.setText(casesTo.getTitle());
            caseInfoText.setText(caseContent.toString());
            caseUnresolvedText.setText(casesTo.getProblem());
            if(casesTo.getOpinion() != null && !casesTo.getOpinion().equals("null") && !casesTo.getOpinion().equals("")) {
                caseOpinionText.setText(casesTo.getOpinion());
            }
            caseModelText.setText(casesTo.getConsult_tp());
            if(editor.get("userType", "").equals("1") && casesTo.getStatus().equals("讨论中")){
                updateOpinionBtn.setVisibility(View.VISIBLE);
            }
        }
    };

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例详情");
        title_text.setTextSize(20);
        
        count = (Button)findViewById(R.id.header_right_tip);

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

        right_text=(TextView)findViewById(R.id.header_right);
        right_text.setTextSize(18);
        right_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if((casesTo.getStatus().equals("新建") || casesTo.getStatus().equals("已驳回") || casesTo.getStatus().equals("拒受理"))) {
                    // 进入修改界面
                    Intent intent=new Intent(CaseInfoActivity.this, CaseMoreActivity.class);
                    intent.putExtra("btn1", "修改");
                    intent.putExtra("btn2", "删除");
                    intent.putExtra("flag", "1");
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("isUpdate", true);
                    intent.putExtra("departmentId", casesTo.getDepart_id());
                    intent.putExtra("expertId", casesTo.getExpert_userid());
                    intent.putExtra("expertName", casesTo.getExpert_name());
                    intent.putExtra("patientId", casesTo.getPatient_id());
                    intent.putExtra("patientName", casesTo.getPatient_name());
                    intent.putExtra("consultType", casesTo.getConsult_tp());
                    intent.putExtra("titles", casesTo.getTitle());
                    intent.putExtra("problem", casesTo.getProblem());
                    StringBuffer buffer=new StringBuffer();
                    if(haveCase){
                        if(casesTo.getCaseContentTo().getFill_tp().equals("1")) {
                            // txt
                            buffer.append(casesTo.getCaseContentTo().getContent_zs_txt());
                            buffer.append(casesTo.getCaseContentTo().getContent_tz_txt()).append("&");
                            buffer.append(casesTo.getCaseContentTo().getContent_jy_txt()).append("&");
                            buffer.append(casesTo.getCaseContentTo().getContent_zljg_txt()).append("&");
                            buffer.append(casesTo.getCaseContentTo().getContent_jws_txt()).append("&");
                            buffer.append(casesTo.getCaseContentTo().getContent_jzs_txt()).append("&");
                            intent.putExtra("content", buffer.toString());
                        } else if(casesTo.getCaseContentTo().getFill_tp().equals("2")) {
                            // xml
                            buffer.append(casesTo.getCaseContentTo().getContent_zs_xml()).append(",");
                            buffer.append(casesTo.getCaseContentTo().getContent_tz_xml()).append(",");
                            buffer.append(casesTo.getCaseContentTo().getContent_jy_xml()).append(",");
                            buffer.append(casesTo.getCaseContentTo().getContent_zljg_xml()).append(",");
                            buffer.append(casesTo.getCaseContentTo().getContent_jws_xml()).append(",");
                            buffer.append(casesTo.getCaseContentTo().getContent_jzs_xml()).append(",");
                            intent.putExtra("content", buffer.toString());
                        }
                    }
                    startActivity(intent);
                    CaseBroadcastReceiver.setHander(new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            initData();
                            initView();
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {

                        }
                    });
                } else if(casesTo.getStatus().equals("讨论中") && !casesTo.getConsult_tp().equals("公开讨论") && (editor.get("userType", "").equals("2") || editor.get("userType", "").equals("1"))) {
                    Intent intent=new Intent(CaseInfoActivity.this, CaseMoreActivity.class);
                    intent.putExtra("btn1", "手术或住院");
                    intent.putExtra("btn2", "讨论");
                    intent.putExtra("flag", "2");
                    intent.putExtra("viewingCount", casesTo.getViewingCount()+"");
                    intent.putExtra("opinion", casesTo.getOpinion());
                    intent.putExtra("consultType", casesTo.getConsult_tp());
                    intent.putExtra("caseId", caseId);
                    startActivity(intent);
                }else if(casesTo.getStatus().equals("讨论中") && casesTo.getConsult_tp().equals("公开讨论") && (editor.get("userType", "").equals("2") || editor.get("userType", "").equals("1"))) {
                    // 进入讨论界面
                    Intent intent=new Intent(CaseInfoActivity.this, DiscussionCaseActivity.class);
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("opinion", casesTo.getOpinion());
                    intent.putExtra("consultType", casesTo.getConsult_tp());
                    startActivity(intent);
                }else if(right_text.getText().toString().equals("评价")) {
                    // 进入评价界面
                    Intent intent=new Intent(CaseInfoActivity.this, EvaluationCaseActivity.class);
                    intent.putExtra("doctorUserId", casesTo.getDoctor_userid());
                    intent.putExtra("caseId", casesTo.getId());
                    startActivity(intent);
                } else if(right_text.getText().toString().equals("查看评价")) {
                    Intent intent=new Intent(CaseInfoActivity.this, EvaluationCaseActivity.class);
                    intent.putExtra("doctorUserId", casesTo.getDoctor_userid());
                    startActivity(intent);
                } else if(casesTo.getStatus().equals("已审核") && editor.get("userType", "").equals("2")
                    && !casesTo.getConsult_tp().equals("公开讨论")) {
                    Intent intent=new Intent(CaseInfoActivity.this, CaseMoreActivity.class);
                    intent.putExtra("btn1", "受理");
                    intent.putExtra("btn2", "拒绝");
                    intent.putExtra("flag", "3");
                    intent.putExtra("caseId", caseId);
                    startActivity(intent);
                }
            }
        });

        all=(LinearLayout)findViewById(R.id.case_info_all_layout);
        all.setVisibility(View.GONE);

        doctorPhoto=(ImageView)findViewById(R.id.case_info_primary_imageView);
        expertPhoto=(ImageView)findViewById(R.id.case_info_expert_imageView);

        primaryName=(TextView)findViewById(R.id.case_info_primary_name_text);
        primaryName.setTextSize(20);
        primaryDepartment=(TextView)findViewById(R.id.case_info_primary_department_text);
        primaryDepartment.setTextSize(15);
        primaryTitle=(TextView)findViewById(R.id.case_info_primary_title_text);
        primaryTitle.setTextSize(15);
        primaryHospital=(TextView)findViewById(R.id.case_info_primary_hospital_text);
        primaryHospital.setTextSize(13);
        primaryTip=(TextView)findViewById(R.id.case_info_primary_tip_text);
        primaryTip.setTextSize(16);

        expertName=(TextView)findViewById(R.id.case_info_expert_name_text);
        expertName.setTextSize(20);
        expertDepartment=(TextView)findViewById(R.id.case_info_expert_department_text);
        expertDepartment.setTextSize(15);
        expertTitle=(TextView)findViewById(R.id.case_info_expert_title_text);
        expertTitle.setTextSize(15);
        expertHospital=(TextView)findViewById(R.id.case_info_expert_hospital_text);
        expertHospital.setTextSize(13);
        expertTip=(TextView)findViewById(R.id.case_info_expert_tip_text);
        expertTip.setTextSize(16);

        caseTitle=(TextView)findViewById(R.id.case_info_title_text);
        caseTitle.setTextSize(17);

        caseModel=(TextView)findViewById(R.id.case_info_mode_text);
        caseModel.setTextSize(17);
        
        picText=(TextView)findViewById(R.id.case_info_pic_text);
        picText.setTextSize(17);

        caseModelText=(TextView)findViewById(R.id.case_info_mode_info_text);
        caseModelText.setTextSize(17);

        caseOpinion=(TextView)findViewById(R.id.case_info_opinion_text);
        caseOpinion.setTextSize(17);

        caseOpinionText=(TextView)findViewById(R.id.case_info_opinion_info_text);
        caseOpinionText.setTextSize(17);

        caseStatus=(TextView)findViewById(R.id.case_info_status_text);
        caseStatus.setTextSize(17);

        caseStatusText=(TextView)findViewById(R.id.case_info_status_info_text);
        caseStatusText.setTextSize(17);

        caseTitleText=(TextView)findViewById(R.id.case_info_title_info_text);
        caseTitleText.setTextSize(17);

        caseInfo=(TextView)findViewById(R.id.case_info_details_text);
        caseInfo.setTextSize(17);

        caseInfoText=(TextView)findViewById(R.id.case_info_details_info_text);
        caseInfoText.setTextSize(17);

        caseUnresolved=(TextView)findViewById(R.id.case_info_unresolved_text);
        caseUnresolved.setTextSize(17);

        caseUnresolvedText=(TextView)findViewById(R.id.case_info_unresolved_info_text);
        caseUnresolvedText.setTextSize(17);
        
        picLayout = (LinearLayout)findViewById(R.id.case_info_pic_layout);
        picLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //进入图片界面
                if(isXml(casesTo.getDepart_id())){
                    //xml界面
                    Intent intent = new Intent(CaseInfoActivity.this,CaseTestViewActivity.class);
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("departmentId", casesTo.getDepart_id());
                    startActivity(intent);
                }else{
                    //非xml界面
                    Intent intent = new Intent(CaseInfoActivity.this,CaseTestViewTxtActivity.class);
                    intent.putExtra("caseId", caseId);
                    startActivity(intent);
                }
            }

            private boolean isXml(String departmentId) {
                AssetManager assetManager=getAssets();
                try {
                    for(String str: assetManager.list("")) {
                        if(str.equals(departmentId + "case.xml")) {
                            return true;
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }
        });
        
        updateOpinionBtn=(ImageView)findViewById(R.id.case_info_opinion_update_image);
        updateOpinionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                caseTitleText.setVisibility(View.GONE);
                opinionEdit.setText(caseTitleText.getText());
                opinionEdit.setVisibility(View.VISIBLE);
                updateOpinionBtn.setVisibility(View.GONE);
                saveOpinionBtn.setVisibility(View.VISIBLE);
                opinionEdit.setFocusable(true);
                opinionEdit.setFocusableInTouchMode(true);
                opinionEdit.requestFocus();
                InputMethodManager inputManager=
                    (InputMethodManager)opinionEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(opinionEdit, 0);
            }
        });
        saveOpinionBtn=(ImageView)findViewById(R.id.case_info_opinion_save_image);
        saveOpinionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("caseId", caseId);
                parmas.put("opinion", opinionEdit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoActivity.this);
                OpenApiService.getInstance(CaseInfoActivity.this).getCaseOpinion(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(CaseInfoActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    caseTitleText.setText(opinionEdit.getText().toString());
                                    updateOpinionBtn.setVisibility(View.VISIBLE);
                                    saveOpinionBtn.setVisibility(View.GONE);
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                            Toast.makeText(CaseInfoActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }
}
