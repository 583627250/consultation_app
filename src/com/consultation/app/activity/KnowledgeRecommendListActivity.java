package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.consultation.app.R;
import com.consultation.app.model.RecommendTo;
import com.consultation.app.view.PullToRefreshLayout;
import com.consultation.app.view.PullToRefreshLayout.OnRefreshListener;
import com.consultation.app.view.PullableListView;
import com.consultation.app.view.PullableListView.OnLoadListener;


public class KnowledgeRecommendListActivity extends Activity implements OnLoadListener{
    
    private LinearLayout back_layout;
    
    private TextView back_text,title_text;
    
    private PullableListView recommendListView;

    private List<RecommendTo> recommend_content_list=new ArrayList<RecommendTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private RequestQueue mQueue;
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                myAdapter.notifyDataSetChanged();
                ((PullToRefreshLayout)msg.obj).refreshFinish(PullToRefreshLayout.SUCCEED);
                recommendListView.setHasMoreData(true);
                break;
            case 1:
                ((PullableListView)msg.obj).finishLoading();
                myAdapter.notifyDataSetChanged();
                break;
            }
            
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_recommend_list_layout);
        initDate();
        initView();
    }

    private void initView() {
        title_text = (TextView)findViewById(R.id.header_text);
        title_text.setText("推荐科普");
        title_text.setTextSize(20);
        
        back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text = (TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        ((PullToRefreshLayout)findViewById(R.id.knowledge_recommend_list_refresh_view))
        .setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            initDate();
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
        
        myAdapter = new MyAdapter();
        recommendListView=(PullableListView)findViewById(R.id.knowledge_recommend_list_listView);
        recommendListView.setAdapter(myAdapter);
        recommendListView.setOnLoadListener(this);
        recommendListView.setHasMoreData(false);
        recommendListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(KnowledgeRecommendListActivity.this, recommend_content_list.size()+"----"+recommend_content_list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDate() {
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
    
    private static class ViewHolder {

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
                convertView=LayoutInflater.from(KnowledgeRecommendListActivity.this).inflate(R.layout.recommend_list_item, null);
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

    @Override
    public void onLoad(final PullableListView pullableListView) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    initDate();
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
