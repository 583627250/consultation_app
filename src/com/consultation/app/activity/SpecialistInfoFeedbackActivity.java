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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.DoctorCommentsTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class SpecialistInfoFeedbackActivity extends Activity implements OnLoadListener {

    private LinearLayout back_layout;

    private TextView back_text,title_text;

    private PullableListView helpListView;

    private List<DoctorCommentsTo> feedbackList=new ArrayList<DoctorCommentsTo>();

    private FeedbackAdapter feedbackAdapter;

    private FeedbackHolder feedbackViewHolder;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private Context mContext;

    private int page=1;

    private boolean hasMore=true;

    private String id;
    
    private SharePreferencesEditor editor;

    private Handler handler=new Handler() {

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    feedbackAdapter.notifyDataSetChanged();
                    page=1;
                    ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case 1:
                    if(hasMore) {
                        ((PullableListView)msg.obj).finishLoading();
                    } else {
                        helpListView.setHasMoreData(false);
                    }
                    feedbackAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    helpListView.setHasMoreData(true);
                    page=1;
                    ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.FAIL);
                    break;
                case 3:
                    page=1;
                    feedbackAdapter.notifyDataSetChanged();
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mContext=this;
        editor=new SharePreferencesEditor(mContext);
        id = getIntent().getStringExtra("id");
        mQueue=Volley.newRequestQueue(SpecialistInfoFeedbackActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    private void initData() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        if(!"".equals(ClientUtil.getToken())){
            parmas.put("accessToken", ClientUtil.getToken());
            parmas.put("uid", editor.get("uid", ""));
        }
        parmas.put("doctor_userid", id);
        CommonUtil.showLoadingDialog(mContext);
        OpenApiService.getInstance(mContext).getDoctorComment(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("doctorComments");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            DoctorCommentsTo commentsTo = new DoctorCommentsTo();
                            commentsTo.setId(info.getString("id"));
                            commentsTo.setComment_desc(info.getString("comment_desc"));
                            commentsTo.setCommenter(info.getString("commenter"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                commentsTo.setCreate_time(0);
                            } else {
                                commentsTo.setCreate_time(Long.parseLong(createTime));
                            }
                            commentsTo.setStar_value(info.getInt("star_value"));
                            String photo_url = info.getJSONObject("user").getString("icon_url");
                            commentsTo.setPhoto_url(photo_url);
                            feedbackList.add(commentsTo);
                        }
                        if(infos.length() == 10) {
                            helpListView.setHasMoreData(true);
                        } else {
                            helpListView.setHasMoreData(false);
                        }
                    } else if(responses.getInt("rtnCode") == 10004){
                        Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(SpecialistInfoFeedbackActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setTextSize(20);
        title_text.setText("病例评价");
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((PullToRefreshLayout)findViewById(R.id.specialist_info_help_refresh_view)).setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("page", "1");
                parmas.put("rows", "10");
                if(!"".equals(ClientUtil.getToken())){
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                }
                parmas.put("doctor_userid", id);
                OpenApiService.getInstance(mContext).getDoctorComment(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                JSONArray infos=responses.getJSONArray("doctorComments");
                                feedbackList.clear();
                                for(int i=0; i < infos.length(); i++) {
                                    JSONObject info=infos.getJSONObject(i);
                                    DoctorCommentsTo commentsTo = new DoctorCommentsTo();
                                    commentsTo.setId(info.getString("id"));
                                    commentsTo.setComment_desc(info.getString("comment_desc"));
                                    commentsTo.setCommenter(info.getString("commenter"));
                                    String createTime=info.getString("create_time");
                                    if(createTime.equals("null")) {
                                        commentsTo.setCreate_time(0);
                                    } else {
                                        commentsTo.setCreate_time(Long.parseLong(createTime));
                                    }
                                    commentsTo.setStar_value(info.getInt("star_value"));
                                    String photo_url = info.getJSONObject("user").getString("icon_url");
                                    commentsTo.setPhoto_url(photo_url);
                                    feedbackList.add(commentsTo);
                                }
                                if(infos.length() == 10) {
                                    helpListView.setHasMoreData(true);
                                } else {
                                    helpListView.setHasMoreData(false);
                                }
                                Message msg=handler.obtainMessage();
                                msg.what=0;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                            } else if(responses.getInt("rtnCode") == 10004){
                                Message msg=handler.obtainMessage();
                                msg.what=2;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                                Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                    @Override
                                    public void onSuccess(String rspContent, int statusCode) {
                                        initData();
                                    }

                                    @Override
                                    public void onFailure(ConsultationCallbackException exp) {
                                    }
                                });
                                startActivity(new Intent(SpecialistInfoFeedbackActivity.this, LoginActivity.class));
                            } else {
                                Message msg=handler.obtainMessage();
                                msg.what=2;
                                msg.obj=pullToRefreshLayout;
                                handler.sendMessage(msg);
                                Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        feedbackAdapter=new FeedbackAdapter();
        helpListView=(PullableListView)findViewById(R.id.specialist_info_help_listView);
        helpListView.setAdapter(feedbackAdapter);
        helpListView.setOnLoadListener(this);
        helpListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

            }
        });
    }

    private static class FeedbackHolder {

        ImageView photo;

        TextView name;

        TextView date;
        
        TextView message;
        
        RatingBar feedbackRatingBar;

    }

    private class FeedbackAdapter extends BaseAdapter {

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
                feedbackViewHolder=new FeedbackHolder();
                convertView=LayoutInflater.from(mContext).inflate(R.layout.specialist_feedback_list_item, null);
                feedbackViewHolder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_feedback_item_user_photo);
                feedbackViewHolder.name=(TextView)convertView.findViewById(R.id.specialist_info_item_name_text);
                feedbackViewHolder.date=(TextView)convertView.findViewById(R.id.specialist_info_item_feedbackDate_text);
                feedbackViewHolder.feedbackRatingBar=(RatingBar)convertView.findViewById(R.id.specialist_info_user_feedback_ratingBar);
                feedbackViewHolder.message=(TextView)convertView.findViewById(R.id.specialist_info_item_message_text);
                convertView.setTag(feedbackViewHolder);
            } else {
                feedbackViewHolder=(FeedbackHolder)convertView.getTag();
            }
            final String imgUrl=feedbackList.get(position).getPhoto_url();
            feedbackViewHolder.photo.setTag(imgUrl);
            feedbackViewHolder.photo.setImageResource(R.drawable.photo_patient);
            feedbackViewHolder.name.setText(feedbackList.get(position).getCommenter());
            feedbackViewHolder.name.setTextSize(16);
            feedbackViewHolder.message.setText(feedbackList.get(position).getComment_desc());
            feedbackViewHolder.message.setTextSize(17);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(feedbackList.get(position).getCreate_time()));
            feedbackViewHolder.date.setText(sd);
            feedbackViewHolder.date.setTextSize(14);
            feedbackViewHolder.feedbackRatingBar.setRating((float)feedbackList.get(position).getStar_value()/10);
            if(imgUrl != null && !imgUrl.equals("") && !"null".equals(imgUrl)) {
                ImageListener listener = ImageLoader.getImageListener(feedbackViewHolder.photo, R.drawable.photo_patient, R.drawable.photo_patient);
                mImageLoader.get(imgUrl, listener);
            }
            return convertView;
        }
    }

    @Override
    public void onLoad(final PullableListView pullableListView) {
        Map<String, String> parmas=new HashMap<String, String>();
        page++;
        parmas.put("page", String.valueOf(page));
        parmas.put("rows", "10");
        if(!"".equals(ClientUtil.getToken())){
            parmas.put("accessToken", ClientUtil.getToken());
            parmas.put("uid", editor.get("uid", ""));
        }
        parmas.put("doctor_userid", id);
        OpenApiService.getInstance(mContext).getDoctorComment(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("doctorComments");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            DoctorCommentsTo commentsTo = new DoctorCommentsTo();
                            commentsTo.setId(info.getString("id"));
                            commentsTo.setComment_desc(info.getString("comment_desc"));
                            commentsTo.setCommenter(info.getString("commenter"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                commentsTo.setCreate_time(0);
                            } else {
                                commentsTo.setCreate_time(Long.parseLong(createTime));
                            }
                            commentsTo.setStar_value(info.getInt("star_value"));
                            String photo_url = info.getJSONObject("user").getString("icon_url");
                            commentsTo.setPhoto_url(photo_url);
                            feedbackList.add(commentsTo);
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
                    } else if(responses.getInt("rtnCode") == 10004){
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(SpecialistInfoFeedbackActivity.this, LoginActivity.class));
                    } else {
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(mContext, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
