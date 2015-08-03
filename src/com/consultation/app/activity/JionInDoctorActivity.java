package com.consultation.app.activity;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;


public class JionInDoctorActivity extends Activity {
    
    private TextView title,hospital,department,goodAt,level,
    certificateCode,certificateImage,agreenmentText,agreenmentCheckText,
    titleEdit,hospitalEdit,departmentEdit,certificateImageEdit;
    
    private EditText goodAtEdit,certificateCodeEdit;
    
    private CheckBox agreenment;
    
    private Button submit;
    
    private RadioButton primary,specialist;
    
    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jion_layout);
        initView();
    }

    private void initView() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("提交资料");
        title_text.setTextSize(20);
    
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        primary = (RadioButton)findViewById(R.id.jion_doctor_level_primary_radio);
        specialist = (RadioButton)findViewById(R.id.jion_doctor_level_specialist_radio);
        agreenment = (CheckBox)findViewById(R.id.jion_doctor_agreement_checkBox);
        
        title = (TextView)findViewById(R.id.jion_doctor_title_text);
        title.setTextSize(16);
        
        hospital = (TextView)findViewById(R.id.jion_doctor_hospital_text);
        hospital.setTextSize(16);
        
        department = (TextView)findViewById(R.id.jion_doctor_department_text);
        department.setTextSize(16);        
        goodAt = (TextView)findViewById(R.id.jion_doctor_good_at_text);
        goodAt.setTextSize(16);
        
        certificateCode = (TextView)findViewById(R.id.jion_doctor_certificate_text);
        certificateCode.setTextSize(16);
        
        certificateImage = (TextView)findViewById(R.id.jion_doctor_certificate_photo_text);
        certificateImage.setTextSize(16);
        
        agreenmentText = (TextView)findViewById(R.id.jion_doctor_agreement_text);
        agreenmentText.setTextSize(16);
        agreenmentText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
        agreenmentText.getPaint().setAntiAlias(true);
        
        agreenmentCheckText = (TextView)findViewById(R.id.jion_doctor_agreement_check_text);
        agreenmentCheckText.setTextSize(16);
    
        level = (TextView)findViewById(R.id.jion_doctor_level_text);
        level.setTextSize(16);
        
        titleEdit = (TextView)findViewById(R.id.jion_doctor_title_input_edit);
        titleEdit.setTextSize(16);
        
        hospitalEdit = (TextView)findViewById(R.id.jion_doctor_hospital_input_edit);
        hospitalEdit.setTextSize(16);
        
        departmentEdit = (TextView)findViewById(R.id.jion_doctor_department_input_edit);
        departmentEdit.setTextSize(16);
        
        goodAtEdit = (EditText)findViewById(R.id.jion_doctor_good_at_input_edit);
        goodAtEdit.setTextSize(16);
        
        certificateCodeEdit = (EditText)findViewById(R.id.jion_doctor_certificate_input_text);
        certificateCodeEdit.setTextSize(16);
        
        certificateImageEdit = (TextView)findViewById(R.id.jion_doctor_certificate_photo_input_edit);
        certificateImageEdit.setTextSize(16);
    
        submit = (Button)findViewById(R.id.jion_doctor_btn_commint);
        submit.setTextSize(22);
    
        submit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(!agreenment.isChecked()){
                    Toast.makeText(JionInDoctorActivity.this, "请阅读免责条款", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }
}
