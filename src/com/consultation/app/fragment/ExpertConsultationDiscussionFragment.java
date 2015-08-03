package com.consultation.app.fragment;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.model.PatientTo;
import com.consultation.app.model.PcasesTo;
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
public class ExpertConsultationDiscussionFragment extends Fragment implements OnLoadListener {

    private View expertConsultationDiscussionFragment;

    private PullableListView patientListView;

    private List<PcasesTo> patientList=new ArrayList<PcasesTo>();

    private MyAdapter myAdapter;

    private PatientViewHolder holder;

    private SharePreferencesEditor editor;

    private int page=1;

    private boolean hasMore=true;

    private RequestQueue mQueue;
    
    private ImageLoader mImageLoader;

    private Handler handler=new Handler() {

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    myAdapter.notifyDataSetChanged();
                    patientListView.setHasMoreData(true);
                    page=1;
                    ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.SUCCEED);
                    break;
                case 1:
                    if(hasMore) {
                        ((PullableListView)msg.obj).finishLoading();
                    } else {
                        patientListView.setHasMoreData(false);
                    }
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    patientListView.setHasMoreData(true);
                    page=1;
                    ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.FAIL);
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        expertConsultationDiscussionFragment=inflater.inflate(R.layout.consulation_list_all_layout, container, false);
        editor=new SharePreferencesEditor(expertConsultationDiscussionFragment.getContext());
        initData();
        initLayout();
        return expertConsultationDiscussionFragment;
    }

    private void initData() {
        mQueue=Volley.newRequestQueue(expertConsultationDiscussionFragment.getContext());
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("userTp", editor.get("userType", ""));
        CommonUtil.showLoadingDialog(expertConsultationDiscussionFragment.getContext());
        OpenApiService.getInstance(expertConsultationDiscussionFragment.getContext()).getPatientCaseList(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    CommonUtil.closeLodingDialog();
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONArray infos=responses.getJSONArray("pcases");
                            patientList.clear();
                            for(int i=0; i < infos.length(); i++) {
                                JSONObject info=infos.getJSONObject(i);
                                PcasesTo pcasesTo=new PcasesTo();
                                pcasesTo.setId(info.getString("id"));
                                pcasesTo.setContent(info.getString("content"));
                                pcasesTo.setStatus(info.getString("status"));
                                pcasesTo.setDestination(info.getString("destination"));
                                pcasesTo.setCreate_time(info.getLong("create_time"));
                                pcasesTo.setTitle(info.getString("title"));
                                pcasesTo.setDepart_id(info.getString("depart_id"));
                                pcasesTo.setDoctor_userid(info.getString("doctor_userid"));
                                pcasesTo.setPatient_userid(info.getString("patient_userid"));
                                pcasesTo.setConsult_fee(info.getInt("consult_fee"));
                                pcasesTo.setDoctor_name(info.getString("doctor_name"));
                                pcasesTo.setExpert_userid(info.getString("expert_userid"));
                                pcasesTo.setExpert_name(info.getString("expert_name"));
                                pcasesTo.setProblem(info.getString("problem"));
                                pcasesTo.setConsult_tp(info.getString("consult_tp"));
                                pcasesTo.setOpinion(info.getString("opinion"));
                                pcasesTo.setUid(info.getString("uid"));
                                PatientTo patientTo=new PatientTo();
                                JSONObject pObject=info.getJSONObject("user");
                                patientTo.setAddress(pObject.getString("address"));
                                patientTo.setId(pObject.getInt("id") + "");
                                patientTo.setState(pObject.getString("state"));
                                // patientTo.setCreate_time(pObject.getLong("create_time"));
                                patientTo.setTp(pObject.getString("tp"));
                                patientTo.setDoctor(pObject.getString("doctor"));
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
                                patientTo.setUid(pObject.getString("uid"));
                                pcasesTo.setPatient(patientTo);
                                patientList.add(pcasesTo);
                            }
                            if(infos.length() == 10) {
                                patientListView.setHasMoreData(true);
                            } else {
                                patientListView.setHasMoreData(false);
                            }
                        } else {
                            Toast.makeText(expertConsultationDiscussionFragment.getContext(), responses.getString("rtnMsg"),
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    CommonUtil.closeLodingDialog();
                    Toast.makeText(expertConsultationDiscussionFragment.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void initLayout() {
        ((PullToRefreshLayout)expertConsultationDiscussionFragment.findViewById(R.id.consulation_list_all_refresh_view))
            .setOnRefreshListener(new OnRefreshListener() {

                @Override
                public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("page", "1");
                    parmas.put("rows", "10");
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    parmas.put("userTp", editor.get("userType", ""));
                    OpenApiService.getInstance(expertConsultationDiscussionFragment.getContext()).getPatientCaseList(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        JSONArray infos=responses.getJSONArray("pcases");
                                        patientList.clear();
                                        for(int i=0; i < infos.length(); i++) {
                                            JSONObject info=infos.getJSONObject(i);
                                            PcasesTo pcasesTo=new PcasesTo();
                                            pcasesTo.setId(info.getString("id"));
                                            pcasesTo.setContent(info.getString("content"));
                                            pcasesTo.setStatus(info.getString("status"));
                                            pcasesTo.setDestination(info.getString("destination"));
                                            pcasesTo.setCreate_time(info.getLong("create_time"));
                                            pcasesTo.setTitle(info.getString("title"));
                                            pcasesTo.setDepart_id(info.getString("depart_id"));
                                            pcasesTo.setDoctor_userid(info.getString("doctor_userid"));
                                            pcasesTo.setConsult_fee(info.getInt("consult_fee"));
                                            pcasesTo.setPatient_userid(info.getString("patient_userid"));
                                            pcasesTo.setDoctor_name(info.getString("doctor_name"));
                                            pcasesTo.setExpert_userid(info.getString("expert_userid"));
                                            pcasesTo.setExpert_name(info.getString("expert_name"));
                                            pcasesTo.setProblem(info.getString("problem"));
                                            pcasesTo.setConsult_tp(info.getString("consult_tp"));
                                            pcasesTo.setOpinion(info.getString("opinion"));
                                            pcasesTo.setUid(info.getString("uid"));
                                            PatientTo patientTo=new PatientTo();
                                            JSONObject pObject=info.getJSONObject("user");
                                            patientTo.setAddress(pObject.getString("address"));
                                            patientTo.setId(pObject.getInt("id") + "");
                                            patientTo.setState(pObject.getString("state"));
                                            // patientTo.setCreate_time(pObject.getLong("create_time"));
                                            patientTo.setTp(pObject.getString("tp"));
                                            patientTo.setDoctor(pObject.getString("doctor"));
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
                                            patientTo.setUid(pObject.getString("uid"));
                                            pcasesTo.setPatient(patientTo);
                                            patientList.add(pcasesTo);
                                        }
                                        Message msg=handler.obtainMessage();
                                        msg.what=0;
                                        msg.obj=pullToRefreshLayout;
                                        handler.sendMessage(msg);
                                    } else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=2;
                                        msg.obj=pullToRefreshLayout;
                                        handler.sendMessage(msg);
                                        Toast.makeText(expertConsultationDiscussionFragment.getContext(), responses.getString("rtnMsg"),
                                            Toast.LENGTH_SHORT).show();
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError arg0) {
                                Toast.makeText(expertConsultationDiscussionFragment.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT)
                                    .show();
                            }
                        });
                }
            });
        myAdapter=new MyAdapter();
        patientListView=(PullableListView)expertConsultationDiscussionFragment.findViewById(R.id.consulation_list_all_listView);
        patientListView.setAdapter(myAdapter);
        patientListView.setOnLoadListener(this);
        patientListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private class PatientViewHolder {
        
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
            return patientList.size();
        }

        @Override
        public Object getItem(int location) {
            return patientList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new PatientViewHolder();
                convertView=
                    LayoutInflater.from(expertConsultationDiscussionFragment.getContext()).inflate(
                        R.layout.consulation_primary_list_all_item, null);
                holder.photo=(ImageView)convertView.findViewById(R.id.consulation_primary_list_all_item_image);
                holder.titleText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_title);
                holder.doctorText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_doctor);
                holder.dateText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_date);
                holder.moneyText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_money);
                holder.stateText=(TextView)convertView.findViewById(R.id.consulation_primary_list_all_item_state);
                convertView.setTag(holder);
            } else {
                holder=(PatientViewHolder)convertView.getTag();
            }
            holder.titleText.setText(patientList.get(position).getTitle());
            holder.titleText.setTextSize(20);
            holder.doctorText.setText(patientList.get(position).getPatient().getReal_name()+"(患者)|"+patientList.get(position).getExpert_name()+"(专家)");
//            holder.doctorText.setText("站三三(患者)|李思思(专家)");
            holder.doctorText.setTextSize(16);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
            String sd = sdf.format(new Date(patientList.get(position).getCreate_time()));  
            holder.dateText.setText(sd);
            holder.dateText.setTextSize(14);
            holder.moneyText.setText("￥"+patientList.get(position).getConsult_fee());
            holder.moneyText.setTextSize(18);
            holder.stateText.setText(patientList.get(position).getStatus());
            holder.stateText.setTextSize(18);
            final String imgUrl=patientList.get(position).getPatient().getIcon_url();
            holder.photo.setTag(imgUrl);
            holder.photo.setImageResource(R.drawable.photo);
            if(imgUrl != null && !imgUrl.equals("")) {
                ImageListener listener = ImageLoader.getImageListener(holder.photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
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
        parmas.put("userTp", editor.get("userType", ""));
        OpenApiService.getInstance(expertConsultationDiscussionFragment.getContext()).getPatientCaseList(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONArray infos=responses.getJSONArray("pcases");
                            for(int i=0; i < infos.length(); i++) {
                                JSONObject info=infos.getJSONObject(i);
                                PcasesTo pcasesTo=new PcasesTo();
                                pcasesTo.setId(info.getString("id"));
                                pcasesTo.setContent(info.getString("content"));
                                pcasesTo.setStatus(info.getString("status"));
                                pcasesTo.setDestination(info.getString("destination"));
                                pcasesTo.setCreate_time(info.getLong("create_time"));
                                pcasesTo.setTitle(info.getString("title"));
                                pcasesTo.setDepart_id(info.getString("depart_id"));
                                pcasesTo.setDoctor_userid(info.getString("doctor_userid"));
                                pcasesTo.setConsult_fee(info.getInt("consult_fee"));
                                pcasesTo.setPatient_userid(info.getString("patient_userid"));
                                pcasesTo.setDoctor_name(info.getString("doctor_name"));
                                pcasesTo.setExpert_userid(info.getString("expert_userid"));
                                pcasesTo.setExpert_name(info.getString("expert_name"));
                                pcasesTo.setProblem(info.getString("problem"));
                                pcasesTo.setConsult_tp(info.getString("consult_tp"));
                                pcasesTo.setOpinion(info.getString("opinion"));
                                pcasesTo.setUid(info.getString("uid"));
                                PatientTo patientTo=new PatientTo();
                                JSONObject pObject=info.getJSONObject("user");
                                patientTo.setAddress(pObject.getString("address"));
                                patientTo.setId(pObject.getInt("id") + "");
                                patientTo.setState(pObject.getString("state"));
                                // patientTo.setCreate_time(pObject.getLong("create_time"));
                                patientTo.setTp(pObject.getString("tp"));
                                patientTo.setDoctor(pObject.getString("doctor"));
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
                                patientTo.setUid(pObject.getString("uid"));
                                pcasesTo.setPatient(patientTo);
                                patientList.add(pcasesTo);
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
                        } else {
                            Toast.makeText(expertConsultationDiscussionFragment.getContext(), responses.getString("rtnMsg"),
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    Toast.makeText(expertConsultationDiscussionFragment.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }
}