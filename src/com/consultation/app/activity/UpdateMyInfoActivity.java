package com.consultation.app.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.GetPathFromUri4kitkat;
import com.consultation.app.util.PhoneUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.SelectPicDialog;

public class UpdateMyInfoActivity extends Activity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout, expert_gradeLine, expert_gradeLayout, invite_codeLine, invite_codeLayout, expert_tp_codeLine,
            expert_tpLayout;

    private TextView tip_text, titles_text, hospital_text, department_text, good_at_text, glasses_text, image_text, image_tip_text,
            read_text, expert_grade_text, invite_code_text, expert_tp_text;

    private RadioButton primary_RadioButton, expert_RadioButton, expert_clinical, expert_technology;

    private String title_id, department_id, hospital_id, expert_gradeId;

    private CheckBox readBox;

    private Button submit_btn;

    private LinearLayout imagesLayout;

    private List<String> pathList=new ArrayList<String>();

    private SharePreferencesEditor editor;

    private EditText titleEdit, departmentEdit, hospitalEdit, goodAtEdit, expert_gradeEdit, invite_codeEdit;

    private int width;

    private int height;

    private String headerTitle;

    private String infos;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private String doctorId;

    private Uri photoUri;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_my_info_layout);
        editor=new SharePreferencesEditor(this);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        if(savedInstanceState != null) {
        if(savedInstanceState.getString("photoUri") != null) {
            photoUri=Uri.parse(savedInstanceState.getString("photoUri"));
        }
        }
        mQueue=Volley.newRequestQueue(this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        headerTitle=getIntent().getStringExtra("headerTitle");
        infos=getIntent().getStringExtra("infos");
        initView();
        initData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void initData() {
        if(!"".equals(infos) && null != infos) {
            try {
                JSONObject jsonObject=new JSONObject(infos);
                title_id=jsonObject.getString("title_id");
                doctorId=jsonObject.getString("id");
                department_id=jsonObject.getString("depart_id");
                hospital_id=jsonObject.getString("hospital_id");
                if(jsonObject.getString("grade").equals("1")) {
                    primary_RadioButton.setChecked(true);
                    expert_RadioButton.setChecked(false);
                    titleEdit.setText(jsonObject.getString("title"));
                    departmentEdit.setText(jsonObject.getString("depart_name"));
                    hospitalEdit.setText(jsonObject.getString("hospital_name"));
                    goodAtEdit.setText(jsonObject.getString("goodat_fields"));
                } else {
                    primary_RadioButton.setChecked(false);
                    expert_RadioButton.setChecked(true);
                    if(jsonObject.getString("expert_tp").equals("1")) {
                        expert_clinical.setChecked(true);
                        expert_technology.setChecked(false);
                    } else {
                        expert_clinical.setChecked(false);
                        expert_technology.setChecked(true);
                    }
                    expert_gradeLine.setVisibility(View.VISIBLE);
                    expert_gradeLayout.setVisibility(View.VISIBLE);
                    invite_codeLine.setVisibility(View.VISIBLE);
                    invite_codeLayout.setVisibility(View.VISIBLE);
                    expert_tp_codeLine.setVisibility(View.VISIBLE);
                    expert_tpLayout.setVisibility(View.VISIBLE);
                    expert_gradeId=jsonObject.getString("expert_gradeid");
                    titleEdit.setText(jsonObject.getString("title"));
                    departmentEdit.setText(jsonObject.getString("depart_name"));
                    hospitalEdit.setText(jsonObject.getString("hospital_name"));
                    goodAtEdit.setText(jsonObject.getString("goodat_fields"));
                    expert_gradeEdit.setText(jsonObject.getString("expert_grade"));
                    invite_codeLine.setVisibility(View.GONE);
                    invite_codeLayout.setVisibility(View.GONE);
                }
                pathList.clear();
                pathList.add("add");
                pathList.add(jsonObject.getString("certif_pic_url"));
                showRightLayout();
            } catch(JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText(headerTitle);
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

        expert_grade_text=(TextView)findViewById(R.id.update_my_info_expert_grade_text);
        expert_grade_text.setTextSize(18);

        invite_code_text=(TextView)findViewById(R.id.update_my_info_invitation_code_text);
        invite_code_text.setTextSize(18);

        expert_tp_text=(TextView)findViewById(R.id.update_my_info_expert_tp_text);
        expert_tp_text.setTextSize(18);

        image_text=(TextView)findViewById(R.id.update_my_info_photo_text);
        image_text.setTextSize(18);

        image_tip_text=(TextView)findViewById(R.id.update_my_info_image_tip);
        image_tip_text.setTextSize(15);

        read_text=(TextView)findViewById(R.id.update_my_info_read_show);
        read_text.setTextSize(18);
        read_text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        read_text.getPaint().setAntiAlias(true);// 抗锯齿

        expert_gradeLine=(LinearLayout)findViewById(R.id.update_my_info_expert_grade_line);

        expert_gradeLayout=(LinearLayout)findViewById(R.id.update_my_info_expert_grade_layout);

        invite_codeLine=(LinearLayout)findViewById(R.id.update_my_info_invitation_code_line);

        invite_codeLayout=(LinearLayout)findViewById(R.id.update_my_info_invitation_code_layout);

        expert_tp_codeLine=(LinearLayout)findViewById(R.id.update_my_info_expert_tp_line);

        expert_tpLayout=(LinearLayout)findViewById(R.id.update_my_info_expert_tp_layout);

        expert_gradeLine.setVisibility(View.GONE);
        expert_gradeLayout.setVisibility(View.GONE);
        invite_codeLine.setVisibility(View.GONE);
        invite_codeLayout.setVisibility(View.GONE);
        expert_tp_codeLine.setVisibility(View.GONE);
        expert_tpLayout.setVisibility(View.GONE);

        primary_RadioButton=(RadioButton)findViewById(R.id.update_my_info_glasses_primary);
        primary_RadioButton.setTextSize(18);
        primary_RadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    expert_gradeLine.setVisibility(View.GONE);
                    expert_gradeLayout.setVisibility(View.GONE);
                    invite_codeLine.setVisibility(View.GONE);
                    invite_codeLayout.setVisibility(View.GONE);
                    expert_tp_codeLine.setVisibility(View.GONE);
                    expert_tpLayout.setVisibility(View.GONE);
                }
            }
        });

        expert_RadioButton=(RadioButton)findViewById(R.id.update_my_info_glasses_expert);
        expert_RadioButton.setTextSize(18);
        expert_RadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    expert_gradeLine.setVisibility(View.VISIBLE);
                    expert_gradeLayout.setVisibility(View.VISIBLE);
                    invite_codeLine.setVisibility(View.VISIBLE);
                    invite_codeLayout.setVisibility(View.VISIBLE);
                    expert_tp_codeLine.setVisibility(View.VISIBLE);
                    expert_tpLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        expert_clinical=(RadioButton)findViewById(R.id.update_my_info_expert_clinical);
        expert_clinical.setTextSize(18);

        expert_technology=(RadioButton)findViewById(R.id.update_my_info_expert_technology);
        expert_technology.setTextSize(18);

        readBox=(CheckBox)findViewById(R.id.update_my_info_read_btn);
        readBox.setTextSize(18);

        submit_btn=(Button)findViewById(R.id.update_my_info_btn_submit);
        submit_btn.setTextSize(22);
        submit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == hospitalEdit.getText().toString() || "".equals(hospitalEdit.getText().toString())) {
                    Toast.makeText(UpdateMyInfoActivity.this, "请选择医院", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == departmentEdit.getText().toString() || "".equals(departmentEdit.getText().toString())) {
                    Toast.makeText(UpdateMyInfoActivity.this, "请选择科室", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == titleEdit.getText().toString() || "".equals(titleEdit.getText().toString())) {
                    Toast.makeText(UpdateMyInfoActivity.this, "请选择职称", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null == goodAtEdit.getText().toString() || "".equals(goodAtEdit.getText().toString())) {
                    Toast.makeText(UpdateMyInfoActivity.this, "请输入擅长领域", Toast.LENGTH_LONG).show();
                    return;
                }
                if(expert_RadioButton.isChecked()) {
                    if(null == expert_gradeEdit.getText().toString() || "".equals(expert_gradeEdit.getText().toString())) {
                        Toast.makeText(UpdateMyInfoActivity.this, "请选择专家级别", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if(null == pathList || pathList.size() == 0) {
                    Toast.makeText(UpdateMyInfoActivity.this, "请添加证书照片", Toast.LENGTH_LONG).show();
                    return;
                }
                File[] files=new File[1];
                if(!pathList.get(1).startsWith("http:")) {
                    files[0]=CommonUtil.getSmallBitmapFile(pathList.get(1));
                }
                Map<String, String> parmas=new HashMap<String, String>();
                if(!"".equals(doctorId) && null != doctorId) {
                    parmas.put("id", doctorId);
                }
                parmas.put("hospital_id", hospital_id);
                parmas.put("hospital_name", hospitalEdit.getText().toString());
                parmas.put("depart_id", department_id);
                parmas.put("depart_name", departmentEdit.getText().toString());
                parmas.put("goodat_fields", goodAtEdit.getText().toString());
                parmas.put("title_id", title_id);
                parmas.put("title", titleEdit.getText().toString());
                if(primary_RadioButton.isChecked()) {
                    parmas.put("grade", "1");
                }
                if(expert_RadioButton.isChecked()) {
                    parmas.put("grade", "2");
                    parmas.put("expert_gradeid", expert_gradeId);
                    parmas.put("expert_grade", expert_gradeEdit.getText().toString());
                    if(expert_clinical.isChecked()) {
                        parmas.put("expert_tp", "1");
                    } else {
                        parmas.put("expert_tp", "2");
                    }
                }
                if(null != invite_codeEdit.getText().toString() && !"".equals(invite_codeEdit.getText().toString())) {
                    parmas.put("invite_code", invite_codeEdit.getText().toString());
                }
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(UpdateMyInfoActivity.this);
                OpenApiService.getInstance(UpdateMyInfoActivity.this).getUploadFiles(ClientUtil.GET_JION_DOCTOR_URL,
                    UpdateMyInfoActivity.this, new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            CommonUtil.closeLodingDialog();
                            // Toast.makeText(UpdateMyInfoActivity.this, "提交成功", Toast.LENGTH_LONG).show();
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

        invite_codeEdit=(EditText)findViewById(R.id.update_my_info_invitation_code_input_edit);
        invite_codeEdit.setTextSize(18);

        expert_gradeEdit=(EditText)findViewById(R.id.update_my_info_expert_grade_input_edit);
        expert_gradeEdit.setTextSize(18);
        expert_gradeEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyExpertGradeActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        expert_gradeEdit.setText(rspContent.split(",")[0]);
                        expert_gradeId=rspContent.split(",")[1];
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                Intent intent=new Intent(UpdateMyInfoActivity.this, MyExpertGradeActivity.class);
                if(expert_clinical.isChecked()) {
                    intent.putExtra("expert_tp", "1");
                } else {
                    intent.putExtra("expert_tp", "2");
                }
                startActivity(intent);
            }
        });
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
                    // startActivityForResult(new Intent(UpdateMyInfoActivity.this, AddPatientPicActivity.class), 0);
                    final SelectPicDialog dialog=
                        new SelectPicDialog(UpdateMyInfoActivity.this, R.style.selectPicDialog, R.layout.select_pic_dialog);
                    dialog.setCancelable(true);
                    dialog.setPhotographButton(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            String SDState=Environment.getExternalStorageState();
                            if(SDState.equals(Environment.MEDIA_MOUNTED)) {
                                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                ContentValues values=new ContentValues();
                                photoUri=
                                    UpdateMyInfoActivity.this.getContentResolver().insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(intent, 2);
                            } else {
                                Toast.makeText(UpdateMyInfoActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialog.setSelectButton(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent=new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 3);
                        }
                    });
                    dialog.show();
                }
            });
            imageView.setBackgroundResource(R.drawable.update_my_info_add_images);
        } else {
            imageView.setOnLongClickListener(this);
            if(path.startsWith("http:")) {
                imageView.setTag(path);
                imageView.setBackgroundResource(R.anim.loading_anim);
                AnimationDrawable animation=(AnimationDrawable)imageView.getBackground();
                animation.start();
                if(!"null".equals(path) && !"".equals(path)) {
                    ImageListener listener=ImageLoader.getImageListener(imageView, 0, android.R.drawable.ic_menu_delete);
                    mImageLoader.get(path, listener, 200, 200);
                }
            } else {
                Bitmap bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(UpdateMyInfoActivity.this), path);
                imageView.setImageBitmap(bitmap);
            }
        }
        relativeLayout.addView(imageView);
        return relativeLayout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if(data != null) {
        // String photoPath=data.getStringExtra("bitmap");
        // pathList.clear();
        // pathList.add("add");
        // pathList.add(photoPath);
        // showRightLayout();
        // }
        switch(requestCode) {
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    pathList.remove(1);
                    showRightLayout();
                }
                break;
            case 2:
                if(resultCode == Activity.RESULT_OK) {
                    doPhoto(requestCode, data);
                }
                break;
            case 3:
                if(resultCode == Activity.RESULT_OK) {
                    doPhoto(requestCode, data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent data) {
        if(requestCode == 3) {
            if(data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri=data.getData();
            if(photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String picPath=GetPathFromUri4kitkat.getPath(UpdateMyInfoActivity.this, photoUri);
        if(picPath != null
            && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            pathList.clear();
            pathList.add("add");
            pathList.add(picPath);
            showRightLayout();
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
//        showDialogs(v.getId());
        Intent intent=new Intent(UpdateMyInfoActivity.this, DialogNewActivity.class);
        intent.putExtra("flag", 0);
        intent.putExtra("titleText", "删除该图片?");
        startActivityForResult(intent, 1);
        return false;
    }

//    private void showDialogs(final int index) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(UpdateMyInfoActivity.this);
//        builder.setMessage("删除该图片?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int id) {
//                pathList.remove(index);
//                showRightLayout();
//            }
//        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        }).create().show();
//    }

}
