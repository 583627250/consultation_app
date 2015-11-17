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
public class EvaluationCaseActivity extends Activity implements OnLoadListener {

    private EditText contentEdit;

    private Button submit;

    private TextView title_text,stars_tip;

    private LinearLayout back_layout;

    private TextView back_text;

    private PullableListView listView;

    private List<DoctorCommentsTo> evaluationList=new ArrayList<DoctorCommentsTo>();

    private MyAdapter myAdapter;
    
    private LinearLayout line1,line2,bottom,stars;

    private ViewHolder holder;

    private RatingBar ratingBar;

    private SharePreferencesEditor editor;

    private int page=1;

    private boolean hasMore=true;

    private String doctorUserId,caseId;

    private RequestQueue mQueue;
    
    private ImageLoader mImageLoader;

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
        setContentView(R.layout.evaluation_case_layout);
        editor=new SharePreferencesEditor(EvaluationCaseActivity.this);
        doctorUserId=getIntent().getStringExtra("doctorUserId");
        caseId=getIntent().getStringExtra("caseId");
        mQueue=Volley.newRequestQueue(EvaluationCaseActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    private void initData() {
        evaluationList.clear();
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("doctor_userid", doctorUserId);
        CommonUtil.showLoadingDialog(EvaluationCaseActivity.this);
        OpenApiService.getInstance(EvaluationCaseActivity.this).getDoctorComment(mQueue, parmas, new Response.Listener<String>() {

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
                            evaluationList.add(commentsTo);
                        }
                        if(infos.length() == 10) {
                            listView.setHasMoreData(true);
                        } else {
                            listView.setHasMoreData(false);
                        }
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(EvaluationCaseActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(EvaluationCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例评价");
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

        ratingBar=(RatingBar)findViewById(R.id.evalution_feedback_ratingBar);
        
        stars_tip=(TextView)findViewById(R.id.case_evaluation_stars_text);
        stars_tip.setTextSize(18);

        contentEdit=(EditText)findViewById(R.id.case_evaluation_input_edit);
        contentEdit.setTextSize(18);
        contentEdit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    line1.setVisibility(View.VISIBLE);
                    stars.setVisibility(View.VISIBLE);
                } else {
                    line1.setVisibility(View.GONE);
                    stars.setVisibility(View.GONE);
                }
            }
        });

        submit=(Button)findViewById(R.id.case_evaluation_btn);
        submit.setTextSize(18);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == contentEdit.getText().toString() || "".equals(contentEdit.getText().toString())) {
                    Toast.makeText(EvaluationCaseActivity.this, "请输入评价内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((int)ratingBar.getRating() == 0) {
                    Toast.makeText(EvaluationCaseActivity.this, "请选择评价星级", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("comment_userid", editor.get("uid", ""));
                parmas.put("commenter", editor.get("real_name", "医生"));
                parmas.put("doctor_userid", doctorUserId);
                parmas.put("star_value", (int)(ratingBar.getRating())*10+"");
                parmas.put("comment_desc", contentEdit.getText().toString());
                parmas.put("case_id", caseId);
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(EvaluationCaseActivity.this);
                OpenApiService.getInstance(EvaluationCaseActivity.this).getSaveDoctorComment(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    contentEdit.setText("");
                                    ratingBar.setRating(0);
                                    line1.setVisibility(View.GONE);
                                    stars.setVisibility(View.GONE);
                                    initData();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(EvaluationCaseActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(EvaluationCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        line1=(LinearLayout)findViewById(R.id.case_evaluation_line1);
        line2=(LinearLayout)findViewById(R.id.case_evaluation_line2);
        bottom=(LinearLayout)findViewById(R.id.case_evaluation_bottom_layout);
        stars=(LinearLayout)findViewById(R.id.case_evaluation_stars_layout);
        line1.setVisibility(View.GONE);
        stars.setVisibility(View.GONE);
        if(editor.get("userType", "").equals("2")) {
            line2.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
        }

        ((PullToRefreshLayout)findViewById(R.id.case_evaluation_refresh_view)).setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("page", "1");
                parmas.put("rows", "10");
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                parmas.put("doctor_userid", doctorUserId);
                OpenApiService.getInstance(EvaluationCaseActivity.this).getDoctorComment(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    JSONArray infos=responses.getJSONArray("doctorComments");
                                    evaluationList.clear();
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
                                        evaluationList.add(commentsTo);
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
                                    Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(EvaluationCaseActivity.this, LoginActivity.class));
                                } else {
                                    Message msg=handler.obtainMessage();
                                    msg.what=2;
                                    msg.obj=pullToRefreshLayout;
                                    handler.sendMessage(msg);
                                    Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
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
                            Toast.makeText(EvaluationCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        myAdapter=new MyAdapter();
        listView=(PullableListView)findViewById(R.id.case_evaluation_listView);
        listView.setAdapter(myAdapter);
        listView.setOnLoadListener(this);
    }

    private class ViewHolder {

        ImageView photo;

        TextView name;

        TextView date;
        
        TextView message;
        
        RatingBar feedbackRatingBar;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return evaluationList.size();
        }

        @Override
        public Object getItem(int location) {
            return evaluationList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(EvaluationCaseActivity.this).inflate(R.layout.specialist_feedback_list_item, null);
                holder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_feedback_item_user_photo);
                holder.name=(TextView)convertView.findViewById(R.id.specialist_info_item_name_text);
                holder.date=(TextView)convertView.findViewById(R.id.specialist_info_item_feedbackDate_text);
                holder.feedbackRatingBar=(RatingBar)convertView.findViewById(R.id.specialist_info_user_feedback_ratingBar);
                holder.message=(TextView)convertView.findViewById(R.id.specialist_info_item_message_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            final String imgUrl=evaluationList.get(position).getPhoto_url();
            holder.photo.setTag(imgUrl);
            holder.photo.setImageResource(R.drawable.photo_primary);
            holder.name.setText(evaluationList.get(position).getCommenter());
            holder.name.setTextSize(16);
            holder.message.setText(evaluationList.get(position).getComment_desc());
            holder.message.setTextSize(17);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(evaluationList.get(position).getCreate_time()));
            holder.date.setText(sd);
            holder.date.setTextSize(14);
            holder.feedbackRatingBar.setRating(evaluationList.get(position).getStar_value());
            if(imgUrl != null && !imgUrl.equals("") && !"null".equals(imgUrl)) {
                ImageListener listener = ImageLoader.getImageListener(holder.photo, R.drawable.photo_primary, R.drawable.photo_primary);
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
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("doctor_userid", doctorUserId);
        OpenApiService.getInstance(EvaluationCaseActivity.this).getDoctorComment(mQueue, parmas, new Response.Listener<String>() {

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
                            evaluationList.add(commentsTo);
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
                        Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(EvaluationCaseActivity.this, LoginActivity.class));
                    } else {
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(EvaluationCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EvaluationCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
