package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.GetPathFromUri4kitkat;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.SelectPicDialog;

public class CaseMoreNewActivity extends Activity {

    private LinearLayout layout1, layout2, layout3, layout4;

    private RelativeLayout layoutAll;

    private TextView textView1, textView2, textView3, textView4;

    private String btn1, btn2, btn3, caseId;

    private int btnCount;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_more_select_new_layout);
        if(savedInstanceState != null) {
            if(savedInstanceState.getString("photoUri") != null) {
                photoUri=Uri.parse(savedInstanceState.getString("photoUri"));
            }
        }
        mQueue=Volley.newRequestQueue(CaseMoreNewActivity.this);
        editor=new SharePreferencesEditor(CaseMoreNewActivity.this);
        caseId=getIntent().getStringExtra("caseId");
        btn1=getIntent().getStringExtra("btn1");
        btn2=getIntent().getStringExtra("btn2");
        btn3=getIntent().getStringExtra("btn3");
        btnCount=getIntent().getIntExtra("btnCount", 0);
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        textView1=(TextView)findViewById(R.id.case_more_new_select_1_text);
        textView1.setTextSize(18);
        textView1.setText(btn1);
        textView2=(TextView)findViewById(R.id.case_more_new_select_2_text);
        textView2.setTextSize(18);
        textView2.setText(btn2);
        textView3=(TextView)findViewById(R.id.case_more_new_select_3_text);
        textView3.setTextSize(18);
        textView3.setText(btn3);
        textView4=(TextView)findViewById(R.id.case_more_new_select_cancel_text);
        textView4.setTextSize(18);

        layout1=(LinearLayout)findViewById(R.id.case_more_new_select_1_layout);
        layout1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 上传图片
                final SelectPicDialog dialog=
                    new SelectPicDialog(CaseMoreNewActivity.this, R.style.selectPicDialog, R.layout.select_pic_dialog);
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
                                CaseMoreNewActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values);
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, 3);
                        } else {
                            Toast.makeText(CaseMoreNewActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
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
                        startActivityForResult(intent, 4);
                    }
                });
                dialog.show();
                // startActivityForResult(new Intent(CaseMoreNewActivity.this, AddPatientPicActivity.class), 1);
            }
        });
        layout1.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout2=(LinearLayout)findViewById(R.id.case_more_new_select_2_layout);
        layout2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 线下医疗服务
                Intent intent=new Intent(CaseMoreNewActivity.this, DialogNewActivity.class);
                intent.putExtra("flag", 0);
                intent.putExtra("titleText", "确认后病例将归档\r\n本次讨论结束\r\n是否确认线下医疗服务？");
                startActivityForResult(intent, 2);
            }
        });
        layout2.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        if(btnCount == 2) {
            layout2.setVisibility(View.GONE);
        }
        
        layout3=(LinearLayout)findViewById(R.id.case_more_new_select_3_layout);
        layout3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 完成
                Intent intent=new Intent(CaseMoreNewActivity.this, DialogNewActivity.class);
                intent.putExtra("flag", 0);
                intent.putExtra("titleText", "确认后病例将归档\r\n本次讨论结束\r\n是否确认完成？");
                startActivityForResult(intent, 0);
            }
        });
        layout3.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());
        layout4=(LinearLayout)findViewById(R.id.case_more_new_select_cancel_layout);
        layout4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout4.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.case_more_select_btn_shape),
            getResources().getDrawable(R.drawable.case_more_select_btn_pressed_shape)).getBtnTouchListener());

        layoutAll=(RelativeLayout)findViewById(R.id.case_more_new_all_layout);
        layoutAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 0:
                    // 完成
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreNewActivity.this);
                    OpenApiService.getInstance(CaseMoreNewActivity.this).getDiscussionCaseFinsh(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Intent intent=new Intent();
                                        intent.putExtra("status", "3");
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreNewActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    break;
                // case 1:
                // if(data != null){
                // Intent intent=new Intent();
                // intent.putExtra("status", "1");
                // intent.putExtra("path", data.getStringExtra("bitmap"));
                // setResult(Activity.RESULT_OK, intent);
                // finish();
                // }
                // break;
                case 2:
                    Map<String, String> toSurgeryCaseparmas=new HashMap<String, String>();
                    toSurgeryCaseparmas.put("caseId", caseId);
                    toSurgeryCaseparmas.put("accessToken", ClientUtil.getToken());
                    toSurgeryCaseparmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseMoreNewActivity.this);
                    OpenApiService.getInstance(CaseMoreNewActivity.this).getToSurgeryCase(mQueue, toSurgeryCaseparmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Intent intent=new Intent();
                                        intent.putExtra("status", "2");
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseMoreNewActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseMoreNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseMoreNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    break;
                case 3:
                    if(resultCode == Activity.RESULT_OK) {
                        doPhoto(requestCode, data);
                    }
                    break;
                case 4:
                    if(resultCode == Activity.RESULT_OK) {
                        doPhoto(requestCode, data);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent data) {
        if(requestCode == 4) {
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
        String picPath=GetPathFromUri4kitkat.getPath(CaseMoreNewActivity.this, photoUri);
        if(picPath != null
            && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            Intent intent=new Intent();
            intent.putExtra("status", "1");
            intent.putExtra("path", picPath);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }
}
