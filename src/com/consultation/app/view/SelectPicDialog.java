package com.consultation.app.view;

import com.consultation.app.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class SelectPicDialog extends Dialog {

    private android.view.View.OnClickListener photographOnClickListener;
    
    private android.view.View.OnClickListener selectOnClickListener;

    private int layoutRes;// 布局文件

    public SelectPicDialog(Context context) {
        super(context);
    }

    public SelectPicDialog(Context context, int resLayout) {
        super(context);
        this.layoutRes=resLayout;
    }

    public SelectPicDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.layoutRes=resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        TextView photographText = (TextView)findViewById(R.id.select_pic_dialog_photograph);
        photographText.setTextSize(18);
        photographText.setOnClickListener(photographOnClickListener);
        TextView selectText = (TextView)findViewById(R.id.select_pic_dialog_select);
        selectText.setTextSize(18);
        selectText.setOnClickListener(selectOnClickListener);
    }
    
    public void setPhotographButton(android.view.View.OnClickListener clickListener){
        photographOnClickListener = clickListener;
    }

    public void setSelectButton(android.view.View.OnClickListener clickListener){
        selectOnClickListener = clickListener;
    }
}
