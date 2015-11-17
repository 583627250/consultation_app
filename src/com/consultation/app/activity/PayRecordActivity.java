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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.CasesTo;
import com.consultation.app.model.PayRecordTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class PayRecordActivity extends Activity implements OnLoadListener {

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private TextView titleAmount, titleStatus, titleTime;

    private List<PayRecordTo> payRecordTos=new ArrayList<PayRecordTo>();

    private MyAdapter myAdapter=new MyAdapter();

    private ViewHolder holder;

    private int page=1;

    private PullableListView listView;

    private boolean hasMore=true;

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
        setContentView(R.layout.my_acount_pay_record_layout);
        editor=new SharePreferencesEditor(this);
        mQueue=Volley.newRequestQueue(this);
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("page", "1");
        parmas.put("rows", "10");
        CommonUtil.showLoadingDialog(this);
        OpenApiService.getInstance(this).getMyAcountPayRecord(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("payments");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            PayRecordTo payRecordTo = new PayRecordTo();
                            payRecordTo.setAmount(info.getString("amount"));
                            payRecordTo.setCreate_time(info.getString("create_time"));
                            payRecordTo.setType(info.getString("status"));
                            JSONObject caseJsonObject = info.getJSONObject("patientCase");
                            CasesTo casesTo = new CasesTo();
                            casesTo.setTitle(caseJsonObject.getString("title"));
                            casesTo.setDoctor_name(caseJsonObject.getString("doctor_name"));
                            casesTo.setExpert_name(caseJsonObject.getString("expert_name"));
                            if(!"".equals(caseJsonObject.getString("create_time")) && !"null".equals(caseJsonObject.getString("create_time"))){
                                casesTo.setCreate_time(Long.parseLong(caseJsonObject.getString("create_time")));
                            }else{
                                casesTo.setCreate_time(0);
                            }
                            payRecordTo.setCasesTo(casesTo);
                            payRecordTos.add(payRecordTo);
                        }
                        if(infos.length() == 10) {
                            listView.setHasMoreData(true);
                        } else {
                            listView.setHasMoreData(false);
                        }
                        myAdapter.notifyDataSetChanged();
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(PayRecordActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(PayRecordActivity.this, LoginActivity.class));
                    }else{
                        Toast.makeText(PayRecordActivity.this, responses.getString("rtnMsg"),
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
                Toast.makeText(PayRecordActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("消费记录");
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

        titleAmount=(TextView)findViewById(R.id.my_acount_pay_title_amount_text);
        titleAmount.setTextSize(18);
        titleStatus=(TextView)findViewById(R.id.my_acount_pay_title_status_text);
        titleStatus.setTextSize(18);
        titleStatus.setText("状态");
        titleTime=(TextView)findViewById(R.id.my_acount_pay_title_time_text);
        titleTime.setTextSize(18);

        ((PullToRefreshLayout)findViewById(R.id.my_acount_pay_list_refresh_view))
            .setOnRefreshListener(new OnRefreshListener() {

                @Override
                public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("uid", editor.get("uid", ""));
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("page", "1");
                    parmas.put("rows", "10");
                    OpenApiService.getInstance(PayRecordActivity.this).getMyAcountPayRecord(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        payRecordTos.clear();
                                        JSONArray infos=responses.getJSONArray("payments");
                                        for(int i=0; i < infos.length(); i++) {
                                            JSONObject info=infos.getJSONObject(i);
                                            PayRecordTo payRecordTo = new PayRecordTo();
                                            payRecordTo.setAmount(info.getString("amount"));
                                            payRecordTo.setCreate_time(info.getString("create_time"));
                                            payRecordTo.setType(info.getString("status"));
                                            JSONObject caseJsonObject = info.getJSONObject("patientCase");
                                            CasesTo casesTo = new CasesTo();
                                            casesTo.setTitle(caseJsonObject.getString("title"));
                                            casesTo.setDoctor_name(caseJsonObject.getString("doctor_name"));
                                            casesTo.setExpert_name(caseJsonObject.getString("expert_name"));
                                            if(!"".equals(caseJsonObject.getString("create_time")) && !"null".equals(caseJsonObject.getString("create_time"))){
                                                casesTo.setCreate_time(Long.parseLong(caseJsonObject.getString("create_time")));
                                            }else{
                                                casesTo.setCreate_time(0);
                                            }
                                            payRecordTo.setCasesTo(casesTo);
                                            payRecordTos.add(payRecordTo);
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
                                        Toast.makeText(PayRecordActivity.this, responses.getString("rtnMsg"),
                                            Toast.LENGTH_SHORT).show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                                initDate();
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(PayRecordActivity.this, LoginActivity.class));
                                    } else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=2;
                                        msg.obj=pullToRefreshLayout;
                                        handler.sendMessage(msg);
                                        Toast.makeText(PayRecordActivity.this, responses.getString("rtnMsg"),
                                            Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PayRecordActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            });
        listView=(PullableListView)findViewById(R.id.my_acount_pay_title_list_listView);
        listView.setAdapter(myAdapter);
        listView.setOnLoadListener(this);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder)view.getTag();
                if(viewHolder.caseLayout.isShown()){
                    viewHolder.caseLayout.setVisibility(View.GONE);
                    viewHolder.icon.setImageResource(R.drawable.pay_close);
                }else{
                    viewHolder.caseLayout.setVisibility(View.VISIBLE);
                    viewHolder.icon.setImageResource(R.drawable.pay_open);
                }
            }
        });
    }

    private static class ViewHolder {

        TextView amount;

        TextView type;
        
        TextView time;
        
        TextView caseTitle;
        
        TextView expertAndDoctor;
        
        ImageView icon;
        
        LinearLayout caseLayout;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return payRecordTos.size();
        }

        @Override
        public Object getItem(int location) {
            return payRecordTos.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(PayRecordActivity.this).inflate(R.layout.pay_record_list_item, null);
                holder=new ViewHolder();
                holder.amount=(TextView)convertView.findViewById(R.id.my_acount_pay_amount_text);
                holder.type=(TextView)convertView.findViewById(R.id.my_acount_pay_type_text);
                holder.time=(TextView)convertView.findViewById(R.id.my_acount_pay_time_text);
                holder.caseTitle=(TextView)convertView.findViewById(R.id.my_acount_pay_title_text);
                holder.expertAndDoctor=(TextView)convertView.findViewById(R.id.my_acount_pay_expert_doctor_text);
                holder.caseLayout=(LinearLayout)convertView.findViewById(R.id.my_acount_pay_case_info_layout);
                holder.icon=(ImageView)convertView.findViewById(R.id.my_acount_pay_case_info_icon);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.icon.setImageResource(R.drawable.pay_close);
            holder.amount.setTextSize(17);
            holder.amount.setText(payRecordTos.get(position).getAmount());
            holder.type.setTextSize(17);
            if(payRecordTos.get(position).getType().equals("1")){
                holder.type.setText("冻结");
            }else if(payRecordTos.get(position).getType().equals("2")){
                holder.type.setText("已付");
            }
            holder.time.setTextSize(17);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(Long.parseLong(payRecordTos.get(position).getCreate_time())));
            holder.time.setText(sd);
            holder.caseLayout.setVisibility(View.GONE);
            holder.caseTitle.setTextSize(15);
            holder.caseTitle.setText(payRecordTos.get(position).getCasesTo().getTitle());
            holder.expertAndDoctor.setTextSize(15);
            holder.expertAndDoctor.setText(payRecordTos.get(position).getCasesTo().getDoctor_name()+"(初诊)|"+payRecordTos.get(position).getCasesTo().getExpert_name()+"(专家)");
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
        OpenApiService.getInstance(PayRecordActivity.this).getMyAcountPayRecord(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONArray infos=responses.getJSONArray("payments");
                            for(int i=0; i < infos.length(); i++) {
                                JSONObject info=infos.getJSONObject(i);
                                PayRecordTo payRecordTo = new PayRecordTo();
                                payRecordTo.setAmount(info.getString("amount"));
                                payRecordTo.setCreate_time(info.getString("create_time"));
                                payRecordTo.setType(info.getString("status"));
                                JSONObject caseJsonObject = info.getJSONObject("patientCase");
                                CasesTo casesTo = new CasesTo();
                                casesTo.setTitle(caseJsonObject.getString("title"));
                                casesTo.setDoctor_name(caseJsonObject.getString("doctor_name"));
                                casesTo.setExpert_name(caseJsonObject.getString("expert_name"));
                                if(!"".equals(caseJsonObject.getString("create_time")) && !"null".equals(caseJsonObject.getString("create_time"))){
                                    casesTo.setCreate_time(Long.parseLong(caseJsonObject.getString("create_time")));
                                }else{
                                    casesTo.setCreate_time(0);
                                }
                                payRecordTo.setCasesTo(casesTo);
                                payRecordTos.add(payRecordTo);
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
                            Toast.makeText(PayRecordActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initDate();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(PayRecordActivity.this, LoginActivity.class));
                        } else {
                            hasMore=true;
                            Message msg=handler.obtainMessage();
                            msg.what=1;
                            msg.obj=pullableListView;
                            handler.sendMessage(msg);
                            Toast.makeText(PayRecordActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PayRecordActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
