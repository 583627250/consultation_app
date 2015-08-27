package com.consultation.app.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.PhoneUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class UpdateMyInfoActivity extends Activity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private TextView tip_text, titles_text, hospital_text, department_text, good_at_text, glasses_text, certificate_code_text,
            image_text, image_tip_text, read_text;

    private RadioButton primary_RadioButton, expert_RadioButton;
    
    private String title_id,department_id,hospital_id;

    private CheckBox readBox;

    private Button submit_btn;

    private LinearLayout imagesLayout;

    private List<String> pathList=new ArrayList<String>();

    private SharePreferencesEditor editor;

    private EditText titleEdit, departmentEdit, hospitalEdit, goodAtEdit, codeEdit;

    private int width;

    private int height;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_my_info_layout);
        editor=new SharePreferencesEditor(this);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        initData();
        initView();
    }

    private void initData() {

    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("修改我的资料");
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
        tip_text=(TextView)findViewById(R.id.update_my_info_tip_text);
        tip_text.setTextSize(15);

        titles_text=(TextView)findViewById(R.id.update_my_info_title_text);
        titles_text.setTextSize(18);

        hospital_text=(TextView)findViewById(R.id.update_my_info_hospital_text);
        hospital_text.setTextSize(18);

        department_text=(TextView)findViewById(R.id.update_my_info_department_text);
        department_text.setTextSize(18);

        good_at_text=(TextView)findViewById(R.id.update_my_info_good_at_text);
        good_at_text.setTextSize(18);

        glasses_text=(TextView)findViewById(R.id.update_my_info_glasses_text);
        glasses_text.setTextSize(18);

        certificate_code_text=(TextView)findViewById(R.id.update_my_info_code_text);
        certificate_code_text.setTextSize(18);

        image_text=(TextView)findViewById(R.id.update_my_info_photo_text);
        image_text.setTextSize(18);

        image_tip_text=(TextView)findViewById(R.id.update_my_info_image_tip);
        image_tip_text.setTextSize(15);

        read_text=(TextView)findViewById(R.id.update_my_info_read_show);
        read_text.setTextSize(18);
        read_text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        read_text.getPaint().setAntiAlias(true);// 抗锯齿

        primary_RadioButton=(RadioButton)findViewById(R.id.update_my_info_glasses_primary);
        primary_RadioButton.setTextSize(18);

        expert_RadioButton=(RadioButton)findViewById(R.id.update_my_info_glasses_expert);
        expert_RadioButton.setTextSize(18);

        readBox=(CheckBox)findViewById(R.id.update_my_info_read_btn);
        readBox.setTextSize(18);

        submit_btn=(Button)findViewById(R.id.update_my_info_btn_submit);
        submit_btn.setTextSize(22);
        submit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == hospitalEdit.getText().toString() || "".equals(hospitalEdit.getText().toString())){
                    Toast.makeText(UpdateMyInfoActivity.this, "请选择医院", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == departmentEdit.getText().toString() || "".equals(departmentEdit.getText().toString())){
                    Toast.makeText(UpdateMyInfoActivity.this, "请选择科室", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == titleEdit.getText().toString() || "".equals(titleEdit.getText().toString())){
                    Toast.makeText(UpdateMyInfoActivity.this, "请选择职称", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == goodAtEdit.getText().toString() || "".equals(goodAtEdit.getText().toString())){
                    Toast.makeText(UpdateMyInfoActivity.this, "请输入擅长领域", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == codeEdit.getText().toString() || "".equals(codeEdit.getText().toString())){
                    Toast.makeText(UpdateMyInfoActivity.this, "请输入证书编码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == pathList || pathList.size() ==0){
                    Toast.makeText(UpdateMyInfoActivity.this, "请添加证书照片", Toast.LENGTH_LONG).show();
                    return;
                }
                File[] files=new File[pathList.size()];
                for(int i=0; i < pathList.size(); i++) {
                    File file=new File(pathList.get(i));
                    files[i]=file;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("hospital_id", hospital_id);
                parmas.put("hospital_name", hospitalEdit.getText().toString());
                parmas.put("depart_id", department_id);
                parmas.put("depart_name", departmentEdit.getText().toString());
                parmas.put("goodat_fields", goodAtEdit.getText().toString());
                parmas.put("title_id", title_id);
                parmas.put("title", titleEdit.getText().toString());
                parmas.put("certificate_no", codeEdit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(UpdateMyInfoActivity.this);
                OpenApiService.getInstance(UpdateMyInfoActivity.this).getUploadFiles(ClientUtil.GET_JION_DOCTOR_URL, UpdateMyInfoActivity.this,
                    new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(UpdateMyInfoActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(UpdateMyInfoActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                        }
                    }, files, parmas);
            }
        });
        submit_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        imagesLayout=(LinearLayout)findViewById(R.id.update_my_info_photo_layout);

        titleEdit=(EditText)findViewById(R.id.update_my_info_title_input_edit);
        titleEdit.setTextSize(18);
        titleEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyTitleActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        titleEdit.setText(rspContent.split(",")[0]);
                        title_id=rspContent.split(",")[1];
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                startActivity(new Intent(UpdateMyInfoActivity.this, MyTitleActivity.class));
            }
        });

        departmentEdit=(EditText)findViewById(R.id.update_my_info_department_input_edit);
        departmentEdit.setTextSize(18);
        departmentEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyDepartmentActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        departmentEdit.setText(rspContent.split(",")[0]);
                        department_id=rspContent.split(",")[1];
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                startActivity(new Intent(UpdateMyInfoActivity.this, MyDepartmentActivity.class));
            }
        });

        hospitalEdit=(EditText)findViewById(R.id.update_my_info_hospital_input_edit);
        hospitalEdit.setTextSize(18);
        hospitalEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyHospitalActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        hospitalEdit.setText(rspContent.split(",")[0]);
                        hospital_id=rspContent.split(",")[1];
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                startActivity(new Intent(UpdateMyInfoActivity.this, MyHospitalActivity.class));
            }
        });

        goodAtEdit=(EditText)findViewById(R.id.update_my_info_good_at_input_edit);
        goodAtEdit.setTextSize(18);

        codeEdit=(EditText)findViewById(R.id.update_my_info_code_input_edit);
        codeEdit.setTextSize(18);

        pathList.add("add");
        showRightLayout();
    }

    private void showRightLayout() {
        imagesLayout.removeAllViews();
        if(null != pathList && pathList.size() != 0) {
            LinearLayout rowsLayout=new LinearLayout(UpdateMyInfoActivity.this);
            LinearLayout relativeLayout=new LinearLayout(UpdateMyInfoActivity.this);
            for(int i=0; i < pathList.size(); i++) {
                if(i % 3 == 0) {
                    rowsLayout=createLinearLayout();
                    imagesLayout.addView(rowsLayout);
                }
                relativeLayout=createImage(pathList.get(i), i);
                rowsLayout.addView(relativeLayout);
            }
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout=new LinearLayout(UpdateMyInfoActivity.this);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_VERTICAL;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, height / 100, 0, height / 100);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private LinearLayout createImage(String path, int id) {
        LinearLayout relativeLayout=new LinearLayout(UpdateMyInfoActivity.this);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 30;
        relativeLayout.setLayoutParams(layoutParams);

        ImageView imageView=new ImageView(UpdateMyInfoActivity.this);
        imageView.setId(id);
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        if(id == 0) {
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(UpdateMyInfoActivity.this, AddPatientPicActivity.class), 0);
                }
            });
            imageView.setBackgroundResource(R.drawable.update_my_info_add_images);
        } else {
            imageView.setOnLongClickListener(this);
            Bitmap bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(UpdateMyInfoActivity.this), path);
            imageView.setImageBitmap(bitmap);
        }
        relativeLayout.addView(imageView);

        return relativeLayout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            String photoPath=data.getStringExtra("bitmap");
            pathList.clear();
            if(!pathList.contains(photoPath)) {
                pathList.add(photoPath);
            }
            showRightLayout();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onLongClick(View v) {
        showDialogs(v.getId());
        return false;
    }

    private void showDialogs(final int index) {
        AlertDialog.Builder builder=new AlertDialog.Builder(UpdateMyInfoActivity.this);
        builder.setMessage("删除该图片?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                pathList.remove(index);
                showRightLayout();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).create().show();
    }

}
