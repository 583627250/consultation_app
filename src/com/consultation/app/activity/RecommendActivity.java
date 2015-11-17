package com.consultation.app.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.util.CommonUtil;


public class RecommendActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text,titile_text;
    
    private WebView webView;
    
    private String url,title,id;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_layout);
        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        initView();
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        titile_text=(TextView)findViewById(R.id.header_text);
        titile_text.setTextSize(20);
        titile_text.setText(title);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView)findViewById(R.id.recommend_webView);
//        webView.loadUrl(ClientUtil.GET_KNOWLEDGE_BYID_URL+"?id="+id+"&from=mobile");
        if("".equals(id) || null == id){
            webView.loadUrl(url);
        }else{
            webView.loadUrl(url+"?id="+id+"&from=mobile");
        }
        webView.setWebViewClient(new MyWebViewClient ());
    }
    
    private class MyWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);
            return true; 
        }
        
        @Override  
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {  
            super.onReceivedError(view, errorCode, description, failingUrl);  
            //错误提示
            Toast.makeText(RecommendActivity.this, description, Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            CommonUtil.showLoadingDialog(RecommendActivity.this);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            CommonUtil.closeLodingDialog();
        }
    }
    
}
