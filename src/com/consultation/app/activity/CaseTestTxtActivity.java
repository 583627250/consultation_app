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
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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

public class CaseTestTxtActivity extends Activity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private LinearLayout rightLayout;

    private Context context;

    private int width;

    private int height;

    private TextView textAdd, tip;

    private Button saveBtn;

    private List<String> pathList=new ArrayList<String>();
    
    private SharePreferencesEditor editor;
    
    private String caseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_test_txt_layout);
        context=this;
        editor=new SharePreferencesEditor(context);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        caseId = getIntent().getStringExtra("caseId");
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("检验");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                finish();
            }
        });
        rightLayout=(LinearLayout)findViewById(R.id.test_txt_layout);

        textAdd=(TextView)findViewById(R.id.test_txt_add_image_text);
        textAdd.setText(Html.fromHtml("<u>" + "添加" + "</u>"));
        textAdd.setGravity(Gravity.CENTER_VERTICAL);
        textAdd.setTextColor(Color.BLUE);
        textAdd.setTextSize(17);
        textAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CaseTestTxtActivity.this, AddPatientPicActivity.class), 0);
            }
        });

        tip=(TextView)findViewById(R.id.test_txt_image_tip);
        tip.setTextSize(14);

        saveBtn=(Button)findViewById(R.id.test_txt_image_btn_save);
        saveBtn.setTextSize(17);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pathList.size() == 0) {
                    Toast.makeText(context, "请添加图片", Toast.LENGTH_LONG).show();
                    return;
                }
                File[] files=new File[pathList.size()];
                for(int i=0; i < pathList.size(); i++) {
                    File file=new File(pathList.get(i));
                    files[i]=file;
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("case_id", caseId);
                params.put("accessToken", ClientUtil.getToken());
                params.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(context);
                OpenApiService.getInstance(context).getUploadFiles(ClientUtil.GET_UPLOAD_IMAGES_URL, context,
                    new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            CommonUtil.closeLodingDialog();
                            System.out.println("sccTextis ========> " + rspContent);
                            Toast.makeText(context, "上传成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(context, "上传失败", Toast.LENGTH_LONG).show();
                        }
                    }, files, params);
            }
        });
        saveBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
    }

    private void showRightLayout() {
        rightLayout.removeAllViews();
        if(null != pathList && pathList.size() != 0) {
            LinearLayout rowsLayout=new LinearLayout(context);
            LinearLayout relativeLayout=new LinearLayout(context);
            for(int i=0; i < pathList.size(); i++) {
                if(i % 3 == 0) {
                    rowsLayout=createLinearLayout();
                    rightLayout.addView(rowsLayout);
                }
                relativeLayout=createImage(pathList.get(i), i);
                rowsLayout.addView(relativeLayout);
            }
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_VERTICAL;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, height / 100, 0, height / 100);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private LinearLayout createImage(String path, int id) {
        LinearLayout relativeLayout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 30;
        relativeLayout.setLayoutParams(layoutParams);

        ImageView imageView=new ImageView(context);
        imageView.setId(id);
        imageView.setOnLongClickListener(this);
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        Bitmap bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CaseTestTxtActivity.this), path);
        imageView.setImageBitmap(bitmap);
        relativeLayout.addView(imageView);

        return relativeLayout;
    }

    private void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            String photoPath=data.getStringExtra("bitmap");
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
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
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
