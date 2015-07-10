package com.consultation.app.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.activity.KnowledgeRecommendListActivity;
import com.consultation.app.model.RecommendTo;
import com.consultation.app.view.MyImgScroll;

public class KnowledgeFragment extends Fragment {
    
    private View knowledgeLayout;

    private RequestQueue mQueue;
    
//    private ImageLoader mImageLoader;

    private TextView header_text;

    private TextView department_text,more_text;
    
    private TextView fckText,ekText,ctText,gkText,pfkText,nkText,lrkfText,moreText;

    private MyImgScroll myPager; // 图片容器

    private LinearLayout ovalLayout,fckLayout,ekLayout,ctLayout,gkLayout,pfkLayout,nkLayout,lrkfLayout,moreLayout; // 圆点容器

    private List<View> listViews; // 图片组

    private ListView listView;

    private LayoutInflater knowledgeInflater;
    
    private List<RecommendTo> recommend_content_list=new ArrayList<RecommendTo>();

    private static Context context;
    
    private ViewHolder holder;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        knowledgeLayout=inflater.inflate(R.layout.knowledge_layout, container, false);
        knowledgeInflater=inflater;
        initLayout();
        return knowledgeLayout;
    }
    
    public static KnowledgeFragment getInstance(Context ctx) { 
        context = ctx;
        return new KnowledgeFragment();
    } 
    
    private void initData() {
        RecommendTo TO1=new RecommendTo();
        TO1.setAuthor("王忠");
        TO1.setDepartment("内科");
        TO1.setTitle("关于药物的服用时间和注意事项");
        
        RecommendTo TO2=new RecommendTo();
        TO2.setAuthor("老冯");
        TO2.setDepartment("内科");
        TO2.setTitle("发烧就要用退烧药么？");
        
        RecommendTo TO3=new RecommendTo();
        TO3.setAuthor("季秀君");
        TO3.setDepartment("皮肤科");
        TO3.setTitle("痣，祛还是留");
        
        RecommendTo TO4=new RecommendTo();
        TO4.setAuthor("马晓红");
        TO4.setDepartment("儿科");
        TO4.setTitle("婴幼儿血管瘤需要综合治疗");
        
        RecommendTo TO5=new RecommendTo();
        TO5.setAuthor("刘永生");
        TO5.setDepartment("皮肤科");
        TO5.setTitle("手总脱皮是缺少维生素？");
        
        recommend_content_list.add(TO1);
        recommend_content_list.add(TO2);
        recommend_content_list.add(TO3);
        recommend_content_list.add(TO4);
        recommend_content_list.add(TO5);
    }

    private void initLayout() {
        mQueue = Volley.newRequestQueue(context);
//        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        initData();
        header_text=(TextView)knowledgeLayout.findViewById(R.id.header_text);
        header_text.setText("科普知识");
        header_text.setTextSize(20);

        myPager = (MyImgScroll)knowledgeLayout.findViewById(R.id.image_scroll);
        ovalLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.image_scroll_layout);
        InitViewPager();//初始化图片
        //开始滚动
        myPager.start((Activity)context, listViews, 4000, ovalLayout,
                R.layout.ad_bottom_item, R.id.ad_item_v,
                R.drawable.dot_focused, R.drawable.dot_normal);

        department_text=(TextView)knowledgeLayout.findViewById(R.id.department_text);
        department_text.setTextSize(16);

        fckText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_1_text);
        fckText.setTextSize(14);
        ekText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_2_text);
        ekText.setTextSize(14);
        ctText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_3_text);
        ctText.setTextSize(14);
        gkText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_4_text);
        gkText.setTextSize(14);
        pfkText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_5_text);
        pfkText.setTextSize(14);
        nkText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_6_text);
        nkText.setTextSize(14);
        lrkfText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_7_text);
        lrkfText.setTextSize(14);
        moreText=(TextView)knowledgeLayout.findViewById(R.id.hot_department_8_text);
        moreText.setTextSize(14);
        
        
        
        more_text=(TextView)knowledgeLayout.findViewById(R.id.knowledge_listView_more);
        more_text.setTextSize(18);
        more_text.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "加载跟多", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, KnowledgeRecommendListActivity.class));
            }
        });
        listView=(ListView)knowledgeLayout.findViewById(R.id.knowledge_listView);
        listView.setAdapter(new MyAdapter());
        setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                // 推荐详情
                Toast.makeText(context, "第"+position+"篇", Toast.LENGTH_SHORT).show();
            }
        });
        fckLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_fck);
        fckLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击妇产科", Toast.LENGTH_SHORT).show();
            }
        });
        ekLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_ek);
        ekLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击儿科", Toast.LENGTH_SHORT).show();
            }
        });
        ctLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_ct);
        ctLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击内科", Toast.LENGTH_SHORT).show();
            }
        });
        gkLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_gk);
        gkLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击骨科", Toast.LENGTH_SHORT).show();
            }
        });
        pfkLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_pfk);
        pfkLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击皮肤科", Toast.LENGTH_SHORT).show();
            }
        });
        nkLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_nk);
        nkLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击脑科", Toast.LENGTH_SHORT).show();
            }
        });
        lrkfLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_lrkf);
        lrkfLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击老人康复", Toast.LENGTH_SHORT).show();
            }
        });
        moreLayout = (LinearLayout)knowledgeLayout.findViewById(R.id.knowledge_hot_department_more);
        moreLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击更多", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 初始化图片
     */
    private void InitViewPager() {
        listViews = new ArrayList<View>();
        int[] imageResId = new int[] { R.drawable.a, R.drawable.b,
                R.drawable.c, R.drawable.d, R.drawable.e };
        for (int i = 0; i < imageResId.length; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {// 设置图片点击事件
                    Toast.makeText(context,
                            "点击了:" + myPager.getCurIndex(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
            imageView.setImageResource(imageResId[i]);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            listViews.add(imageView);
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight=0;
        for(int i=0; i < listAdapter.getCount(); i++) {
            View listItem=listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height=totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    
    private static class ViewHolder {

//        ImageView photo;

        TextView title;

        TextView author;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recommend_content_list.size();
        }

        @Override
        public Object getItem(int location) {
            return recommend_content_list.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=knowledgeInflater.inflate(R.layout.recommend_list_item, null);
                holder=new ViewHolder();
                holder.title=(TextView)convertView.findViewById(R.id.recommend_list_item_text_title);
                holder.author=(TextView)convertView.findViewById(R.id.recommend_list_item_text_author);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.title.setTextSize(18);
            holder.title.setText(recommend_content_list.get(position).getTitle());
            holder.author.setTextSize(15);
            holder.author.setText("@" + recommend_content_list.get(position).getDepartment() + "-"
                + recommend_content_list.get(position).getAuthor());
//            final String imgUrl=recommend_content_list.get(position).getPhoto();
//            // 给 ImageView 设置一个 tag
//            holder.photo.setTag(imgUrl);
//            // 预设一个图片
//            holder.photo.setImageResource(CommonUtil.getResourceId(context, "drawable", "pfk"));
//            if(imgUrl != null && !imgUrl.equals("")) {
//                ImageListener listener = ImageLoader.getImageListener(holder.photo, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
//                mImageLoader.get(imgUrl, listener);
//            }
            return convertView;
        }
    }
}