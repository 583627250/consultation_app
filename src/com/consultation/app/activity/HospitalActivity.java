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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.CityTo;
import com.consultation.app.model.HospitalTo;
import com.consultation.app.model.ProvinceTo;
import com.consultation.app.util.SelectHospitalDB;

public class HospitalActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout, search_layout, search_line_layout;

    private TextView back_text, search_text_btn;

    private EditText contents;

    private ListView provinceListView;

    private List<String> temp=new ArrayList<String>();

    private MyAdapter myAdapter;

    private boolean isSelectProvince=false;

    private boolean isSelectCitys=false;

    private boolean isSelectHospaitals=false;

    private List<ProvinceTo> provinces=new ArrayList<ProvinceTo>();

    private List<CityTo> cityTemp=new ArrayList<CityTo>();

    private List<HospitalTo> hospitalTemp=new ArrayList<HospitalTo>();

    private static ConsultationCallbackHandler h;

    private SelectHospitalDB myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_layout);
        initData();
        initView();
    }

    private void initData() {
        initHospitalDatas();
    }

    public static void setHandler(ConsultationCallbackHandler handler) {
        h=handler;
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("选择省");
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

        search_layout=(LinearLayout)findViewById(R.id.search_layout);
        search_line_layout=(LinearLayout)findViewById(R.id.search_line_layout);

        contents=(EditText)findViewById(R.id.search_edit);

        search_text_btn=(TextView)findViewById(R.id.search_text_btn);
        search_text_btn.setTextSize(14);
        search_text_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null != contents.getText().toString() && !"".equals(contents.getText().toString())) {
                    temp.clear();
                    for(int i=0; i < hospitalTemp.size(); i++) {
                        if(hospitalTemp.get(i).getName().contains(contents.getText().toString())) {
                            temp.add(hospitalTemp.get(i).getName());
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HospitalActivity.this, "请输入医院名称", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myAdapter=new MyAdapter();
        provinceListView=(ListView)findViewById(R.id.province_listView);
        provinceListView.setAdapter(myAdapter);
        provinceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSelectProvince) {
                    isSelectProvince=true;
                    if(position == 0){
                        h.onSuccess("不限", 1);
                        finish();
                    }else{
                        temp.clear();
                        title_text.setText("选择市");
                        cityTemp=getCitys(provinces.get(position).getId());
                        for(int i=0; i < cityTemp.size(); i++) {
                            temp.add(cityTemp.get(i).getName());
                        }
                        myAdapter.notifyDataSetChanged();
                        provinceListView.setSelection(0);
                    }
                } else {
                    if(!isSelectCitys) {
                        isSelectCitys=true;
                        search_layout.setVisibility(View.VISIBLE);
                        search_line_layout.setVisibility(View.VISIBLE);
                        temp.clear();
                        title_text.setText("选择医院");
                        hospitalTemp=getHospitals(cityTemp.get(position).getId());
                        for(int i=0; i < hospitalTemp.size(); i++) {
                            temp.add(hospitalTemp.get(i).getName());
                        }
                        myAdapter.notifyDataSetChanged();
                        provinceListView.setSelection(0);
                    } else {
                        if(!isSelectHospaitals) {
                            h.onSuccess(temp.get(position) + "," + hospitalTemp.get(position).getId(), 0);
                            finish();
                        }
                    }
                }
            }
        });
    }

    private void initHospitalDatas() {
        myDbHelper=new SelectHospitalDB(HospitalActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM area where pid=? ORDER BY indx ASC";
            String[] selectionArgs={"0"};
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
            provinces.add(new ProvinceTo());
            temp.add("不限");
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    ProvinceTo provinceTo=new ProvinceTo();
                    provinceTo.setId(cursor.getString(0));
                    provinceTo.setIndex(cursor.getInt(3));
                    provinceTo.setName(cursor.getString(1));
                    provinceTo.setPid(cursor.getString(2));
                    provinceTo.setStatus(cursor.getString(4));
                    provinces.add(provinceTo);
                    temp.add(provinceTo.getName());
                }
            }
            cursor.close();
        } catch(IOException ioe) {
            throw new Error("Unable to create database");
        }

    }

    private List<CityTo> getCitys(String pid) {
        List<CityTo> citys=new ArrayList<CityTo>();
        String sql="SELECT * FROM area where pid=? ORDER BY indx ASC";
        String[] citysSelectionArgs={pid};
        Cursor citysCursor=myDbHelper.getReadableDatabase().rawQuery(sql, citysSelectionArgs);
        if(citysCursor != null) {
            for(int k=0; k < citysCursor.getCount(); k++) {
                citysCursor.moveToPosition(k);
                CityTo cityTo=new CityTo();
                cityTo.setId(citysCursor.getString(0));
                cityTo.setIndex(citysCursor.getInt(3));
                cityTo.setName(citysCursor.getString(1));
                cityTo.setPid(citysCursor.getString(2));
                cityTo.setStatus(citysCursor.getString(4));
                citys.add(cityTo);
            }
        }
        citysCursor.close();
        return citys;
    }

    private List<HospitalTo> getHospitals(String pid) {
        List<HospitalTo> hospiatals=new ArrayList<HospitalTo>();
        String sql="SELECT * FROM hospital where area_city_id=?";
        String[] hospiatalsSelectionArgs={pid};
        Cursor hospiatalsCursor=myDbHelper.getReadableDatabase().rawQuery(sql, hospiatalsSelectionArgs);
        if(hospiatalsCursor != null) {
            for(int y=0; y < hospiatalsCursor.getCount(); y++) {
                hospiatalsCursor.moveToPosition(y);
                HospitalTo hospitalTo=new HospitalTo();
                hospitalTo.setId(hospiatalsCursor.getString(0));
                hospitalTo.setName(hospiatalsCursor.getString(2));
                hospitalTo.setArea_city_id(hospiatalsCursor.getString(1));
                hospitalTo.setStatus(hospiatalsCursor.getString(3));
                hospiatals.add(hospitalTo);
            }
        }
        hospiatalsCursor.close();
        return hospiatals;
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
                convertView=LayoutInflater.from(HospitalActivity.this).inflate(R.layout.province_list_item, null);
            }
            TextView name=(TextView)convertView.findViewById(R.id.province_list_item_text);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));
            name.setText(temp.get(position));
            return convertView;
        }
    }
}
