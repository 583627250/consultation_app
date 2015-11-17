package com.consultation.app.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.MyImageLoader;
import com.consultation.app.view.ZoomImageView;

public class BigImageActivity extends Activity {

    private ZoomImageView imageView;
    
    private ImageView loading;

    private static MyImageLoader imageLoader;
    
    private RequestQueue mQueue;

    private static String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_image_layout);
        ininView();
    }

    public static void setViewData(String url) {
        imgUrl=url;
    }

    @SuppressWarnings("deprecation")
    private void ininView() {
        imageView=(ZoomImageView)findViewById(R.id.big_image);
        loading=(ImageView)findViewById(R.id.big_image_loading);
        loading.setBackgroundResource(R.anim.image_loading_anim);  
        final AnimationDrawable animation = (AnimationDrawable)loading.getBackground();  
        animation.start();
        mQueue = Volley.newRequestQueue(BigImageActivity.this);
        imageLoader=new MyImageLoader(mQueue, new BitmapCache());
        MyImageLoader.setHandler(new ConsultationCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                imageView.setVisibility(View.VISIBLE);
                animation.stop();
                loading.setVisibility(View.GONE);
            }
            
            @Override
            public void onFailure(ConsultationCallbackException exp) {
            }
        });
        if(!"null".equals(imgUrl) && !"".equals(imgUrl)) {
            if(imgUrl.startsWith("http://")) {
                ImageListener listener=
                    MyImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_delete);
                imageLoader.get(imgUrl, listener);
            } else {
                imageView.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                WindowManager wm=this.getWindowManager();
                int width=wm.getDefaultDisplay().getWidth();
                Bitmap bitmap=CommonUtil.readBitMap(width, imgUrl);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
