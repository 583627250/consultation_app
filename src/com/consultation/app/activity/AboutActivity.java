package com.consultation.app.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.consultation.app.R;

public class AboutActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text,desText,bottomText,companyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        initDate();
        initView();
    }

    private void initDate() {
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("关于我们");
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

        desText=(TextView)findViewById(R.id.about_description_text);
        desText.setTextSize(18);
        
        bottomText=(TextView)findViewById(R.id.about_bottom_text);
        bottomText.setTextSize(16);
        
        companyText=(TextView)findViewById(R.id.about_company_text);
        companyText.setTextSize(16);
    }

}
