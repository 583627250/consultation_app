package com.consultation.app.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.activity.SpecialistInfoActivity;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.view.ExpandTabView;
import com.consultation.app.view.LeftFilterView;
import com.consultation.app.view.MiddleFilterView;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;
import com.consultation.app.view.RightFilterView;

@SuppressLint("HandlerLeak")
public class SpecialistFragment extends Fragment implements OnLoadListener{

    private View specialistLayout;
    
    private ExpandTabView expandTabView;

    private ArrayList<View> mViewArray=new ArrayList<View>();

    private LeftFilterView viewLeft;

    private MiddleFilterView viewMiddle;

    private RightFilterView viewRight;

    private TextView header_text;

    private PullableListView specialistListView;

    private List<String> specialistList=new ArrayList<String>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private RequestQueue mQueue;
    
    private ImageLoader mImageLoader;
    
    private static Context mContext;
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                myAdapter.notifyDataSetChanged();
                ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.SUCCEED);
                specialistListView.setHasMoreData(true);
                break;
            case 1:
                ((PullableListView)msg.obj).finishLoading();
                myAdapter.notifyDataSetChanged();
                break;
            }
            
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        specialistLayout=inflater.inflate(R.layout.specialist_layout, container, false);
        initData();
        initLayout();
        initVaule();
        initListener();
        return specialistLayout;
    }
    
    public static SpecialistFragment getInstance(Context ctx) { 
        mContext = ctx;
        return new SpecialistFragment();
    }

    private void initData() {
        mQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        specialistList.add("");
        specialistList.add("");
        specialistList.add("");
        specialistList.add("");
        specialistList.add("");
        specialistList.add("");
    }

    private void initLayout() {
        header_text=(TextView)specialistLayout.findViewById(R.id.header_text);
        header_text.setText("专家库");
        header_text.setTextSize(20);

        expandTabView = (ExpandTabView) specialistLayout.findViewById(R.id.specialist_select_expandtab_view);

        viewLeft = new LeftFilterView(mContext);

        viewMiddle = new MiddleFilterView(mContext);

        String names[] = new String[] { "初级医师", "主治医师", "专家" };
        viewRight = new RightFilterView(mContext, names);
        
        myAdapter=new MyAdapter();
        
        ((PullToRefreshLayout)specialistLayout.findViewById(R.id.specialist_info_refresh_view))
        .setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            specialistList.add("");
                            specialistList.add("");
                            specialistList.add("");
                            specialistList.add("");
                            specialistList.add("");
                            specialistList.add("");
                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = pullToRefreshLayout;
                            handler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                    }
                }).start();
            }
        });
        
        specialistListView=(PullableListView)specialistLayout.findViewById(R.id.specialist_info_listView);
        specialistListView.setAdapter(myAdapter);
        specialistListView.setOnLoadListener(this);
        specialistListView.setHasMoreData(false);
        specialistListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id >= 0) {
                    startActivity(new Intent(mContext, SpecialistInfoActivity.class));
                }
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
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return specialistList.size();
        }

        @Override
        public Object getItem(int location) {
            return specialistList.get(location);
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
                holder.departmen=(TextView)convertView.findViewById(R.id.specialist_info_department);
                holder.hospital=(TextView)convertView.findViewById(R.id.specialist_info_hospital);
                holder.patients=(TextView)convertView.findViewById(R.id.specialist_info_patient_text);
                holder.patientCount=(TextView)convertView.findViewById(R.id.specialist_info_patient_count_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            final String imgUrl="";
            // 给 ImageView 设置一个 tag
            holder.photo.setTag(imgUrl);
            // 预设一个图片
            holder.photo.setImageResource(R.drawable.ic_launcher);
            holder.name.setText("张三");
            holder.name.setTextSize(16);
            holder.departmen.setText("妇产科" + "|" + "主治医生");
            holder.departmen.setTextSize(14);
            holder.hospital.setText("北京协和医院");
            holder.hospital.setTextSize(14);
            holder.patients.setTextSize(14);
            holder.patientCount.setText("3");
            holder.patientCount.setTextSize(14);
            if(imgUrl != null && !imgUrl.equals("")) {
                if(imgUrl != null && !imgUrl.equals("")) {
                    ImageListener listener = ImageLoader.getImageListener(holder.photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                    mImageLoader.get(imgUrl, listener);
                }
            }
            return convertView;
        }
    }    
    
    private void initVaule() {
        mViewArray.add(viewLeft);
        mViewArray.add(viewMiddle);
        mViewArray.add(viewRight);
        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("医院");
        mTextArray.add("科室");
        mTextArray.add("职称");
        expandTabView.setValue(mTextArray, mViewArray);
        expandTabView.setTitle("医院", 0);
        expandTabView.setTitle("科室", 1);
        expandTabView.setTitle("职称", 2);
    }

    private void initListener() {
        viewLeft.setOnSelectListener(new LeftFilterView.OnItemSelectListener() {

            @Override
            public void getValue(String showText) {
                onRefresh(viewLeft, showText);
            }
        });
        viewMiddle.setOnSelectListener(new MiddleFilterView.OnItemSelectListener() {

            @Override
            public void getValue(String showText) {
                onRefresh(viewMiddle, showText);
            }
        });

        viewRight.setOnSelectListener(new RightFilterView.OnSelectListener() {
            @Override
            public void getValue(String distance, String showText) {
                onRefresh(viewRight, showText);
            }
        });
    }

    private void onRefresh(View view, String showText) {
        expandTabView.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
            expandTabView.setTitle(showText, position);
        }
        Toast.makeText(mContext, showText, Toast.LENGTH_SHORT).show();
    }

    private int getPositon(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onLoad(final PullableListView pullableListView) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    specialistList.add("");
                    specialistList.add("");
                    specialistList.add("");
                    specialistList.add("");
                    specialistList.add("");
                    specialistList.add("");
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = pullableListView;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}