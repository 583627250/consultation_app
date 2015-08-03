package com.consultation.app.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class AddPatientPicActivity extends Activity implements OnClickListener {

    public static final int SELECT_PIC_BY_TACK_PHOTO=1;

    public static final int SELECT_PIC_BY_PICK_PHOTO=2;

    private LinearLayout dialogLayout;

    private Button takePhotoBtn, pickPhotoBtn, cancelBtn;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pic_layout);
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
        if(requestCode == SELECT_PIC_BY_PICK_PHOTO) {
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
        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(photoUri, pojo, null, null,null); 
        String picPath = null;
        if(cursor != null ){
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            cursor.close();
        }
        if(picPath != null && ( picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))){
            Intent intent = new Intent();
            intent.putExtra("bitmap", picPath);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

//    private void setPicToView(Intent picdata) {
//        Bundle extras=picdata.getExtras();
//        if(extras != null) {
//            Bitmap photo=extras.getParcelable("data");
//            /**
//             * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上 传到服务器，QQ头像上传采用的方法跟这个类似
//             */
//            ByteArrayOutputStream stream=new ByteArrayOutputStream();
//            photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
//            byte[] b=stream.toByteArray();
//            // 将图片流以字符串形式存储下来
//            String tp=new String(Base64Coder.encodeLines(b));
//            System.out.println(tp);
//            // 这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
//            // 服务器处理的方法是服务器那边的事了，吼吼
//            //
//            // 如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
//            // 为我们可以用的图片类型就OK啦...吼吼
//            // Bitmap dBitmap=BitmapFactory.decodeFile(tp);
//
//        }
//        setResult(Activity.RESULT_OK, picdata);
//        finish();
//    }
}
