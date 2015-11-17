package com.consultation.app.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import com.consultation.app.model.DoctorTo;
import com.consultation.app.model.HelpPatientTo;
import com.consultation.app.model.UserStatisticsTo;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.view.CircularImage;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class SpecialistInfoActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text;

    private TextView nameText, titleText, goodAt, goodAtText, helpText, helpCountText, feedbackScoreText, feedbackScoreCountText;

    private RatingBar feedbackRatingBar;

    private LinearLayout helpLayout, feedbackLayout, helpListLayout, feedbackListLayout;

    private CircularImage photo;

    // private ListView helpListView,feedbackListView;

    // private HelpAdapter helpAdapter;

    // private FeedbackAdapter feedbackAdapter;

    // private HelpViewHolder helpViewHolder;

    // private FeedbackHolder feedbackViewHolder;

    private ArrayList<DoctorCommentsTo> commentsTos=new ArrayList<DoctorCommentsTo>();

    private ArrayList<HelpPatientTo> helpPatientTos=new ArrayList<HelpPatientTo>();

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private String id;// 根据id获取数据

    private String name, title, photoUrl;

    private Context mContext;

    private DoctorTo doctorTo;

    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialist_info_layout);
        id=getIntent().getStringExtra("id");
        name=getIntent().getStringExtra("name");
        title=getIntent().getStringExtra("title");
        photoUrl=getIntent().getStringExtra("photoUrl");
        mContext=this;
        mQueue=Volley.newRequestQueue(SpecialistInfoActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    private void initData() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("id", id);
        CommonUtil.showLoadingDialog(mContext);
        OpenApiService.getInstance(mContext).getExpertInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONObject infos=responses.getJSONObject("doctor");
                        doctorTo=new DoctorTo();
                        doctorTo.setId(infos.getString("id"));
                        doctorTo.setHospital_name(infos.getString("hospital_name"));
                        doctorTo.setDepart_name(infos.getString("depart_name"));
                        doctorTo.setTitle(infos.getString("title"));
                        doctorTo.setGoodat_fields(infos.getString("goodat_fields"));
                        doctorTo.setApprove_status(infos.getString("approve_status"));
                        doctorTo.setExpert_gradeid(infos.getString("expert_gradeid"));
                        UserTo userTo=new UserTo();
                        JSONObject userJsonObject=infos.getJSONObject("user");
                        userTo.setUser_name(userJsonObject.getString("real_name"));
                        userTo.setUser_id(infos.getInt("user_id"));
                        userTo.setSex(userJsonObject.getString("sex"));
                        userTo.setIcon_url(userJsonObject.getString("icon_url"));
                        doctorTo.setUser(userTo);
                        UserStatisticsTo userStatisticsTo=new UserStatisticsTo();
                        JSONObject userStatisticsJsonObject=infos.getJSONObject("userTj");
                        userStatisticsTo.setTotal_consult(userStatisticsJsonObject.getInt("total_consult"));
                        userStatisticsTo.setStar_value(userStatisticsJsonObject.getInt("star_value"));
                        userStatisticsTo.setTotal_comment(userStatisticsJsonObject.getInt("total_comment"));
                        doctorTo.setUserTj(userStatisticsTo);
                        JSONArray helps=responses.getJSONArray("cases");
                        for(int i=0; i < helps.length(); i++) {
                            JSONObject info=helps.getJSONObject(i);
                            HelpPatientTo helpPatientTo=new HelpPatientTo();
                            helpPatientTo.setId(info.getString("id"));
                            helpPatientTo.setPatient_name(info.getString("patient_name"));
                            helpPatientTo.setStatus(info.getString("status"));
                            helpPatientTo.setTitle(info.getString("title"));
                            String time=info.getString("create_time");
                            if("".equals(time) || "null".equals(time)) {
                                helpPatientTo.setCreate_time(0l);
                            } else {
                                helpPatientTo.setCreate_time(Long.parseLong(time));
                            }
                            String photo_url=info.getJSONObject("user").getString("icon_url");
                            helpPatientTo.setPhoto_url(photo_url);
                            helpPatientTos.add(helpPatientTo);
                        }
                        doctorTo.setHelpPatientTos(helpPatientTos);
                        JSONArray comments=responses.getJSONArray("comments");
                        for(int i=0; i < comments.length(); i++) {
                            JSONObject info=comments.getJSONObject(i);
                            DoctorCommentsTo commentsTo=new DoctorCommentsTo();
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
                            String photo_url=info.getJSONObject("user").getString("icon_url");
                            commentsTo.setPhoto_url(photo_url);
                            commentsTos.add(commentsTo);
                        }
                        // setListViewHeightBasedOnChildren(helpListView);
                        // setListViewHeightBasedOnChildren(feedbackListView);
                        handler.sendEmptyMessage(0);
                    } else if(responses.getInt("rtnCode") == 10004) {
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
                        startActivity(new Intent(SpecialistInfoActivity.this, LoginActivity.class));
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

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
            goodAtText.setText(doctorTo.getGoodat_fields());
            helpCountText.setText(doctorTo.getUserTj().getTotal_consult() + "名");
            feedbackScoreText.setText((float)doctorTo.getUserTj().getStar_value() / 10 + "分");
            feedbackRatingBar.setRating((float)doctorTo.getUserTj().getStar_value() / 10);
            feedbackScoreCountText.setText(doctorTo.getUserTj().getTotal_comment() + "条评论");
            for(int i=0; i < helpPatientTos.size(); i++) {
                View convertView=LayoutInflater.from(SpecialistInfoActivity.this).inflate(R.layout.specialist_help_list_item, null);
                ImageView photo=(ImageView)convertView.findViewById(R.id.specialist_info_item_user_photo);
                TextView titles=(TextView)convertView.findViewById(R.id.specialist_info_item_titles_text);
                TextView nameDate=(TextView)convertView.findViewById(R.id.specialist_info_item_nameAndDate_text);
                ImageView state=(ImageView)convertView.findViewById(R.id.specialist_info_item_state_imageView);
                LinearLayout line=(LinearLayout)convertView.findViewById(R.id.specialist_info_item_line);
                if(i == helpPatientTos.size() - 1) {
                    line.setVisibility(View.GONE);
                }else{
                    line.setVisibility(View.VISIBLE);
                }
                final String imgUrl=helpPatientTos.get(i).getPhoto_url();
                photo.setTag(imgUrl);
                photo.setImageResource(R.drawable.photo_patient);
                titles.setText(helpPatientTos.get(i).getTitle());
                titles.setTextSize(18);
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                String sd=sdf.format(new Date(helpPatientTos.get(i).getCreate_time()));
                nameDate.setText(helpPatientTos.get(i).getPatient_name() + "  " + sd);
                nameDate.setTextSize(14);
                state.setImageResource(R.drawable.specialist_help_complete);
                if(imgUrl != null && !imgUrl.equals("") && !"null".equals(imgUrl)) {
                    ImageListener listener=ImageLoader.getImageListener(photo, R.drawable.photo_patient, R.drawable.photo_patient);
                    mImageLoader.get(imgUrl, listener);
                }
                helpListLayout.addView(convertView);
            }
            for(int i=0; i < helpPatientTos.size(); i++) {
                View convertView=
                    LayoutInflater.from(SpecialistInfoActivity.this).inflate(R.layout.specialist_feedback_list_item, null);
                ImageView photo=(ImageView)convertView.findViewById(R.id.specialist_info_feedback_item_user_photo);
                TextView name=(TextView)convertView.findViewById(R.id.specialist_info_item_name_text);
                TextView date=(TextView)convertView.findViewById(R.id.specialist_info_item_feedbackDate_text);
                RatingBar feedbackRatingBar=(RatingBar)convertView.findViewById(R.id.specialist_info_user_feedback_ratingBar);
                TextView message=(TextView)convertView.findViewById(R.id.specialist_info_item_message_text);
                LinearLayout line=(LinearLayout)convertView.findViewById(R.id.specialist_feedback_item_line);
                if(i == commentsTos.size() - 1) {
                    line.setVisibility(View.GONE);
                }else{
                    line.setVisibility(View.VISIBLE);
                }
                final String imgUrl=commentsTos.get(i).getPhoto_url();
                photo.setTag(imgUrl);
                photo.setImageResource(R.drawable.photo_patient);
                name.setText(commentsTos.get(i).getCommenter());
                name.setTextSize(16);
                message.setText(commentsTos.get(i).getComment_desc());
                message.setTextSize(17);
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                String sd=sdf.format(new Date(commentsTos.get(i).getCreate_time()));
                date.setText(sd);
                date.setTextSize(14);
                feedbackRatingBar.setRating((float)commentsTos.get(i).getStar_value() / 10);
                if(imgUrl != null && !imgUrl.equals("") && !"null".equals(imgUrl)) {
                    ImageListener listener=ImageLoader.getImageListener(photo, R.drawable.photo_patient, R.drawable.photo_patient);
                    mImageLoader.get(imgUrl, listener);
                }
                feedbackListLayout.addView(convertView);
            }
        }
    };

    // private void setListViewHeightBasedOnChildren(ListView listView) {
    // ListAdapter listAdapter=listView.getAdapter();
    // if(listAdapter == null) {
    // return;
    // }
    // int totalHeight=0;
    // for(int i=0; i < listAdapter.getCount(); i++) {
    // View listItem=listAdapter.getView(i, null, listView);
    // listItem.measure(0, 0);
    // totalHeight+=listItem.getMeasuredHeight();
    // }
    // ViewGroup.LayoutParams params=listView.getLayoutParams();
    // params.height=totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    // listView.setLayoutParams(params);
    // }

    private void initView() {
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
        scrollView=(ScrollView)findViewById(R.id.specialist_info_scrollView);
        photo=(CircularImage)findViewById(R.id.specialist_info_user_photo);
        photo.setImageResource(R.drawable.photo_expert);
        if(photoUrl != null && !photoUrl.equals("") && !"null".equals(photoUrl)) {
            ImageListener listener=ImageLoader.getImageListener(photo, R.drawable.photo_expert, R.drawable.photo_expert);
            mImageLoader.get(photoUrl, listener);
        }

        nameText=(TextView)findViewById(R.id.specialist_info_user_name_text);
        nameText.setTextSize(22);
        nameText.setText(name);

        titleText=(TextView)findViewById(R.id.specialist_info_user_title_text);
        titleText.setTextSize(14);
        titleText.setText(title);

        goodAt=(TextView)findViewById(R.id.specialist_info_user_goodAt);
        goodAt.setTextSize(18);

        goodAtText=(TextView)findViewById(R.id.specialist_info_user_goodAt_text);
        goodAtText.setTextSize(16);

        helpText=(TextView)findViewById(R.id.specialist_info_user_help_text);
        helpText.setTextSize(18);

        helpCountText=(TextView)findViewById(R.id.specialist_info_user_help_count_text);
        helpCountText.setTextSize(18);

        feedbackScoreText=(TextView)findViewById(R.id.specialist_info_user_evaluation_score);
        feedbackScoreText.setTextSize(18);

        feedbackScoreCountText=(TextView)findViewById(R.id.specialist_info_user_evaluation_count_text);
        feedbackScoreCountText.setTextSize(18);

        feedbackRatingBar=(RatingBar)findViewById(R.id.specialist_info_user_ratingBar);
        feedbackRatingBar.setRating(0f);

        helpLayout=(LinearLayout)findViewById(R.id.specialist_info_user_help_layout);
        feedbackLayout=(LinearLayout)findViewById(R.id.specialist_info_user_evaluation_layout);

        helpLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SpecialistInfoHelpActivity.class);
                intent.putExtra("id", doctorTo.getUser().getUser_id() + "");
                startActivity(intent);
            }
        });
        feedbackLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SpecialistInfoFeedbackActivity.class);
                intent.putExtra("id", doctorTo.getUser().getUser_id() + "");
                startActivity(intent);
            }
        });

        helpListLayout=(LinearLayout)findViewById(R.id.specialist_info_user_help_list_layout);

        feedbackListLayout=(LinearLayout)findViewById(R.id.specialist_info_user_evaluation_list_layout);

        // helpAdapter = new HelpAdapter();
        // helpListView = (ListView)findViewById(R.id.specialist_info_user_help_listView);
        // helpListView.setAdapter(helpAdapter);
        // helpListView.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        // }
        // });
        // setListViewHeightBasedOnChildren(helpListView);
        //
        // feedbackAdapter = new FeedbackAdapter();
        // feedbackListView = (ListView)findViewById(R.id.specialist_info_user_evaluation_listView);
        // feedbackListView.setAdapter(feedbackAdapter);
        // feedbackListView.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        // }
        // });
        // setListViewHeightBasedOnChildren(feedbackListView);
    }

    // private static class HelpViewHolder {
    //
    // ImageView photo;
    //
    // TextView titles;
    //
    // TextView nameDate;
    //
    // ImageView state;
    //
    // }
    //
    // private static class FeedbackHolder {
    //
    // ImageView photo;
    //
    // TextView name;
    //
    // TextView date;
    //
    // TextView message;
    //
    // RatingBar feedbackRatingBar;
    //
    // }
    //
    // private class HelpAdapter extends BaseAdapter {
    //
    // @Override
    // public int getCount() {
    // return helpPatientTos.size();
    // }
    //
    // @Override
    // public Object getItem(int location) {
    // return helpPatientTos.get(location);
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return position;
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // if(convertView == null) {
    // helpViewHolder=new HelpViewHolder();
    // convertView=LayoutInflater.from(SpecialistInfoActivity.this).inflate(R.layout.specialist_help_list_item, null);
    // helpViewHolder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_item_user_photo);
    // helpViewHolder.titles=(TextView)convertView.findViewById(R.id.specialist_info_item_titles_text);
    // helpViewHolder.nameDate = (TextView)convertView.findViewById(R.id.specialist_info_item_nameAndDate_text);
    // helpViewHolder.state=(ImageView)convertView.findViewById(R.id.specialist_info_item_state_imageView);
    // convertView.setTag(helpViewHolder);
    // } else {
    // helpViewHolder=(HelpViewHolder)convertView.getTag();
    // }
    // final String imgUrl=helpPatientTos.get(position).getPhoto_url();
    // helpViewHolder.photo.setTag(imgUrl);
    // helpViewHolder.photo.setImageResource(R.drawable.photo_patient);
    // helpViewHolder.titles.setText(helpPatientTos.get(position).getTitle());
    // helpViewHolder.titles.setTextSize(18);
    // SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    // String sd=sdf.format(new Date(helpPatientTos.get(position).getCreate_time()));
    // helpViewHolder.nameDate.setText(helpPatientTos.get(position).getPatient_name() + "  " + sd);
    // helpViewHolder.nameDate.setTextSize(14);
    // helpViewHolder.state.setImageResource(R.drawable.specialist_help_complete);
    // if(imgUrl != null && !imgUrl.equals("") && !"null".equals(imgUrl)) {
    // ImageListener listener = ImageLoader.getImageListener(helpViewHolder.photo, R.drawable.photo_patient,
    // R.drawable.photo_patient);
    // mImageLoader.get(imgUrl, listener);
    // }
    // return convertView;
    // }
    // }
    //
    // private class FeedbackAdapter extends BaseAdapter {
    //
    // @Override
    // public int getCount() {
    // return commentsTos.size();
    // }
    //
    // @Override
    // public Object getItem(int location) {
    // return commentsTos.get(location);
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return position;
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // if(convertView == null) {
    // feedbackViewHolder=new FeedbackHolder();
    // convertView=LayoutInflater.from(SpecialistInfoActivity.this).inflate(R.layout.specialist_feedback_list_item, null);
    // feedbackViewHolder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_feedback_item_user_photo);
    // feedbackViewHolder.name=(TextView)convertView.findViewById(R.id.specialist_info_item_name_text);
    // feedbackViewHolder.date=(TextView)convertView.findViewById(R.id.specialist_info_item_feedbackDate_text);
    // feedbackViewHolder.feedbackRatingBar=(RatingBar)convertView.findViewById(R.id.specialist_info_user_feedback_ratingBar);
    // feedbackViewHolder.message=(TextView)convertView.findViewById(R.id.specialist_info_item_message_text);
    // convertView.setTag(feedbackViewHolder);
    // } else {
    // feedbackViewHolder=(FeedbackHolder)convertView.getTag();
    // }
    // final String imgUrl=commentsTos.get(position).getPhoto_url();
    // feedbackViewHolder.photo.setTag(imgUrl);
    // feedbackViewHolder.photo.setImageResource(R.drawable.photo_patient);
    // feedbackViewHolder.name.setText(commentsTos.get(position).getCommenter());
    // feedbackViewHolder.name.setTextSize(16);
    // feedbackViewHolder.message.setText(commentsTos.get(position).getComment_desc());
    // feedbackViewHolder.message.setTextSize(17);
    // SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    // String sd=sdf.format(new Date(commentsTos.get(position).getCreate_time()));
    // feedbackViewHolder.date.setText(sd);
    // feedbackViewHolder.date.setTextSize(14);
    // feedbackViewHolder.feedbackRatingBar.setRating((float)commentsTos.get(position).getStar_value()/10);
    // if(imgUrl != null && !imgUrl.equals("") && !"null".equals(imgUrl)) {
    // ImageListener listener = ImageLoader.getImageListener(feedbackViewHolder.photo, R.drawable.photo_patient,
    // R.drawable.photo_patient);
    // mImageLoader.get(imgUrl, listener);
    // }
    // return convertView;
    // }
    // }
}
