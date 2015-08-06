package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.model.TitleModel;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.PhoneUtil;

@SuppressLint("UseSparseArrays")
public class CaseTestActivity extends CaseBaseActivity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ListView listView;

    private LinearLayout rightLayout;

    private MyAdapter myAdapter;

    private List<String> leftList=new ArrayList<String>();

    private ViewHolder holder;

    private int currentPosition=-1;

    private Context context;

    private int width;

    private int height;

    private TextView textAdd;

    private Map<Integer, List<String>> images=new HashMap<Integer, List<String>>();

    private boolean isCreate=false;

    private View imagesView;

    private List<TitleModel> titleModels;

    // private int page;

    private Button saveBtn;

    private ImageView image0, image1, image2, image3, image4, image5, image6, image7;

    private LinearLayout layout2, layout3, layout4;

    private List<ImageView> imageList=new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_symptom_layout);
        context=this;
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        // page=getIntent().getIntExtra("page", -1);
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("检验");
        title_text.setTextSize(20);

        imagesView=LayoutInflater.from(CaseTestActivity.this).inflate(R.layout.create_case_image_layout, null);

        image0=(ImageView)imagesView.findViewById(R.id.create_case_add_image_0);
        image1=(ImageView)imagesView.findViewById(R.id.create_case_add_image_1);
        image2=(ImageView)imagesView.findViewById(R.id.create_case_add_image_2);
        image3=(ImageView)imagesView.findViewById(R.id.create_case_add_image_3);
        image4=(ImageView)imagesView.findViewById(R.id.create_case_add_image_4);
        image5=(ImageView)imagesView.findViewById(R.id.create_case_add_image_5);
        image6=(ImageView)imagesView.findViewById(R.id.create_case_add_image_6);
        image7=(ImageView)imagesView.findViewById(R.id.create_case_add_image_7);

        image0.setOnLongClickListener(this);
        image1.setOnLongClickListener(this);
        image2.setOnLongClickListener(this);
        image3.setOnLongClickListener(this);
        image4.setOnLongClickListener(this);
        image5.setOnLongClickListener(this);
        image6.setOnLongClickListener(this);
        image7.setOnLongClickListener(this);

        layout2=(LinearLayout)imagesView.findViewById(R.id.create_case_add_image_layout_2_0);
        layout3=(LinearLayout)imagesView.findViewById(R.id.create_case_add_image_layout_3_0);
        layout4=(LinearLayout)imagesView.findViewById(R.id.create_case_add_image_layout_4_0);

        InvisibleAllImage();
        imageList.add(image0);
        imageList.add(image1);
        imageList.add(image2);
        imageList.add(image3);
        imageList.add(image4);
        imageList.add(image5);
        imageList.add(image6);
        imageList.add(image7);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                // Intent intent=new Intent();
                // Bundle bundle=new Bundle();
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                // intent.putExtras(bundle);
                // setResult(Activity.RESULT_OK, intent);
                // finish();
            }
        });
        rightLayout=(LinearLayout)findViewById(R.id.syamptom_right_layout);

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.syamptom_left_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                if(isCreate) {
                    showRightLayout(position);
                } else {
                    isCreate=true;
                    rightLayout.addView(createmImageViews());
                }
                myAdapter.notifyDataSetChanged();
            }
        });

        saveBtn=(Button)findViewById(R.id.syamptom_btn_save);
        saveBtn.setTextSize(20);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存数据
                List<String> paths=images.get(0);
                String[] strs = new String[paths.size()];
                for(int i=0; i < paths.size(); i++) {
                    strs[i] = paths.get(i);
                }
                Intent intent=new Intent();
                intent.putExtra("paths", strs);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void showRightLayout(int position) {
        // 影藏所有图片
        InvisibleAllImage();
        List<String> imagePaths=images.get(currentPosition);
        if(null != imagePaths && imagePaths.size() != 0) {
            for(int i=0; i < imagePaths.size(); i++) {
                ImageView view=imageList.get(i);
                if(i == 2) {
                    layout2.setVisibility(View.VISIBLE);
                } else if(i == 4) {
                    layout3.setVisibility(View.VISIBLE);
                } else if(i == 6) {
                    layout4.setVisibility(View.VISIBLE);
                }
                view.setVisibility(View.VISIBLE);
                Bitmap bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CaseTestActivity.this), imagePaths.get(i));
                view.setImageBitmap(bitmap);
            }
        }
    }

    private void InvisibleAllImage() {
        image0.setVisibility(View.GONE);
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);
        image5.setVisibility(View.GONE);
        image6.setVisibility(View.GONE);
        image7.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
    }

    // private boolean saveData() {
    // boolean isAdd=false;
    // // for(String key: images.keySet()) {
    // // ImageView view=images.get(key);
    // //
    // // }
    // // isSave=true;
    // return isAdd;
    // }

    private void initData() {
        initCaseDatas();
        titleModels=caseList.get(0).getTitleModels();
        for(int i=0; i < titleModels.size(); i++) {
            leftList.add(titleModels.get(i).getTitle());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            String photoPath=data.getStringExtra("bitmap");
            List<String> paths=images.get(currentPosition);
            if(null == paths) {
                paths=new ArrayList<String>();
                paths.add(photoPath);
                images.put(currentPosition, paths);
            } else {
                paths.add(photoPath);
            }
            showRightLayout(currentPosition);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                convertView=LayoutInflater.from(CaseTestActivity.this).inflate(R.layout.search_recommend_list_item, null);
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

    private LinearLayout createmImageViews() {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);

        LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textNameParams.leftMargin=width / 40;

        textAdd=new TextView(context);
        textAdd.setLayoutParams(textNameParams);
        textAdd.setText(Html.fromHtml("<u>" + "添加" + "</u>"));
        textAdd.setGravity(Gravity.CENTER_VERTICAL);
        textAdd.setTextColor(Color.BLUE);
        textAdd.setTextSize(17);
        textAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(null != images.get(currentPosition) && images.get(currentPosition).size() == 8) {
                    Toast.makeText(CaseTestActivity.this, "最多只能添加七张图片", Toast.LENGTH_LONG).show();
                    return;
                }
                startActivityForResult(new Intent(CaseTestActivity.this, AddPatientPicActivity.class), currentPosition);
            }
        });
        layout.addView(textAdd);
        layout.addView(imagesView);
        TextView textName=new TextView(context);
        textName.setLayoutParams(textNameParams);
        textName.setText("提示：长按已选图片可删除图片，最多添加八张图片");
        textName.setGravity(Gravity.CENTER_VERTICAL);
        textName.setTextColor(Color.RED);
        textName.setTextSize(14);
        layout.addView(textName);
        return layout;
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()) {
            case R.id.create_case_add_image_0:
                showDialogs(0);
                break;
            case R.id.create_case_add_image_1:
                showDialogs(1);
                break;
            case R.id.create_case_add_image_2:
                showDialogs(2);
                break;
            case R.id.create_case_add_image_3:
                showDialogs(3);
                break;
            case R.id.create_case_add_image_4:
                showDialogs(4);
                break;
            case R.id.create_case_add_image_5:
                showDialogs(5);
                break;
            case R.id.create_case_add_image_6:
                showDialogs(6);
                break;
            case R.id.create_case_add_image_7:
                showDialogs(7);
                break;

            default:
                break;
        }
        return false;
    }

    private void showDialogs(final int index) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("删除该图片?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                images.get(currentPosition).remove(index);
                showRightLayout(currentPosition);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).create().show();
    }
}
