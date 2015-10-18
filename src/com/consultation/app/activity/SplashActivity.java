package com.consultation.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.consultation.app.R;
import com.consultation.app.service.HeartbeatService;

public class SplashActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
//        if(PhoneUtil.isNetworkAvailable(SplashActivity.this)){
            new Thread() {
                public void run() {
                    try {
                        Intent serviceIntent=new Intent(SplashActivity.this, HeartbeatService.class);
                        startService(serviceIntent);
                        Thread.sleep(2000);
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("selectId", 0);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            }.start();
//        }
    }
}
