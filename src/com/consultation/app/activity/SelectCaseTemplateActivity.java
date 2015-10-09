package com.consultation.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.consultation.app.model.DepartmentBranchTo;
import com.consultation.app.model.DepartmentTo;
import com.consultation.app.model.TemplateTo;
import com.consultation.app.util.SelectHospitalDB;

public class SelectCaseTemplateActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private ListView provinceListView;

    private List<String> temp=new ArrayList<String>();

    private MyAdapter myAdapter;

    private boolean isSelectDepartOne=false;
    
    private boolean isSelectDepartTwo=false;

    private List<DepartmentTo> departments=new ArrayList<DepartmentTo>();
    
    private List<TemplateTo> templateTos=new ArrayList<TemplateTo>();

    private List<DepartmentBranchTo> departmentBranchs=new ArrayList<DepartmentBranchTo>();

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

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("选择科室");
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
                if(!isSelectDepartOne) {
                    isSelectDepartOne=true;
                    departmentBranchs=departments.get(position).getBranchTos();
                    if(departmentBranchs.size() == 0) {
                        getTemplates(departments.get(position).getId());
                        temp.clear();
                        title_text.setText("选择科室模板");
                        for(int i=0; i < templateTos.size(); i++) {
                            temp.add(templateTos.get(i).getTempl_name());
                        }
                        myAdapter.notifyDataSetChanged();
                        provinceListView.setSelection(0);
                    } else {
                        isSelectDepartTwo = true;
                        temp.clear();
                        title_text.setText("选择科室");
                        for(int i=0; i < departmentBranchs.size(); i++) {
                            temp.add(departmentBranchs.get(i).getName());
                        }
                        myAdapter.notifyDataSetChanged();
                        provinceListView.setSelection(0);
                    }
                } else if(isSelectDepartTwo){
                    isSelectDepartTwo = false;
                    getTemplates(departmentBranchs.get(position).getId());
                    temp.clear();
                    title_text.setText("选择科室模板");
                    for(int i=0; i < templateTos.size(); i++) {
                        temp.add(templateTos.get(i).getTempl_name());
                    }
                    myAdapter.notifyDataSetChanged();
                    provinceListView.setSelection(0);
                }else{
                    Intent intent=new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("departName", templateTos.get(position).getTempl_name());
                    bundle.putString("departId", templateTos.get(position).getTempl_id());
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void initHospitalDatas() {
        SelectHospitalDB myDbHelper=new SelectHospitalDB(SelectCaseTemplateActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM depart where pid=? ORDER BY indx ASC";
            String[] selectionArgs={"0"};
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    DepartmentTo departmentTo=new DepartmentTo();
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
                            DepartmentBranchTo departmentBranchTo=new DepartmentBranchTo();
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
    
    private void getTemplates(String departId){
        SelectHospitalDB myDbHelper=new SelectHospitalDB(SelectCaseTemplateActivity.this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            String sql="SELECT * FROM depart_case_template where deaprt_id=? ";
            String[] selectionArgs={departId};
            Cursor cursor=myDbHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
            if(cursor != null) {
                for(int j=0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    TemplateTo templateTo=new TemplateTo();
                    templateTo.setDepart_id(cursor.getString(0));
                    templateTo.setTempl_id(cursor.getString(1));
                    templateTo.setTempl_name(cursor.getString(2));
                    templateTos.add(templateTo);
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
                convertView=LayoutInflater.from(SelectCaseTemplateActivity.this).inflate(R.layout.province_list_item, null);
            }
            TextView name=(TextView)convertView.findViewById(R.id.province_list_item_text);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));
            name.setText(temp.get(position));
            return convertView;
        }
    }
}
