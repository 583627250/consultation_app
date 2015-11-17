package com.consultation.app.activity;

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

import com.consultation.app.R;

public class DialogNewActivity extends Activity {

    private TextView title, cancel, ok;
    
    private EditText content;
    
    private String titleText;

    private LinearLayout all;

    private int width;
    
    private int flag,index;
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        flag = getIntent().getIntExtra("flag",0);
        index = getIntent().getIntExtra("index",0);
        titleText = getIntent().getStringExtra("titleText");
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
        title.setText(titleText);
        content=(EditText)findViewById(R.id.common_dialog_input_edit);
        content.setTextSize(18);
        if(flag==1){
            title.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            content.setHint(titleText);
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
                if(flag==1 && ("".equals(content.getText().toString()) || content.getText().toString() == null)){
                    Toast.makeText(DialogNewActivity.this, "请输入诊断意见", Toast.LENGTH_LONG).show();
                    return;
                }
                closeActivity(content.getText().toString());
            }
        });
    }

    private void closeActivity(String contentString) {
        Intent intent=new Intent();
        intent.putExtra("contentString", contentString);
        intent.putExtra("index", index);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
