package com.consultation.app.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.CaseParams;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.TitleModel;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.GetPathFromUri4kitkat;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.SelectPicDialog;

@SuppressLint({"UseSparseArrays", "HandlerLeak"})
public class CaseTestActivity extends CaseBaseActivity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ListView listView;

    private LinearLayout rightLayout;

    private MyAdapter myAdapter;

    private List<String> leftList=new ArrayList<String>();

    private ViewHolder holder;

    private int currentPosition=0;

    private int page;

    private Context context;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private int width;

    private int height;

    private TextView tip;

    private View currentView;

    private List<TitleModel> titleModels=new ArrayList<TitleModel>();

    private Button saveBtn;

    private boolean isAdd=false, havaNew=false;

    private String caseId, imageString, departmentId="";

    private SharePreferencesEditor editor;

    private Uri photoUri;

    private Cursor cursor;

    private Map<Integer, List<String>> pathMap=new HashMap<Integer, List<String>>();

    private Map<Integer, List<String>> idMap=new HashMap<Integer, List<String>>();

    private Map<Integer, List<String>> bigPathMap=new HashMap<Integer, List<String>>();

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_test_layout);
        if(savedInstanceState != null) {
            ClientUtil.setCaseParams((CaseParams)savedInstanceState.getSerializable("caseParams"));
            currentPosition=savedInstanceState.getInt("currentPosition");
            isAdd=savedInstanceState.getBoolean("isAdd");
            // 解析数据组装map集合
            if(savedInstanceState.getString("photoUri") != null) {
                photoUri=Uri.parse(savedInstanceState.getString("photoUri"));
            }
            // pathMap.clear();
            // idMap.clear();
            // bigPathMap.clear();
            // ArrayList<Integer> pathMapKey=savedInstanceState.getIntegerArrayList("pathMapKey");
            // ArrayList<String> pathMapValue=savedInstanceState.getStringArrayList("pathMapValue");
            // for(int i=0; i < pathMapKey.size(); i++) {
            // ArrayList<String> temp = new ArrayList<String>();
            // if(pathMapValue.get(i).contains(",")){
            // String[] valueStrings = pathMapValue.get(i).split(",");
            // for(String string: valueStrings) {
            // temp.add(string);
            // }
            // pathMap.put(pathMapKey.get(i), temp);
            // }else{
            // temp.add(pathMapValue.get(i));
            // pathMap.put(pathMapKey.get(i), temp);
            // }
            // }
            //
            // ArrayList<Integer> idMapKey=savedInstanceState.getIntegerArrayList("pathMapKey");
            // ArrayList<String> idMapValue=savedInstanceState.getStringArrayList("pathMapValue");
            // for(int i=0; i < idMapKey.size(); i++) {
            // ArrayList<String> temp = new ArrayList<String>();
            // if(idMapValue.get(i).contains(",")){
            // String[] valueStrings = idMapValue.get(i).split(",");
            // for(String string: valueStrings) {
            // temp.add(string);
            // }
            // idMap.put(idMapKey.get(i), temp);
            // }else{
            // temp.add(idMapValue.get(i));
            // idMap.put(idMapKey.get(i), temp);
            // }
            // }
            //
            // ArrayList<Integer> bigPathMapKey=savedInstanceState.getIntegerArrayList("pathMapKey");
            // ArrayList<String> bigPathMapValue=savedInstanceState.getStringArrayList("pathMapValue");
            // for(int i=0; i < bigPathMapKey.size(); i++) {
            // ArrayList<String> temp = new ArrayList<String>();
            // if(bigPathMapValue.get(i).contains(",")){
            // String[] valueStrings = bigPathMapValue.get(i).split(",");
            // for(String string: valueStrings) {
            // temp.add(string);
            // }
            // bigPathMap.put(bigPathMapKey.get(i), temp);
            // }else{
            // temp.add(bigPathMapValue.get(i));
            // bigPathMap.put(bigPathMapKey.get(i), temp);
            // }
            // }

        }
        context=this;
        editor=new SharePreferencesEditor(context);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        caseId=getIntent().getStringExtra("caseId");
        departmentId=getIntent().getStringExtra("departmentId");
        page=getIntent().getIntExtra("page", -1);
        imageString=getIntent().getStringExtra("imageString");
        mQueue=Volley.newRequestQueue(context);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("caseParams", ClientUtil.getCaseParams());
        outState.putInt("currentPosition", currentPosition);
        outState.putBoolean("isAdd", isAdd);
        if(photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }
        // ArrayList<Integer> pathMapKey=new ArrayList<Integer>();
        // ArrayList<String> pathMapValue=new ArrayList<String>();
        // for(Integer key: pathMap.keySet()) {
        // pathMapKey.add(key);
        // StringBuffer sb=new StringBuffer();
        // for(int i=0; i < pathMap.get(key).size(); i++) {
        // if(i == 0) {
        // sb.append(pathMap.get(key).get(i));
        // } else {
        // sb.append(",").append(pathMap.get(key).get(i));
        // }
        // }
        // pathMapValue.add(sb.toString());
        // }
        // outState.putIntegerArrayList("pathMapKey", pathMapKey);
        // outState.putStringArrayList("pathMapValue", pathMapValue);
        //
        // ArrayList<Integer> idMapKey=new ArrayList<Integer>();
        // ArrayList<String> idMapValue=new ArrayList<String>();
        // for(Integer key: idMap.keySet()) {
        // idMapKey.add(key);
        // StringBuffer sb=new StringBuffer();
        // for(int i=0; i < idMap.get(key).size(); i++) {
        // if(i == 0) {
        // sb.append(idMap.get(key).get(i));
        // } else {
        // sb.append(",").append(idMap.get(key).get(i));
        // }
        // }
        // idMapValue.add(sb.toString());
        // }
        // outState.putIntegerArrayList("idMapKey", idMapKey);
        // outState.putStringArrayList("idMapValue", idMapValue);
        //
        // ArrayList<Integer> bigPathMapKey=new ArrayList<Integer>();
        // ArrayList<String> bigPathMapValue=new ArrayList<String>();
        // for(Integer key: bigPathMap.keySet()) {
        // bigPathMapKey.add(key);
        // StringBuffer sb=new StringBuffer();
        // for(int i=0; i < bigPathMap.get(key).size(); i++) {
        // if(i == 0) {
        // sb.append(bigPathMap.get(key).get(i));
        // } else {
        // sb.append(",").append(bigPathMap.get(key).get(i));
        // }
        // }
        // bigPathMapValue.add(sb.toString());
        // }
        // outState.putIntegerArrayList("bigPathMapKey", bigPathMapKey);
        // outState.putStringArrayList("bigPathMapValue", bigPathMapValue);
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        if(page == 5) {
            title_text.setText("检验");
        } else {
            title_text.setText("检查");
        }
        title_text.setTextSize(20);
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isAdd", isAdd);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        TextView rightText=(TextView)findViewById(R.id.header_right);
        rightText.setVisibility(View.GONE);

        ImageView rightImageView=(ImageView)findViewById(R.id.header_right_image);
        rightImageView.setVisibility(View.VISIBLE);
        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final SelectPicDialog dialog=
                    new SelectPicDialog(CaseTestActivity.this, R.style.selectPicDialog, R.layout.select_pic_dialog);
                dialog.setCancelable(true);
                dialog.setPhotographButton(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String SDState=Environment.getExternalStorageState();
                        if(SDState.equals(Environment.MEDIA_MOUNTED)) {
                            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ContentValues values=new ContentValues();
                            photoUri=
                                CaseTestActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values);
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, 2);
                        } else {
                            Toast.makeText(CaseTestActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.setSelectButton(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent=new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 3);
                    }
                });
                dialog.show();
            }
        });

        rightLayout=(LinearLayout)findViewById(R.id.test_right_layout);

        // textAdd=(TextView)findViewById(R.id.test_add_image_text);
        // textAdd.setText(Html.fromHtml("<u>" + "添加" + "</u>"));
        // textAdd.setGravity(Gravity.CENTER_VERTICAL);
        // textAdd.setTextColor(Color.BLUE);
        // textAdd.setTextSize(17);
        // textAdd.setVisibility(View.INVISIBLE);
        // textAdd.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // startActivityForResult(new Intent(CaseTestActivity.this, AddPatientPicActivity.class), 0);
        //
        // final SelectPicDialog dialog=
        // new SelectPicDialog(CaseTestActivity.this, R.style.selectPicDialog, R.layout.select_pic_dialog);
        // dialog.setCancelable(true);
        // dialog.setPhotographButton(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // dialog.dismiss();
        // String SDState=Environment.getExternalStorageState();
        // if(SDState.equals(Environment.MEDIA_MOUNTED)) {
        // Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ContentValues values=new ContentValues();
        // photoUri=
        // CaseTestActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        // values);
        // intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
        // startActivityForResult(intent, 2);
        // } else {
        // Toast.makeText(CaseTestActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
        // }
        // }
        // });
        // dialog.setSelectButton(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // dialog.dismiss();
        // Intent intent=new Intent();
        // intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        // startActivityForResult(intent, 3);
        // }
        // });
        // dialog.show();
        // }
        // });

        tip=(TextView)findViewById(R.id.test_image_tip);
        tip.setTextSize(14);

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.test_left_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentView != null) {
                    currentView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                currentView=view;
                view.setBackgroundColor(Color.parseColor("#E4E4E4"));
                currentPosition=position;
                showRightLayout(position);
                myAdapter.notifyDataSetChanged();
                showRightLayout(currentPosition);
            }
        });

        saveBtn=(Button)findViewById(R.id.test_image_btn_save);
        saveBtn.setTextSize(17);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pathMap.get(currentPosition) == null || pathMap.get(currentPosition).size() == 0) {
                    Toast.makeText(context, "请添加图片", Toast.LENGTH_LONG).show();
                    return;
                }
                List<String> temp=new ArrayList<String>();
                for(int i=0; i < pathMap.get(currentPosition).size(); i++) {
                    if(!pathMap.get(currentPosition).get(i).startsWith("http:")) {
                        temp.add(pathMap.get(currentPosition).get(i));
                    }
                }
                if(temp == null || temp.size() == 0 || !havaNew) {
                    Toast.makeText(context, "请添加新图片", Toast.LENGTH_LONG).show();
                    return;
                }
                File[] files=new File[temp.size()];
                for(int i=0; i < temp.size(); i++) {
                    files[i]=CommonUtil.getSmallBitmapFile(temp.get(i));
                    // File file=new File(temp.get(i));
                    // files[i]=file;
                }
                Map<String, String> params=new HashMap<String, String>();
                params.put("case_id", caseId);
                params.put("test_name", leftList.get(currentPosition));
                if(page == 5) {
                    params.put("case_item", "jy");
                } else {
                    params.put("case_item", "jc");
                }
                params.put("accessToken", ClientUtil.getToken());
                params.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(context);
                OpenApiService.getInstance(context).getUploadFiles(ClientUtil.GET_UPLOAD_IMAGES_URL, context,
                    new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            CommonUtil.closeLodingDialog();
                            try {
                                isAdd=true;
                                havaNew=false;
                                JSONObject repJsonObject=new JSONObject(rspContent);
                                if(ClientUtil.getCaseParams().getValue(page + "") == null
                                    || !"".equals(ClientUtil.getCaseParams().getValue(page + ""))) {
                                    String addNewImage=repJsonObject.getString("caseFiles");
                                    ClientUtil.getCaseParams().add(page + "", addNewImage);
                                } else {
                                    String newString=repJsonObject.getString("caseFiles").replace("[", ",");
                                    String oldString=ClientUtil.getCaseParams().getValue(page + "").replace("]", "");
                                    String addNewImage=newString + oldString;
                                    ClientUtil.getCaseParams().add(page + "", addNewImage);
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
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
        showRightLayout(currentPosition);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            bundle.putBoolean("isAdd", isAdd);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                relativeLayout=createImage(imagePaths.get(i), i, bigPathMap.get(currentPosition).get(i));
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

    private RelativeLayout createImage(String path, int id, final String bigPath) {
        RelativeLayout relativeLayout=new RelativeLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 55;
        layoutParams.weight=1;
        relativeLayout.setLayoutParams(layoutParams);
        ImageView imageView=new ImageView(context);
        imageView.setId(id);
        imageView.setOnLongClickListener(this);
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 展示大图片
                BigImageActivity.setViewData(bigPath);
                startActivity(new Intent(CaseTestActivity.this, BigImageActivity.class));
            }
        });
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        if(!"null".equals(path) && !"".equals(path)) {
            if(path.startsWith("http:")) {
                imageView.setTag(path);
                imageView.setBackgroundResource(R.anim.loading_anim);
                AnimationDrawable animation=(AnimationDrawable)imageView.getBackground();
                animation.start();
                ImageListener listener=ImageLoader.getImageListener(imageView, 0, android.R.drawable.ic_menu_delete);
                mImageLoader.get(path, listener, 200, 200);
            } else {
                Bitmap bitmap=CommonUtil.readBitMap(200, path);
                imageView.setImageBitmap(bitmap);
            }
        }
        relativeLayout.addView(imageView);
        return relativeLayout;
    }

    private void initData() {
        initCaseDatas(departmentId + "case.xml");
        titleModels=caseList.get(page).getTitleModels();
        for(int i=0; i < titleModels.size(); i++) {
            leftList.add(titleModels.get(i).getTitle());
        }
        String addNewImage=ClientUtil.getCaseParams().getValue(page + "");
        if(null != imageString && !"".equals(imageString)) {
            try {
                JSONArray jsonArray=new JSONArray(imageString);
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject imageFilesObject=jsonArray.getJSONObject(i);
                    if(page == 5 && imageFilesObject.getString("case_item").equals("jy")) {
                        int index=leftList.indexOf(imageFilesObject.getString("test_name"));
                        List<String> paths=pathMap.get(index);
                        List<String> bigPaths=bigPathMap.get(index);
                        List<String> ids=idMap.get(index);
                        if(null == paths) {
                            paths=new ArrayList<String>();
                            paths.add(imageFilesObject.getString("little_pic_url"));
                            pathMap.put(index, paths);
                        } else {
                            paths.add(imageFilesObject.getString("little_pic_url"));
                        }
                        if(null == ids) {
                            ids=new ArrayList<String>();
                            ids.add(imageFilesObject.getString("id"));
                            idMap.put(index, ids);
                        } else {
                            ids.add(imageFilesObject.getString("id"));
                        }
                        if(null == bigPaths) {
                            bigPaths=new ArrayList<String>();
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                            bigPathMap.put(index, bigPaths);
                        } else {
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                        }
                    } else if(page == 6 && imageFilesObject.getString("case_item").equals("jc")) {
                        int index=leftList.indexOf(imageFilesObject.getString("test_name"));
                        List<String> paths=pathMap.get(index);
                        List<String> bigPaths=bigPathMap.get(index);
                        List<String> ids=idMap.get(index);
                        if(null == paths) {
                            paths=new ArrayList<String>();
                            paths.add(imageFilesObject.getString("little_pic_url"));
                            pathMap.put(index, paths);
                        } else {
                            paths.add(imageFilesObject.getString("little_pic_url"));
                        }
                        if(null == ids) {
                            ids=new ArrayList<String>();
                            ids.add(imageFilesObject.getString("id"));
                            idMap.put(index, ids);
                        } else {
                            ids.add(imageFilesObject.getString("id"));
                        }
                        if(null == bigPaths) {
                            bigPaths=new ArrayList<String>();
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                            bigPathMap.put(index, bigPaths);
                        } else {
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                        }
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        if(null != addNewImage && !"".equals(addNewImage)) {
            try {
                JSONArray jsonArray=new JSONArray(addNewImage);
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject imageFilesObject=jsonArray.getJSONObject(i);
                    if(page == 5 && imageFilesObject.getString("case_item").equals("jy")) {
                        int index=leftList.indexOf(imageFilesObject.getString("test_name"));
                        List<String> paths=pathMap.get(index);
                        List<String> bigPaths=bigPathMap.get(index);
                        List<String> ids=idMap.get(index);
                        if(null == paths) {
                            paths=new ArrayList<String>();
                            paths.add(imageFilesObject.getString("little_pic_url"));
                            pathMap.put(index, paths);
                        } else {
                            paths.add(imageFilesObject.getString("little_pic_url"));
                        }
                        if(null == ids) {
                            ids=new ArrayList<String>();
                            ids.add(imageFilesObject.getString("id"));
                            idMap.put(index, ids);
                        } else {
                            ids.add(imageFilesObject.getString("id"));
                        }
                        if(null == bigPaths) {
                            bigPaths=new ArrayList<String>();
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                            bigPathMap.put(index, bigPaths);
                        } else {
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                        }
                    } else if(page == 6 && imageFilesObject.getString("case_item").equals("jc")) {
                        int index=leftList.indexOf(imageFilesObject.getString("test_name"));
                        List<String> paths=pathMap.get(index);
                        List<String> bigPaths=bigPathMap.get(index);
                        List<String> ids=idMap.get(index);
                        if(null == paths) {
                            paths=new ArrayList<String>();
                            paths.add(imageFilesObject.getString("little_pic_url"));
                            pathMap.put(index, paths);
                        } else {
                            paths.add(imageFilesObject.getString("little_pic_url"));
                        }
                        if(null == ids) {
                            ids=new ArrayList<String>();
                            ids.add(imageFilesObject.getString("id"));
                            idMap.put(index, ids);
                        } else {
                            ids.add(imageFilesObject.getString("id"));
                        }
                        if(null == bigPaths) {
                            bigPaths=new ArrayList<String>();
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                            bigPathMap.put(index, bigPaths);
                        } else {
                            bigPaths.add(imageFilesObject.getString("pic_url"));
                        }
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode) {
            case 2:
                if(resultCode == Activity.RESULT_OK) {
                    doPhoto(requestCode, data);
                }
                break;
            case 3:
                if(resultCode == Activity.RESULT_OK) {
                    doPhoto(requestCode, data);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    if(!pathMap.get(currentPosition).get(data.getIntExtra("index", 0)).startsWith("http")) {
                        pathMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                        bigPathMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                        showRightLayout(currentPosition);
                    } else {
                        Map<String, String> parmas=new HashMap<String, String>();
                        parmas.put("id", idMap.get(currentPosition).get(data.getIntExtra("index", 0)));
                        parmas.put("accessToken", ClientUtil.getToken());
                        parmas.put("uid", editor.get("uid", ""));
                        CommonUtil.showLoadingDialog(CaseTestActivity.this);
                        OpenApiService.getInstance(CaseTestActivity.this).getDeleteCaseImage(mQueue, parmas,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String arg0) {
                                    CommonUtil.closeLodingDialog();
                                    try {
                                        JSONObject responses=new JSONObject(arg0);
                                        if(responses.getInt("rtnCode") == 1) {
                                            bigPathMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                                            pathMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                                            idMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                                            showRightLayout(currentPosition);
                                        } else if(responses.getInt("rtnCode") == 10004) {
                                            Toast
                                                .makeText(CaseTestActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                                .show();
                                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                                @Override
                                                public void onSuccess(String rspContent, int statusCode) {
                                                }

                                                @Override
                                                public void onFailure(ConsultationCallbackException exp) {
                                                }
                                            });
                                            startActivity(new Intent(CaseTestActivity.this, LoginActivity.class));
                                        } else {
                                            Toast
                                                .makeText(CaseTestActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                    Toast.makeText(CaseTestActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent data) {
        if(requestCode == 3) {
            if(data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri=data.getData();
            if(photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String picPath=GetPathFromUri4kitkat.getPath(context, photoUri);
        // boolean isKitKat=Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // if(isKitKat) {
        //
        // } else {
        // String[] pojo={MediaStore.Images.Media.DATA};
        // cursor=managedQuery(photoUri, pojo, null, null, null);
        // if(cursor != null) {
        // cursor.moveToFirst();
        // int columnIndex=cursor.getColumnIndexOrThrow(pojo[0]);
        // // cursor.close();
        // } else {
        // picPath=photoUri.toString().replace("file://", "");
        // }
        // }

        if(picPath != null
            && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            List<String> paths=pathMap.get(currentPosition);
            List<String> bigPaths=bigPathMap.get(currentPosition);
            if(null == paths) {
                paths=new ArrayList<String>();
                paths.add(picPath);
                pathMap.put(currentPosition, paths);
            } else {
                if(!paths.contains(picPath)) {
                    paths.add(picPath);
                }
            }
            if(null == bigPaths) {
                bigPaths=new ArrayList<String>();
                bigPaths.add(picPath);
                bigPathMap.put(currentPosition, bigPaths);
            } else {
                if(!bigPaths.contains(picPath)) {
                    bigPaths.add(picPath);
                }
            }
            havaNew=true;
            showRightLayout(currentPosition);
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
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
        Intent intent=new Intent(CaseTestActivity.this, DialogNewActivity.class);
        intent.putExtra("flag", 0);
        intent.putExtra("index", v.getId());
        intent.putExtra("titleText", "删除该图片?");
        startActivityForResult(intent, 1);
        return false;
    }

    @Override
    protected void onDestroy() {
        if(cursor != null) {
            cursor.close();
        }
        super.onDestroy();
    }
}
