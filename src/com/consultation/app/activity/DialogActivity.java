package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class DialogActivity extends Activity {

    private TextView title, cancel, ok;
    
    private EditText content;

    private LinearLayout all;

    private int width;
    
    private String flag = "0",caseId;
    
    private RequestQueue mQueue;
    
    private SharePreferencesEditor editor;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        flag = getIntent().getStringExtra("flag");
        caseId = getIntent().getStringExtra("caseId");
        editor=new SharePreferencesEditor(DialogActivity.this);
        mQueue=Volley.newRequestQueue(DialogActivity.this);
        initData();
        initView();
    }

    private void initData() {

    }

    private void initView() {
        all=(LinearLayout)findViewById(R.id.common_dialog_layout);
        LayoutParams layoutParams = new LayoutParams(width / 3 *2, LayoutParams.WRAP_CONTENT);
        all.setLayoutParams(layoutParams);
        all.setGravity(Gravity.CENTER);
        title=(TextView)findViewById(R.id.common_dialog_title);
        title.setTextSize(18);
        content=(EditText)findViewById(R.id.common_dialog_input_edit);
        content.setTextSize(18);
        if(flag.equals("1")){
            title.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
        if(flag.equals("2")){
            title.setText("确认删除该病例？");
        }
        cancel=(TextView)findViewById(R.id.common_dialog_cancel);
        cancel.setTextSize(15);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ok=(TextView)findViewById(R.id.common_dialog_ok);
        ok.setTextSize(15);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(flag.equals("1") && ("".equals(content.getText().toString()) || content.getText().toString() == null)){
                    Toast.makeText(DialogActivity.this, "请输入诊断意见", Toast.LENGTH_LONG).show();
                    return;
                }else if(flag.equals("1")&& !("".equals(content.getText().toString()) || content.getText().toString() == null)){
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("caseId", caseId);
                    parmas.put("opinion", content.getText().toString());
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(DialogActivity.this);
                    OpenApiService.getInstance(DialogActivity.this).getCaseOpinion(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        Toast.makeText(DialogActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        closeActivity(0);
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(DialogActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(DialogActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(DialogActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(DialogActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                }else if(flag.equals("0")){
                    closeActivity(0);
                }else if(flag.equals("2")){
                    closeActivity(0);
                }
            }
        });
    }

    private void closeActivity(int flags) {
        Intent intent=new Intent();
        intent.putExtra("flag", flags);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
