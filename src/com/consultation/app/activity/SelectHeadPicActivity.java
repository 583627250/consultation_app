package com.consultation.app.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.consultation.app.R;

public class SelectHeadPicActivity extends Activity implements OnClickListener {

    public static final int SELECT_PIC_BY_TACK_PHOTO=1;

    public static final int SELECT_PIC_BY_PICK_PHOTO=2;

    public static final int SELECT_PIC_BY_PICK_PHOTO_CUT=3;

    private LinearLayout dialogLayout;

    private Button takePhotoBtn, pickPhotoBtn, cancelBtn;

    private Uri photoUri;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pic_layout);
        System.out.println("SelectHeadPicActivity");
        if(savedInstanceState != null){
            photoUri=Uri.parse(savedInstanceState.getString("photoUri"));
        }
        initView();
    }
    
    /**
     * 初始化加载View
     */
    private void initView() {
        dialogLayout=(LinearLayout)findViewById(R.id.select_pic_dialog_layout);
        dialogLayout.setOnClickListener(this);
        takePhotoBtn=(Button)findViewById(R.id.select_pic_btn_take_photo);
        takePhotoBtn.setTextSize(18);
        takePhotoBtn.setOnClickListener(this);
        pickPhotoBtn=(Button)findViewById(R.id.select_pic_btn_pick_photo);
        pickPhotoBtn.setTextSize(18);
        pickPhotoBtn.setOnClickListener(this);
        cancelBtn=(Button)findViewById(R.id.select_pic_btn_cancel);
        cancelBtn.setTextSize(18);
        cancelBtn.setOnClickListener(this);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("photoUri", photoUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.select_pic_dialog_layout:
                finish();
                break;
            case R.id.select_pic_btn_take_photo:
                takePhoto();
                break;
            case R.id.select_pic_btn_pick_photo:
                pickPhoto();
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState=Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues values=new ContentValues();
            photoUri=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) {
        if(requestCode == SELECT_PIC_BY_PICK_PHOTO_CUT) { // 从相册取图片，有些手机有异常情况，请注意
            if(data != null) {
                setPicToView(data);
            }
        } else if(requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if(data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri=data.getData();
            if(photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            startPhotoZoom(photoUri);
        } else if(requestCode == SELECT_PIC_BY_TACK_PHOTO){
            startPhotoZoom(photoUri);
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO_CUT);
    }

    private void setPicToView(Intent picdata) {
        setResult(Activity.RESULT_OK, picdata);
        finish();
    }
}
