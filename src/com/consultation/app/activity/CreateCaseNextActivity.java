package com.consultation.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.CaseParams;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ActivityList;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class CreateCaseNextActivity extends Activity implements OnClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private LinearLayout symptom_layout, signs_layout, test_layout, check_layout, diagnosis_layout, past_history_layout,
            family_history_layout;

    private TextView symptom_text, signs_text, test_text, check_text, diagnosis_text, past_history_text, family_history_text,
            symptom_status_text, signs_status_text, diagnosis_status_text, past_history_status_text, family_history_status_text,
            test_status_text, check_status_text, tip;

    private Button submitBtn, saveBtn;

    private SharePreferencesEditor editor;

    private String caseId;

    private String departmentId="";

    private boolean isXml=false, isUpdate;

    private RequestQueue mQueue;

    private String content="", imageString;

    private int isOpen;

    private List<String> files=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_new_next_layout);
        editor=new SharePreferencesEditor(CreateCaseNextActivity.this);
        caseId=getIntent().getStringExtra("caseId");
        isUpdate=getIntent().getBooleanExtra("isUpdate", false);
        content=getIntent().getStringExtra("content");
        departmentId=getIntent().getStringExtra("departmentId");
        imageString=getIntent().getStringExtra("imageString");
        isOpen=getIntent().getIntExtra("isOpen",1);
        initData();
        initView(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("isAdd", symptom_status_text.getText().toString() + "," + signs_status_text.getText().toString() + ","
            + diagnosis_status_text.getText().toString() + "," + past_history_status_text.getText().toString() + ","
            + family_history_status_text.getText().toString() + "," + test_status_text.getText().toString() + ","
            + check_status_text.getText().toString() + ",");
        super.onSaveInstanceState(outState);
    }

    private void initData() {
        mQueue=Volley.newRequestQueue(CreateCaseNextActivity.this);
        AssetManager assetManager=getAssets();
        try {
            for(String str: assetManager.list("")) {
                if(str.endsWith("case.xml")) {
                    if(str.equals(departmentId + "case.xml")) {
                        isXml=true;
                    }
                    files.add(str);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
//    @Override
//    protected void onResume() {
//        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm.isActive()) {
//            imm.hideSoftInputFromWindow(symptom_status_text.getApplicationWindowToken(), 0);
//        }
//        super.onResume();
//    }

    private void initView(Bundle savedInstanceState) {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("填写病例");
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
                ClientUtil.reSetCaseParams();
//                ActivityList.getInstance().closeActivity("CreateCaseActivity");
//                Intent intent=new Intent("com.consultation.app.new.case.action");
//                sendBroadcast(intent);
                finish();
            }
        });
        // case_text=(TextView)findViewById(R.id.create_case_case_text);
        // case_text.setTextSize(18);
        symptom_text=(TextView)findViewById(R.id.create_case_symptom_text);
        symptom_text.setTextSize(18);
        signs_text=(TextView)findViewById(R.id.create_case_signs_text);
        signs_text.setTextSize(18);
        test_text=(TextView)findViewById(R.id.create_case_test_text);
        test_text.setTextSize(18);
        check_text=(TextView)findViewById(R.id.create_case_check_text);
        check_text.setTextSize(18);
        diagnosis_text=(TextView)findViewById(R.id.create_case_diagnosis_text);
        diagnosis_text.setTextSize(18);
        past_history_text=(TextView)findViewById(R.id.create_case_past_history_text);
        past_history_text.setTextSize(18);
        family_history_text=(TextView)findViewById(R.id.create_case_family_history_text);
        family_history_text.setTextSize(18);

        symptom_status_text=(TextView)findViewById(R.id.create_case_symptom_status_text);
        symptom_status_text.setTextSize(18);
        signs_status_text=(TextView)findViewById(R.id.create_case_signs_status_text);
        signs_status_text.setTextSize(18);
        test_status_text=(TextView)findViewById(R.id.create_case_test_status_text);
        test_status_text.setTextSize(18);
        check_status_text=(TextView)findViewById(R.id.create_case_check_status_text);
        check_status_text.setTextSize(18);
        diagnosis_status_text=(TextView)findViewById(R.id.create_case_diagnosis_status_text);
        diagnosis_status_text.setTextSize(18);
        past_history_status_text=(TextView)findViewById(R.id.create_case_past_history_status_text);
        past_history_status_text.setTextSize(18);
        family_history_status_text=(TextView)findViewById(R.id.create_case_family_history_status_text);
        family_history_status_text.setTextSize(18);
        
        if(savedInstanceState != null) {
            symptom_status_text.setText(savedInstanceState.getString("isAdd").split(",")[0]);
            signs_status_text.setText(savedInstanceState.getString("isAdd").split(",")[1]);
            diagnosis_status_text.setText(savedInstanceState.getString("isAdd").split(",")[2]);
            past_history_status_text.setText(savedInstanceState.getString("isAdd").split(",")[3]);
            family_history_status_text.setText(savedInstanceState.getString("isAdd").split(",")[4]);
            test_status_text.setText(savedInstanceState.getString("isAdd").split(",")[5]);
            check_status_text.setText(savedInstanceState.getString("isAdd").split(",")[6]);
        }
        
        tip=(TextView)findViewById(R.id.create_case_two_tip_text);
        tip.setTextSize(14);

        symptom_layout=(LinearLayout)findViewById(R.id.create_case_symptom_layout);
        symptom_layout.setOnClickListener(this);

        signs_layout=(LinearLayout)findViewById(R.id.create_case_signs_layout);
        signs_layout.setOnClickListener(this);

        test_layout=(LinearLayout)findViewById(R.id.create_case_test_layout);
        test_layout.setOnClickListener(this);

        check_layout=(LinearLayout)findViewById(R.id.create_case_check_layout);
        check_layout.setOnClickListener(this);

        diagnosis_layout=(LinearLayout)findViewById(R.id.create_case_diagnosis_layout);
        diagnosis_layout.setOnClickListener(this);

        past_history_layout=(LinearLayout)findViewById(R.id.create_case_past_history_layout);
        past_history_layout.setOnClickListener(this);

        family_history_layout=(LinearLayout)findViewById(R.id.create_case_family_history_layout);
        family_history_layout.setOnClickListener(this);

        submitBtn=(Button)findViewById(R.id.create_case_btn_finish_sumbit);
        submitBtn.setTextSize(20);
        submitBtn.setOnClickListener(this);
        saveBtn=(Button)findViewById(R.id.create_case_btn_finish_save);
        saveBtn.setTextSize(20);
        saveBtn.setOnClickListener(this);
        if(imageString != null && !"null".equals(imageString) && !"".equals(imageString) && !"[]".equals(imageString)) {
            if(imageString.contains("\"case_item\":\"jc\"")) {
                check_status_text.setText("已填写");
            } 
            if(imageString.contains("\"case_item\":\"jy\"")) {
                test_status_text.setText("已填写");
            }
        }
        if(content != null && !"null".equals(content) && !"".equals(content)) {
            String[] contents=content.split("&");
            if(contents[0] != null && !"null".equals(contents[0]) && !"".equals(contents[0])) {
                symptom_status_text.setText("已填写");
            }
            if(contents[1] != null && !"null".equals(contents[1]) && !"".equals(contents[1])) {
                signs_status_text.setText("已填写");
            }
            if(contents[2] != null && !"null".equals(contents[2]) && !"".equals(contents[2])) {
                diagnosis_status_text.setText("已填写");
            }
            if(contents[3] != null && !"null".equals(contents[3]) && !"".equals(contents[3])) {
                past_history_status_text.setText("已填写");
            }
            if(contents[4] != null && !"null".equals(contents[4]) && !"".equals(contents[4])) {
                family_history_status_text.setText("已填写");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.create_case_symptom_layout:
                // 现病史
                if(isXml) {
                    Intent intent=new Intent(CreateCaseNextActivity.this, CaseSelelctSymptomActivity.class);
                    intent.putExtra("page", 0);
                    intent.putExtra("titleText", "现病史");
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        intent.putExtra("content", content.split("&")[0]);
                    } else {
                        intent.putExtra("content", "");
                    }
                    intent.putExtra("departmentId", departmentId);
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, SymptomTxtActivity.class);
                    intent.putExtra("page", 0);
                    intent.putExtra("titleText", "现病史");
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        intent.putExtra("content", content.split("&")[0]);
                    } else {
                        intent.putExtra("content", "");
                    }
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.create_case_signs_layout:
                // 体格检查
                if(isXml) {
                    Intent signsIntent=new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                    signsIntent.putExtra("page", 1);
                    signsIntent.putExtra("titleText", "体格检查");
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        signsIntent.putExtra("content", content.split("&")[1]);
                    } else {
                        signsIntent.putExtra("content", "");
                    }
                    signsIntent.putExtra("departmentId", departmentId);
                    startActivityForResult(signsIntent, 1);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, SymptomTxtActivity.class);
                    intent.putExtra("page", 1);
                    intent.putExtra("titleText", "体格检查");
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        intent.putExtra("content", content.split("&")[1]);
                    } else {
                        intent.putExtra("content", "");
                    }
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.create_case_test_layout:
                // 检验
                if(isXml) {
                    Intent checkIntent=new Intent(CreateCaseNextActivity.this, CaseTestActivity.class);
                    checkIntent.putExtra("page", 5);
                    checkIntent.putExtra("caseId", caseId);
                    checkIntent.putExtra("departmentId", departmentId);
                    checkIntent.putExtra("titleText", "检验");
                    checkIntent.putExtra("imageString", imageString);
                    startActivityForResult(checkIntent, 5);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, CaseTestTxtActivity.class);
                    intent.putExtra("titleText", "检验");
                    intent.putExtra("imageString", imageString);
                    intent.putExtra("page", 5);
                    intent.putExtra("caseId", caseId);
                    startActivityForResult(intent, 5);
                }
                break;
            case R.id.create_case_check_layout:
                // 检查
                if(isXml) {
                    Intent checkIntent=new Intent(CreateCaseNextActivity.this, CaseTestActivity.class);
                    checkIntent.putExtra("page", 6);
                    checkIntent.putExtra("caseId", caseId);
                    checkIntent.putExtra("departmentId", departmentId);
                    checkIntent.putExtra("titleText", "检查");
                    checkIntent.putExtra("imageString", imageString);
                    startActivityForResult(checkIntent, 6);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, CaseTestTxtActivity.class);
                    intent.putExtra("titleText", "检查");
                    intent.putExtra("imageString", imageString);
                    intent.putExtra("page", 6);
                    intent.putExtra("caseId", caseId);
                    startActivityForResult(intent, 6);
                }
                break;
            case R.id.create_case_diagnosis_layout:
                // 诊疗经过
                if(isXml) {
                    Intent diagnosisIntent=new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                    diagnosisIntent.putExtra("page", 2);
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        diagnosisIntent.putExtra("content", content.split("&")[2]);
                    } else {
                        diagnosisIntent.putExtra("content", "");
                    }
                    diagnosisIntent.putExtra("departmentId", departmentId);
                    diagnosisIntent.putExtra("titleText", "诊疗经过");
                    startActivityForResult(diagnosisIntent, 2);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, SymptomTxtActivity.class);
                    intent.putExtra("titleText", "诊疗经过");
                    intent.putExtra("page", 2);
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        intent.putExtra("content", content.split("&")[2]);
                    } else {
                        intent.putExtra("content", "");
                    }
                    startActivityForResult(intent, 2);
                }
                break;
            case R.id.create_case_past_history_layout:
                // 既往史
                if(isXml) {
                    Intent historyIntent=new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                    historyIntent.putExtra("page", 3);
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        historyIntent.putExtra("content", content.split("&")[3]);
                    } else {
                        historyIntent.putExtra("content", "");
                    }
                    historyIntent.putExtra("titleText", "既往史");
                    historyIntent.putExtra("departmentId", departmentId);
                    startActivityForResult(historyIntent, 3);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, SymptomTxtActivity.class);
                    intent.putExtra("titleText", "既往史");
                    intent.putExtra("page", 3);
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        intent.putExtra("content", content.split("&")[3]);
                    } else {
                        intent.putExtra("content", "");
                    }
                    startActivityForResult(intent, 3);
                }
                break;
            case R.id.create_case_family_history_layout:
                // 家族史
                if(isXml) {
                    Intent familyIntent=new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                    familyIntent.putExtra("page", 4);
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        familyIntent.putExtra("content", content.split("&")[4]);
                    } else {
                        familyIntent.putExtra("content", "");
                    }
                    familyIntent.putExtra("titleText", "家族史");
                    familyIntent.putExtra("departmentId", departmentId);
                    startActivityForResult(familyIntent, 4);
                } else {
                    Intent intent=new Intent(CreateCaseNextActivity.this, SymptomTxtActivity.class);
                    intent.putExtra("titleText", "家族史");
                    intent.putExtra("page", 4);
                    if(!"null".equals(content) && !"".equals(content) && null != content) {
                        intent.putExtra("content", content.split("&")[4]);
                    } else {
                        intent.putExtra("content", "");
                    }
                    startActivityForResult(intent, 4);
                }
                break;
            case R.id.create_case_btn_finish_save:
                // 提交信息
//                if(!isOk) {
//                    Toast.makeText(CreateCaseNextActivity.this, "请填写病例明细", Toast.LENGTH_LONG).show();
//                    return;
//                }
                getHttpMethod("0");
                break;
            case R.id.create_case_btn_finish_sumbit:
                // 提交信息
//                if(!isOk) {
//                    Toast.makeText(CreateCaseNextActivity.this, "请填写病例明细", Toast.LENGTH_LONG).show();
//                    return;
//                }
                getHttpMethod("1");
                break;
            default:
                break;
        }
    }

    private void getHttpMethod(String isSubmit) {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("case_id", caseId);
        parmas.put("is_submit", isSubmit);
        if(isXml) {
            parmas.put("fill_tp", "2");
        } else {
            parmas.put("fill_tp", "1");
        }
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        CaseParams caseParams=ClientUtil.getCaseParams();
        List<String> keys=caseParams.getKeys();
        for(int i=0; i < keys.size(); i++) {
            switch(Integer.parseInt(keys.get(i))) {
                case 0:
                    if(null != caseParams.getValue("0") && !"".equals(caseParams.getValue("0"))) {
                        parmas.put("content_zs_xml", caseParams.getValue("0"));
                    }
                    break;
                case 1:
                    if(null != caseParams.getValue("1") && !"".equals(caseParams.getValue("1"))) {
                        parmas.put("content_tz_xml", caseParams.getValue("1"));
                    }
                    break;
                case 2:
                    if(null != caseParams.getValue("2") && !"".equals(caseParams.getValue("2"))) {
                        parmas.put("content_zljg_xml", caseParams.getValue("2"));
                    }
                    break;
                case 3:
                    if(null != caseParams.getValue("3") && !"".equals(caseParams.getValue("3"))) {
                        parmas.put("content_jws_xml", caseParams.getValue("3"));
                    }
                    break;
                case 4:
                    if(null != caseParams.getValue("4") && !"".equals(caseParams.getValue("4"))) {
                        parmas.put("content_jzs_xml", caseParams.getValue("4"));
                    }
                    break;
                default:
                    break;
            }
        }
        CommonUtil.showLoadingDialog(CreateCaseNextActivity.this);
        OpenApiService.getInstance(CreateCaseNextActivity.this).getCaseSaveInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        ClientUtil.reSetCaseParams();
                        if(isUpdate) {
                            ActivityList.getInstance().closeActivity("CreateCaseActivity");
                            Intent intent=new Intent("com.consultation.app.update.case.action");
                            intent.putExtra("isOpen", isOpen);
                            sendBroadcast(intent);
                        } else {
                            ActivityList.getInstance().closeActivity("CreateCaseActivity");
                            Intent intent=new Intent("com.consultation.app.new.case.action");
                            sendBroadcast(intent);
                        }
                        finish();
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(CreateCaseNextActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(CreateCaseNextActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(CreateCaseNextActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(CreateCaseNextActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            switch(requestCode) {
                case 0:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        symptom_status_text.setText("已填写");
                    }
                    break;
                case 1:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        signs_status_text.setText("已填写");
                    }
                    break;
                case 2:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        diagnosis_status_text.setText("已填写");
                    }
                    break;
                case 3:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        past_history_status_text.setText("已填写");
                    }
                    break;
                case 4:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        family_history_status_text.setText("已填写");
                    }
                    break;
                case 5:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        test_status_text.setText("已填写");
                    }
                    break;
                case 6:
                    if(data.getExtras().getBoolean("isAdd")) {
//                        isOk=true;
                        check_status_text.setText("已填写");
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientUtil.reSetCaseParams();
    }
}
