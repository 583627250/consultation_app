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
import android.app.Activity;
import android.content.Intent;
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
import com.consultation.app.activity.CaseInfoActivity;
import com.consultation.app.activity.LoginActivity;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.CasesTo;
import com.consultation.app.model.PatientTo;
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
public class PrimaryConsultationDoingFragment extends Fragment implements OnLoadListener {

    private View primaryConsultationDoingFragment;

    private PullableListView patientListView;

    private List<CasesTo> patientList=new ArrayList<CasesTo>();

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
        primaryConsultationDoingFragment=inflater.inflate(R.layout.consulation_list_all_layout, container, false);
        editor=new SharePreferencesEditor(primaryConsultationDoingFragment.getContext());
        myAdapter=new MyAdapter();
        mQueue=Volley.newRequestQueue(primaryConsultationDoingFragment.getContext());
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initLayout();
        return primaryConsultationDoingFragment;
    }

    private void initData() {
        patientList.clear();
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("userTp", editor.get("userType", ""));
        parmas.put("status", "consult_other");
        CommonUtil.showLoadingDialog(primaryConsultationDoingFragment.getContext());
        OpenApiService.getInstance(primaryConsultationDoingFragment.getContext()).getPatientCaseList(mQueue, parmas,
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
                                patientList.add(pcasesTo);
                            }
                            if(infos.length() == 10) {
                                patientListView.setHasMoreData(true);
                            } else {
                                patientListView.setHasMoreData(false);
                            }
                        } else if(responses.getInt("rtnCode") == 10004) {

                            Toast.makeText(primaryConsultationDoingFragment.getContext(), responses.getString("rtnMsg"),
                                Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initData();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(primaryConsultationDoingFragment.getContext(), LoginActivity.class));
                        } else {
                            Toast.makeText(primaryConsultationDoingFragment.getContext(), responses.getString("rtnMsg"),
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
                    Toast.makeText(primaryConsultationDoingFragment.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void initLayout() {
        ((PullToRefreshLayout)primaryConsultationDoingFragment.findViewById(R.id.consulation_list_all_refresh_view))
            .setOnRefreshListener(new OnRefreshListener() {

                @Override
                public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("page", "1");
                    parmas.put("rows", "10");
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    parmas.put("userTp", editor.get("userType", ""));
                    parmas.put("status", "consult_other");
                    OpenApiService.getInstance(primaryConsultationDoingFragment.getContext()).getPatientCaseList(mQueue, parmas,
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
                                            String consult_fee=info.getString("consult_fee");
                                            if(consult_fee.equals("null")) {
                                                pcasesTo.setConsult_fee("0");
                                            } else {
                                                pcasesTo.setConsult_fee(consult_fee);
                                            }
                                            pcasesTo.setPatient_name(info.getString("patient_name"));
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
                                            patientList.add(pcasesTo);
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
                                        Toast.makeText(primaryConsultationDoingFragment.getContext(),
                                            responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                                initData();
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(primaryConsultationDoingFragment.getContext(), LoginActivity.class));
                                    } else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=2;
                                        msg.obj=pullToRefreshLayout;
                                        handler.sendMessage(msg);
                                        Toast.makeText(primaryConsultationDoingFragment.getContext(),
                                            responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(primaryConsultationDoingFragment.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT)
                                    .show();
                            }
                        });
                }
            });
        patientListView=(PullableListView)primaryConsultationDoingFragment.findViewById(R.id.consulation_list_all_listView);
        patientListView.setAdapter(myAdapter);
        patientListView.setOnLoadListener(this);
        patientListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(primaryConsultationDoingFragment.getContext(), CaseInfoActivity.class);
                intent.putExtra("caseId", patientList.get(position).getId());
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            patientList.clear();
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                    LayoutInflater.from(primaryConsultationDoingFragment.getContext()).inflate(
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
            if(patientList.get(position).getConsult_tp().equals("公开讨论")) {
                holder.doctorText.setText(patientList.get(position).getPatient_name() + "(患者)|"+patientList.get(position).getDoctor_name() + "(初诊)");
            } else {
                holder.doctorText.setText(patientList.get(position).getPatient_name() + "(患者)|"
                    + patientList.get(position).getExpert_name() + "(专家)");
            }
            holder.doctorText.setTextSize(16);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(patientList.get(position).getCreate_time()));
            holder.dateText.setText(sd);
            holder.dateText.setTextSize(14);
            holder.moneyText.setText("￥" + patientList.get(position).getConsult_fee());
            holder.moneyText.setTextSize(18);
            holder.stateText.setText(patientList.get(position).getStatus());
            holder.stateText.setTextSize(18);
            final String imgUrl=patientList.get(position).getPatient().getIcon_url();
            holder.photo.setTag(imgUrl);
            holder.photo.setImageResource(R.drawable.photo_patient);
            if(!"null".equals(imgUrl) && !"".equals(imgUrl)) {
                ImageListener listener=ImageLoader.getImageListener(holder.photo, R.drawable.photo_patient, R.drawable.photo_patient);
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
        parmas.put("status", "consult_other");
        OpenApiService.getInstance(primaryConsultationDoingFragment.getContext()).getPatientCaseList(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
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
                                String consult_fee=info.getString("consult_fee");
                                if(consult_fee.equals("null")) {
                                    pcasesTo.setConsult_fee("0");
                                } else {
                                    pcasesTo.setConsult_fee(consult_fee);
                                }
                                pcasesTo.setPatient_name(info.getString("patient_name"));
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
                        } else if(responses.getInt("rtnCode") == 10004) {
                            hasMore=true;
                            Message msg=handler.obtainMessage();
                            msg.what=1;
                            msg.obj=pullableListView;
                            handler.sendMessage(msg);
                            Toast.makeText(primaryConsultationDoingFragment.getContext(), responses.getString("rtnMsg"),
                                Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initData();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(primaryConsultationDoingFragment.getContext(), LoginActivity.class));
                        } else {
                            hasMore=true;
                            Message msg=handler.obtainMessage();
                            msg.what=1;
                            msg.obj=pullableListView;
                            handler.sendMessage(msg);
                            Toast.makeText(primaryConsultationDoingFragment.getContext(), responses.getString("rtnMsg"),
                                Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(primaryConsultationDoingFragment.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }
}