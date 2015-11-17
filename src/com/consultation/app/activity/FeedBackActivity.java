package com.consultation.app.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.FeedBackTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;

@SuppressLint({"SimpleDateFormat", "HandlerLeak"})
public class FeedBackActivity extends Activity implements OnLoadListener {

    private EditText contentEdit;

    private Button submit;

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private PullableListView listView;

    private List<FeedBackTo> feedbackList=new ArrayList<FeedBackTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private SharePreferencesEditor editor;

    private int page=1;

    private boolean hasMore=true;

    private RequestQueue mQueue;

    private Handler handler=new Handler() {

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    myAdapter.notifyDataSetChanged();
                    page=1;
                    ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case 1:
                    if(hasMore) {
                        ((PullableListView)msg.obj).finishLoading();
                    } else {
                        listView.setHasMoreData(false);
                    }
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    listView.setHasMoreData(true);
                    page=1;
                    ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.FAIL);
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        mQueue=Volley.newRequestQueue(FeedBackActivity.this);
        editor=new SharePreferencesEditor(FeedBackActivity.this);
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        CommonUtil.showLoadingDialog(FeedBackActivity.this);
        OpenApiService.getInstance(FeedBackActivity.this).getFeedBackList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    feedbackList.clear();
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("userFeebacks");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            FeedBackTo feedBackTo=new FeedBackTo();
                            feedBackTo.setContent(info.getString("content"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                feedBackTo.setCreate_time(0);
                            } else {
                                feedBackTo.setCreate_time(Long.parseLong(createTime));
                            }
                            feedBackTo.setReply(info.getString("reply"));
                            String replyTime=info.getString("reply_time");
                            if(replyTime.equals("null")) {
                                feedBackTo.setReply_time(0);
                            } else {
                                feedBackTo.setReply_time(Long.parseLong(replyTime));
                            }
                            feedbackList.add(feedBackTo);
                        }
                        if(infos.length() == 10) {
                            listView.setHasMoreData(true);
                        } else {
                            listView.setHasMoreData(false);
                        }
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(FeedBackActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(FeedBackActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("意见反馈");
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

        contentEdit=(EditText)findViewById(R.id.mine_feedback_input_edit);
        contentEdit.setTextSize(18);

        submit=(Button)findViewById(R.id.mine_feedback_btn);
        submit.setTextSize(18);
        submit.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == contentEdit.getText().toString() || "".equals(contentEdit.getText().toString())) {
                    Toast.makeText(FeedBackActivity.this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("content", contentEdit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(FeedBackActivity.this);
                OpenApiService.getInstance(FeedBackActivity.this).getSendFeedBack(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                contentEdit.setText("");
                                initDate();
                            } else if(responses.getInt("rtnCode") == 10004) {
                                Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                    @Override
                                    public void onSuccess(String rspContent, int statusCode) {
                                        initDate();
                                    }

                                    @Override
                                    public void onFailure(ConsultationCallbackException exp) {
                                    }
                                });
                                startActivity(new Intent(FeedBackActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(FeedBackActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ((PullToRefreshLayout)findViewById(R.id.mine_feedback_refresh_view)).setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("page", "1");
                parmas.put("rows", "10");
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                OpenApiService.getInstance(FeedBackActivity.this).getFeedBackList(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                JSONArray infos=responses.getJSONArray("userFeebacks");
                                feedbackList.clear();
                                for(int i=0; i < infos.length(); i++) {
                                    JSONObject info=infos.getJSONObject(i);
                                    FeedBackTo feedBackTo=new FeedBackTo();
                                    feedBackTo.setContent(info.getString("content"));
                                    String createTime=info.getString("create_time");
                                    if(createTime.equals("null")) {
                                        feedBackTo.setCreate_time(0);
                                    } else {
                                        feedBackTo.setCreate_time(Long.parseLong(createTime));
                                    }
                                    feedBackTo.setReply(info.getString("reply"));
                                    String replyTime=info.getString("reply_time");
                                    if(replyTime.equals("null")) {
                                        feedBackTo.setCreate_time(0);
                                    } else {
                                        feedBackTo.setReply_time(Long.parseLong(replyTime));
                                    }
                                    feedbackList.add(feedBackTo);
                                }
                                if(infos.length() == 10) {
                                    listView.setHasMoreData(true);
                                } else {
                                    listView.setHasMoreData(false);
                                }
                                Message msg=handler.obtainMessage();
                                msg.what=0;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                            } else if(responses.getInt("rtnCode") == 10004) {
                                Message msg=handler.obtainMessage();
                                msg.what=2;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                                Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                    @Override
                                    public void onSuccess(String rspContent, int statusCode) {
                                        initDate();
                                    }

                                    @Override
                                    public void onFailure(ConsultationCallbackException exp) {
                                    }
                                });
                                startActivity(new Intent(FeedBackActivity.this, LoginActivity.class));
                            } else {
                                Message msg=handler.obtainMessage();
                                msg.what=2;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                                Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FeedBackActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        myAdapter=new MyAdapter();
        listView=(PullableListView)findViewById(R.id.mine_feedback_listView);
        listView.setAdapter(myAdapter);
        listView.setOnLoadListener(this);
    }

    private class ViewHolder {

        TextView contents;

        TextView reply;

        TextView create_time;

        TextView reply_time;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return feedbackList.size();
        }

        @Override
        public Object getItem(int location) {
            return feedbackList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(FeedBackActivity.this).inflate(R.layout.feedback_list_item, null);
                holder.contents=(TextView)convertView.findViewById(R.id.feedback_item_content);
                holder.create_time=(TextView)convertView.findViewById(R.id.feedback_item_createTime);
                holder.reply=(TextView)convertView.findViewById(R.id.feedback_item_reply);
                holder.reply_time=(TextView)convertView.findViewById(R.id.feedback_item_replyTime);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.contents.setText(feedbackList.get(position).getContent());
            holder.contents.setTextSize(18);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(feedbackList.get(position).getCreate_time()));
            holder.create_time.setText(sd);
            holder.create_time.setTextSize(14);
            if(!"".equals(feedbackList.get(position).getReply()) && !"null".equals(feedbackList.get(position).getReply())){
                holder.reply.setText("回复：" + feedbackList.get(position).getReply());
            }else{
                holder.reply.setText("回复：");
            }
            holder.reply.setTextSize(16);
            if(feedbackList.get(position).getReply_time() != 0){
                String codeText=sdf.format(new Date(feedbackList.get(position).getReply_time()));
                holder.reply_time.setText(codeText);
            }
            holder.reply_time.setTextSize(14);
            return convertView;
        }
    }

    @Override
    public void onLoad(final PullableListView pullableListView) {
        Map<String, String> parmas=new HashMap<String, String>();
        page++;
        parmas.put("page", String.valueOf(page));
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        OpenApiService.getInstance(FeedBackActivity.this).getFeedBackList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("userFeebacks");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            FeedBackTo feedBackTo=new FeedBackTo();
                            feedBackTo.setContent(info.getString("content"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                feedBackTo.setCreate_time(0);
                            } else {
                                feedBackTo.setCreate_time(Long.parseLong(createTime));
                            }
                            feedBackTo.setReply(info.getString("reply"));
                            String replyTime=info.getString("reply_time");
                            if(replyTime.equals("null")) {
                                feedBackTo.setCreate_time(0);
                            } else {
                                feedBackTo.setReply_time(Long.parseLong(replyTime));
                            }
                            feedbackList.add(feedBackTo);
                        }
                        if(infos.length() == 10) {
                            hasMore=true;
                        } else {
                            hasMore=false;
                        }
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                    } else if(responses.getInt("rtnCode") == 10004) {
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(FeedBackActivity.this, LoginActivity.class));
                    } else {
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(FeedBackActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FeedBackActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
