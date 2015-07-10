package com.consultation.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.listener.ConsultationCallbackHandler;


public class PayActivity extends Activity {
    
    private static ConsultationCallbackHandler handler;
    
    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private TextView payText;
    
    private EditText payEdit;
    
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_layout);
        initView();
    }
    
    public static void setHandler(ConsultationCallbackHandler callbackHandler){
        handler = callbackHandler;
    }
    
    private void initView(){
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("账户充值");
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
        
        payText = (TextView)findViewById(R.id.pay_amount_text);
        payText.setTextSize(16);
        
        payEdit = (EditText)findViewById(R.id.pay_amount_input_edit);
        payEdit.setTextSize(16);
    
        submit = (Button)findViewById(R.id.pay_btn_commint);
        submit.setTextSize(22);
    
        submit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if("".equals(payEdit.getText().toString()) || payEdit.getText().toString() == null){
                    Toast.makeText(PayActivity.this, "请输入支付金额", Toast.LENGTH_LONG).show();
                    return;
                }
                handler.onSuccess(payEdit.getText().toString(), 0);
                finish();
            }
        });
    }
}
