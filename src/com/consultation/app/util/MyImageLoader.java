package com.consultation.app.util;

import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.consultation.app.listener.ConsultationCallbackHandler;

public class MyImageLoader extends ImageLoader {
    
    private static ConsultationCallbackHandler callbackHandler;

    public MyImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
    }
    
    public static void setHandler(ConsultationCallbackHandler handler){
        callbackHandler = handler;
    }

    public static ImageListener getImageListener(final ImageView view, final int errorImageResId) {
        return new ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(errorImageResId != 0) {
                    callbackHandler.onSuccess("", 0);
                    view.setImageResource(errorImageResId);
                }
            }

            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                if(response.getBitmap() != null) {
                    view.setImageBitmap(response.getBitmap());
                    callbackHandler.onSuccess("", 0);
                }
            }
        };
    }

    public interface MyImageListener extends ErrorListener {
        public void onResponse(ImageContainer response, boolean isImmediate);
    }
}
