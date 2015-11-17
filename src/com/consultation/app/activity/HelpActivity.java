package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.HelpTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;

public class HelpActivity extends Activity {

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private ListView listView;

    private List<HelpTo> helpList=new ArrayList<HelpTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private RequestQueue mQueue;
    
//    private String helpString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);
        mQueue=Volley.newRequestQueue(HelpActivity.this);
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        CommonUtil.showLoadingDialog(HelpActivity.this);
        OpenApiService.getInstance(HelpActivity.this).getHelpList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        helpList.clear();
                        System.out.println(arg0);
                        JSONArray infos=responses.getJSONArray("datas");
//                        helpString = infos.toString();
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            HelpTo helpTo=new HelpTo();
                            helpTo.setId(info.getString("id"));
                            helpTo.setTitle(info.getString("title"));
//                            helpTo.setHave_sub(info.getString("have_sub"));
//                            helpTo.setContent_url(info.getString("content_url"));
//                            helpTo.setParent_id(info.getString("parent_id"));
                            helpList.add(helpTo);
                        }
                        myAdapter.notifyDataSetChanged();
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(HelpActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(HelpActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(HelpActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(HelpActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("帮助中心");
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
//                if(helpList.get(position).getHave_sub().equals("0")){
                    //打开网页
                  Intent intent=new Intent(HelpActivity.this, RecommendActivity.class);
                  intent.putExtra("url", ClientUtil.GET_HELP_BYID_URL);
                  intent.putExtra("title", "帮助中心");
                  intent.putExtra("id", helpList.get(position).getId());
                  startActivity(intent);
//                }else{
//                    //获取字帮助
//                    Intent intent=new Intent(HelpActivity.this, HelpGradeTwoActivity.class);
//                    intent.putExtra("helpString", helpString);
//                    intent.putExtra("title", helpList.get(position).getTitle());
//                    intent.putExtra("id", helpList.get(position).getId());
//                    startActivity(intent);
//                }
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
                convertView=LayoutInflater.from(HelpActivity.this).inflate(R.layout.help_list_item, null);
                holder.title=(TextView)convertView.findViewById(R.id.help_list_item_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            System.out.println(helpList.get(position).getTitle());
            holder.title.setText(helpList.get(position).getTitle());
            holder.title.setTextSize(18);
            return convertView;
        }
    }

}
