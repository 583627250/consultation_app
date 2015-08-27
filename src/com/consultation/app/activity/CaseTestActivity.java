package com.consultation.app.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.TitleModel;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.PhoneUtil;
import com.consultation.app.util.SharePreferencesEditor;

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

    private TextView textAdd, tip;

    private List<TitleModel> titleModels;

    private Button saveBtn;
    
    private String caseId,departmentId;
    
    private SharePreferencesEditor editor;

    private Map<Integer, List<String>> pathMap=new HashMap<Integer, List<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_test_layout);
        context=this;
        editor=new SharePreferencesEditor(context);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        caseId = getIntent().getStringExtra("caseId");
        departmentId = getIntent().getStringExtra("departmentId");
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("检验");
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

        textAdd=(TextView)findViewById(R.id.test_add_image_text);
        textAdd.setText(Html.fromHtml("<u>" + "添加" + "</u>"));
        textAdd.setGravity(Gravity.CENTER_VERTICAL);
        textAdd.setTextColor(Color.BLUE);
        textAdd.setTextSize(17);
        textAdd.setVisibility(View.INVISIBLE);
        textAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CaseTestActivity.this, AddPatientPicActivity.class), currentPosition);
            }
        });

        tip=(TextView)findViewById(R.id.test_image_tip);
        tip.setTextSize(14);
        tip.setVisibility(View.INVISIBLE);

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.test_left_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                tip.setVisibility(View.VISIBLE);
                textAdd.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                showRightLayout(position);
                myAdapter.notifyDataSetChanged();
            }
        });

        saveBtn=(Button)findViewById(R.id.test_image_btn_save);
        saveBtn.setTextSize(17);
        saveBtn.setVisibility(View.INVISIBLE);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pathMap.get(currentPosition) == null || pathMap.get(currentPosition).size() == 0) {
                    Toast.makeText(context, "请添加图片", Toast.LENGTH_LONG).show();
                    return;
                }
                File[] files=new File[pathMap.get(currentPosition).size()];
                for(int i=0; i < pathMap.get(currentPosition).size(); i++) {
                    File file=new File(pathMap.get(currentPosition).get(i));
                    files[i]=file;
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("case_id", caseId);
                params.put("test_name", leftList.get(currentPosition));
                params.put("accessToken", ClientUtil.getToken());
                params.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(context);
                OpenApiService.getInstance(context).getUploadFiles(ClientUtil.GET_UPLOAD_IMAGES_URL, context,
                    new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            CommonUtil.closeLodingDialog();
                            // {"files":[{"fileSize":"1126 Kb","fileType":"application/octet-stream; charset=utf-8","bytes":null,"fileName":"1437968253117.jpg"},{"fileSize":"1126 Kb","fileType":"application/octet-stream; charset=utf-8","bytes":null,"fileName":"1437968253117.jpg"}]}
                            System.out.println("sccTextis ========> " + rspContent);
                            Toast.makeText(context, "图片上传成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                            Toast.makeText(context, "图片上传失败，请重新上传", Toast.LENGTH_LONG).show();
                            CommonUtil.closeLodingDialog();
                        }
                    }, files, params);
            }
        });
        saveBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
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
        imageView.setOnLongClickListener(this);
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        Bitmap bitmap=CommonUtil.readBitMap(PhoneUtil.getInstance().getWidth(CaseTestActivity.this), path);
        imageView.setImageBitmap(bitmap);
        relativeLayout.addView(imageView);

        return relativeLayout;
    }

    private void initData() {
        initCaseDatas("123456case.xml");
        titleModels=caseList.get(0).getTitleModels();
        for(int i=0; i < titleModels.size(); i++) {
            leftList.add(titleModels.get(i).getTitle());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            String photoPath=data.getStringExtra("bitmap");
            List<String> paths=pathMap.get(currentPosition);
            if(null == paths) {
                paths=new ArrayList<String>();
                paths.add(photoPath);
                pathMap.put(currentPosition, paths);
            } else {
                if(!paths.contains(photoPath)){
                    paths.add(photoPath);
                }
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

    @Override
    public boolean onLongClick(View v) {
        showDialogs(v.getId());
        return false;
    }

    private void showDialogs(final int index) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("删除该图片?").setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                pathMap.get(currentPosition).remove(index);
                showRightLayout(currentPosition);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).create().show();
    }
}
