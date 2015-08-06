package com.consultation.app.activity;

import java.util.HashMap;
import java.util.List;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.CaseParams;
import com.consultation.app.R;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class CreateCaseNextActivity extends Activity implements OnClickListener {

    private TextView title_text, back_text, case_text;

    private LinearLayout back_layout;

    private LinearLayout symptom_layout, signs_layout, test_layout, check_layout, diagnosis_layout, past_history_layout, family_history_layout;

    private TextView symptom_text, signs_text, test_text, check_text, diagnosis_text, past_history_text, family_history_text, symptom_status_text,
            signs_status_text, diagnosis_status_text, past_history_status_text, family_history_status_text, test_status_text, check_status_text,tip;

    private Button submitBtn,saveBtn;
    
    private SharePreferencesEditor editor;
    
    private String caseId;
    
    private String[] paths;
    
    private RequestQueue mQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_new_next_layout);
        editor=new SharePreferencesEditor(CreateCaseNextActivity.this);
        caseId = getIntent().getStringExtra("caseId");
        initData();
        initView();
    }

    private void initData() {
        mQueue=Volley.newRequestQueue(CreateCaseNextActivity.this);
    }

    private void initView() {
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
                finish();
            }
        });
        case_text=(TextView)findViewById(R.id.create_case_case_text);
        case_text.setTextSize(18);
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

        
        submitBtn = (Button)findViewById(R.id.create_case_btn_finish_sumbit);
        submitBtn.setTextSize(20);
        submitBtn.setOnClickListener(this);
        saveBtn = (Button)findViewById(R.id.create_case_btn_finish_save);
        saveBtn.setTextSize(20);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.create_case_symptom_layout:
                // 症状
                Intent intent = new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                intent.putExtra("page", 0);
                intent.putExtra("titleText", "症状");
                startActivityForResult(intent, 0);
                break;
            case R.id.create_case_signs_layout:
                // 体征
                Intent signsIntent = new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                signsIntent.putExtra("page", 1);
                signsIntent.putExtra("titleText", "体征");
                startActivityForResult(signsIntent, 1);
                break;
            case R.id.create_case_test_layout:
                // 检验
                Intent testIntent = new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                testIntent.putExtra("page", 2);
                testIntent.putExtra("titleText", "检验");
                startActivityForResult(testIntent, 2);
                break;
            case R.id.create_case_check_layout:
                // 检查
                Intent checkIntent = new Intent(CreateCaseNextActivity.this, CaseTestActivity.class);
                checkIntent.putExtra("page", 3);
                checkIntent.putExtra("titleText", "检查");
                startActivityForResult(checkIntent, 3);
                break;
            case R.id.create_case_diagnosis_layout:
                // 诊疗经过
                Intent diagnosisIntent = new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                diagnosisIntent.putExtra("page", 4);
                diagnosisIntent.putExtra("titleText", "诊疗经过");
                startActivityForResult(diagnosisIntent, 4);
                break;
            case R.id.create_case_past_history_layout:
                // 既往史
                Intent historyIntent = new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                historyIntent.putExtra("page", 5);
                historyIntent.putExtra("titleText", "既往史");
                startActivityForResult(historyIntent, 5);
                break;
            case R.id.create_case_family_history_layout:
                // 家族史
                Intent familyIntent = new Intent(CreateCaseNextActivity.this, SymptomActivity.class);
                familyIntent.putExtra("page", 6);
                familyIntent.putExtra("titleText", "家族史");
                startActivityForResult(familyIntent, 6);
                break;
            case R.id.create_case_btn_finish_save:
                //提交信息
                getHttpMethod("0");
            case R.id.create_case_btn_finish_sumbit:
                //提交信息
                getHttpMethod("1");
                break;
            default:
                break;
        }
    }
    
    private void getHttpMethod(String isSubmit){
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("id", "10");
        parmas.put("is_submit", isSubmit);
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        CaseParams caseParams = ClientUtil.getCaseParams();
        List<String> keys = caseParams.getKeys();
        for(int i=0; i < keys.size(); i++) {
            switch(Integer.parseInt(keys.get(i))) {
                case 0:
                    if(null != caseParams.getValue("0") && !"".equals(caseParams.getValue("0"))){
                        parmas.put("content_zs_xml", caseParams.getValue("0"));
                    }
                    break;
                case 1:
                    if(null != caseParams.getValue("1") && !"".equals(caseParams.getValue("1"))){
                        parmas.put("content_tz_xml", caseParams.getValue("1"));
                    }
                    break;
                case 2:
                    if(null != caseParams.getValue("2") && !"".equals(caseParams.getValue("2"))){
                        parmas.put("content_jy_xml", caseParams.getValue("2"));
                    }
                    break;
                case 4:
                    if(null != caseParams.getValue("4") && !"".equals(caseParams.getValue("4"))){
                        parmas.put("content_zljg_xml", caseParams.getValue("4"));
                    }
                    break;
                case 5:
                    if(null != caseParams.getValue("5") && !"".equals(caseParams.getValue("5"))){
                        parmas.put("content_jws_xml", caseParams.getValue("5"));
                    }
                    break;
                case 6:
                    if(null != caseParams.getValue("6") && !"".equals(caseParams.getValue("6"))){
                        parmas.put("content_jzs_xml", caseParams.getValue("6"));
                    }
                    break;

                default:
                    break;
            }
        }
        
//        FormFile[] files = new FormFile[paths.length];
//        for(int i=0; i < paths.length; i++) {
//            String filname = paths[i].substring(paths[i].lastIndexOf("/")+1, paths[i].length());
//            String parameterName = paths[i].substring(paths[i].lastIndexOf("/")+1, paths[i].lastIndexOf("."));
//            files[i] = new FormFile(filname, new File(paths[i]), parameterName, null);
//        }
        CommonUtil.showLoadingDialog(CreateCaseNextActivity.this);
        OpenApiService.getInstance(CreateCaseNextActivity.this).getCaseSaveInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        Toast.makeText(CreateCaseNextActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
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
                        symptom_status_text.setText("已填写");
                    }
                    break;
                case 1:
                    if(data.getExtras().getBoolean("isAdd")) {
                        signs_status_text.setText("已填写");
                    }
                    break;
                case 2:
                    if(data.getExtras().getBoolean("isAdd")) {
                        test_status_text.setText("已填写");
                    }
                    break;
                case 3:
                    paths = data.getExtras().getStringArray("paths");
                    if(paths.length != 0 && null != paths) {
                        check_status_text.setText("已填写");
                    }
                    break;
                case 4:
                    if(data.getExtras().getBoolean("isAdd")) {
                        diagnosis_status_text.setText("已填写");
                    }
                    break;
                case 5:
                    if(data.getExtras().getBoolean("isAdd")) {
                        past_history_status_text.setText("已填写");
                    }
                    break;
                case 6:
                    if(data.getExtras().getBoolean("isAdd")) {
                        family_history_status_text.setText("已填写");
                    }
                    break;
                    
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
