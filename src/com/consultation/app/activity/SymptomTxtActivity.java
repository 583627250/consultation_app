package com.consultation.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.util.ClientUtil;

public class SymptomTxtActivity extends Activity {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private EditText editText;
    
    private boolean isAdd = false;

//    private Button saveBtn;

    private String titleText;

    private int page;

    private String content="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_symptom_txt_layout);
        page=getIntent().getIntExtra("page", -1);
        titleText=getIntent().getStringExtra("titleText");
        content=getIntent().getStringExtra("content");
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText(titleText);
        title_text.setTextSize(20);
        TextView header_right=(TextView)findViewById(R.id.header_right);
        header_right.setText("保存");
        header_right.setVisibility(View.VISIBLE);
        header_right.setTextSize(18);
        header_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存数据
                if(null == editText.getText().toString() || "".equals(editText.getText().toString().replace(" ",""))) {
                    Toast.makeText(SymptomTxtActivity.this, "请输入病例内容", Toast.LENGTH_LONG).show();
                    return;
                }
//                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                if(imm.isActive()) {
//                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//                }
                ClientUtil.getCaseParams().add(String.valueOf(page), editText.getText().toString());
                isAdd = true;
                Toast.makeText(SymptomTxtActivity.this, "已保存到本地", Toast.LENGTH_LONG).show();
//                Intent intent=new Intent();
//                Bundle bundle=new Bundle();
//                bundle.putBoolean("isAdd", true);
//                intent.putExtras(bundle);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
            }
        });
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                if(null == editText.getText().toString() || "".equals(editText.getText().toString().replace(" ",""))) {
                    isAdd = false;
                }else{
                    isAdd = true;
                    ClientUtil.getCaseParams().add(String.valueOf(page), editText.getText().toString());
                }
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isAdd", isAdd);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        editText=(EditText)findViewById(R.id.syamptom_txt_input_edit);
        editText.setTextSize(16);
        if(null != content && !"".equals(content) && !"null".equals(content)) {
            editText.setText(content);
            editText.setSelection(content.length());
        }

//        saveBtn=(Button)findViewById(R.id.syamptom_txt_btn_save);
//        saveBtn.setTextSize(20);
//        saveBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 保存数据
//                if(null == editText.getText().toString() || "".equals(editText.getText().toString())) {
//                    Toast.makeText(SymptomTxtActivity.this, "请输入病例内容", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                ClientUtil.getCaseParams().add(String.valueOf(page), editText.getText().toString());
//                Intent intent=new Intent();
//                Bundle bundle=new Bundle();
//                bundle.putBoolean("isAdd", true);
//                intent.putExtras(bundle);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
//            }
//        });
        if(ClientUtil.getCaseParams().size() != 0 && ClientUtil.getCaseParams().getValue(String.valueOf(page)) != null
            && !"".equals(ClientUtil.getCaseParams().getValue(String.valueOf(page)))) {
            editText.setText(ClientUtil.getCaseParams().getValue(String.valueOf(page)));
        }
    }

    private void initData() {

    }
}
