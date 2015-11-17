package com.consultation.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.ExpertGradeTo;
import com.consultation.app.util.SelectHospitalDB;

public class MyExpertGradeActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text, titleName, titleData;

    private ListView provinceListView;

    private MyAdapter myAdapter;

    private List<ExpertGradeTo> expertGradeTos=new ArrayList<ExpertGradeTo>();

    private static ConsultationCallbackHandler h;
    
    private String expert_tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expert_grade_layout);
        expert_tp = getIntent().getStringExtra("expert_tp");
        initData();
        initView();
    }

    private void initData() {
        SelectHospitalDB myDbHelper=new SelectHospitalDB(MyExpertGradeActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM expert_grade";
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, null);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    ExpertGradeTo expertGradeTo=new ExpertGradeTo();
                    expertGradeTo.setExpert_gradeid(cursor.getString(0));
                    expertGradeTo.setExpert_grade(cursor.getString(1));
                    expertGradeTo.setClinic_fee(cursor.getInt(2) + "");
                    expertGradeTo.setTechnology_fee(cursor.getInt(3) + "");
                    expertGradeTos.add(expertGradeTo);
                }
            }
            cursor.close();
        } catch(IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    public static void setHandler(ConsultationCallbackHandler handler) {
        h=handler;
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("选择专家级别");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleName=(TextView)findViewById(R.id.expert_grade_name_text);
        titleName.setTextSize(20);

        titleData=(TextView)findViewById(R.id.expert_grade_data_text);
        titleData.setTextSize(20);

        myAdapter=new MyAdapter();
        provinceListView=(ListView)findViewById(R.id.expert_grade_listView);
        provinceListView.setAdapter(myAdapter);
        provinceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                h.onSuccess(
                    expertGradeTos.get(position).getExpert_grade() + "," + expertGradeTos.get(position).getExpert_gradeid(), 0);
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return expertGradeTos.size();
        }

        @Override
        public Object getItem(int location) {
            return expertGradeTos.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(MyExpertGradeActivity.this).inflate(R.layout.expert_grade_list_item, null);
            }
            TextView name=(TextView)convertView.findViewById(R.id.expert_grade_list_item_name_text);
            TextView money=(TextView)convertView.findViewById(R.id.expert_grade_list_item_money_text);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));
            name.setText(expertGradeTos.get(position).getExpert_grade());
            money.setTextSize(20);
            money.setTextColor(Color.parseColor("#000000"));
            if(expert_tp.equals("1")){
                money.setText(expertGradeTos.get(position).getClinic_fee());
            }else{
                money.setText(expertGradeTos.get(position).getTechnology_fee());
            }
            return convertView;
        }
    }
}
