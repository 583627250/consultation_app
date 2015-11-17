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
import com.consultation.app.model.InvitationTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;

@SuppressLint({"SimpleDateFormat", "HandlerLeak"})
public class InvitationActivity extends Activity implements OnLoadListener {

    private EditText phoneEdit;

    private Button submit, person;

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private PullableListView listView;

    private List<InvitationTo> invitationList=new ArrayList<InvitationTo>();

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
        setContentView(R.layout.invitation_layout);
        mQueue=Volley.newRequestQueue(InvitationActivity.this);
        editor=new SharePreferencesEditor(InvitationActivity.this);
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        CommonUtil.showLoadingDialog(InvitationActivity.this);
        OpenApiService.getInstance(InvitationActivity.this).getInvitationList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("doctorInvitCodes");
                        invitationList.clear();
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            InvitationTo invitationTo=new InvitationTo();
                            invitationTo.setCode(info.getString("code"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                invitationTo.setCreate_time(0);
                            } else {
                                invitationTo.setCreate_time(Long.parseLong(createTime));
                            }
                            invitationTo.setIs_joined(info.getString("is_joined"));
                            invitationTo.setMobile_ph(info.getString("mobile_ph"));
                            invitationTo.setValid_date(info.getInt("valid_date"));
                            invitationList.add(invitationTo);
                        }
                        if(infos.length() == 10) {
                            listView.setHasMoreData(true);
                        } else {
                            listView.setHasMoreData(false);
                        }
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(InvitationActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(InvitationActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("邀请列表");
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

        phoneEdit=(EditText)findViewById(R.id.mine_invitation_input_edit);
        phoneEdit.setTextSize(18);

        person=(Button)findViewById(R.id.mine_invitation_btn_person);
        person.setTextSize(18);
        person.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
        person.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //到联系人界面
                startActivityForResult(new Intent(InvitationActivity.this, ContactActivity.class), 0);
            }
        });
        submit=(Button)findViewById(R.id.mine_invitation_btn);
        submit.setTextSize(18);
        submit.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == phoneEdit.getText().toString() || "".equals(phoneEdit.getText().toString())) {
                    Toast.makeText(InvitationActivity.this, "请输入被邀请人的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountUtil.isPhoneNum(phoneEdit.getText().toString())) {
                    Toast.makeText(InvitationActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("mobile_ph", phoneEdit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(InvitationActivity.this);
                OpenApiService.getInstance(InvitationActivity.this).getSendInvitation(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    initDate();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                    startActivity(new Intent(InvitationActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                            Toast.makeText(InvitationActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        ((PullToRefreshLayout)findViewById(R.id.mine_invitation_refresh_view)).setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("page", "1");
                parmas.put("rows", "10");
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                OpenApiService.getInstance(InvitationActivity.this).getInvitationList(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    JSONArray infos=responses.getJSONArray("doctorInvitCodes");
                                    invitationList.clear();
                                    for(int i=0; i < infos.length(); i++) {
                                        JSONObject info=infos.getJSONObject(i);
                                        InvitationTo invitationTo=new InvitationTo();
                                        invitationTo.setCode(info.getString("code"));
                                        String createTime=info.getString("create_time");
                                        if(createTime.equals("null")) {
                                            invitationTo.setCreate_time(0);
                                        } else {
                                            invitationTo.setCreate_time(Long.parseLong(createTime));
                                        }
                                        invitationTo.setIs_joined(info.getString("is_joined"));
                                        invitationTo.setMobile_ph(info.getString("mobile_ph"));
                                        invitationTo.setValid_date(info.getInt("valid_date"));
                                        invitationList.add(invitationTo);
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
                                    Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                    startActivity(new Intent(InvitationActivity.this, LoginActivity.class));
                                } else {
                                    Message msg=handler.obtainMessage();
                                    msg.what=2;
                                    msg.obj=pullToRefreshLayout;
                                    handler.sendMessage(msg);
                                    Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                            Toast.makeText(InvitationActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        myAdapter=new MyAdapter();
        listView=(PullableListView)findViewById(R.id.mine_invitation_listView);
        listView.setAdapter(myAdapter);
        listView.setOnLoadListener(this);
    }

    private class ViewHolder {

        TextView phone;

        TextView code;

        TextView create_time;

        TextView valid_date;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return invitationList.size();
        }

        @Override
        public Object getItem(int location) {
            return invitationList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(InvitationActivity.this).inflate(R.layout.invitation_list_item, null);
                holder.phone=(TextView)convertView.findViewById(R.id.invitation_item_phone);
                holder.code=(TextView)convertView.findViewById(R.id.invitation_item_code);
                holder.create_time=(TextView)convertView.findViewById(R.id.invitation_item_createDate);
                holder.valid_date=(TextView)convertView.findViewById(R.id.invitation_item_isJoin);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.phone.setText(invitationList.get(position).getMobile_ph());
            holder.phone.setTextSize(20);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String sd=sdf.format(new Date(invitationList.get(position).getCreate_time()));
            holder.create_time.setText(sd);
            holder.create_time.setTextSize(20);
            if(invitationList.get(position).getIs_joined().equals("1")) {
                holder.valid_date.setText("已加入");
                holder.valid_date.setTextSize(16);
                String codeText=sdf.format(new Date(invitationList.get(position).getJoined_time()));
                holder.code.setText(codeText);
                holder.code.setTextSize(16);
            } else {
                String validText=sdf.format(new Date(invitationList.get(position).getValid_date()));
                holder.valid_date.setText("有效期至" + validText);
                holder.valid_date.setTextSize(14);
                holder.code.setText("邀请码：" + invitationList.get(position).getCode());
                holder.code.setTextSize(14);
            }
            return convertView;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            phoneEdit.setText(data.getStringExtra("phone"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLoad(final PullableListView pullableListView) {
        Map<String, String> parmas=new HashMap<String, String>();
        page++;
        parmas.put("page", String.valueOf(page));
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        OpenApiService.getInstance(InvitationActivity.this).getInvitationList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("doctorInvitCodes");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            InvitationTo invitationTo=new InvitationTo();
                            invitationTo.setCode(info.getString("code"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                invitationTo.setCreate_time(0);
                            } else {
                                invitationTo.setCreate_time(Long.parseLong(createTime));
                            }
                            invitationTo.setIs_joined(info.getString("is_joined"));
                            invitationTo.setMobile_ph(info.getString("mobile_ph"));
                            invitationTo.setValid_date(info.getInt("valid_date"));
                            invitationList.add(invitationTo);
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
                        Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(InvitationActivity.this, LoginActivity.class));
                    } else {
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(InvitationActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(InvitationActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
