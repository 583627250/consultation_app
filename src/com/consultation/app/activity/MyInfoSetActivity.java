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

public class MyInfoSetActivity extends Activity implements OnClickListener{

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private TextView set_text, update_text, about_text;

    private LinearLayout set_layout, update_layout, about_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_info_set_layout);
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("设置");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setText("返回");
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
        
        set_text=(TextView)findViewById(R.id.mine_info_set_text);
        set_text.setTextSize(18);

        update_text=(TextView)findViewById(R.id.mine_info_update_text);
        update_text.setTextSize(18);

        about_text=(TextView)findViewById(R.id.mine_info_about_text);
        about_text.setTextSize(18);
        
        set_layout=(LinearLayout)findViewById(R.id.mine_my_set_layout);
        set_layout.setOnClickListener(this);
        
        update_layout=(LinearLayout)findViewById(R.id.mine_my_update_layout);
        update_layout.setOnClickListener(this);
        
        about_layout=(LinearLayout)findViewById(R.id.mine_my_about_layout);
        about_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.mine_my_set_layout:
                
                break;
            case R.id.mine_my_update_layout:
                
                break;
            case R.id.mine_my_about_layout:
                
                break;

            default:
                break;
        }
    }
}
