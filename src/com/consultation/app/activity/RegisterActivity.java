package com.consultation.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.util.CommonUtil;


@SuppressLint("HandlerLeak")
public class RegisterActivity extends Activity {
    
    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private static ConsultationCallbackHandler handler;
    
    private TextView phone_text,verification_text,pwd_text,repwd_text,
                     tip_text;
    
    private EditText phone_edit,verification_edit,pwd_edit,repwd_edit;
    
    private Button getVerification_btn,commit_btm;
    
    private boolean isGetVerification = false;
    
    private ImageView tipImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        init();
    }
    
    public static void setHandler(ConsultationCallbackHandler h){
        handler = h;
    }

    private void init() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("用户注册");
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
        
        tipImageView = (ImageView)findViewById(R.id.register_title_tip_iamge);
//        LayoutParams tipImageViewParams=
//                new LayoutParams((int)SizeUtil.getImageSize(RegisterActivity.this, 30), (int)SizeUtil.getImageSize(
//                    RegisterActivity.this, 30));
//        tipImageViewParams.gravity = Gravity.CENTER;
//        tipImageView.setLayoutParams(tipImageViewParams);
        
        phone_text = (TextView)findViewById(R.id.register_phone_text);
        phone_text.setTextSize(18);
        
        verification_text = (TextView)findViewById(R.id.register_code_text);
        verification_text.setTextSize(18);
        
        pwd_text = (TextView)findViewById(R.id.register_pwd_text);
        pwd_text.setTextSize(18);
        
        repwd_text = (TextView)findViewById(R.id.register_repwd_text);
        repwd_text.setTextSize(18);
        
        tip_text = (TextView)findViewById(R.id.register_tip_text);
        tip_text.setTextSize(15);
        

        phone_edit = (EditText)findViewById(R.id.register_phone_input_edit);
        phone_edit.setTextSize(18);
        
        verification_edit = (EditText)findViewById(R.id.register_code_input_edit);
        verification_edit.setTextSize(18);
        
        pwd_edit = (EditText)findViewById(R.id.register_pwd_input_edit);
        pwd_edit.setTextSize(18);
        
        repwd_edit = (EditText)findViewById(R.id.register_repwd_input_edit);
        repwd_edit.setTextSize(18);
        
        
        getVerification_btn = (Button)findViewById(R.id.register_phone_get_btn);
        getVerification_btn.setTextSize(14);
        getVerification_btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //获取验证码
                if(!isGetVerification){
                    isGetVerification = true;
                    CommonUtil.showLoadingDialog(RegisterActivity.this, "Loading...");
                    new Thread(new Runnable() {
                        
                        @Override
                        public void run() {
                            for(int i=60; i >= 0; i--) {
                                try {
                                    h.sendEmptyMessage(i);
                                    Thread.sleep(1000);
                                    CommonUtil.closeLodingDialog();
                                } catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(RegisterActivity.this, "你已获取验证码，请稍等！", Toast.LENGTH_LONG).show();
                }
            }
        });
        getVerification_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
        
        
        commit_btm = (Button)findViewById(R.id.register_btn_register);
        commit_btm.setTextSize(22);
        commit_btm.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                handler.onSuccess("注册成功", 0);
                finish();
            }
        });
        commit_btm.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
    }
    
    private Handler h = new Handler(){
        
        public void dispatchMessage(Message msg) {
            if(msg.what == 0){
                isGetVerification = false;
                getVerification_btn.setText("获取验证码");
            }else{
                getVerification_btn.setText(msg.what+"s后重新发送");
            }
        };
    };
}
