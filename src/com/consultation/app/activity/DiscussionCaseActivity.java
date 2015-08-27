package com.consultation.app.activity;

import java.io.File;
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
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.DiscussionTo;
import com.consultation.app.model.ImageFilesTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ActivityList;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.PhoneUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class DiscussionCaseActivity extends Activity implements OnLoadListener {

    private EditText contentEdit;

    private Button submit, imageBtn;;

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text, finsh_text;

    private PullableListView listView;

    private List<DiscussionTo> discussionList=new ArrayList<DiscussionTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private SharePreferencesEditor editor;

    private int page=1;

    private boolean hasMore=true;

    private String caseId, consultType;

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
        setContentView(R.layout.discussion_case_layout);
        editor=new SharePreferencesEditor(DiscussionCaseActivity.this);
        caseId=getIntent().getStringExtra("caseId");
        consultType=getIntent().getStringExtra("consultType");
        mQueue=Volley.newRequestQueue(DiscussionCaseActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        ActivityList.getInstance().setActivitys("DiscussionCaseActivity", this);
        initData();
        initView();
    }

    private void initData() {
        discussionList.clear();
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("page", "1");
        parmas.put("rows", "10");
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("case_id", caseId);
        CommonUtil.showLoadingDialog(DiscussionCaseActivity.this);
        OpenApiService.getInstance(DiscussionCaseActivity.this).getDiscussionCaseList(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    CommonUtil.closeLodingDialog();
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        System.out.println(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONArray infos=responses.getJSONArray("caseDiscusss");
                            System.out.println(infos.length());
                            for(int i=0; i < infos.length(); i++) {
                                JSONObject info=infos.getJSONObject(i);
                                DiscussionTo discussionTo=new DiscussionTo();
                                discussionTo.setId(info.getString("id"));
                                discussionTo.setContent(info.getString("content"));
                                String createTime=info.getString("create_time");
                                if(createTime.equals("null")) {
                                    discussionTo.setCreate_time(0);
                                } else {
                                    discussionTo.setCreate_time(Long.parseLong(createTime));
                                }
                                discussionTo.setCase_id(info.getString("case_id"));
                                discussionTo.setAt_userid(info.getString("at_userid"));
                                discussionTo.setAt_username(info.getString("at_username"));
                                discussionTo.setDiscusser(info.getString("discusser"));
                                discussionTo.setDiscusser_userid(info.getString("discusser_userid"));
                                discussionTo.setIs_view(info.getString("is_view"));
                                discussionTo.setHave_photos(info.getString("have_photos"));
                                if(info.getString("have_photos").equals("1")){
                                    ImageFilesTo filesTo = new ImageFilesTo();
                                    List<ImageFilesTo> list = new ArrayList<ImageFilesTo>();
                                    JSONArray jsonArray = info.getJSONArray("cdFiles");
                                    for(int j=0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                                        filesTo.setCase_id(jsonObject.getString("case_id"));
                                        filesTo.setPic_url(jsonObject.getString("pic_url"));
                                        filesTo.setTest_name(jsonObject.getString("test_name"));
                                        list.add(filesTo);
                                    }
                                    discussionTo.setImageFilesTos(list);
                                }
                                discussionList.add(discussionTo);
                            }
                            if(infos.length() == 10) {
                                listView.setHasMoreData(true);
                            } else {
                                listView.setHasMoreData(false);
                            }
                        } else if(responses.getInt("rtnCode") == 10004) {
                            Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initData();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(DiscussionCaseActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    CommonUtil.closeLodingDialog();
                    Toast.makeText(DiscussionCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例讨论");
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

        if(editor.get("userType", "").equals("1")) {
            if(!consultType.equals("公开讨论") && !consultType.equals("手术或住院")) {
                finsh_text=(TextView)findViewById(R.id.header_right);
                finsh_text.setVisibility(View.VISIBLE);
                finsh_text.setTextSize(18);
                finsh_text.setText("更多");
                finsh_text.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 点击更多按钮
                        Intent intent=new Intent(DiscussionCaseActivity.this, CaseMoreActivity.class);
                        intent.putExtra("btn1", "手术或住院");
                        intent.putExtra("btn2", "完成");
                        intent.putExtra("flag", "4");
                        intent.putExtra("caseId", caseId);
                        startActivity(intent);
                    }
                });
            } else {
                imageBtn.setVisibility(View.GONE);
                finsh_text=(TextView)findViewById(R.id.header_right);
                finsh_text.setVisibility(View.VISIBLE);
                finsh_text.setTextSize(18);
                finsh_text.setText("完成");
                finsh_text.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 点击完成按钮
                        Map<String, String> parmas=new HashMap<String, String>();
                        parmas.put("caseId", caseId);
                        parmas.put("accessToken", ClientUtil.getToken());
                        parmas.put("uid", editor.get("uid", ""));
                        CommonUtil.showLoadingDialog(DiscussionCaseActivity.this);
                        OpenApiService.getInstance(DiscussionCaseActivity.this).getDiscussionCaseFinsh(mQueue, parmas,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String arg0) {
                                    CommonUtil.closeLodingDialog();
                                    try {
                                        JSONObject responses=new JSONObject(arg0);
                                        if(responses.getInt("rtnCode") == 1) {
                                            Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"),
                                                Toast.LENGTH_SHORT).show();
                                            DiscussionCaseActivity.this.finish();
                                        } else if(responses.getInt("rtnCode") == 10004) {
                                            Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"),
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
                                            startActivity(new Intent(DiscussionCaseActivity.this, LoginActivity.class));
                                        } else {
                                            Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"),
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
                                    Toast.makeText(DiscussionCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                });
            }
        }

        contentEdit=(EditText)findViewById(R.id.case_disscussion_input_edit);
        contentEdit.setTextSize(18);

        imageBtn=(Button)findViewById(R.id.case_disscussion_btn_image);
        imageBtn.setTextSize(22);
        imageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DiscussionCaseActivity.this, AddPatientPicActivity.class), 0);
            }
        });
        submit=(Button)findViewById(R.id.case_disscussion_btn);
        submit.setTextSize(22);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null == contentEdit.getText().toString() || "".equals(contentEdit.getText().toString())) {
                    Toast.makeText(DiscussionCaseActivity.this, "请输入讨论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("case_id", caseId);
                parmas.put("discusser_userid", editor.get("uid", ""));
                parmas.put("discusser", editor.get("real_name", "医生"));
                parmas.put("content", contentEdit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(DiscussionCaseActivity.this);
                OpenApiService.getInstance(DiscussionCaseActivity.this).getSendDiscussionCase(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    contentEdit.setText("");
                                    initData();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                    startActivity(new Intent(DiscussionCaseActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                            Toast.makeText(DiscussionCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        ((PullToRefreshLayout)findViewById(R.id.case_disscussion_refresh_view)).setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("page", "1");
                parmas.put("rows", "10");
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                parmas.put("case_id", caseId);
                OpenApiService.getInstance(DiscussionCaseActivity.this).getDiscussionCaseList(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    JSONArray infos=responses.getJSONArray("caseDiscusss");
                                    discussionList.clear();
                                    for(int i=0; i < infos.length(); i++) {
                                        JSONObject info=infos.getJSONObject(i);
                                        DiscussionTo discussionTo=new DiscussionTo();
                                        discussionTo.setId(info.getString("id"));
                                        discussionTo.setContent(info.getString("content"));
                                        String createTime=info.getString("create_time");
                                        if(createTime.equals("null")) {
                                            discussionTo.setCreate_time(0);
                                        } else {
                                            discussionTo.setCreate_time(Long.parseLong(createTime));
                                        }
                                        discussionTo.setCase_id(info.getString("case_id"));
                                        discussionTo.setAt_userid(info.getString("at_userid"));
                                        discussionTo.setAt_username(info.getString("at_username"));
                                        discussionTo.setDiscusser(info.getString("discusser"));
                                        discussionTo.setDiscusser_userid(info.getString("discusser_userid"));
                                        discussionTo.setIs_view(info.getString("is_view"));
                                        discussionTo.setHave_photos(info.getString("have_photos"));
                                        if(info.getString("have_photos").equals("1")){
                                            ImageFilesTo filesTo = new ImageFilesTo();
                                            List<ImageFilesTo> list = new ArrayList<ImageFilesTo>();
                                            JSONArray jsonArray = info.getJSONArray("cdFiles");
                                            for(int j=0; j < jsonArray.length(); j++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                                filesTo.setCase_id(jsonObject.getString("case_id"));
                                                filesTo.setPic_url(jsonObject.getString("pic_url"));
                                                filesTo.setTest_name(jsonObject.getString("test_name"));
                                                list.add(filesTo);
                                            }
                                            discussionTo.setImageFilesTos(list);
                                        }
                                        discussionList.add(discussionTo);
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
                                    Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                    startActivity(new Intent(DiscussionCaseActivity.this, LoginActivity.class));
                                } else {
                                    Message msg=handler.obtainMessage();
                                    msg.what=2;
                                    msg.obj=pullToRefreshLayout;
                                    handler.sendMessage(msg);
                                    Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                            Toast.makeText(DiscussionCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        myAdapter=new MyAdapter();
        listView=(PullableListView)findViewById(R.id.case_disscussion_listView);
        listView.setAdapter(myAdapter);
        listView.setOnLoadListener(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null && resultCode == Activity.RESULT_OK) {
            final String photoPath=data.getStringExtra("bitmap");
            File[] files=new File[1];
            File file=new File(photoPath);
            files[0]=file;
            Map<String, String> params = new HashMap<String, String>();
            params.put("case_id", caseId);
            params.put("discusser_userid", editor.get("uid", ""));
            params.put("discusser", editor.get("real_name", "医生"));
            params.put("accessToken", ClientUtil.getToken());
            params.put("uid", editor.get("uid", ""));
            CommonUtil.showLoadingDialog(DiscussionCaseActivity.this);
            OpenApiService.getInstance(DiscussionCaseActivity.this).getUploadFiles(ClientUtil.GET_UPLOAD_IMAGES_URL, DiscussionCaseActivity.this,
                new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        CommonUtil.closeLodingDialog();
                        System.out.println(rspContent);
                        Toast.makeText(DiscussionCaseActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                        DiscussionTo discussionTo=new DiscussionTo();
                        discussionTo.setCreate_time(System.currentTimeMillis());
                        discussionTo.setCase_id(caseId);
                        discussionTo.setDiscusser(editor.get("real_name", "医生"));
                        discussionTo.setDiscusser_userid(editor.get("uid", ""));
                        discussionTo.setIs_view("1");
                        discussionTo.setHave_photos("1");
                        ImageFilesTo filesTo = new ImageFilesTo();
                        filesTo.setCase_id(caseId);
                        filesTo.setPic_url(photoPath);
                        filesTo.setTest_name("");
                        List<ImageFilesTo> list = new ArrayList<ImageFilesTo>();
                        list.add(filesTo);
                        discussionTo.setImageFilesTos(list);
                        discussionList.add(discussionTo);
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(DiscussionCaseActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                    }
                }, files, params);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ViewHolder {

        TextView contents;

        TextView name;

        TextView create_time;
        
        ImageView imageView;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return discussionList.size();
        }

        @Override
        public Object getItem(int location) {
            return discussionList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(DiscussionCaseActivity.this).inflate(R.layout.discussion_case_list_item, null);
                holder.contents=(TextView)convertView.findViewById(R.id.discussion_case_item_content);
                holder.create_time=(TextView)convertView.findViewById(R.id.discussion_case_item_createTime);
                holder.name=(TextView)convertView.findViewById(R.id.discussion_case_item_name);
                holder.imageView=(ImageView)convertView.findViewById(R.id.discussion_case_item_content_imageView);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            if(discussionList.get(position).getHave_photos().equals("1")){
                holder.contents.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
                final String imgUrl=discussionList.get(position).getImageFilesTos().get(0).getPic_url();
                if(imgUrl.startsWith("http://")){
                    holder.imageView.setTag(imgUrl);
                    if(!"null".equals(imgUrl) && !"".equals(imgUrl)) {
                        ImageListener listener = ImageLoader.getImageListener(holder.imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_delete);
                        mImageLoader.get(imgUrl, listener);
                    }
                }else{
                    Bitmap bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(DiscussionCaseActivity.this), imgUrl);
                    holder.imageView.setImageBitmap(bitmap);
                }
            }else{
                holder.contents.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.GONE);
                holder.contents.setText(discussionList.get(position).getContent());
                holder.contents.setTextSize(18);
            }
            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd  HH:mm");
            String sd=sdf.format(new Date(discussionList.get(position).getCreate_time()));
            holder.create_time.setText(sd);
            holder.create_time.setTextSize(15);
            holder.name.setText(discussionList.get(position).getDiscusser());
            holder.name.setTextSize(15);
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
        parmas.put("case_id", caseId);
        OpenApiService.getInstance(DiscussionCaseActivity.this).getFeedBackList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                System.out.println(arg0);
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("caseDiscusss");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            DiscussionTo discussionTo=new DiscussionTo();
                            discussionTo.setId(info.getString("id"));
                            discussionTo.setContent(info.getString("content"));
                            String createTime=info.getString("create_time");
                            if(createTime.equals("null")) {
                                discussionTo.setCreate_time(0);
                            } else {
                                discussionTo.setCreate_time(Long.parseLong(createTime));
                            }
                            discussionTo.setCase_id(info.getString("case_id"));
                            discussionTo.setAt_userid(info.getString("at_userid"));
                            discussionTo.setAt_username(info.getString("at_username"));
                            discussionTo.setDiscusser(info.getString("discusser"));
                            discussionTo.setDiscusser_userid(info.getString("discusser_userid"));
                            discussionTo.setIs_view(info.getString("is_view"));
                            discussionTo.setHave_photos(info.getString("have_photos"));
                            if(info.getString("have_photos").equals("1")){
                                ImageFilesTo filesTo = new ImageFilesTo();
                                List<ImageFilesTo> list = new ArrayList<ImageFilesTo>();
                                JSONArray jsonArray = info.getJSONArray("cdFiles");
                                for(int j=0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                                    filesTo.setCase_id(jsonObject.getString("case_id"));
                                    filesTo.setPic_url(jsonObject.getString("pic_url"));
                                    filesTo.setTest_name(jsonObject.getString("test_name"));
                                    list.add(filesTo);
                                }
                                discussionTo.setImageFilesTos(list);
                            }
                            discussionList.add(discussionTo);
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
                        Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(DiscussionCaseActivity.this, LoginActivity.class));
                    } else {
                        hasMore=true;
                        Message msg=handler.obtainMessage();
                        msg.what=1;
                        msg.obj=pullableListView;
                        handler.sendMessage(msg);
                        Toast.makeText(DiscussionCaseActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DiscussionCaseActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
