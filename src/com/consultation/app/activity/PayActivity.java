package com.consultation.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SelectHospitalDB;

public class PayActivity extends CaseBaseActivity {

    private ListView listView;

    private List<String> list=new ArrayList<String>();
    
    private List<Boolean> isCheck=new ArrayList<Boolean>();

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ViewHolder holder;

    private MyAdapter myAdapter;

    private TextView gradeText, gradeTipText, payText, aliapyText, weixinpayText;
    
    private CheckBox alipayBox, weixinpayBox;

    private Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_layout);
        initData();
        initView();
    }

    private void initData() {
        SelectHospitalDB myDbHelper=new SelectHospitalDB(PayActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM member_grade";
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, null);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    list.add(cursor.getString(1)+"/"+cursor.getString(2)+"元");
                    isCheck.add(false);
                }
            }
            cursor.close();
        } catch(IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("会费充值");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                finish();
            }
        });

        gradeText=(TextView)findViewById(R.id.pay_select_grade_text);
        gradeText.setTextSize(18);
        gradeTipText=(TextView)findViewById(R.id.pay_select_grade_tip_text);
        gradeTipText.setTextSize(18);
        payText=(TextView)findViewById(R.id.pay_select_type_text);
        payText.setTextSize(18);
        aliapyText=(TextView)findViewById(R.id.pay_select_pay_alipay_text);
        aliapyText.setTextSize(18);
        weixinpayText=(TextView)findViewById(R.id.pay_select_pay_weixinpay_text);
        weixinpayText.setTextSize(18);
        
        alipayBox=(CheckBox)findViewById(R.id.pay_select_pay_alipay_check);
        alipayBox.setTextSize(18);
        alipayBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    weixinpayBox.setChecked(false);
                }
            }
        });
        weixinpayBox=(CheckBox)findViewById(R.id.pay_select_pay_weixinpay_check);
        weixinpayBox.setTextSize(18);
        weixinpayBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    alipayBox.setChecked(false);
                }
            }
        });
        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.pay_select_grade_listView);
        listView.setAdapter(myAdapter);
        setListViewHeightBasedOnChildren(listView);

        payBtn=(Button)findViewById(R.id.pay_btn_submit);
        payBtn.setTextSize(20);
        payBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int grade = -1;
                for(int i=0; i < isCheck.size(); i++) {
                    if(isCheck.get(i)){
                        grade = i;
                    }
                }
                if(grade == -1){
                    Toast.makeText(PayActivity.this, "请选择会员级别", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(alipayBox.isChecked() || weixinpayBox.isChecked())){
                    Toast.makeText(PayActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                if(alipayBox.isChecked()){
                    parmas.put("payType", "支付宝");
                }else{
                    parmas.put("payType", "微信");
                }
                parmas.put("accessToken", ClientUtil.getToken());
                CommonUtil.showLoadingDialog(PayActivity.this);
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight=0;
        for(int i=0; i < listAdapter.getCount(); i++) {
            View listItem=listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height=totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private static class ViewHolder {

        TextView title;

        CheckBox isCheck;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int location) {
            return list.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(PayActivity.this).inflate(R.layout.pay_list_item, null);
                holder=new ViewHolder();
                holder.title=(TextView)convertView.findViewById(R.id.pay_select_name);
                holder.isCheck=(CheckBox)convertView.findViewById(R.id.pay_select_isCheck);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.title.setTextSize(18);
            holder.title.setText(list.get(position));
            holder.isCheck.setTextSize(18);
            holder.isCheck.setChecked(isCheck.get(position));
            holder.isCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        isCheck(position);
                    }
                }
            });
            return convertView;
        }
    }
    
    private void isCheck(int index) {
        for(int i=0; i < isCheck.size(); i++) {
            if(i != index) {
                isCheck.set(i, false);
            } else {
                isCheck.set(i, true);
            }
        }
        myAdapter.notifyDataSetChanged();
    }
}
