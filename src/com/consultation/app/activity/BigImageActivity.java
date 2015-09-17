package com.consultation.app.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.consultation.app.R;
import com.consultation.app.util.CommonUtil;

public class BigImageActivity extends Activity {

    private ImageView imageView;

    private static ImageLoader imageLoader;

    private static String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_image_layout);
        ininView();
    }

    public static void setViewData(ImageLoader loader, String url) {
        imageLoader=loader;
        imgUrl=url;
    }

    private void ininView() {
        imageView=(ImageView)findViewById(R.id.big_image);
        if(!"null".equals(imgUrl) && !"".equals(imgUrl)) {
            if(imgUrl.startsWith("http://")) {
                ImageListener listener=
                    ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_delete);
                imageLoader.get(imgUrl, listener);
            } else {
                WindowManager wm=this.getWindowManager();
                int width=wm.getDefaultDisplay().getWidth();
                Bitmap bitmap=CommonUtil.readBitMap(width, imgUrl);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
