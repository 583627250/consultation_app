package com.consultation.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.consultation.app.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        new Thread() {

            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    SplashActivity.this.finish();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
