package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.consultation.app.model.ImageFilesTo;
import com.consultation.app.model.TitleModel;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint({"UseSparseArrays", "HandlerLeak"})
public class CaseTestViewActivity extends CaseBaseActivity{

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ListView listView;

    private LinearLayout rightLayout;

    private MyAdapter myAdapter;

    private List<String> leftList=new ArrayList<String>();

    private ViewHolder holder;

    private int currentPosition=-1;

    private Context context;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private int width;

    private int height;

    private List<TitleModel> titleModels=new ArrayList<TitleModel>();

    private List<ImageFilesTo> imageFilesTos=new ArrayList<ImageFilesTo>();

    private String caseId, departmentId="";

    private SharePreferencesEditor editor;
    
    private int flag;
    
    private Map<Integer, List<String>> pathMap=new HashMap<Integer, List<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_case_add_test_layout);
        context=this;
        editor=new SharePreferencesEditor(context);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        caseId=getIntent().getStringExtra("caseId");
        flag=getIntent().getIntExtra("flag", 0);
        departmentId=getIntent().getStringExtra("departmentId");
        mQueue=Volley.newRequestQueue(context);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("查看检验图片");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                finish();
            }
        });
        rightLayout=(LinearLayout)findViewById(R.id.test_right_layout);

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.test_left_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                if(flag == 0){
                    showRightLayout(position);
                }else{
                    getImage(leftList.get(position));
                }
            }
        });
    }

    private void showRightLayout(int position) {
        rightLayout.removeAllViews();
        List<String> imagePaths=pathMap.get(currentPosition);
        if(null != imagePaths && imagePaths.size() != 0) {
            LinearLayout rowsLayout=new LinearLayout(context);
            RelativeLayout relativeLayout=new RelativeLayout(context);
            for(int i=0; i < imagePaths.size(); i++) {
                if(i % 2 == 0) {
                    rowsLayout=createLinearLayout();
                    rightLayout.addView(rowsLayout);
                }
                relativeLayout=createImage(imagePaths.get(i), i);
                rowsLayout.addView(relativeLayout);
            }
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_VERTICAL;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, height / 100, 0, height / 100);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private RelativeLayout createImage(String path, int id) {
        RelativeLayout relativeLayout=new RelativeLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 55;
        layoutParams.weight=1;
        relativeLayout.setLayoutParams(layoutParams);
        ImageView imageView=new ImageView(context);
        imageView.setId(id);
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        if(path.startsWith("http:")) {
            imageView.setTag(path);
            if(!"null".equals(path) && !"".equals(path)) {
                ImageListener listener=
                    ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_delete);
                mImageLoader.get(path, listener, 200, 200);
            }
        } else {
            Bitmap bitmap=CommonUtil.readBitMap(200, path);
            imageView.setImageBitmap(bitmap);
        }
        relativeLayout.addView(imageView);
        return relativeLayout;
    }

    private void getImage(String testName) {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("test_name", testName);
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("case_id", caseId);
        CommonUtil.showLoadingDialog(context);
        OpenApiService.getInstance(context).getCaseImageList(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        JSONArray infos=responses.getJSONArray("caseFiles");
                        for(int i=0; i < infos.length(); i++) {
                            JSONObject info=infos.getJSONObject(i);
                            ImageFilesTo filesTo=new ImageFilesTo();
                            filesTo.setId(info.getString("id"));
                            filesTo.setCase_id(info.getString("case_id"));
                            filesTo.setPic_url(info.getString("pic_url"));
                            filesTo.setTest_name(info.getString("test_name"));
                            imageFilesTos.add(filesTo);
                        }
                        if(imageFilesTos.size() == 0){
                            TextView tip = (TextView)findViewById(R.id.test_add_tip_text);
                            tip.setTextSize(18);
                            tip.setVisibility(View.VISIBLE);
                        }
                        handler.sendEmptyMessage(0);
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(context, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initData();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(context, LoginActivity.class));
                    } else {
                        Toast.makeText(context, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(context, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<String> paths=pathMap.get(currentPosition);
            for(int i=0; i < imageFilesTos.size(); i++) {
                if(null == paths) {
                    paths=new ArrayList<String>();
                    paths.add(imageFilesTos.get(i).getPic_url());
                    pathMap.put(currentPosition, paths);
                } else {
                    paths.add(imageFilesTos.get(i).getPic_url());
                    pathMap.put(currentPosition, paths);
                }
            }
            showRightLayout(currentPosition);
        }
    };

    private void initData() {
        initCaseDatas(departmentId + "case.xml");
        titleModels=caseList.get(6).getTitleModels();
        for(int i=0; i < titleModels.size(); i++) {
            leftList.add(titleModels.get(i).getTitle());
        }
    }


    private static class ViewHolder {

        TextView textName;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return leftList.size();
        }

        @Override
        public Object getItem(int location) {
            return leftList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(CaseTestViewActivity.this).inflate(R.layout.search_recommend_list_item, null);
                holder=new ViewHolder();
                holder.textName=(TextView)convertView.findViewById(R.id.search_recommend_list_item_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.textName.setTextSize(16);
            holder.textName.setText(leftList.get(position));
            convertView.setBackgroundColor(Color.WHITE);
            if(position == currentPosition) {
                convertView.setBackgroundColor(Color.parseColor("#E4E4E4"));
            }
            return convertView;
        }
    }
}
