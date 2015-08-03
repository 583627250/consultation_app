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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.consultation.app.model.SpecialistTo;
import com.consultation.app.model.UserStatisticsTo;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.CommonUtil;

public class SearchSpecialistResultActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text,title_text;
    
    private ListView recommendListView;

    private MyAdapter myAdapter;
    
    private ViewHolder holder;
    
    private List<SpecialistTo> specialist_content_list=new ArrayList<SpecialistTo>();
    
    private RequestQueue mQueue;
    
    private ImageLoader mImageLoader;
    
    private String nameString;
    
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_recommend_list_search_result_layout);
        nameString = getIntent().getStringExtra("nameString");
        mContext = this;
        initDate();
        initView();
    }

    private void initDate() {
        mQueue = Volley.newRequestQueue(SearchSpecialistResultActivity.this);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("real_name", nameString);
        CommonUtil.showLoadingDialog(mContext);
        OpenApiService.getInstance(mContext).getExpertList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses = new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1){
                        JSONArray infos = responses.getJSONArray("experts");
                        specialist_content_list.clear();
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info = infos.getJSONObject(i);
                            SpecialistTo specialistTo = new SpecialistTo();
                            specialistTo.setApprove_status(info.getString("approve_status"));
                            specialistTo.setDepart_name(info.getString("depart_name"));
                            specialistTo.setGoodat_fields(info.getString("goodat_fields"));
                            specialistTo.setHospital_name(info.getString("hospital_name"));
                            specialistTo.setId(info.getString("id"));
                            specialistTo.setTitle(info.getString("title"));
                            JSONObject userToJsonObject = info.getJSONObject("user");
                            UserTo userTo = new UserTo(userToJsonObject.getString("real_name"), userToJsonObject.getString("sex"), userToJsonObject.getString("birth_year"), userToJsonObject.getString("tp"), userToJsonObject.getString("icon_url"));
                            JSONObject userStatisticsJsonObject = info.getJSONObject("userTj");
                            specialistTo.setUser(userTo);
                            UserStatisticsTo userStatistics = new UserStatisticsTo(userStatisticsJsonObject.getInt("total_consult"), 1);
                            specialistTo.setUserTj(userStatistics);
                            specialist_content_list.add(specialistTo);
                        }
                        myAdapter.notifyDataSetChanged();
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
                Toast.makeText(mContext, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("专家库");
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
                Intent intent = new Intent(SearchSpecialistResultActivity.this, SpecialistInfoActivity.class);
                intent.putExtra("id", specialist_content_list.get(position).getId());
                intent.putExtra("name", specialist_content_list.get(position).getUser().getUser_name());
                intent.putExtra("title", specialist_content_list.get(position).getTitle());
                intent.putExtra("photoUrl", specialist_content_list.get(position).getUser().getIcon_url());
                startActivity(intent);
            }
        });
    }
    
    private static class ViewHolder {

        ImageView photo;

        TextView name;

        TextView departmen;

        TextView hospital;

        TextView patients;

        TextView patientCount;
        
        TextView score;
        
        RatingBar scoreRatingBar;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return specialist_content_list.size();
        }

        @Override
        public Object getItem(int location) {
            return specialist_content_list.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(((Activity)mContext)).inflate(R.layout.specialist_info_item, null);
                holder.photo=(ImageView)convertView.findViewById(R.id.specialist_info_imageView);
                holder.name=(TextView)convertView.findViewById(R.id.specialist_info_name);
                holder.scoreRatingBar=(RatingBar)convertView.findViewById(R.id.specialist_info_score_ratingBar);
                holder.score=(TextView)convertView.findViewById(R.id.specialist_info_score);
                holder.departmen=(TextView)convertView.findViewById(R.id.specialist_info_department);
                holder.hospital=(TextView)convertView.findViewById(R.id.specialist_info_hospital);
                holder.patients=(TextView)convertView.findViewById(R.id.specialist_info_patient_text);
                holder.patientCount=(TextView)convertView.findViewById(R.id.specialist_info_patient_count_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            final String imgUrl=specialist_content_list.get(position).getUser().getIcon_url();
            holder.photo.setTag(imgUrl);
            holder.photo.setImageResource(R.drawable.photo);
            holder.name.setText(specialist_content_list.get(position).getUser().getUser_name());
            holder.name.setTextSize(18);
            holder.score.setText(specialist_content_list.get(position).getUserTj().getStar_value()+"分");
            holder.score.setTextSize(16);
            holder.scoreRatingBar.setRating((float)specialist_content_list.get(position).getUserTj().getStar_value());
            holder.departmen.setText(specialist_content_list.get(position).getDepart_name()+"|"+specialist_content_list.get(position).getTitle());
            holder.departmen.setTextSize(16);
            holder.hospital.setText(specialist_content_list.get(position).getHospital_name());
            holder.hospital.setTextSize(16);
            holder.patients.setTextSize(14);
            holder.patientCount.setText(specialist_content_list.get(position).getUserTj().getTotal_consult()+"");
            holder.patientCount.setTextSize(16);
            if(imgUrl != null && !imgUrl.equals("")) {
                ImageListener listener = ImageLoader.getImageListener(holder.photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                mImageLoader.get(imgUrl, listener);
            }
            return convertView;
        }
    }    
}
