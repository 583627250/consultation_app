package com.consultation.app.activity;

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


public class ProvinceActivity extends BaseActivity {
    
    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private ListView provinceListView;
    
    private String[] temp;
    
    private MyAdapter myAdapter;
    
    private boolean isSelectProvince = false;
    
    private boolean isSelectCitys = false;
    
    private boolean isSelectAreas = false;
    
    private static ConsultationCallbackHandler h;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_layout);
        init();
    }
    
    public static void setHandler(ConsultationCallbackHandler handler){
        h = handler;
    }

    private void init() {
        initProvinceDatas();
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("选择地区");
        title_text.setTextSize(20);
    
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        temp = mProvinceDatas;
        myAdapter = new MyAdapter();
        provinceListView = (ListView)findViewById(R.id.province_listView);
        provinceListView.setAdapter(myAdapter);
        provinceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSelectProvince){
                    isSelectProvince = true;
                    mCurrentProviceName = temp[position];
                    String[] cities=mCitisDatasMap.get(mCurrentProviceName);
                    temp = cities;
                    myAdapter.notifyDataSetChanged();
                }else{
                    if(!isSelectCitys){
                        isSelectCitys = true;
                        mCurrentCityName = temp[position];
                        String[] areas=mDistrictDatasMap.get(mCurrentCityName);
                        temp = areas;
                        myAdapter.notifyDataSetChanged();
                    }else{
                        if(!isSelectAreas){
                            mCurrentDistrictName = temp[position];
                            h.onSuccess(mCurrentProviceName+","+mCurrentCityName+","+mCurrentDistrictName, 0);
                            finish();
                        }
                    }
                }
            }
        });
    }
    
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return temp.length;
        }

        @Override
        public Object getItem(int location) {
            return temp[location];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ProvinceActivity.this).inflate(R.layout.province_list_item, null);  
            }
            TextView name = (TextView)convertView.findViewById(R.id.province_list_item_text);
            name.setTextSize( 20);
            name.setTextColor(Color.parseColor("#000000"));
            name.setText(temp[position]);
            return convertView;
        }
    }
}
