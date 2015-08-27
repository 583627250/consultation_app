package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.RecommendTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.ExpandTabRecommendedView;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;
import com.consultation.app.view.RecommendMiddleFilterView;


@SuppressLint("HandlerLeak")
public class KnowledgeRecommendListActivity extends Activity implements OnLoadListener{
    
    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private ArrayList<View> mViewArray=new ArrayList<View>();
    
    private PullableListView recommendListView;

    private List<RecommendTo> recommend_content_list=new ArrayList<RecommendTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private RequestQueue mQueue;
    
    private ExpandTabRecommendedView expandTabView;
    
    private RecommendMiddleFilterView viewMiddle;
    
    private ImageView searchImage;
    
    private int page = 1;
    
    private boolean hasMore = true;
    
    private SharePreferencesEditor editor;
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                myAdapter.notifyDataSetChanged();
                page = 1;
                ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.SUCCEED);
                break;
            case 1:
                if(hasMore){
                    ((PullableListView)msg.obj).finishLoading();
                }else{
                    recommendListView.setHasMoreData(false);
                }
                myAdapter.notifyDataSetChanged();
                break;
            case 2:
                recommendListView.setHasMoreData(true);
                page = 1;
                ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.FAIL);
                break;
            }
            
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_recommend_list_layout);
        editor = new SharePreferencesEditor(KnowledgeRecommendListActivity.this);
        mQueue = Volley.newRequestQueue(KnowledgeRecommendListActivity.this);
        initDate();
        initView();
        initVaule();
        initListener();
    }

    private void initView() {
        expandTabView = (ExpandTabRecommendedView)findViewById(R.id.header_title_expandTabView);
        
        String names[] = new String[] { "妇产科", "儿科", "皮肤科", "内科", "脑科", "外科" };
        viewMiddle = new RecommendMiddleFilterView(KnowledgeRecommendListActivity.this, names);
        
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
        
        ((PullToRefreshLayout)findViewById(R.id.knowledge_recommend_list_refresh_view))
        .setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("page", "1");
                parmas.put("rows", "10");
                if(!"".equals(editor.get("uid", ""))){
                    parmas.put("uid", editor.get("uid", ""));
                    parmas.put("userTp", editor.get("userType", ""));
                }
                OpenApiService.getInstance(KnowledgeRecommendListActivity.this).getKnowledgeList(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        try {
                            JSONObject responses = new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1){
                                JSONArray infos = responses.getJSONArray("knowledges");
                                recommend_content_list.clear();
                                for(int i=0; i < infos.length(); i++) {
                                    JSONObject info = infos.getJSONObject(i);
                                    recommend_content_list.add(new RecommendTo(info.getString("id"), info.getString("title"), info.getString("depart_name"), info.getString("user_name")));
                                }
                                if(infos.length() == 10) {
                                    recommendListView.setHasMoreData(true);
                                } else {
                                    recommendListView.setHasMoreData(false);
                                }
                                Message msg = handler.obtainMessage();
                                msg.what = 0;
                                msg.obj = pullToRefreshLayout;
                                handler.sendMessage(msg);
                            } else if(responses.getInt("rtnCode") == 10004){
                                Message msg=handler.obtainMessage();
                                msg.what=2;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                                Toast.makeText(KnowledgeRecommendListActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                    @Override
                                    public void onSuccess(String rspContent, int statusCode) {
                                        initDate();
                                    }

                                    @Override
                                    public void onFailure(ConsultationCallbackException exp) {
                                    }
                                });
                                startActivity(new Intent(KnowledgeRecommendListActivity.this, LoginActivity.class));
                            } else{
                                Message msg = handler.obtainMessage();
                                msg.what = 2;
                                msg.obj = pullToRefreshLayout;
                                handler.sendMessage(msg);
                                Toast.makeText(KnowledgeRecommendListActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Message msg=handler.obtainMessage();
                        msg.what=2;
                        msg.obj=pullToRefreshLayout;
                        handler.sendMessage(msg);
                        Toast.makeText(KnowledgeRecommendListActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        myAdapter = new MyAdapter();
        recommendListView=(PullableListView)findViewById(R.id.knowledge_recommend_list_listView);
        recommendListView.setAdapter(myAdapter);
        recommendListView.setOnLoadListener(this);
        recommendListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(KnowledgeRecommendListActivity.this, RecommendActivity.class);
                intent.putExtra("id", recommend_content_list.get(position).getId());
                startActivity(intent);
            }
        });
        searchImage = (ImageView)findViewById(R.id.header_right_image);
        searchImage.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KnowledgeRecommendListActivity.this, SearchRecommendActivity.class));
            }
        });
    }

    private void initDate() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("page", String.valueOf(page));
        parmas.put("rows", "10");
        if(!"".equals(editor.get("uid", ""))){
            parmas.put("uid", editor.get("uid", ""));
            parmas.put("userTp", editor.get("userType", ""));
        }
        CommonUtil.showLoadingDialog(KnowledgeRecommendListActivity.this);
        OpenApiService.getInstance(KnowledgeRecommendListActivity.this).getKnowledgeList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses = new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1){
                        JSONArray infos = responses.getJSONArray("knowledges");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info = infos.getJSONObject(i);
                            recommend_content_list.add(new RecommendTo(info.getString("id"), info.getString("title"), info.getString("depart_name"), info.getString("user_name")));
                        }
                        if(infos.length()==10){
                            recommendListView.setHasMoreData(true);
                        }else{
                            recommendListView.setHasMoreData(false);
                        }
                    }else if(responses.getInt("rtnCode") == 10004){
                        Toast.makeText(KnowledgeRecommendListActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(KnowledgeRecommendListActivity.this, LoginActivity.class));
                    } else{
                        Toast.makeText(KnowledgeRecommendListActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(KnowledgeRecommendListActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
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
                convertView=LayoutInflater.from(KnowledgeRecommendListActivity.this).inflate(R.layout.recommend_list_item, null);
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

    @Override
    public void onLoad(final PullableListView pullableListView) {
        Map<String, String> parmas = new HashMap<String, String>();
        page++;
        parmas.put("page", String.valueOf(page));
        parmas.put("rows", "10");
        if(!"".equals(editor.get("uid", ""))){
            parmas.put("uid", editor.get("uid", ""));
            parmas.put("userTp", editor.get("userType", ""));
        }
        OpenApiService.getInstance(KnowledgeRecommendListActivity.this).getKnowledgeList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                try {
                    JSONObject responses = new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1){
                        JSONArray infos = responses.getJSONArray("knowledges");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info = infos.getJSONObject(i);
                            recommend_content_list.add(new RecommendTo(info.getString("id"), info.getString("title"), info.getString("depart_name"), info.getString("user_name")));
                        }
                        if(infos.length()==10){
                            hasMore = true;
                        }else{
                            hasMore = false;
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = pullableListView;
                        handler.sendMessage(msg);
                    }else if(responses.getInt("rtnCode") == 10004){
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(KnowledgeRecommendListActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(KnowledgeRecommendListActivity.this, LoginActivity.class));
                    } else{
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(KnowledgeRecommendListActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                hasMore=true;
                Message msg=handler.obtainMessage();
                msg.what=1;
                msg.obj=pullableListView;
                handler.sendMessage(msg);
                Toast.makeText(KnowledgeRecommendListActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void initVaule() {
        mViewArray.add(viewMiddle);
        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("选择专业");
        expandTabView.setValue(mTextArray, mViewArray);
        expandTabView.setTitle("选择专业", 0);
    }

    private void initListener() {
        viewMiddle.setOnSelectListener(new RecommendMiddleFilterView.OnSelectListener() {
            @Override
            public void getValue(String distance, String showText) {
                onRefresh(viewMiddle, showText);
            }
        });
    }

    private void onRefresh(View view, String showText) {
        expandTabView.onPressBack();
        if(showText.length()>4){
            expandTabView.setTitle(showText.substring(0, 4)+"...",0);
        }else{
            expandTabView.setTitle(showText,0);
        }
        Toast.makeText(KnowledgeRecommendListActivity.this, showText, Toast.LENGTH_SHORT).show();
    }
}
