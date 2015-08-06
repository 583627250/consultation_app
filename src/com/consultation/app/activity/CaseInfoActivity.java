package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.model.CasesTo;
import com.consultation.app.model.PatientTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class CaseInfoActivity extends Activity {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private TextView primaryName, primaryDepartment, primaryTitle, primaryHospital, primaryTip, expertName, expertDepartment,
            expertTitle, expertHospital, expertTip, caseStatus, caseStatusText, caseTitle, caseTitleText, caseInfo, caseInfoText,
            caseUnresolved, caseUnresolvedText;

    private LinearLayout all;

    private ImageView updateTitleBtn,updateInfoBtn,updateUnresolvedBtn;

    private String caseId;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private String caseContent;

    private CasesTo casesTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_info_layout);
        caseId=getIntent().getStringExtra("caseId");
        editor=new SharePreferencesEditor(CaseInfoActivity.this);
        initData();
        initView();
    }

    private void initView() {
        mQueue=Volley.newRequestQueue(CaseInfoActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
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
                        caseContent=responses.getString("caseContent");
                        JSONObject info=responses.getJSONObject("patientCase");
                        casesTo=new CasesTo();
                        casesTo.setId(info.getString("id"));
                        casesTo.setStatus(info.getString("status"));
                        casesTo.setDestination(info.getString("destination"));
                        casesTo.setCreate_time(info.getLong("create_time"));
                        casesTo.setTitle(info.getString("title"));
                        casesTo.setDepart_id(info.getString("depart_id"));
                        casesTo.setDoctor_userid(info.getString("doctor_userid"));
                        casesTo.setPatient_name(info.getString("patient_name"));
                        casesTo.setConsult_fee(info.getInt("consult_fee"));
                        casesTo.setDoctor_name(info.getString("doctor_name"));
                        casesTo.setExpert_userid(info.getString("expert_userid"));
                        casesTo.setExpert_name(info.getString("expert_name"));
                        casesTo.setProblem(info.getString("problem"));
                        casesTo.setConsult_tp(info.getString("consult_tp"));
                        casesTo.setOpinion(info.getString("opinion"));
                        casesTo.setUid(info.getString("uid"));
                        PatientTo patientTo=new PatientTo();
                        JSONObject pObject=info.getJSONObject("user");
                        patientTo.setAddress(pObject.getString("address"));
                        patientTo.setId(pObject.getInt("id") + "");
                        patientTo.setState(pObject.getString("state"));
                        // patientTo.setCreate_time(pObject.getLong("create_time"));
                        patientTo.setTp(pObject.getString("tp"));
                        patientTo.setDoctor(pObject.getString("doctor"));
                        patientTo.setMobile_ph(pObject.getString("mobile_ph"));
                        patientTo.setPwd(pObject.getString("pwd"));
                        patientTo.setReal_name(pObject.getString("real_name"));
                        patientTo.setSex(pObject.getString("sex"));
                        patientTo.setBirth_year(pObject.getString("birth_year"));
                        patientTo.setBirth_month(pObject.getString("birth_month"));
                        patientTo.setBirth_day(pObject.getString("birth_day"));
                        patientTo.setIdentity_id(pObject.getString("identity_id"));
                        patientTo.setArea_province(pObject.getString("area_province"));
                        patientTo.setArea_city(pObject.getString("area_city"));
                        patientTo.setArea_county(pObject.getString("area_county"));
                        patientTo.setIcon_url(pObject.getString("icon_url"));
                        patientTo.setModify_time(pObject.getString("modify_time"));
                        patientTo.setUid(pObject.getString("uid"));
                        casesTo.setPatient(patientTo);
                        handler.sendEmptyMessage(0);
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
            all.setVisibility(View.VISIBLE);
            primaryName.setText(casesTo.getDoctor_name());
            primaryDepartment.setText("");
            primaryTitle.setText("");
            primaryHospital.setText("");
            expertName.setText(casesTo.getExpert_name());
            expertDepartment.setText("");
            expertTitle.setText("");
            expertHospital.setText("");
            caseStatusText.setText(casesTo.getStatus());
            caseTitleText.setText(casesTo.getTitle());
            caseInfoText.setText(caseContent);
            caseUnresolvedText.setText("");
        }
    };

    private void initData() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例详情");
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

        all=(LinearLayout)findViewById(R.id.case_info_all_layout);
        all.setVisibility(View.GONE);

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
        
        updateTitleBtn = (ImageView)findViewById(R.id.case_info_title_update_image);
        updateTitleBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(CaseInfoActivity.this, "修改标题", Toast.LENGTH_LONG).show();
            }
        });
        updateInfoBtn = (ImageView)findViewById(R.id.case_info_details_update_image);
        updateInfoBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(CaseInfoActivity.this, "修改明细", Toast.LENGTH_LONG).show();
            }
        });
        updateUnresolvedBtn = (ImageView)findViewById(R.id.case_info_unresolved_update_image);
        updateUnresolvedBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(CaseInfoActivity.this, "修改待解决", Toast.LENGTH_LONG).show();
            }
        });
    }

}
