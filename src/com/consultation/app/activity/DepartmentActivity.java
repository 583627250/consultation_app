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
import com.consultation.app.model.DepartmentBranchTo;
import com.consultation.app.model.DepartmentTo;
import com.consultation.app.util.SelectHospitalDB;

public class DepartmentActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private ListView provinceListView;

    private List<String> temp=new ArrayList<String>();

    private MyAdapter myAdapter;

    private boolean isSelectDepartment=false;

    private List<DepartmentTo> departments=new ArrayList<DepartmentTo>();

    private List<DepartmentBranchTo> departmentBranchs=new ArrayList<DepartmentBranchTo>();

    private static ConsultationCallbackHandler h;
    
    private String currentString;
    
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
        title_text.setText("选择专业");
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
                if(!isSelectDepartment) {
                    isSelectDepartment=true;
                    if(position == 0){
                        h.onSuccess("不限", 0);
                        finish();
                    }else{
                        departmentBranchs=departments.get(position-1).getBranchTos();
                        if(departmentBranchs.size() == 0){
                            h.onSuccess(departments.get(position-1).getName()+","+departments.get(position-1).getId(), 1);
                            finish();
                        }else{
                            temp.clear();
                            temp.add("不限");
                            title_text.setText("选择科室");
                            currentString = departments.get(position-1).getName()+","+departments.get(position-1).getId();
                            for(int i=0; i < departmentBranchs.size(); i++) {
                                temp.add(departmentBranchs.get(i).getName());
                            }
                            myAdapter.notifyDataSetChanged();
                            provinceListView.setSelection(0);
                        }
                    }
                } else {
                    if(position == 0){
                        h.onSuccess(currentString, 1);
                        finish();
                    }else{
                        h.onSuccess(departmentBranchs.get(position).getName()+","+departmentBranchs.get(position).getId(), 1);
                        finish();
                    }
                }
            }
        });
    }

    private void initHospitalDatas() {
        SelectHospitalDB myDbHelper=new SelectHospitalDB(DepartmentActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM depart where pid=? ORDER BY indx ASC";
            String[] selectionArgs={"0"};
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    DepartmentTo departmentTo = new DepartmentTo();
                    departmentTo.setId(cursor.getString(0));
                    departmentTo.setIndex(cursor.getInt(4));
                    departmentTo.setHas_sub(cursor.getString(3));
                    departmentTo.setName(cursor.getString(2));
                    departmentTo.setPid(cursor.getString(1));
                    departmentTo.setStatus(cursor.getString(5));
                    sql="SELECT * FROM depart where pid=? ORDER BY indx ASC";
                    String[] citysSelectionArgs={cursor.getString(0)};
                    Cursor branchCursor=myDbHelper.getReadableDatabase().rawQuery(sql, citysSelectionArgs);
                    if(branchCursor != null) {
                        List<DepartmentBranchTo> branchs=new ArrayList<DepartmentBranchTo>();
                        for(int k=0; k < branchCursor.getCount(); k++) {
                            branchCursor.moveToPosition(k);
                            DepartmentBranchTo departmentBranchTo = new DepartmentBranchTo();
                            departmentBranchTo.setId(branchCursor.getString(0));
                            departmentBranchTo.setIndex(branchCursor.getInt(4));
                            departmentBranchTo.setHas_sub(branchCursor.getString(3));
                            departmentBranchTo.setName(branchCursor.getString(2));
                            departmentBranchTo.setPid(branchCursor.getString(1));
                            departmentBranchTo.setStatus(branchCursor.getString(5));
                            branchs.add(departmentBranchTo);
                        }
                        branchCursor.close();
                        departmentTo.setBranchTos(branchs);
                    }
                    departments.add(departmentTo);
                    temp.add(departmentTo.getName());
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
                convertView=LayoutInflater.from(DepartmentActivity.this).inflate(R.layout.province_list_item, null);
            }
            TextView name=(TextView)convertView.findViewById(R.id.province_list_item_text);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));
            name.setText(temp.get(position));
            return convertView;
        }
    }
}
