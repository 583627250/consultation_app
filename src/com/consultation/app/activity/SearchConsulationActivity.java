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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.util.SharePreferencesEditor;

public class SearchConsulationActivity extends Activity {

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

    private RelativeLayout waitingLayout, finshLayout, histryLayout;

    private LinearLayout allLayout;

    private boolean isBBS=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_list_search_layout);
        isBBS=getIntent().getBooleanExtra("isBBS", false);
        initDate();
        initView();
    }

    private void initDate() {
        editor=new SharePreferencesEditor(SearchConsulationActivity.this);
        if(editor.get("filter", null) != null && !"".equals(editor.get("filter", null))) {
            isHasHistory=true;
            String[] historys=editor.get("filter", null).split(",");
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
        searchEditText.setHint("请输入病例相关内容");
        searchEditText.setHintTextColor(Color.parseColor("#D3D3D3"));
        deleteBtn=(ImageView)findViewById(R.id.header_image_delete);
        deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });
        deleteText=(TextView)findViewById(R.id.case_search_history_listView_delete);
        deleteText.setTextSize(16);
        if(!isHasHistory) {
            deleteText.setVisibility(View.GONE);
        }
        deleteText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.put("filter", "");
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
                    if(editor.get("filter", null) == null || "".equals(editor.get("filter", null))) {
                        editor.put("filter", editTextString);
                    } else {
                        for(String temp: editor.get("filter", null).split(",")) {
                            if(temp.equals(editTextString)) {
                                isHave=true;
                            }
                        }
                        if(!isHave) {
                            editor.put("filter", editor.get("filter", null) + "," + editTextString);
                        }
                        isHave=false;
                    }
                    Intent intent=new Intent(SearchConsulationActivity.this, SearchConsulationResultActivity.class);
                    intent.putExtra("isBBS", isBBS);
                    intent.putExtra("filter", editTextString);
                    startActivity(intent);
                    deleteText.setVisibility(View.VISIBLE);
                    historyList.add(editTextString);
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        myAdapter=new MyAdapter();
        historyListView=(ListView)findViewById(R.id.case_search_history_listView);
        historyListView.setAdapter(myAdapter);
        historyListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(SearchConsulationActivity.this, SearchConsulationResultActivity.class);
                intent.putExtra("isBBS", isBBS);
                intent.putExtra("filter", historyList.get(position));
                startActivity(intent);
            }
        });

        TextView searchTitleText=(TextView)findViewById(R.id.case_search_title_text);
        searchTitleText.setTextSize(18);
        TextView waitingText=(TextView)findViewById(R.id.case_search_waiting_text);
        waitingText.setTextSize(15);
        TextView finshText=(TextView)findViewById(R.id.case_search_finsh_text);
        finshText.setTextSize(15);
        TextView histryText=(TextView)findViewById(R.id.case_search_histry_text);
        histryText.setTextSize(15);
        waitingLayout=(RelativeLayout)findViewById(R.id.case_search_waiting_layout);
        waitingLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchConsulationActivity.this, SearchConsulationResultActivity.class);
                intent.putExtra("flag", 1);
                startActivity(intent);
            }
        });
        finshLayout=(RelativeLayout)findViewById(R.id.case_search_finsh_layout);
        finshLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchConsulationActivity.this, SearchConsulationResultActivity.class);
                intent.putExtra("flag", 2);
                startActivity(intent);
            }
        });
        histryLayout=(RelativeLayout)findViewById(R.id.case_search_histry_layout);
        histryLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchConsulationActivity.this, SearchConsulationResultActivity.class);
                intent.putExtra("flag", 3);
                startActivity(intent);
            }
        });
        allLayout=(LinearLayout)findViewById(R.id.case_search_title_layout);
        
        TextView searchDisTitleText=(TextView)findViewById(R.id.case_search_dis_title_text);
        searchDisTitleText.setTextSize(18);
        TextView disText=(TextView)findViewById(R.id.case_search_dis_discussion_text);
        disText.setTextSize(15);
        LinearLayout disAllLayout=(LinearLayout)findViewById(R.id.case_search_dis_title_layout);
        RelativeLayout disDiscussionLayout=(RelativeLayout)findViewById(R.id.case_search_dis_discussion_layout);
        disDiscussionLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchConsulationActivity.this, SearchConsulationResultActivity.class);
                intent.putExtra("flag", 4);
                startActivity(intent);
            }
        });
        if(isBBS) {
            allLayout.setVisibility(View.GONE);
            disAllLayout.setVisibility(View.VISIBLE);
        }else{
            allLayout.setVisibility(View.VISIBLE);
            disAllLayout.setVisibility(View.GONE);
        }
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
                convertView=LayoutInflater.from(SearchConsulationActivity.this).inflate(R.layout.search_recommend_list_item, null);
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
