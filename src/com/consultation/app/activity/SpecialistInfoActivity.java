package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.consultation.app.model.DoctorTo;
import com.consultation.app.model.UserStatisticsTo;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.view.CircularImage;


public class SpecialistInfoActivity extends Activity {

    private LinearLayout back_layout;
    
    private TextView back_text;
    
    private TextView nameText,titleText,goodAt,helpText,helpCountText,feedbackScoreText,feedbackScoreCountText;
    
    private RatingBar feedbackRatingBar;
    
    private LinearLayout helpLayout,feedbackLayout;
    
    private CircularImage photo;
    
    private ListView helpListView,feedbackListView;
    
    private List<String> helpList = new ArrayList<String>();
    
    private List<String> feedbackList = new ArrayList<String>();
    
    private HelpAdapter helpAdapter;
    
    private FeedbackAdapter feedbackAdapter;
    
    private HelpViewHolder helpViewHolder;
    
    private FeedbackHolder feedbackViewHolder;
    
    private RequestQueue mQueue;
    
    private ImageLoader mImageLoader;
    
    private String id;//根据id获取数据
    
    private String name,title,photoUrl;
    
    private Context mContext;
    
    private DoctorTo doctorTo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialist_info_layout);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        title = getIntent().getStringExtra("title");
        photoUrl = getIntent().getStringExtra("photoUrl");
        mContext = this;
        initData();
        initView();
    }

    private void initData() {
        mQueue = Volley.newRequestQueue(SpecialistInfoActivity.this);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("id", id);
        CommonUtil.showLoadingDialog(mContext);
        OpenApiService.getInstance(mContext).getExpertInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses = new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1){
                        JSONObject infos = responses.getJSONObject("doctor");
                        doctorTo = new DoctorTo();
                        doctorTo.setId(infos.getString("id"));
                        doctorTo.setHospital_name(infos.getString("hospital_name"));
                        doctorTo.setDepart_name(infos.getString("depart_name"));
                        doctorTo.setTitle(infos.getString("title"));
                        doctorTo.setGoodat_fields(infos.getString("goodat_fields"));
                        UserTo userTo = new UserTo();
                        JSONObject userJsonObject = infos.getJSONObject("user");
                        userTo.setUser_name(userJsonObject.getString("real_name"));
                        userTo.setSex(userJsonObject.getString("sex"));
                        userTo.setIcon_url(userJsonObject.getString("icon_url"));
                        doctorTo.setUser(userTo);
                        UserStatisticsTo userStatisticsTo = new UserStatisticsTo();
                        JSONObject userStatisticsJsonObject = infos.getJSONObject("userTj");
                        userStatisticsTo.setTotal_consult(userStatisticsJsonObject.getInt("total_consult"));
                        userStatisticsTo.setStar_value(userStatisticsJsonObject.getInt("star_value"));
                        userStatisticsTo.setTotal_comment(userStatisticsJsonObject.getInt("total_apply"));
                        doctorTo.setUserTj(userStatisticsTo);
                        
//                        doctorTo.setCases(infos.getString("approve_status"));
//                        doctorTo.setComments(infos.getString("approve_status"));
                        
                        
                        setListViewHeightBasedOnChildren(helpListView);
                        setListViewHeightBasedOnChildren(feedbackListView);
                    }else{
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
                Toast.makeText(mContext, arg0.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        helpList.add("");
        helpList.add("");
        feedbackList.add("");
        feedbackList.add("");
        feedbackList.add("");
    }
    
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight=0;
        for(int i=0; i < listAdapter.getCount(); i++) {
            View listItem=listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height=totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void initView() {
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
        
        photo = (CircularImage)findViewById(R.id.specialist_info_user_photo);
//        if(photoUrl != null && !photoUrl.equals("")) {
//            ImageListener listener = ImageLoader.getImageListener(photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
//            mImageLoader.get(photoUrl, listener);
//        }
        photo.setImageResource(R.drawable.photo);
        
        nameText = (TextView)findViewById(R.id.specialist_info_user_name_text);
        nameText.setTextSize(22);
        nameText.setText(name);
        
        titleText = (TextView)findViewById(R.id.specialist_info_user_title_text);
        titleText.setTextSize(14);
        titleText.setText(title);
        
        goodAt = (TextView)findViewById(R.id.specialist_info_user_goodAt);
        goodAt.setTextSize(18);
       
        helpText = (TextView)findViewById(R.id.specialist_info_user_help_text);
        helpText.setTextSize(18);
        
        helpCountText = (TextView)findViewById(R.id.specialist_info_user_help_count_text);
        helpCountText.setTextSize(18);
        
        feedbackScoreText = (TextView)findViewById(R.id.specialist_info_user_evaluation_score);
        feedbackScoreText.setTextSize(18);
        
        feedbackScoreCountText = (TextView)findViewById(R.id.specialist_info_user_evaluation_count_text);
        feedbackScoreCountText.setTextSize(18);
        
        feedbackRatingBar = (RatingBar)findViewById(R.id.specialist_info_user_ratingBar);
        feedbackRatingBar.setRating(4.5f);
        
        
        helpLayout = (LinearLayout)findViewById(R.id.specialist_info_user_help_layout);
        feedbackLayout = (LinearLayout)findViewById(R.id.specialist_info_user_evaluation_layout);
        
        helpLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
            }
        });
        feedbackLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
            }
        });
        
        helpAdapter = new HelpAdapter();
        helpListView = (ListView)findViewById(R.id.specialist_info_user_help_listView);
        helpListView.setAdapter(helpAdapter);
        helpListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//                Intent intent = new Intent(context, RecommendActivity.class);
//                startActivity(intent);
            }
        });
        setListViewHeightBasedOnChildren(helpListView);
        
        feedbackAdapter = new FeedbackAdapter();
        feedbackListView = (ListView)findViewById(R.id.specialist_info_user_evaluation_listView);
        feedbackListView.setAdapter(feedbackAdapter);
        feedbackListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//                Intent intent = new Intent(context, RecommendActivity.class);
//                startActivity(intent);
            }
        });
        setListViewHeightBasedOnChildren(feedbackListView);
    }
    
    private static class HelpViewHolder {

        ImageView photo;

        TextView titles;

        TextView nameDate;
        
        ImageView state;
        
    }
    
    private static class FeedbackHolder {

        ImageView photo;

        TextView name;

        TextView date;
        
        TextView message;
        
        RatingBar feedbackRatingBar;

    }

    private class HelpAdapter extends BaseAdapter {

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
                helpViewHolder=new HelpViewHolder();
                convertView=LayoutInflater.from(SpecialistInfoActivity.this).inflate(R.layout.specialist_help_list_item, null);
                helpViewHolder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_item_user_photo);
                helpViewHolder.titles=(TextView)convertView.findViewById(R.id.specialist_info_item_titles_text);
                helpViewHolder.nameDate = (TextView)convertView.findViewById(R.id.specialist_info_item_nameAndDate_text);
                helpViewHolder.state=(ImageView)convertView.findViewById(R.id.specialist_info_item_state_imageView);
                convertView.setTag(helpViewHolder);
            } else {
                helpViewHolder=(HelpViewHolder)convertView.getTag();
            }
            final String imgUrl="";
            helpViewHolder.photo.setTag(imgUrl);
            helpViewHolder.photo.setImageResource(R.drawable.photo);
//            helpViewHolder.titles.setText();
            helpViewHolder.titles.setTextSize(18);
//            helpViewHolder.nameDate.setText();
            helpViewHolder.nameDate.setTextSize(14);
            helpViewHolder.state.setImageResource(R.drawable.specialist_help_complete);
//            if(imgUrl != null && !imgUrl.equals("")) {
//                ImageListener listener = ImageLoader.getImageListener(holder.photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
//                mImageLoader.get(imgUrl, listener);
//            }
            return convertView;
        }
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
                convertView=LayoutInflater.from(SpecialistInfoActivity.this).inflate(R.layout.specialist_feedback_list_item, null);
                feedbackViewHolder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_feedback_item_user_photo);
                feedbackViewHolder.name=(TextView)convertView.findViewById(R.id.specialist_info_item_name_text);
                feedbackViewHolder.date=(TextView)convertView.findViewById(R.id.specialist_info_item_feedbackDate_text);
                feedbackViewHolder.feedbackRatingBar=(RatingBar)convertView.findViewById(R.id.specialist_info_user_feedback_ratingBar);
                feedbackViewHolder.message=(TextView)convertView.findViewById(R.id.specialist_info_item_message_text);
                convertView.setTag(feedbackViewHolder);
            } else {
                feedbackViewHolder=(FeedbackHolder)convertView.getTag();
            }
            final String imgUrl="";
            feedbackViewHolder.photo.setTag(imgUrl);
            feedbackViewHolder.photo.setImageResource(R.drawable.photo);
//            feedbackViewHolder.name.setText("");
            feedbackViewHolder.name.setTextSize(16);
//            feedbackViewHolder.message.setText("");
            feedbackViewHolder.message.setTextSize(17);
//            feedbackViewHolder.date.setText("");
            feedbackViewHolder.date.setTextSize(14);
            feedbackViewHolder.feedbackRatingBar.setRating(4.5f);
            
//            if(imgUrl != null && !imgUrl.equals("")) {
//                ImageListener listener = ImageLoader.getImageListener(holder.photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
//                mImageLoader.get(imgUrl, listener);
//            }
            return convertView;
        }
    }    
}
