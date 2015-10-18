package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.util.SharePreferencesEditor;

public class SearchSpecialistActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text, search_text, hide_text;

    private EditText searchEditText;

    private ListView historyListView;

    private MyAdapter myAdapter;

    private List<String> historyList=new ArrayList<String>();

    private ViewHolder holder;

    private SharePreferencesEditor editor;

    private boolean isHasHistory=false;

    private TextView deleteText;

    private boolean isHave=false;

    private ImageView deleteBtn;

    private String hospital_id=null;

    private String department_id=null;

    private String title_id=null;

    private TextView hospital_text, department_text, title_text;

    private LinearLayout hospital_layout, department_layout, title_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialist_search_layout);
        initDate();
        initView();
    }

    private void initDate() {
        editor=new SharePreferencesEditor(SearchSpecialistActivity.this);
        if(editor.get("specialistHistory", null) != null && !"".equals(editor.get("specialistHistory", null))) {
            isHasHistory=true;
            String[] historys=editor.get("specialistHistory", null).split(",");
            for(String string: historys) {
                historyList.add(string);
            }
        }
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        hide_text=(TextView)findViewById(R.id.header_text);
        hide_text.setTextSize(20);
        search_text=(TextView)findViewById(R.id.header_right);
        search_text.setTextSize(18);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        searchEditText=(EditText)findViewById(R.id.header_edit);
        searchEditText.setTextSize(16);
        searchEditText.setHint("请输入专家姓名");
        searchEditText.setHintTextColor(Color.parseColor("#D3D3D3"));
        deleteBtn=(ImageView)findViewById(R.id.header_image_delete);
        deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });
        deleteText=(TextView)findViewById(R.id.recommend_search_history_listView_delete);
        deleteText.setTextSize(16);
        if(!isHasHistory) {
            deleteText.setVisibility(View.GONE);
        }
        deleteText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.put("specialistHistory", "");
                historyList.clear();
                myAdapter.notifyDataSetChanged();
                isHasHistory=false;
                deleteText.setVisibility(View.GONE);
            }
        });

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
        search_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String editTextString=searchEditText.getText().toString().trim();
                if(null != editTextString && !"".equals(editTextString)) {
                    if(editor.get("specialistHistory", null) == null || "".equals(editor.get("specialistHistory", null))) {
                        editor.put("specialistHistory", editTextString);
                    } else {
                        for(String temp: editor.get("specialistHistory", null).split(",")) {
                            if(temp.equals(editTextString)) {
                                isHave=true;
                            }
                        }
                        if(!isHave) {
                            editor.put("specialistHistory", editor.get("specialistHistory", null) + "," + editTextString);
                        }
                        isHave=false;
                    }
                    deleteText.setVisibility(View.VISIBLE);
                    historyList.add(editTextString);
                    myAdapter.notifyDataSetChanged();
                }
                if(hospital_id == null && department_id == null && title_id == null && (null == editTextString || "".equals(editTextString))){
                    return;
                }
                Intent intent=new Intent(SearchSpecialistActivity.this, SearchSpecialistResultActivity.class);
                intent.putExtra("nameString", editTextString);
                intent.putExtra("hospital_id", hospital_id);
                intent.putExtra("department_id", department_id);
                intent.putExtra("title_id", title_id);
                startActivity(intent);
            }
        });
        myAdapter=new MyAdapter();
        historyListView=(ListView)findViewById(R.id.recommend_search_history_listView);
        historyListView.setAdapter(myAdapter);
        historyListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(SearchSpecialistActivity.this, SearchSpecialistResultActivity.class);
                intent.putExtra("nameString", historyList.get(position));
                intent.putExtra("hospital_id", hospital_id);
                intent.putExtra("department_id", department_id);
                intent.putExtra("title_id", title_id);
                startActivity(intent);
            }
        });

        hospital_text=(TextView)findViewById(R.id.specialist_select_hospital_text);
        hospital_text.setTextSize(17);
        hospital_layout=(LinearLayout)findViewById(R.id.specialist_select_hospital_layout);
        hospital_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HospitalActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        switch(statusCode) {
                            case 0:
                                hospital_text.setText(rspContent.split(",")[0]);
                                hospital_id=rspContent.split(",")[1];
                                break;
                            case 2:
                                hospital_text.setText("选择医院");
                                hospital_id=null;
                                break;

                            default:
                                break;
                        }

                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                startActivity(new Intent(SearchSpecialistActivity.this, HospitalActivity.class));
            }
        });

        department_text=(TextView)findViewById(R.id.specialist_select_department_text);
        department_text.setTextSize(17);
        department_layout=(LinearLayout)findViewById(R.id.specialist_select_department_layout);
        department_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DepartmentActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        switch(statusCode) {
                            case 0:
                                department_text.setText("选择专业");
                                department_id=null;
                                break;
                            case 1:
                                department_text.setText(rspContent.split(",")[0]);
                                department_id=rspContent.split(",")[1];
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                startActivity(new Intent(SearchSpecialistActivity.this, DepartmentActivity.class));
            }
        });

        title_text=(TextView)findViewById(R.id.specialist_select_title_text);
        title_text.setTextSize(17);
        title_layout=(LinearLayout)findViewById(R.id.specialist_select_title_layout);
        title_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TitleActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        switch(statusCode) {
                            case 0:
                                title_text.setText("选择职称");
                                title_id=null;
                                break;
                            case 1:
                                title_text.setText(rspContent.split(",")[0]);
                                title_id=rspContent.split(",")[1];
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {

                    }
                });
                startActivity(new Intent(SearchSpecialistActivity.this, TitleActivity.class));
            }
        });
    }

    private static class ViewHolder {

        TextView searchText;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(isHasHistory) {
                return historyList.size();
            }
            return historyList.size();
        }

        @Override
        public Object getItem(int location) {
            return historyList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(SearchSpecialistActivity.this).inflate(R.layout.search_recommend_list_item, null);
                holder=new ViewHolder();
                holder.searchText=(TextView)convertView.findViewById(R.id.search_recommend_list_item_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.searchText.setTextSize(16);
            holder.searchText.setText(historyList.get(position));
            return convertView;
        }
    }
}
