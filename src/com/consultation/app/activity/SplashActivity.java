package com.consultation.app.activity;


import com.consultation.app.R;
import com.consultation.app.util.PhoneUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		if(PhoneUtil.isNetworkAvailable(SplashActivity.this)){
		    new Thread() {
	            public void run() {
	                try {
	                    Thread.sleep(2000);
	                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
	                    SplashActivity.this.finish();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            };
	        }.start();
		}else{
		    Toast.makeText(SplashActivity.this, "网络链接失败，请稍后再试", Toast.LENGTH_LONG).show();
		}
	}
}
