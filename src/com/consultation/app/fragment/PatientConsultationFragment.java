package com.consultation.app.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.consultation.app.R;

public class PatientConsultationFragment extends Fragment {
    
    private View consultationLayout;

    private TextView header_text;

    private ViewPager mPager;// 页卡内容

    private List<Fragment> listViews; // Tab页面列表

    private ImageView cursor;// 动画图片

    private TextView t1, t2, t3, t4;// 页卡头标

    private int offset=0;// 动画图片偏移量

    private int currIndex=0;// 当前页卡编号

    private int bmpW;// 动画图片宽度
    
    private static Context mContext;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        consultationLayout=inflater.inflate(R.layout.consultation_layout, container, false);
        initLayout();
        return consultationLayout;
    }

    private void initLayout() {
        header_text=(TextView)consultationLayout.findViewById(R.id.header_text);
        header_text.setText("我的病例");
        header_text.setTextSize(20);

        t1 = (TextView)consultationLayout.findViewById(R.id.consulation_list_title_1);
        t1.setText("全部");
        t1.setTextSize(17);
        t2 = (TextView)consultationLayout.findViewById(R.id.consulation_list_title_2);
        t2.setText("专家咨询");
        t2.setTextSize(17);
        t3 = (TextView)consultationLayout.findViewById(R.id.consulation_list_title_3);
        t3.setText("公开讨论");
        t3.setTextSize(17);
        t4 = (TextView)consultationLayout.findViewById(R.id.consulation_list_title_4);
        t4.setText("历史");
        t4.setTextSize(17);
        TextView t5 = (TextView)consultationLayout.findViewById(R.id.consulation_list_title_5);
        t5.setVisibility(View.GONE);
        t1.setTextColor(Color.parseColor("#2CB67A"));
        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        t3.setOnClickListener(new MyOnClickListener(2));
        t4.setOnClickListener(new MyOnClickListener(3));
        InitImageView();
        InitViewPager();
    }
    
    public static PatientConsultationFragment getInstance(Context context) { 
        mContext = context;
        return new PatientConsultationFragment();
    } 
    
    private void InitImageView() {
        cursor = (ImageView)consultationLayout.findViewById(R.id.consulation_list_title_image);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.pageview_title_image)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }
    
    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量
        int three = one * 3;
        
        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
            case 0:
                t1.setTextColor(Color.parseColor("#2CB67A"));
                t2.setTextColor(Color.parseColor("#A2A2A2"));
                t3.setTextColor(Color.parseColor("#A2A2A2"));
                t4.setTextColor(Color.parseColor("#A2A2A2"));
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(three, 0, 0, 0);
                }
                break;
            case 1:
                t2.setTextColor(Color.parseColor("#2CB67A"));
                t1.setTextColor(Color.parseColor("#A2A2A2"));
                t3.setTextColor(Color.parseColor("#A2A2A2"));
                t4.setTextColor(Color.parseColor("#A2A2A2"));
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(three, one, 0, 0);
                }
                break;
            case 2:
                t3.setTextColor(Color.parseColor("#2CB67A"));
                t1.setTextColor(Color.parseColor("#A2A2A2"));
                t2.setTextColor(Color.parseColor("#A2A2A2"));
                t4.setTextColor(Color.parseColor("#A2A2A2"));
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(three, two, 0, 0);
                }
                break;
            case 3:
                t4.setTextColor(Color.parseColor("#2CB67A"));
                t1.setTextColor(Color.parseColor("#A2A2A2"));
                t2.setTextColor(Color.parseColor("#A2A2A2"));
                t3.setTextColor(Color.parseColor("#A2A2A2"));
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, three, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, three, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, three, 0, 0);
                } 
                break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };
    
    private void InitViewPager() {
        mPager = (ViewPager)consultationLayout.findViewById(R.id.consulation_list_viewPager);
        listViews = new ArrayList<Fragment>();
        listViews.add(new PatientConsultationAllFragment());
        listViews.add(new PatientConsultationDoingFragment());
        listViews.add(new PatientConsultationDiscussionFragment());
        listViews.add(new PatientConsultationHistoryFragment());
        mPager.setAdapter(new MyPagerAdapter(((FragmentActivity)mContext).getSupportFragmentManager(),listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    
    public class MyPagerAdapter extends FragmentPagerAdapter {
        
        public List<Fragment> mListViews;

        public MyPagerAdapter(FragmentManager fragmentManager,List<Fragment> mListViews) {
            super(fragmentManager);
            this.mListViews = mListViews;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mListViews.get(arg0);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }
    }
}