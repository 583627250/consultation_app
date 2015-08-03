package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.http.MultiPartStack;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.PhoneUtil;

@SuppressLint("UseSparseArrays")
public class CreateCaseActivity extends Activity implements OnClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private TextView expert_text, consulatioan_text, patient_text, titles_text, 
                     hope_text, case_text, others_text;

    private EditText expert_edit, patient_edit, title_edit, hope_edit;

    private RadioButton online, outline;

    private LinearLayout symptom_layout, signs_layout, test_layout, check_layout, diagnosis_layout, past_history_layout, family_history_layout,
            image_two_layout;

    private TextView symptom_text, signs_text, test_text, check_text, diagnosis_text, past_history_text, family_history_text, symptom_status_text,
            signs_status_text, diagnosis_status_text, past_history_status_text, family_history_status_text, test_status_text, check_status_text;

    private ImageView imageAdd, image0, image1, image2, image3, image4, image5, image6, imaged0, imaged1, imaged2, imaged3,
            imaged4, imaged5, imaged6;

    // private Map<Integer, List<ImageView>> maps=new HashMap<Integer, List<ImageView>>();

    private List<String> bitmapPaths=new ArrayList<String>();
    
    private static RequestQueue mSingleQueue;
    
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_new_layout);
        initData();
        initView();
    }

    private void initData() {
        mSingleQueue=Volley.newRequestQueue(this, new MultiPartStack());
        
    }

    // private void setMaps() {
    // List<ImageView> list0=new ArrayList<ImageView>();
    // list0.add(image0);
    // list0.add(imaged0);
    // List<ImageView> list1=new ArrayList<ImageView>();
    // list1.add(image1);
    // list1.add(imaged1);
    // List<ImageView> list2=new ArrayList<ImageView>();
    // list2.add(image2);
    // list2.add(imaged2);
    // List<ImageView> list3=new ArrayList<ImageView>();
    // list3.add(image3);
    // list3.add(imaged3);
    // List<ImageView> list4=new ArrayList<ImageView>();
    // list4.add(image4);
    // list4.add(imaged4);
    // List<ImageView> list5=new ArrayList<ImageView>();
    // list5.add(image5);
    // list5.add(imaged5);
    // List<ImageView> list6=new ArrayList<ImageView>();
    // list6.add(image6);
    // list6.add(imaged6);
    // maps.put(0, list0);
    // maps.put(1, list1);
    // maps.put(2, list2);
    // maps.put(3, list3);
    // maps.put(4, list4);
    // maps.put(5, list5);
    // maps.put(6, list6);
    // }

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
        titles_text=(TextView)findViewById(R.id.create_case_title_text);
        titles_text.setTextSize(18);
        hope_text=(TextView)findViewById(R.id.create_case_hope_text);
        hope_text.setTextSize(18);
        case_text=(TextView)findViewById(R.id.create_case_case_text);
        case_text.setTextSize(18);
        others_text=(TextView)findViewById(R.id.create_case_others_text);
        others_text.setTextSize(18);

        expert_edit=(EditText)findViewById(R.id.create_case_select_expert_input_edit);
        expert_edit.setTextSize(18);
        expert_edit.setOnClickListener(this);
        patient_edit=(EditText)findViewById(R.id.create_case_patient_name_input_edit);
        patient_edit.setTextSize(18);
        patient_edit.setOnClickListener(this);

        title_edit=(EditText)findViewById(R.id.create_case_title_input_edit);
        title_edit.setTextSize(18);
        hope_edit=(EditText)findViewById(R.id.create_case_hope_input_edit);
        hope_edit.setTextSize(18);

        online=(RadioButton)findViewById(R.id.create_case_select_consultation_online);
        online.setTextSize(18);
        outline=(RadioButton)findViewById(R.id.create_case_select_consultation_outline);
        outline.setTextSize(18);

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

        image_two_layout=(LinearLayout)findViewById(R.id.create_case_add_image_layout_bottom_all);

        imageAdd=(ImageView)findViewById(R.id.create_case_add_image_add);
        imageAdd.setOnClickListener(this);

        image0=(ImageView)findViewById(R.id.create_case_add_image_0);
        image1=(ImageView)findViewById(R.id.create_case_add_image_1);
        image2=(ImageView)findViewById(R.id.create_case_add_image_2);
        image3=(ImageView)findViewById(R.id.create_case_add_image_3);
        image4=(ImageView)findViewById(R.id.create_case_add_image_4);
        image5=(ImageView)findViewById(R.id.create_case_add_image_5);
        image6=(ImageView)findViewById(R.id.create_case_add_image_6);

        imaged0=(ImageView)findViewById(R.id.create_case_add_image_delete_0);
        imaged1=(ImageView)findViewById(R.id.create_case_add_image_delete_1);
        imaged2=(ImageView)findViewById(R.id.create_case_add_image_delete_2);
        imaged3=(ImageView)findViewById(R.id.create_case_add_image_delete_3);
        imaged4=(ImageView)findViewById(R.id.create_case_add_image_delete_4);
        imaged5=(ImageView)findViewById(R.id.create_case_add_image_delete_5);
        imaged6=(ImageView)findViewById(R.id.create_case_add_image_delete_6);
        imaged0.setOnClickListener(this);
        imaged1.setOnClickListener(this);
        imaged2.setOnClickListener(this);
        imaged3.setOnClickListener(this);
        imaged4.setOnClickListener(this);
        imaged5.setOnClickListener(this);
        imaged6.setOnClickListener(this);
        // setMaps();
        
        submitBtn = (Button)findViewById(R.id.create_case_btn_submit);
        submitBtn.setTextSize(20);
        submitBtn.setOnClickListener(this);
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
                startActivityForResult(new Intent(CreateCaseActivity.this, SelectPatientActivity.class), 1);
                break;
            case R.id.create_case_symptom_layout:
                // 症状
                startActivityForResult(new Intent(CreateCaseActivity.this, SymptomActivity.class), 2);
                break;
            case R.id.create_case_signs_layout:
                // 体征
                break;
            case R.id.create_case_diagnosis_layout:
                // 诊疗经过
                break;
            case R.id.create_case_past_history_layout:
                // 既往史
                break;
            case R.id.create_case_family_history_layout:
                // 家族史
                break;
            case R.id.create_case_add_image_add:
                // 添加照片
                if(bitmapPaths.size() == 7) {
                    Toast.makeText(CreateCaseActivity.this, "最多只能添加七张图片", Toast.LENGTH_LONG).show();
                    return;
                }
                startActivityForResult(new Intent(CreateCaseActivity.this, AddPatientPicActivity.class), 3);
                break;
            case R.id.create_case_add_image_delete_0:
                // 删除第一张，重新设置剩下图片
                bitmapPaths.remove(0);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                break;
            case R.id.create_case_add_image_delete_1:
                // 删除第二张，重新设置剩下图片
                bitmapPaths.remove(1);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                break;
            case R.id.create_case_add_image_delete_2:
                // 删除第三张，重新设置剩下图片
                bitmapPaths.remove(2);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                break;
            case R.id.create_case_add_image_delete_3:
                // 删除第四张，重新设置剩下图片
                bitmapPaths.remove(3);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                break;
            case R.id.create_case_add_image_delete_4:
                bitmapPaths.remove(4);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                // 删除第五张，重新设置剩下图片
                break;
            case R.id.create_case_add_image_delete_5:
                // 删除第六张，重新设置剩下图片
                bitmapPaths.remove(5);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                break;
            case R.id.create_case_add_image_delete_6:
                // 删除第七张，重新设置剩下图片
                bitmapPaths.remove(6);
                if(bitmapPaths.size() == 3) {
                    image_two_layout.setVisibility(View.GONE);
                }
                // 影藏所有界面，再重新显示
                adjustmentImage();
                break;
                
            case R.id.create_case_btn_submit:
                // 提交病例
                
                break;
            case R.id.create_case_test_layout:
                // 检验
                
                break;
            case R.id.create_case_check_layout:
                // 检查
                
                break;

            default:
                break;
        }
    }

    private void adjustmentImage() {
        image0.setVisibility(View.INVISIBLE);
        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
        image4.setVisibility(View.INVISIBLE);
        image5.setVisibility(View.INVISIBLE);
        image6.setVisibility(View.INVISIBLE);
        imaged0.setVisibility(View.INVISIBLE);
        imaged1.setVisibility(View.INVISIBLE);
        imaged2.setVisibility(View.INVISIBLE);
        imaged3.setVisibility(View.INVISIBLE);
        imaged4.setVisibility(View.INVISIBLE);
        imaged5.setVisibility(View.INVISIBLE);
        imaged6.setVisibility(View.INVISIBLE);
        for(int i=0; i < bitmapPaths.size(); i++) {
            Bitmap bitmap=null;
            switch(i) {
                case 0:
                    image0.setVisibility(View.VISIBLE);
                    imaged0.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image0.setImageBitmap(bitmap);
                    break;
                case 1:
                    image1.setVisibility(View.VISIBLE);
                    imaged1.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image1.setImageBitmap(bitmap);
                    break;
                case 2:
                    image2.setVisibility(View.VISIBLE);
                    imaged2.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image2.setImageBitmap(bitmap);
                    break;
                case 3:
                    image3.setVisibility(View.VISIBLE);
                    imaged3.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image3.setImageBitmap(bitmap);
                    break;
                case 4:
                    image4.setVisibility(View.VISIBLE);
                    imaged4.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image4.setImageBitmap(bitmap);
                    break;
                case 5:
                    image5.setVisibility(View.VISIBLE);
                    imaged5.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image5.setImageBitmap(bitmap);
                    break;
                case 6:
                    image6.setVisibility(View.VISIBLE);
                    imaged6.setVisibility(View.VISIBLE);
                    bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), bitmapPaths.get(i));
                    image6.setImageBitmap(bitmap);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            switch(requestCode) {
                case 0:
                    expert_edit.setText(data.getExtras().getString("expertName"));
                    data.getExtras().getString("expertId");
                    break;
                case 1:
                    patient_edit.setText(data.getExtras().getString("patientName"));
                    data.getExtras().getString("patientId");
                    break;
                case 2:
                    if(data.getExtras().getBoolean("isAdd")) {
                        symptom_status_text.setText("已填写");
                    }
                    break;
                case 3:
                    String photoPath=data.getStringExtra("bitmap");
                    Bitmap bitmap=null;
                    if(photoPath != null) {
                        switch(bitmapPaths.size()) {
                            case 0:
                                image0.setVisibility(View.VISIBLE);
                                imaged0.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image0.setImageBitmap(bitmap);
                                break;
                            case 1:
                                image1.setVisibility(View.VISIBLE);
                                imaged1.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image1.setImageBitmap(bitmap);
                                break;
                            case 2:
                                image2.setVisibility(View.VISIBLE);
                                imaged2.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image2.setImageBitmap(bitmap);
                                break;
                            case 3:
                                image_two_layout.setVisibility(View.VISIBLE);
                                image3.setVisibility(View.VISIBLE);
                                imaged3.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image3.setImageBitmap(bitmap);
                                break;
                            case 4:
                                image4.setVisibility(View.VISIBLE);
                                imaged4.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image4.setImageBitmap(bitmap);
                                break;
                            case 5:
                                image5.setVisibility(View.VISIBLE);
                                imaged5.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image5.setImageBitmap(bitmap);
                                break;
                            case 6:
                                image6.setVisibility(View.VISIBLE);
                                imaged6.setVisibility(View.VISIBLE);
                                bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CreateCaseActivity.this), photoPath);
                                image6.setImageBitmap(bitmap);
                                break;

                            default:
                                break;
                        }
                        bitmapPaths.add(photoPath);
                    }
                    break;

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
