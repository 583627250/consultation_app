package com.consultation.app.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ActivityList;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SelectHospitalDB;
import com.consultation.app.util.SharePreferencesEditor;

public class CreateCaseActivity extends Activity implements OnClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout, expert_layout, expert_layout_line;

    private TextView expert_text, consulatioan_text, patient_text, depart_text,titles_text, hope_text;

    private EditText expert_edit, patient_edit, depart_edit,title_edit, hope_edit;

    private RadioButton radioButton1, radioButton3;

    private Button submitBtn;

    private RequestQueue mQueue;

    private SharePreferencesEditor editor;
    
    private boolean isUpdate;

    private String patientId, expertId, caseId, expertName, patientName, consultType, titles, problem, content, imageString, departmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_new_layout);
        if(savedInstanceState != null) {
            ClientUtil.setToken(savedInstanceState.getString("token"));
        }
        mQueue=Volley.newRequestQueue(CreateCaseActivity.this);
        editor=new SharePreferencesEditor(CreateCaseActivity.this);
        patientId=getIntent().getStringExtra("patientId");
        expertId=getIntent().getStringExtra("expertId");
        caseId=getIntent().getStringExtra("caseId");
        departmentId=getIntent().getStringExtra("departmentId");
        expertName=getIntent().getStringExtra("expertName");
        patientName=getIntent().getStringExtra("patientName");
        consultType=getIntent().getStringExtra("consultType");
        titles=getIntent().getStringExtra("titles");
        isUpdate=getIntent().getBooleanExtra("isUpdate",false);
        problem=getIntent().getStringExtra("problem");
        content=getIntent().getStringExtra("content");
        imageString=getIntent().getStringExtra("imageString");
        initData();
        initView();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("token", ClientUtil.getToken());
        super.onSaveInstanceState(outState);
    }

    private void initData() {
        ActivityList.getInstance().setActivitys("CreateCaseActivity", this);
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

        expert_text=(TextView)findViewById(R.id.create_case_select_expert_text);
        expert_text.setTextSize(18);
        consulatioan_text=(TextView)findViewById(R.id.create_case_select_consultation_text);
        consulatioan_text.setTextSize(18);
        patient_text=(TextView)findViewById(R.id.create_case_patient_name_text);
        patient_text.setTextSize(18);
        depart_text=(TextView)findViewById(R.id.create_case_department_name_text);
        depart_text.setTextSize(18);
        titles_text=(TextView)findViewById(R.id.create_case_title_text);
        titles_text.setTextSize(18);
        hope_text=(TextView)findViewById(R.id.create_case_hope_text);
        hope_text.setTextSize(18);

        expert_edit=(EditText)findViewById(R.id.create_case_select_expert_input_edit);
        expert_edit.setTextSize(18);
        expert_edit.setOnClickListener(this);
        patient_edit=(EditText)findViewById(R.id.create_case_patient_name_input_edit);
        patient_edit.setTextSize(18);
        patient_edit.setOnClickListener(this);
        depart_edit=(EditText)findViewById(R.id.create_case_department_name_input_edit);
        depart_edit.setTextSize(18);
        depart_edit.setOnClickListener(this);

        title_edit=(EditText)findViewById(R.id.create_case_title_input_edit);
        title_edit.setTextSize(18);
        hope_edit=(EditText)findViewById(R.id.create_case_hope_input_edit);
        hope_edit.setTextSize(18);

        radioButton1=(RadioButton)findViewById(R.id.create_case_select_consultation_explicit);
        radioButton1.setTextSize(18);
        radioButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    expert_layout_line.setVisibility(View.VISIBLE);
                    expert_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        radioButton3=(RadioButton)findViewById(R.id.create_case_select_consultation_public);
        radioButton3.setTextSize(18);
        if(radioButton3.isChecked()) {
            expert_layout_line.setVisibility(View.GONE);
            expert_layout.setVisibility(View.GONE);
        }
        radioButton3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    expert_layout.setVisibility(View.GONE);
                    expert_layout_line.setVisibility(View.GONE);
                }
            }
        });

        expert_layout_line=(LinearLayout)findViewById(R.id.create_case_select_expert_layout_line);
        expert_layout=(LinearLayout)findViewById(R.id.create_case_select_expert_layout);

        submitBtn=(Button)findViewById(R.id.create_case_btn_submit);
        submitBtn.setTextSize(20);
        submitBtn.setOnClickListener(this);

        if(!"".equals(caseId) && null != caseId) {
            expert_edit.setText(expertName);
            patient_edit.setText(patientName);
            depart_edit.setText(getTemplates(departmentId));
            if(consultType.equals("公开讨论")) {
                radioButton3.setChecked(true);
                radioButton1.setChecked(false);
            } else if(consultType.equals("专家咨询")) {
                radioButton1.setChecked(true);
                radioButton3.setChecked(false);
            }
            title_edit.setText(titles);
            title_edit.setSelection(title_edit.getText().length());
            hope_edit.setText(problem);
        }
    }
    
    private String getTemplates(String templateId){
        String templateName="";
        SelectHospitalDB myDbHelper=new SelectHospitalDB(CreateCaseActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM depart_case_template where templ_id=? ";
            String[] selectionArgs={templateId};
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    if(cursor.getString(1).equals(templateId)){
                        templateName = cursor.getString(2);
                        break;
                    }
                }
            }
            cursor.close();
            return templateName;
        } catch(IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.create_case_select_expert_input_edit:
                // 选择专家
                startActivityForResult(new Intent(CreateCaseActivity.this, SelectExpertActivity.class), 0);
                break;
            case R.id.create_case_patient_name_input_edit:
                // 选择患者
                Intent intent = new Intent(CreateCaseActivity.this, SelectPatientActivity.class);
                if(radioButton1.isChecked()){
                    intent.putExtra("isPublic", false);
                }else{
                    intent.putExtra("isPublic", true);
                }
                startActivityForResult(intent, 1);
                break;
            case R.id.create_case_department_name_input_edit:
                // 选择科室
                startActivityForResult(new Intent(CreateCaseActivity.this, SelectCaseTemplateActivity.class), 2);
                break;
            case R.id.create_case_btn_submit:
                // 提交病例基本信息
                if("".equals(patient_edit.getText().toString()) || null == patient_edit.getText().toString()){
                    Toast.makeText(CreateCaseActivity.this, "请选择患者", Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(depart_edit.getText().toString()) || null == depart_edit.getText().toString()){
                    Toast.makeText(CreateCaseActivity.this, "请选择就诊科室", Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(title_edit.getText().toString()) || null == title_edit.getText().toString()){
                    Toast.makeText(CreateCaseActivity.this, "请输入主诉", Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(hope_edit.getText().toString()) || null == hope_edit.getText().toString()){
                    Toast.makeText(CreateCaseActivity.this, "请输入问题与需求", Toast.LENGTH_LONG).show();
                    return;
                }
                if(title_edit.getText().toString().length()>20){
                    Toast.makeText(CreateCaseActivity.this, "请输入20个字以内的主诉", Toast.LENGTH_LONG).show();
                    return;
                }
//                if(hope_edit.getText().toString().length()<10 || hope_edit.getText().toString().length()>100){
//                    Toast.makeText(CreateCaseActivity.this, "请输入10~100个字以内的问题与需求", Toast.LENGTH_LONG).show();
//                    return;
//                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("patient_userid", patientId);
                parmas.put("case_templ_id", departmentId);
                parmas.put("patient_name", patient_edit.getText().toString());
                if(radioButton1.isChecked()) {
                    parmas.put("consult_tp", "20");
                    if("".equals(expert_edit.getText().toString()) || null == expert_edit.getText().toString()){
                        Toast.makeText(CreateCaseActivity.this, "请选择专家", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parmas.put("expert_userid", expertId);
                    parmas.put("expert_name", expert_edit.getText().toString());
                }else if(radioButton3.isChecked()) {
                    parmas.put("consult_tp", "10");
                }
                if(!"".equals(caseId) && null != caseId) {
                    parmas.put("id", caseId);
                }
                parmas.put("title", title_edit.getText().toString());
                parmas.put("problem", hope_edit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CreateCaseActivity.this);
                OpenApiService.getInstance(CreateCaseActivity.this).getCaseSave(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                Intent intent=new Intent(CreateCaseActivity.this, CreateCaseNextActivity.class);
                                intent.putExtra("caseId", responses.getString("id"));
                                intent.putExtra("departmentId", departmentId);
                                intent.putExtra("isUpdate", isUpdate);
                                intent.putExtra("imageString", imageString);
                                intent.putExtra("content", content);
                                if(radioButton1.isChecked()) {
                                    intent.putExtra("isOpen", 1);
                                }else if(radioButton3.isChecked()) {
                                    intent.putExtra("isOpen", 0);
                                }
                                startActivity(intent);
                            } else if(responses.getInt("rtnCode") == 10004){
                                Toast.makeText(CreateCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                    @Override
                                    public void onSuccess(String rspContent, int statusCode) {
                                    }

                                    @Override
                                    public void onFailure(ConsultationCallbackException exp) {
                                    }
                                });
                                startActivity(new Intent(CreateCaseActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(CreateCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(CreateCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            switch(requestCode) {
                case 0:
                    expert_edit.setText(data.getExtras().getString("expertName"));
                    expertId=data.getExtras().getString("expertId");
                    break;
                case 1:
                    patient_edit.setText(data.getExtras().getString("patientName"));
                    patientId=data.getExtras().getString("patientId");
                    break;
                case 2:
                    if(resultCode == Activity.RESULT_OK){
                        depart_edit.setText(data.getExtras().getString("departName"));
                        departmentId=data.getExtras().getString("departId");
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
