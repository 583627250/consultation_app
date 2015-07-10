package com.consultation.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.consultation.app.R;


public class SpecialistInfoActivity extends Activity {

    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private TextView nameText,titieText,departmentText,hospitalText,authenticationText,
                     goodAtTitleText,goodAtText,authenticationTitileText,authenticationDetailedText,
                     feedbackTitieText,feedbackScoreIntText,feedbackScoreFlaotText,feedbackText,feedbackCountText;
                     
    private RatingBar feedbackRatingBar;
    
    private LinearLayout feedbackLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialist_info_layout);
        initView();
    }

    private void initView() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("张三");
        title_text.setTextSize(20);
    
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        
    }
}
