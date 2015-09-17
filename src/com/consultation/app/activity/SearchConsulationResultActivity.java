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
import com.consultation.app.model.PatientTo;
import com.consultation.app.model.CasesTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint("SimpleDateFormat")
public class SearchConsulationResultActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text, title_text, noData;

    private ListView consulationListView;

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private ImageLoader mImageLoader;

    private List<CasesTo> consulationList=new ArrayList<CasesTo>();

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private String filterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_recommend_list_search_result_layout);
        filterString=getIntent().getStringExtra("filter");
        editor=new SharePreferencesEditor(SearchConsulationResultActivity.this);
        mQueue=Volley.newRequestQueue(SearchConsulationResultActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("filter", filterString);
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("userTp", editor.get("userType", ""));
        parmas.put("accessToken", ClientUtil.getToken());
        CommonUtil.showLoadingDialog(SearchConsulationResultActivity.this);
        OpenApiService.getInstance(SearchConsulationResultActivity.this).getPatientCaseList(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    CommonUtil.closeLodingDialog();
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONArray infos=responses.getJSONArray("pcases");
                            for(int i=0; i < infos.length(); i++) {
                                JSONObject info=infos.getJSONObject(i);
                                CasesTo pcasesTo=new CasesTo();
                                pcasesTo.setId(info.getString("id"));
                                pcasesTo.setStatus(info.getString("status"));
                                pcasesTo.setDestination(info.getString("destination"));
                                String createTime=info.getString("create_time");
                                if(createTime.equals("null")) {
                                    pcasesTo.setCreate_time(0);
                                } else {
                                    pcasesTo.setCreate_time(Long.parseLong(createTime));
                                }
                                pcasesTo.setTitle(info.getString("title"));
                                pcasesTo.setDepart_id(info.getString("depart_id"));
                                pcasesTo.setDoctor_userid(info.getString("doctor_userid"));
                                pcasesTo.setPatient_name(info.getString("patient_name"));
                                String consult_fee=info.getString("consult_fee");
                                if(consult_fee.equals("null")) {
                                    pcasesTo.setConsult_fee("0");
                                } else {
                                    pcasesTo.setConsult_fee(consult_fee);
                                }
                                pcasesTo.setDoctor_name(info.getString("doctor_name"));
                                pcasesTo.setExpert_userid(info.getString("expert_userid"));
                                pcasesTo.setExpert_name(info.getString("expert_name"));
                                pcasesTo.setProblem(info.getString("problem"));
                                pcasesTo.setConsult_tp(info.getString("consult_tp"));
                                pcasesTo.setOpinion(info.getString("opinion"));
                                PatientTo patientTo=new PatientTo();
                                JSONObject pObject=info.getJSONObject("user");
                                patientTo.setAddress(pObject.getString("address"));
                                patientTo.setId(pObject.getInt("id") + "");
                                patientTo.setState(pObject.getString("state"));
                                patientTo.setTp(pObject.getString("tp"));
                                patientTo.setUserBalance(pObject.getString("userBalance"));
                                patientTo.setMobile_ph(pObject.getString("mobile_ph"));
                                patientTo.setPwd(pObject.getString("pwd"));
                                patientTo.setReal_name(pObject.getString("real_name"));
                                patientTo.setSex(pObject.getString("sex"));
                                patientTo.setBirth_year(pObject.getString("birth_year"));
                                patientTo.setBirth_month(pObject.getString("birth_month"));
                                patientTo.setBirth_day(pObject.getString("birth_day"));
                                patientTo.setIdentity_id(pObject.getString("identity_id"));
                                patientTo.setArea_province(pObject.getString("area_province"));
                                patientTo.setArea_city(pObject.getString("area_city"));
                                patientTo.setArea_county(pObject.getString("area_county"));
                                patientTo.setIcon_url(pObject.getString("icon_url"));
                                patientTo.setModify_time(pObject.getString("modify_time"));
                                pcasesTo.setPatient(patientTo);
                                consulationList.add(pcasesTo);
                            }
                            if(consulationList.size() == 0){
                                noData = (TextView)findViewById(R.id.recommend_search_no_listView_text);
                                noData.setTextSize(18);
                                noData.setText("对不起！没有该专家");
                                noData.setVisibility(View.VISIBLE);
                            }
                        } else if(responses.getInt("rtnCode") == 10004) {
                            Toast.makeText(SearchConsulationResultActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                .show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initDate();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(SearchConsulationResultActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SearchConsulationResultActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                    Toast.makeText(SearchConsulationResultActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例讨论");
        title_text.setTextSize(20);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myAdapter=new MyAdapter();
        consulationListView=(ListView)findViewById(R.id.knowledge_recommend_list_search_result_listView);
        consulationListView.setAdapter(myAdapter);
        consulationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(SearchConsulationResultActivity.this, CaseInfoActivity.class);
                intent.putExtra("caseId", consulationList.get(position).getId());
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            consulationList.clear();
            initDate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ViewHolder {

        ImageView photo;

        TextView titleText;

        TextView doctorText;

        TextView dateText;

        TextView moneyText;

        TextView stateText;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return consulationList.size();
        }

        @Override
        public Object getItem(int location) {
            return consulationList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=
                    LayoutInflater.from(SearchConsulationResultActivity.this).inflate(R.layout.consulation_primary_list_all_item,
                        null);
                holder.photo=(ImageView)convertView.findViewById(R.id.consulation_primary_list_all_item_image);
                holder.titleText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_title);
                holder.doctorText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_doctor);
                holder.dateText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_date);
                holder.moneyText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_money);
                holder.stateText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_state);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.titleText.setText(consulationList.get(position).getTitle());
            holder.titleText.setTextSize(20);
            if(consulationList.get(position).getConsult_tp().equals("公开讨论")){
                holder.doctorText.setText(consulationList.get(position).getDoctor_name() + "(初诊)");
            }else{
                holder.doctorText.setText(consulationList.get(position).getDoctor_name() + "(初诊)|"
                        + consulationList.get(position).getExpert_name() + "(专家)");
            }
            holder.doctorText.setTextSize(16);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(consulationList.get(position).getCreate_time()));
            holder.dateText.setText(sd);
            holder.dateText.setTextSize(14);
            holder.moneyText.setText("￥" + consulationList.get(position).getConsult_fee());
            holder.moneyText.setTextSize(18);
            holder.stateText.setText(consulationList.get(position).getStatus());
            holder.stateText.setTextSize(18);
            final String imgUrl=consulationList.get(position).getPatient().getIcon_url();
            holder.photo.setTag(imgUrl);
            holder.photo.setImageResource(R.drawable.photo_patient);
            if(imgUrl != null && !imgUrl.equals("")&& !imgUrl.equals("null")) {
                ImageListener listener=ImageLoader.getImageListener(holder.photo, R.drawable.photo_patient, R.drawable.photo_patient);
                mImageLoader.get(imgUrl, listener);
            }
            return convertView;
        }
    }
}
