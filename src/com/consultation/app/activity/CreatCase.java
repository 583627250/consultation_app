package com.consultation.app.activity;

import com.consultation.app.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CreatCase extends Activity{
    
    private TextView title_text,back_text;
    
    private LinearLayout back_layout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_new);
        initData();
        initView();
    }

    private void initView() {
        
    }

    private void initData() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("创建病例");
        title_text.setTextSize(20);
        
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {     
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );   
                }
                finish();
            }
        });
    }
}
