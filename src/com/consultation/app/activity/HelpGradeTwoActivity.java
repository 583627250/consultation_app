package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.model.HelpTo;

public class HelpGradeTwoActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private ListView listView;

    private List<HelpTo> helpList=new ArrayList<HelpTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private String helpString, title, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);
        helpString=getIntent().getStringExtra("helpString");
        title=getIntent().getStringExtra("title");
        id=getIntent().getStringExtra("id");
        initDate();
        initView();
    }

    private void initDate() {
        JSONArray infos;
        try {
            infos=new JSONArray(helpString);
            for(int i=0; i < infos.length(); i++) {
                JSONObject info=infos.getJSONObject(i);
                if(info.getString("parent_id").equals(id)){
                    HelpTo helpTo=new HelpTo();
                    helpTo.setId(info.getString("id"));
                    helpTo.setTitle(info.getString("title"));
                    helpTo.setHave_sub(info.getString("have_sub"));
                    helpTo.setContent_url(info.getString("content_url"));
                    helpTo.setParent_id(info.getString("parent_id"));
                    helpList.add(helpTo);
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText(title);
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

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.help_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                // 打开网页
                Intent intent=new Intent(HelpGradeTwoActivity.this, RecommendActivity.class);
                intent.putExtra("url", helpList.get(position).getContent_url());
                startActivity(intent);
            }
        });
    }

    private class ViewHolder {

        TextView title;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return helpList.size();
        }

        @Override
        public Object getItem(int location) {
            return helpList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(HelpGradeTwoActivity.this).inflate(R.layout.help_list_item, null);
                holder.title=(TextView)convertView.findViewById(R.id.help_list_item_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.title.setText(helpList.get(position).getTitle());
            holder.title.setTextSize(18);
            return convertView;
        }
    }

}
