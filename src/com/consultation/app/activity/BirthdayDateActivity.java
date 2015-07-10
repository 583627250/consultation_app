package com.consultation.app.activity;

import java.util.Date;

import android.app.Activity;
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


public class BirthdayDateActivity extends Activity {
    
    private TextView title_text;
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private ListView provinceListView;
    
    private MyAdapter myAdapter;
    
    private boolean isSelectProvince = false;
    
    private boolean isSelectCitys = false;
    
    private boolean isSelectAreas = false;
    
    private static ConsultationCallbackHandler h;
    
    private int[] temp;
    
    private int[] years;
    
    private int[] months = {1,2,3,4,5,6,7,8,9,10,11,12};
    
    private int[] days;
    
    private String year;
    
    private String month;
    
    private String day;
    
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
        initYearDatas();
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("选择日期");
        title_text.setTextSize(22);
    
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
        temp = years;
        myAdapter = new MyAdapter();
        provinceListView = (ListView)findViewById(R.id.province_listView);
        provinceListView.setAdapter(myAdapter);
        provinceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSelectProvince){
                    isSelectProvince = true;
                    year = String.valueOf(temp[position]);
                    temp = months;
                    myAdapter.notifyDataSetChanged();
                }else{
                    if(!isSelectCitys){
                        isSelectCitys = true;
                        if(temp[position]<10){
                            month = "0"+ String.valueOf(temp[position]);
                        }else{
                            month = String.valueOf(temp[position]);
                        }
                        switch(temp[position]) {
                            case 1:
                                days = getDaysCount(31);
                                break;
                            case 2:
                                days = getDaysCount(28);
                                break;
                            case 3:
                                days = getDaysCount(31);
                                break;
                            case 4:
                                days = getDaysCount(30);
                                break;
                            case 5:
                                days = getDaysCount(31);
                                break;
                            case 6:
                                days = getDaysCount(30);
                                break;
                            case 7:
                                days = getDaysCount(31);
                                break;
                            case 8:
                                days = getDaysCount(31);
                                break;
                            case 9:
                                days = getDaysCount(30);
                                break;
                            case 10:
                                days = getDaysCount(31);
                                break;
                            case 11:
                                days = getDaysCount(30);
                                break;
                            case 12:
                                days = getDaysCount(31);
                                break;
                            default:
                                break;
                        }
                        if(isLeapYear(Integer.parseInt(year)) && Integer.parseInt(month) == 2){
                            days = getDaysCount(29);
                        }
                        temp = days;
                        myAdapter.notifyDataSetChanged();
                    }else{
                        if(!isSelectAreas){
                            if(temp[position]<10){
                                day = "0"+ String.valueOf(temp[position]);
                            }else{
                                day = String.valueOf(temp[position]);
                            }
                            h.onSuccess(year + "-" + month + "-" + day, 0);
                            finish();
                        }
                    }
                }
            }
        });
    }
    
    private void initYearDatas() {
        Date data = new Date();
        years = new int[data.getYear()];
        for(int i=0; i < years.length; i++) {
            years[i] = data.getYear()+1900-i;
        }
    }

    class MyAdapter extends BaseAdapter{

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
                convertView = LayoutInflater.from(BirthdayDateActivity.this).inflate(R.layout.province_list_item, null);  
            }
            TextView name = (TextView)convertView.findViewById(R.id.province_list_item_text);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));
            if(!isSelectProvince){
                name.setText(String.valueOf(temp[position])+"年");
            }else{
                if(!isSelectCitys){
                    if(temp[position]<10){
                        name.setText("0"+String.valueOf(temp[position])+"月");
                    }else{
                        name.setText(String.valueOf(temp[position])+"月");
                    }
                }else{
                    if(temp[position]<10){
                        name.setText("0"+String.valueOf(temp[position])+"日");
                    }else{
                        name.setText(String.valueOf(temp[position])+"日");
                    }
                }
            }
            return convertView;
        }
    }
    
    private int[] getDaysCount(int daysCount){
        int[] days = new int[daysCount];
        for(int i=0; i < daysCount; i++) {
            days[i] = i+1;
        }
        return days;
    }
    
    private boolean isLeapYear(int year){
        if(year%4==0&&year%100!=0||year%400==0){
            return true;
        }
        return false;
    }
}
