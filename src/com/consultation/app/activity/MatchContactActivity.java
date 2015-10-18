package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.consultation.app.model.ContactTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.util.Trans2PinYin;


@SuppressLint("DefaultLocale")
public class MatchContactActivity extends Activity{

    private static final String[] PHONES_PROJECTION=new String[]{Phone.DISPLAY_NAME, Phone.NUMBER, Phone.SORT_KEY_PRIMARY};

    private static final int PHONES_DISPLAY_NAME_INDEX=0;

    private static final int PHONES_NUMBER_INDEX=1;

    private List<ContactTo> contacts=new ArrayList<ContactTo>();

    private List<ContactTo> temp=new ArrayList<ContactTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private ListView listView;

    private TextView title_text;

    private LinearLayout back_layout, allLayout;

    private TextView back_text;

    private EditText editText;

    private ImageView delete;
    
    private StringBuffer phoneBuffer;
    
    private SharePreferencesEditor editor;

    private RequestQueue mQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_contact_layout);
        editor=new SharePreferencesEditor(MatchContactActivity.this);
        mQueue=Volley.newRequestQueue(MatchContactActivity.this);
        initData();
        initView();
    }

    private void initData() {
        ContentResolver resolver=MatchContactActivity.this.getContentResolver();
        Cursor phoneCursor=resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, Phone.SORT_KEY_PRIMARY);
        int count = 0;
        if(phoneCursor != null) {
            phoneBuffer = new StringBuffer("[");
            while(phoneCursor.moveToNext()) {
                String phoneNumber=phoneCursor.getString(PHONES_NUMBER_INDEX);
                if(TextUtils.isEmpty(phoneNumber))
                    continue;
                if(phoneNumber.contains(" ")) {
                    phoneNumber = phoneNumber.replace(" ", "");
                }
                if(phoneNumber.contains("-")) {
                    phoneNumber = phoneNumber.replace("-", "");
                }
                if(phoneNumber.contains("+86")) {
                    phoneNumber = phoneNumber.replace("+86", "");
                }
                String contactName=phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                ContactTo contactTo=new ContactTo();
                contactTo.setInvitation("0");
                contactTo.setName(contactName);
                contactTo.setPhone(phoneNumber);
                if(AccountUtil.isPhoneNum(phoneNumber)){
                    contacts.add(contactTo);
//                    temp.add(contactTo);
                    if(count == 0){
                        phoneBuffer.append("{");
                        phoneBuffer.append("\"name\":\""+contactName+"\",\"mobile_ph\":\""+phoneNumber+"\",\"condition\":\"0\"}");
                    }else{
                        phoneBuffer.append(",").append("{").append("\"name\":\""+contactName+"\",\"mobile_ph\":\""+phoneNumber+"\",\"condition\":\"0\"}");
                    }
                    count++;
                }
            }
            phoneBuffer.append("]");
            phoneCursor.close();
        }
        if(contacts.size()!=0){
            Map<String, String> parmas=new HashMap<String, String>();
            parmas.put("mobile_phs", phoneBuffer.toString());
            parmas.put("accessToken", ClientUtil.getToken());
            parmas.put("uid", editor.get("uid", ""));
            CommonUtil.showLoadingDialog(MatchContactActivity.this);
            OpenApiService.getInstance(MatchContactActivity.this).getContactStatus(mQueue, parmas, new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    CommonUtil.closeLodingDialog();
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONArray infos=responses.getJSONArray("conditions");
                            temp.clear();
                            allLayout.setVisibility(View.VISIBLE);
                            for(int i=0; i < infos.length(); i++) {
                                JSONObject info=infos.getJSONObject(i);
                                ContactTo contactTo=new ContactTo();
                                contactTo.setInvitation(info.getString("condition"));
                                contactTo.setName(info.getString("name"));
                                contactTo.setPhone(info.getString("mobile_ph"));
                                temp.add(contactTo);
                            }
                            myAdapter.notifyDataSetChanged();
                        } else if(responses.getInt("rtnCode") == 10004) {
                            Toast.makeText(MatchContactActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initData();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(MatchContactActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(MatchContactActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    CommonUtil.closeLodingDialog();
                    Toast.makeText(MatchContactActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("手机通讯录");
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
        
        allLayout=(LinearLayout)findViewById(R.id.contact_all_layout);

        delete=(ImageView)findViewById(R.id.contact_image_delete);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText=(EditText)findViewById(R.id.contact_edit);
        editText.setTextSize(18);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getList(s.toString());
            }
        });
        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.contact_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    
    private void getList(String name) {
        temp.clear();
        if("".equals(name) || name == null) {
            for(int i=0; i < contacts.size(); i++) {
                temp.add(contacts.get(i));
            }
        } else {
            for(int i=0; i < contacts.size(); i++) {
                String strPinYin=Trans2PinYin.trans2PinYin(contacts.get(i).getName());
                if(contacts.get(i).getName().contains(name) || strPinYin.startsWith(name.toLowerCase())) {
                    temp.add(contacts.get(i));
                }
            }
        }
        myAdapter.notifyDataSetChanged();
    }
    
    private class ViewHolder {

        TextView phone;

        TextView name;

        Button btn;

        TextView isInvitation;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return temp.size();
        }

        @Override
        public Object getItem(int location) {
            return temp.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(MatchContactActivity.this).inflate(R.layout.contact_list_item, null);
                holder.phone=(TextView)convertView.findViewById(R.id.contact_number);
                holder.name=(TextView)convertView.findViewById(R.id.contact_name);
                holder.btn=(Button)convertView.findViewById(R.id.contact_btn);
                holder.isInvitation=(TextView)convertView.findViewById(R.id.contact_isInvitation);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.phone.setText(temp.get(position).getPhone());
            holder.phone.setTextSize(16);
            holder.name.setText(temp.get(position).getName());
            holder.name.setTextSize(18);
            if(temp.get(position).getIsInvitation().equals("2")){
                holder.btn.setText("呼叫");
                holder.btn.setTextSize(16);
                holder.btn.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        //者联系呼叫
                        Intent intent= new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + temp.get(position).getPhone()));
                        MatchContactActivity.this.startActivity(intent);
                    }
                });
            }else if(temp.get(position).getIsInvitation().equals("0")){
                holder.btn.setText("邀请");
                holder.btn.setTextSize(16);
                holder.btn.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        //邀请
                    }
                });
            }else if(temp.get(position).getIsInvitation().equals("1")){
                holder.btn.setVisibility(View.GONE);
                holder.isInvitation.setTextSize(16);
                holder.isInvitation.setVisibility(View.VISIBLE);
            }
            holder.btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
                getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
            return convertView;
        }
    }
}
