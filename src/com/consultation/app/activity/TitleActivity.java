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
import com.consultation.app.model.TitleTo;
import com.consultation.app.util.SelectHospitalDB;

public class TitleActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private ListView provinceListView;

    private List<String> temp=new ArrayList<String>();

    private MyAdapter myAdapter;

    private List<TitleTo> titles=new ArrayList<TitleTo>();

    private static ConsultationCallbackHandler h;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_layout);
        initData();
        initView();
    }

    private void initData() {
        temp.add("不限");
        initHospitalDatas();
    }

    public static void setHandler(ConsultationCallbackHandler handler) {
        h=handler;
    }

    private void initView() {
        initHospitalDatas();
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("选择职称");
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

        myAdapter=new MyAdapter();
        provinceListView=(ListView)findViewById(R.id.province_listView);
        provinceListView.setAdapter(myAdapter);
        provinceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    h.onSuccess("不限", 0);
                    finish();
                }else{
                    h.onSuccess(titles.get(position).getName()+","+titles.get(position).getId(), 1);
                    finish();
                }
            }
        });
    }

    private void initHospitalDatas() {
        SelectHospitalDB myDbHelper=new SelectHospitalDB(TitleActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM title ORDER BY indx ASC";
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, null);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    TitleTo titleTo = new TitleTo();
                    titleTo.setId(cursor.getString(0));
                    titleTo.setIndex(cursor.getInt(2));
                    titleTo.setName(cursor.getString(1));
                    titles.add(titleTo);
                    temp.add(titleTo.getName());
                }
            }
            cursor.close();
        } catch(IOException ioe) {
            throw new Error("Unable to create database");
        }

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return temp.size();
        }

        @Override
        public Object getItem(int location) {
            return temp.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(TitleActivity.this).inflate(R.layout.province_list_item, null);
            }
            TextView name=(TextView)convertView.findViewById(R.id.province_list_item_text);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));
            name.setText(temp.get(position));
            return convertView;
        }
    }
}
