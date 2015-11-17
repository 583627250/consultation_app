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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.GetPathFromUri4kitkat;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.SelectPicDialog;

@SuppressLint("HandlerLeak")
public class CaseTestTxtActivity extends Activity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private LinearLayout rightLayout;

    private Context context;

    private int width;

    private int height;

    private TextView tip;

    private Button saveBtn;

    private ArrayList<String> pathList=new ArrayList<String>();

    private ArrayList<String> idList=new ArrayList<String>();

    private ArrayList<String> bigPathList=new ArrayList<String>();

    private SharePreferencesEditor editor;

    private String caseId, imageString;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private boolean isAdd=false,havaNew = false;

    private int page;

    private Uri photoUri;

    private Cursor cursor;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_test_txt_layout);
        context=this;
        if(savedInstanceState != null) {
            ClientUtil.setCaseParams((CaseParams)savedInstanceState.getSerializable("caseParams"));
            isAdd=savedInstanceState.getBoolean("isAdd");
            if(savedInstanceState.getString("photoUri") != null) {
                photoUri=Uri.parse(savedInstanceState.getString("photoUri"));
            }
            pathList=savedInstanceState.getStringArrayList("pathList");
            idList=savedInstanceState.getStringArrayList("idList");
            bigPathList=savedInstanceState.getStringArrayList("bigPathList");
        }
        editor=new SharePreferencesEditor(context);
        mQueue=Volley.newRequestQueue(context);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        caseId=getIntent().getStringExtra("caseId");
        page=getIntent().getIntExtra("page", -1);
        imageString=getIntent().getStringExtra("imageString");
        initData();
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("caseParams", ClientUtil.getCaseParams());
        outState.putBoolean("isAdd", isAdd);
        if(photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }
        outState.putStringArrayList("pathList", pathList);
        outState.putStringArrayList("idList", idList);
        outState.putStringArrayList("bigPathList", bigPathList);
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
        rightLayout=(LinearLayout)findViewById(R.id.test_txt_layout);

        TextView rightText=(TextView)findViewById(R.id.header_right);
        rightText.setVisibility(View.GONE);

        ImageView rightImageView=(ImageView)findViewById(R.id.header_right_image);
        rightImageView.setVisibility(View.VISIBLE);
        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final SelectPicDialog dialog=
                    new SelectPicDialog(CaseTestTxtActivity.this, R.style.selectPicDialog, R.layout.select_pic_dialog);
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
                                CaseTestTxtActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values);
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, 2);
                        } else {
                            Toast.makeText(CaseTestTxtActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
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

        // textAdd=(TextView)findViewById(R.id.test_txt_add_image_text);
        // textAdd.setText(Html.fromHtml("<u>" + "添加" + "</u>"));
        // textAdd.setGravity(Gravity.CENTER_VERTICAL);
        // textAdd.setTextColor(Color.BLUE);
        // textAdd.setTextSize(17);
        // textAdd.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // startActivityForResult(new Intent(CaseTestTxtActivity.this, AddPatientPicActivity.class), 0);
        // final SelectPicDialog dialog=
        // new SelectPicDialog(CaseTestTxtActivity.this, R.style.selectPicDialog, R.layout.select_pic_dialog);
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
        // CaseTestTxtActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        // values);
        // intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
        // startActivityForResult(intent, 2);
        // } else {
        // Toast.makeText(CaseTestTxtActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
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

        tip=(TextView)findViewById(R.id.test_txt_image_tip);
        tip.setTextSize(14);

        saveBtn=(Button)findViewById(R.id.test_txt_image_btn_save);
        saveBtn.setTextSize(17);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pathList.size() == 0) {
                    Toast.makeText(context, "请添加图片", Toast.LENGTH_LONG).show();
                    return;
                }
                List<String> temp=new ArrayList<String>();
                for(int i=0; i < pathList.size(); i++) {
                    if(!pathList.get(i).startsWith("http:")) {
                        temp.add(pathList.get(i));
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
                                havaNew = false;
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
                            Toast.makeText(context, "上传成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(context, "上传失败", Toast.LENGTH_LONG).show();
                        }
                    }, files, params);
            }
        });
        saveBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
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

    private void showRightLayout() {
        if(rightLayout == null) {
            rightLayout=(LinearLayout)findViewById(R.id.test_txt_layout);
        }
        rightLayout.removeAllViews();
        if(null != pathList && pathList.size() != 0) {
            LinearLayout rowsLayout=new LinearLayout(context);
            LinearLayout relativeLayout=new LinearLayout(context);
            for(int i=0; i < pathList.size(); i++) {
                if(i % 3 == 0) {
                    rowsLayout=createLinearLayout();
                    rightLayout.addView(rowsLayout);
                }
                relativeLayout=createImage(pathList.get(i), i, bigPathList.get(i));
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

    private LinearLayout createImage(String path, int id, final String bigPath) {
        LinearLayout relativeLayout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 30;
        layoutParams.rightMargin=width / 30;
        relativeLayout.setLayoutParams(layoutParams);
        ImageView imageView=new ImageView(context);
        imageView.setId(id);
        imageView.setOnLongClickListener(this);
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 展示大图片
                BigImageActivity.setViewData(bigPath);
                startActivity(new Intent(CaseTestTxtActivity.this, BigImageActivity.class));
            }
        });
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        if(!"null".equals(path) && !"".equals(path)) {
            if(path.startsWith("http:")) {
                imageView.setTag(path);
                imageView.setBackgroundResource(R.anim.loading_anim);
                AnimationDrawable animation = (AnimationDrawable)imageView.getBackground();  
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
        if(null != imageString && !"".equals(imageString)) {
            try {
                JSONArray jsonArray=new JSONArray(imageString);
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject imageFilesObject=jsonArray.getJSONObject(i);
                    if(page == 5 && imageFilesObject.getString("case_item").equals("jy")) {
                        pathList.add(imageFilesObject.getString("little_pic_url"));
                        idList.add(imageFilesObject.getString("id"));
                        bigPathList.add(imageFilesObject.getString("pic_url"));
                    } else if(page == 6 && imageFilesObject.getString("case_item").equals("jc")) {
                        pathList.add(imageFilesObject.getString("little_pic_url"));
                        idList.add(imageFilesObject.getString("id"));
                        bigPathList.add(imageFilesObject.getString("pic_url"));
                    }
                }
                showRightLayout();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        String addNewImage=ClientUtil.getCaseParams().getValue(page + "");
        if(null != addNewImage && !"".equals(addNewImage)) {
            try {
                JSONArray jsonArray=new JSONArray(addNewImage);
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject imageFilesObject=jsonArray.getJSONObject(i);
                    if(page == 5 && imageFilesObject.getString("case_item").equals("jy")) {
                        pathList.add(imageFilesObject.getString("little_pic_url"));
                        idList.add(imageFilesObject.getString("id"));
                        bigPathList.add(imageFilesObject.getString("pic_url"));
                    } else if(page == 6 && imageFilesObject.getString("case_item").equals("jc")) {
                        pathList.add(imageFilesObject.getString("little_pic_url"));
                        idList.add(imageFilesObject.getString("id"));
                        bigPathList.add(imageFilesObject.getString("pic_url"));
                    }
                }
                showRightLayout();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode) {
            case 0:
                if(data != null) {
                    String photoPath=data.getStringExtra("bitmap");
                    if(!pathList.contains(photoPath)) {
                        pathList.add(photoPath);
                        bigPathList.add(photoPath);
                    }
                    showRightLayout();
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    if(!pathList.get(data.getIntExtra("index", 0)).startsWith("http")) {
                        pathList.remove(data.getIntExtra("index", 0));
                        bigPathList.remove(data.getIntExtra("index", 0));
                        showRightLayout();
                    } else {
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("id", idList.get(data.getIntExtra("index", 0)));
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseTestTxtActivity.this);
                    OpenApiService.getInstance(CaseTestTxtActivity.this).getDeleteCaseImage(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        pathList.remove(data.getIntExtra("index", 0));
                                        bigPathList.remove(data.getIntExtra("index", 0));
                                        idList.remove(data.getIntExtra("index", 0));
                                        showRightLayout();
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(CaseTestTxtActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseTestTxtActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseTestTxtActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseTestTxtActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
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
        // String[] pojo={MediaStore.Images.Media.DATA};
        // cursor=managedQuery(photoUri, pojo, null, null, null);
        String picPath=GetPathFromUri4kitkat.getPath(context, photoUri);
        // if(cursor != null) {
        // int columnIndex=cursor.getColumnIndexOrThrow(pojo[0]);
        // cursor.moveToFirst();
        // picPath=cursor.getString(columnIndex);
        // // cursor.close();
        // } else {
        // picPath=photoUri.toString().replace("file://", "");
        // }
        if(picPath != null
            && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            if(!pathList.contains(picPath)) {
                pathList.add(picPath);
                bigPathList.add(picPath);
            }
            havaNew = true;
            showRightLayout();
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent intent=new Intent(CaseTestTxtActivity.this, DialogNewActivity.class);
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
