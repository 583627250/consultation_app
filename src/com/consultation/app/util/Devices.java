
package com.consultation.app.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

@SuppressLint("NewApi")
public class Devices {

    private int width; // 屏幕宽度（像素）

    private int height; // 屏幕高度（像素）

    private float density; // 屏幕密度（0.75 / 1.0 / 1.5）

    private int dpi; // 屏幕密度DPI（120 / 160 / 240..）
    
    private double screenSize;
    
    private double diagonalPixels;

    private static Devices instance=null;

    public synchronized static Devices getInstance(Context ctx) {
        if(instance == null) {
            instance=new Devices(ctx);
        }
        return instance;
    }

    private Devices(Context ctx) {
        DisplayMetrics metric=new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(metric);
        width=metric.widthPixels;
        height=metric.heightPixels;
        density=metric.density;
        dpi=metric.densityDpi;
        this.diagonalPixels=Math.sqrt(Math.pow(width, 2)+Math.pow(height, 2));
        this.screenSize=diagonalPixels/(160*density);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getDensity() {
        return density;
    }

    public int getDpi() {
        return dpi;
    }

    public int getScale(){
        return (int)(this.dpi/160.0+0.5);
    }

    
    public double getScreenSize() {
        return screenSize;
    }

    
    public void setScreenSize(double screenSize) {
        this.screenSize=screenSize;
    }

    
    public double getDiagonalPixels() {
        return diagonalPixels;
    }

    
    public void setDiagonalPixels(double diagonalPixels) {
        this.diagonalPixels=diagonalPixels;
    }
    
    
}
