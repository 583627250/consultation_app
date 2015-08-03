package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.model.RecommendTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class SearchRecommendResultActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text,title_text;
    
    private ListView recommendListView;

    private MyAdapter myAdapter;
    
    private ViewHolder holder;
    
    private List<RecommendTo> recommend_content_list=new ArrayList<RecommendTo>();
    
    private SharePreferencesEditor editor;
    
    private RequestQueue mQueue;
    
    private String titleString;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_recommend_list_search_result_layout);
        titleString = getIntent().getStringExtra("titleString");
        editor = new SharePreferencesEditor(SearchRecommendResultActivity.this);
        initDate();
        initView();
    }

    private void initDate() {
        mQueue = Volley.newRequestQueue(SearchRecommendResultActivity.this);
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("title", titleString);
        if(!"".equals(editor.get("uid", ""))){
            parmas.put("uid", editor.get("uid", ""));
            parmas.put("userTp", editor.get("userType", ""));
        }
        CommonUtil.showLoadingDialog(SearchRecommendResultActivity.this);
        OpenApiService.getInstance(SearchRecommendResultActivity.this).getKnowledgeList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses = new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1){
                        recommend_content_list.clear();
                        JSONArray infos = responses.getJSONArray("knowledges");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info = infos.getJSONObject(i);
                            recommend_content_list.add(new RecommendTo(info.getString("id"), info.getString("title"), info.getString("depart_name"), info.getString("user_name")));
                        }
                        myAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(SearchRecommendResultActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(SearchRecommendResultActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("科普知识");
        title_text.setTextSize(20);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        myAdapter = new MyAdapter();
        recommendListView=(ListView)findViewById(R.id.knowledge_recommend_list_search_result_listView);
        recommendListView.setAdapter(myAdapter);
        recommendListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchRecommendResultActivity.this, RecommendActivity.class);
                intent.putExtra("id", recommend_content_list.get(position).getId());
                startActivity(intent);
            }
        });
    }
    
    private static class ViewHolder {
        
        TextView title;

        TextView author;
  }

  private class MyAdapter extends BaseAdapter {

      @Override
      public int getCount() {
          return recommend_content_list.size();
      }

      @Override
      public Object getItem(int location) {
          return recommend_content_list.get(location);
      }

      @Override
      public long getItemId(int position) {
          return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
          if(convertView == null) {
              convertView=LayoutInflater.from(SearchRecommendResultActivity.this).inflate(R.layout.recommend_list_item, null);
              holder=new ViewHolder();
              holder.title=(TextView)convertView.findViewById(R.id.recommend_list_item_text_title);
              holder.author=(TextView)convertView.findViewById(R.id.recommend_list_item_text_author);
              convertView.setTag(holder);
          }else {
              holder=(ViewHolder)convertView.getTag();
          }
          holder.title.setTextSize(18);
          holder.title.setText(recommend_content_list.get(position).getTitle());
          holder.author.setTextSize(15);
          holder.author.setText("@" + recommend_content_list.get(position).getDepart_name() + "-"
              + recommend_content_list.get(position).getUser_name());
          return convertView;
      }
  }
}
