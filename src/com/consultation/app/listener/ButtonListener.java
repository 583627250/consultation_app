/**
 * 
 */
package com.consultation.app.listener;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class ButtonListener {

    private Drawable commonImage;

    private Drawable pressImage;
    
    private int commonColor;
    
    private int pressColor;
    
    private boolean isColor = false;
    
    public ButtonListener setImage(Drawable commonImage, Drawable pressImage) {
        isColor = false;
        this.commonImage=commonImage;
        this.pressImage=pressImage;
        return this;
    }
    
    public ButtonListener setImageColor(int commonColor, int pressColor) {
        isColor = true;
        this.commonColor=commonColor;
        this.pressColor=pressColor;
        return this;
    }

    // 按钮切换
    private OnTouchListener btnTouchListener=new OnTouchListener() {

        @SuppressWarnings("deprecation")
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if(v instanceof ImageButton) {
                    if(isColor){
                        ((ImageButton)v).setBackgroundColor(pressColor);
                    }else{
                        ((ImageButton)v).setBackgroundDrawable(pressImage);
                    }
                } else if(v instanceof Button) {
                    if(isColor){
                        ((Button)v).setBackgroundColor(pressColor);
                    }else{
                        ((Button)v).setBackgroundDrawable(pressImage);
                    }
                }
                else if(v instanceof LinearLayout) {
                    if(isColor){
                        ((LinearLayout)v).setBackgroundColor(pressColor);
                    }else{
                        ((LinearLayout)v).setBackgroundDrawable(pressImage);
                    }
                }
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                if(v instanceof ImageButton) {
                    if(isColor){
                        ((ImageButton)v).setBackgroundColor(commonColor);
                    }else{
                        ((ImageButton)v).setBackgroundDrawable(commonImage);
                    }
                } else if(v instanceof Button) {
                    if(isColor){
                        ((Button)v).setBackgroundColor(commonColor);
                    }else{
                        ((Button)v).setBackgroundDrawable(commonImage);
                    }
                } else if(v instanceof LinearLayout) {
                    if(isColor){
                        ((LinearLayout)v).setBackgroundColor(commonColor);
                    }else{
                        ((LinearLayout)v).setBackgroundDrawable(commonImage);
                    }
                }
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL) {
                if(v instanceof ImageButton) {
                    if(isColor){
                        ((ImageButton)v).setBackgroundColor(commonColor);
                    }else{
                        ((ImageButton)v).setBackgroundDrawable(commonImage);
                    }
                } else if(v instanceof Button) {
                    if(isColor){
                        ((Button)v).setBackgroundColor(commonColor);
                    }else{
                        ((Button)v).setBackgroundDrawable(commonImage);
                    }
                } else if(v instanceof LinearLayout) {
                    if(isColor){
                        ((LinearLayout)v).setBackgroundColor(commonColor);
                    }else{
                        ((LinearLayout)v).setBackgroundDrawable(commonImage);
                    }
                }
            }
            return false;
        }
    };

    public OnTouchListener getBtnTouchListener() {
        return btnTouchListener;
    }

}
