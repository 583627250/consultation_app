package com.consultation.app.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.activity.FeedBackActivity;
import com.consultation.app.activity.HelpActivity;
import com.consultation.app.activity.HomeActivity;
import com.consultation.app.activity.LoginActivity;
import com.consultation.app.activity.MyAccountActivity;
import com.consultation.app.activity.MyInfoActivity;
import com.consultation.app.activity.MyInfoSetActivity;
import com.consultation.app.activity.UpdateMyInfoActivity;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.consultation.app.view.SelectPicDialog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

@SuppressLint("HandlerLeak")
public class MineFragment extends Fragment implements OnClickListener {

    private View mineLayout;

    private TextView header_text, header_right, myInfo_text, pay_text, blance_text, share_text, jion_text, feedback_text,
            help_text, userName, phone, title, hospital, grade, status, description;

    private LinearLayout info_layout, pay_layout, share_layout, jion_layout, feedback_layout, help_layout, line, doctor_layout;

    private ImageView photos;

    private Button logoutBtn;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private UserTo userTo;

    private boolean isInit=false;

    private ImageLoader mImageLoader;

    private long blance;

    private String doctorInfo;

    private static Activity mainActivity;

    private Uri photoUri;
    
    private final UMSocialService mController=UMServiceFactory.getUMSocialService("com.umeng.share");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mineLayout=inflater.inflate(R.layout.mine_layout, container, false);
        editor=new SharePreferencesEditor(mineLayout.getContext());
        mQueue=Volley.newRequestQueue(mineLayout.getContext());
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        if(!editor.get("photoUri", "").equals("")){
            photoUri = Uri.parse(editor.get("photoUri", ""));
        }
        initLayout();
        initDate(1);
        // 配置需要分享的相关平台
        configPlatforms();
        // 设置分享的内容
        setShareContent();
        return mineLayout;
    }

    public void initDate(final int isShow) {
        isInit=true;
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("accessToken", ClientUtil.getToken());
        if(isShow == 1) {
            CommonUtil.showLoadingDialog(mineLayout.getContext());
        }
        OpenApiService.getInstance(mineLayout.getContext()).getUserInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                if(isShow == 1) {
                    CommonUtil.closeLodingDialog();
                }
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        userTo=new UserTo();
                        JSONObject jsonObject=new JSONObject(arg0);
                        JSONObject object=jsonObject.getJSONObject("user");
                        userTo.setIcon_url(object.getString("icon_url"));
                        userTo.setUser_name(object.getString("real_name"));
                        editor.put("userType", object.getString("tp"));
                        Message msg=new Message();
                        msg.obj=object.getString("icon_url");
                        if(object.getJSONObject("userBalance").getString("current_balance").equals("null")) {
                            blance=0;
                        } else {
                            blance=object.getJSONObject("userBalance").getLong("current_balance");
                        }
                        doctorInfo=jsonObject.getString("doctor");
                        handler.dispatchMessage(msg);
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(mineLayout.getContext(), responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate(0);
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(mineLayout.getContext(), LoginActivity.class));
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                if(isShow == 1) {
                    CommonUtil.closeLodingDialog();
                }
                Toast.makeText(mineLayout.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isInit) {
            Map<String, String> parmas=new HashMap<String, String>();
            parmas.put("uid", editor.get("uid", ""));
            parmas.put("accessToken", ClientUtil.getToken());
            OpenApiService.getInstance(mineLayout.getContext()).getUserInfo(mQueue, parmas, new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            userTo=new UserTo();
                            JSONObject jsonObject=new JSONObject(arg0);
                            JSONObject object=jsonObject.getJSONObject("user");
                            userTo.setIcon_url(object.getString("icon_url"));
                            userTo.setUser_name(object.getString("real_name"));
                            editor.put("userType", object.getString("tp"));
                            Message msg=new Message();
                            msg.obj=object.getString("icon_url");
                            if(object.getJSONObject("userBalance").getString("current_balance").equals("null")) {
                                blance=0;
                            } else {
                                blance=object.getJSONObject("userBalance").getLong("current_balance");
                            }
                            doctorInfo=jsonObject.getString("doctor");
                            handler.dispatchMessage(msg);
                        } else if(responses.getInt("rtnCode") == 10004) {
                            Toast.makeText(mineLayout.getContext(), responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    // initDate();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(mineLayout.getContext(), LoginActivity.class));
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    Toast.makeText(mineLayout.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
        }
        isInit=false;
    }

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String icon_url=(String)msg.obj;
            int photoId=0;
            Intent broadcastIntent=new Intent("com.consultation.app.count.case.action");
            broadcastIntent.putExtra("type", "switch");
            broadcastIntent.putExtra("count", 0);
            mineLayout.getContext().sendBroadcast(broadcastIntent);
            if(editor.get("userType", "").equals("1")) {
                photos.setBackgroundResource(R.drawable.photo_primary);
                photoId=R.drawable.photo_primary;
            } else if(editor.get("userType", "").equals("2")) {
                photos.setBackgroundResource(R.drawable.photo_expert);
                photoId=R.drawable.photo_expert;
            } else if(editor.get("userType", "").equals("0")) {
                photos.setBackgroundResource(R.drawable.photo_patient);
                photoId=R.drawable.photo_patient;
            }
            if(!"null".equals(icon_url) && !"".equals(icon_url) && null != icon_url) {
                ImageListener listener=ImageLoader.getImageListener(photos, photoId, photoId);
                mImageLoader.get(icon_url, listener);
            }
            if(!"null".equals(userTo.getUser_name()) && !"".equals(userTo.getUser_name()) && userTo.getUser_name() != null) {
                userName.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                userName.setText(userTo.getUser_name());
                phone.setText(editor.get("phone", ""));
            } else {
                userName.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
                myInfo_text.setText("请填写个人信息");
            }
            if(!editor.get("userType", "").equals("0")) {
                line.setVisibility(View.GONE);
                jion_layout.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
                jion_layout.setVisibility(View.VISIBLE);
            }
            // if(editor.get("userType", "").equals("0")) {
            // invitation_layout.setVisibility(View.GONE);
            // }else{
            // invitation_layout.setVisibility(View.VISIBLE);
            // }
            blance_text.setText("余额:" + blance + "元");
            if(null != doctorInfo && !"null".equals(doctorInfo) && !"".equals(doctorInfo)) {
                doctor_layout.setVisibility(View.VISIBLE);
                line.setVisibility(View.GONE);
                jion_layout.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(doctorInfo);
                    title.setText(jsonObject.getString("depart_name") + "|" + jsonObject.getString("title"));
                    hospital.setText(jsonObject.getString("hospital_name"));
                    if(jsonObject.getString("grade").equals("1")) {
                        status.setVisibility(View.INVISIBLE);
                        if(jsonObject.getString("approve_status").equals("0")) {
                            grade.setText("待认证");
                            grade.setTextColor(Color.parseColor("#990000"));
                            description.setVisibility(View.GONE);
                        } else if(jsonObject.getString("approve_status").equals("1")) {
                            grade.setText("已认证");
                            grade.setTextColor(Color.parseColor("#006633"));
                            description.setVisibility(View.GONE);
                            editor.put("depart_id", jsonObject.getString("depart_id"));
                        } else if(jsonObject.getString("approve_status").equals("2")) {
                            grade.setText("未认证通过");
                            grade.setTextColor(Color.parseColor("#990000"));
                            description.setText(jsonObject.getString("approve_desc"));
                        }
                    } else {
                        grade.setText(jsonObject.getString("expert_grade"));
                        if(jsonObject.getString("approve_status").equals("0")) {
                            status.setText("待认证");
                            description.setVisibility(View.GONE);
                        } else if(jsonObject.getString("approve_status").equals("1")) {
                            status.setText("已认证");
                            status.setTextColor(Color.parseColor("#006633"));
                            description.setVisibility(View.GONE);
                            editor.put("depart_id", jsonObject.getString("depart_id"));
                        } else if(jsonObject.getString("approve_status").equals("2")) {
                            status.setText("未认证通过");
                            description.setText(jsonObject.getString("approve_desc"));
                        }
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            } else {
                doctor_layout.setVisibility(View.GONE);
                line.setVisibility(View.VISIBLE);
                jion_layout.setVisibility(View.VISIBLE);
            }
        }
    };

    public static MineFragment getInstance(Context ctx) {
        mainActivity=(Activity)ctx;
        return new MineFragment();
    }

    private void initLayout() {
        header_text=(TextView)mineLayout.findViewById(R.id.header_text);
        header_text.setText("我的");
        header_text.setTextSize(20);

        header_right=(TextView)mineLayout.findViewById(R.id.header_right);
        header_right.setText("设置");
        header_right.setVisibility(View.VISIBLE);
        header_right.setTextSize(18);
        header_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mineLayout.getContext(), MyInfoSetActivity.class);
                startActivity(intent);
            }
        });

        doctor_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_doctor_info_layout);
        doctor_layout.setOnClickListener(this);

        photos=(ImageView)mineLayout.findViewById(R.id.mine_info_imageView);
        photos.setOnClickListener(this);

        myInfo_text=(TextView)mineLayout.findViewById(R.id.mine_info_my_text);
        myInfo_text.setTextSize(18);

        userName=(TextView)mineLayout.findViewById(R.id.mine_info_my_name_text);
        userName.setTextSize(18);

        phone=(TextView)mineLayout.findViewById(R.id.mine_info_my_phone_text);
        phone.setTextSize(16);

        pay_text=(TextView)mineLayout.findViewById(R.id.mine_info_pay_text);
        pay_text.setTextSize(18);

        blance_text=(TextView)mineLayout.findViewById(R.id.mine_info_blance_text);
        blance_text.setTextSize(18);

        jion_text=(TextView)mineLayout.findViewById(R.id.mine_info_jion_text);
        jion_text.setTextSize(18);

        share_text=(TextView)mineLayout.findViewById(R.id.mine_info_share_text);
        share_text.setTextSize(18);

        // invitation_text=(TextView)mineLayout.findViewById(R.id.mine_info_invitation_text);
        // invitation_text.setTextSize(18);

        feedback_text=(TextView)mineLayout.findViewById(R.id.mine_info_feedback_text);
        feedback_text.setTextSize(18);

        help_text=(TextView)mineLayout.findViewById(R.id.mine_info_help_text);
        help_text.setTextSize(18);

        title=(TextView)mineLayout.findViewById(R.id.mine_info_doctor_info_title_text);
        title.setTextSize(16);
        hospital=(TextView)mineLayout.findViewById(R.id.mine_info_doctor_info_hospital_text);
        hospital.setTextSize(16);
        grade=(TextView)mineLayout.findViewById(R.id.mine_info_doctor_info_grede_text);
        grade.setTextSize(16);
        status=(TextView)mineLayout.findViewById(R.id.mine_info_doctor_info_status_text);
        status.setTextSize(16);
        description=(TextView)mineLayout.findViewById(R.id.mine_info_doctor_info_desc_text);
        description.setTextSize(16);

        info_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_info_icon_layout);
        info_layout.setOnClickListener(this);

        pay_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_pay_layout);
        pay_layout.setOnClickListener(this);

        jion_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_jion_layout);
        jion_layout.setOnClickListener(this);
        line=(LinearLayout)mineLayout.findViewById(R.id.mine_my_jion_layout_line);
        if(!editor.get("userType", "").equals("0")) {
            line.setVisibility(View.GONE);
            jion_layout.setVisibility(View.GONE);
        }

        share_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_share_layout);
        share_layout.setOnClickListener(this);

        // invitation_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_invitation_layout);
        // invitation_layout.setOnClickListener(this);
        // if(editor.get("userType", "").equals("0")) {
        // invitation_layout.setVisibility(View.GONE);
        // }

        feedback_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_feedback_layout);
        feedback_layout.setOnClickListener(this);

        help_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_help_layout);
        help_layout.setOnClickListener(this);

        logoutBtn=(Button)mineLayout.findViewById(R.id.mine_info_logout_btn);
        logoutBtn.setTextSize(18);
        logoutBtn.setOnClickListener(this);
        logoutBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.mine_logout_btn_shape),
            getResources().getDrawable(R.drawable.mine_logout_press_btn_shape)).getBtnTouchListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 0:
                if(resultCode == Activity.RESULT_OK) {
                    doPhoto(requestCode, data);
                }
                // if(data != null) {
                // Bundle extras=data.getExtras();
                // if(extras != null) {
                // final Bitmap photo=extras.getParcelable("data");
                // File file=new File(Environment.getExternalStorageDirectory() + File.separator + "photo.jpg");// 将要保存图片的路径
                // try {
                // BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
                // photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                // bos.flush();
                // bos.close();
                // File[] files=new File[1];
                // files[0]=file;
                // Map<String, String> params=new HashMap<String, String>();
                // params.put("accessToken", ClientUtil.getToken());
                // params.put("uid", editor.get("uid", ""));
                // CommonUtil.showLoadingDialog(mineLayout.getContext());
                // OpenApiService.getInstance(mineLayout.getContext()).getUploadFiles(ClientUtil.GET_USER_ICON_URL,
                // mineLayout.getContext(), new ConsultationCallbackHandler() {
                //
                // @Override
                // public void onSuccess(String rspContent, int statusCode) {
                // CommonUtil.closeLodingDialog();
                // photos.setImageBitmap(photo);
                // JSONObject jsonObject;
                // try {
                // jsonObject=new JSONObject(rspContent);
                // editor.put("icon_url", jsonObject.getString("filePath"));
                // } catch(JSONException e) {
                // e.printStackTrace();
                // }
                // Toast.makeText(mineLayout.getContext(), "图片上传成功", Toast.LENGTH_LONG).show();
                // }
                //
                // @Override
                // public void onFailure(ConsultationCallbackException exp) {
                // Toast.makeText(mineLayout.getContext(), "图片上传失败，请重新上传", Toast.LENGTH_LONG).show();
                // CommonUtil.closeLodingDialog();
                // }
                // }, files, params);
                //
                // } catch(IOException e) {
                // e.printStackTrace();
                // }
                // }
                // }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    doPhoto(requestCode, data);
                }
                break;
            case 2:
                if(resultCode == Activity.RESULT_OK) {
                    if(data.getExtras().getBoolean("logout")) {
                        mainActivity.finish();
                    }
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
        if(requestCode == 3) { // 从相册取图片，有些手机有异常情况，请注意
            if(data != null) {
                Bundle extras=data.getExtras();
                if(extras != null) {
                    final Bitmap photo=extras.getParcelable("data");
                    File file=new File(Environment.getExternalStorageDirectory() + File.separator + "photo.jpg");// 将要保存图片的路径
                    try {
                        BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        File[] files=new File[1];
                        files[0]=file;
                        Map<String, String> params=new HashMap<String, String>();
                        params.put("accessToken", ClientUtil.getToken());
                        params.put("uid", editor.get("uid", ""));
                        CommonUtil.showLoadingDialog(mineLayout.getContext());
                        OpenApiService.getInstance(mineLayout.getContext()).getUploadFiles(ClientUtil.GET_USER_ICON_URL,
                            mineLayout.getContext(), new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    CommonUtil.closeLodingDialog();
//                                    photos.setImageBitmap(photo);
                                    JSONObject jsonObject;
                                    try {
                                        jsonObject=new JSONObject(rspContent);
                                        editor.put("photoUri", "");
                                        editor.put("icon_url", jsonObject.getString("filePath"));
                                    } catch(JSONException e) {
                                        e.printStackTrace();
                                    }
                                    initDate(0);
                                    Toast.makeText(mineLayout.getContext(), "图片上传成功", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                    Toast.makeText(mineLayout.getContext(), "图片上传失败，请重新上传", Toast.LENGTH_LONG).show();
                                    CommonUtil.closeLodingDialog();
                                }
                            }, files, params);

                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if(requestCode == 1) {
            if(data == null) {
                Toast.makeText(mainActivity, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri=data.getData();
            if(photoUri == null) {
                Toast.makeText(mainActivity, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            startPhotoZoom(photoUri);
        } else if(requestCode == 0) {
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
        startActivityForResult(intent, 3);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.mine_my_info_icon_layout:
                Intent intent=new Intent(mineLayout.getContext(), MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_info_imageView:
                // startActivityForResult(new Intent(mineLayout.getContext(), SelectHeadPicActivity.class), 0);
                final SelectPicDialog dialog=new SelectPicDialog(mainActivity, R.style.selectPicDialog, R.layout.select_pic_dialog);
                dialog.setCancelable(true);
                dialog.setPhotographButton(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String SDState=Environment.getExternalStorageState();
                        if(SDState.equals(Environment.MEDIA_MOUNTED)) {
                            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ContentValues values=new ContentValues();
                            photoUri=mainActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            editor.put("photoUri", photoUri.toString());
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, 0);
                        } else {
                            Toast.makeText(mainActivity, "内存卡不存在", Toast.LENGTH_LONG).show();
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
                        startActivityForResult(intent, 1);
                    }
                });
                dialog.show();
                break;
            case R.id.mine_my_pay_layout:
                startActivity(new Intent(mineLayout.getContext(), MyAccountActivity.class));
                break;
            case R.id.mine_my_share_layout:
                // 分享
                shareMethod();
                break;
            case R.id.mine_my_jion_layout:
                Intent updateIntent=new Intent(mineLayout.getContext(), UpdateMyInfoActivity.class);
                updateIntent.putExtra("headerTitle", "我的资料");
                updateIntent.putExtra("infos", "");
                startActivity(updateIntent);
                break;
            // case R.id.mine_my_invitation_layout:
            // startActivity(new Intent(mineLayout.getContext(), InvitationActivity.class));
            // break;
            case R.id.mine_my_feedback_layout:
                startActivity(new Intent(mineLayout.getContext(), FeedBackActivity.class));
                break;
            case R.id.mine_my_help_layout:
                startActivity(new Intent(mineLayout.getContext(), HelpActivity.class));
                break;
            case R.id.mine_my_doctor_info_layout:
                // 进入修改医生信息的界面
                Intent infoIntent=new Intent(mineLayout.getContext(), UpdateMyInfoActivity.class);
                infoIntent.putExtra("headerTitle", "修改我的资料");
                infoIntent.putExtra("infos", doctorInfo);
                startActivity(infoIntent);
                break;
            case R.id.mine_info_logout_btn:
                LoginActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        Intent intent=new Intent(mineLayout.getContext(), HomeActivity.class);
                        intent.putExtra("selectId", 3);
                        startActivity(intent);
                        ((Activity)mineLayout.getContext()).finish();
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                    }
                });
                Intent loginIntent=new Intent(mineLayout.getContext(), LoginActivity.class);
                loginIntent.putExtra("flag", 1);
                startActivityForResult(loginIntent, 2);
                break;
            default:
                break;
        }
    }

    private void shareMethod() {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,
            SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
        mController.openShare((Activity)mineLayout.getContext(), false);
    }
    
    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }
    
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId="wx807ff2a55add4359";
        String appSecret="d4624c36b6795d1d99dcf0547af5443d";
        // 添加微信平台
        UMWXHandler wxHandler=new UMWXHandler(mineLayout.getContext(), appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler=new UMWXHandler(mineLayout.getContext(), appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }
    
    private void setShareContent() {
        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        QZoneSsoHandler qZoneSsoHandler=new QZoneSsoHandler((Activity)mineLayout.getContext(), "100424468", "c7394704798a158208a74ab60104f0ba");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能。http://www.baidu.com");

        UMImage resImage = new UMImage(mineLayout.getContext(), R.drawable.share_icon);

        WeiXinShareContent weixinContent=new WeiXinShareContent();
        weixinContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-微信。http://www.baidu.com");
        weixinContent.setTitle("这是标题");
        weixinContent.setTargetUrl("http://www.baidu.com");
        weixinContent.setShareMedia(resImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia=new CircleShareContent();
        circleMedia.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-朋友圈。http://www.baidu.com");
        circleMedia.setTitle("这是标题");
        circleMedia.setShareMedia(resImage);
        circleMedia.setTargetUrl("http://www.baidu.com");
        mController.setShareMedia(circleMedia);

        // 设置renren分享内容
//        RenrenShareContent renrenShareContent=new RenrenShareContent();
//        renrenShareContent.setShareContent("人人分享内容");
//        UMImage image=new UMImage(mineLayout.getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.share_icon));
//        image.setTitle("这是标题");
//        image.setThumb("http://www.umeng.com/images/pic/social/integrated_3.png");
//        renrenShareContent.setShareImage(image);
//        renrenShareContent.setAppWebSite("http://www.baidu.com");
//        mController.setShareMedia(renrenShareContent);

        // 设置QQ空间分享内容
        QZoneShareContent qzone=new QZoneShareContent();
        qzone.setShareContent("这是分享内容");
        qzone.setTargetUrl("http://www.baidu.com");
        qzone.setTitle("这是标题");
        qzone.setShareMedia(resImage);
        mController.setShareMedia(qzone);

        QQShareContent qqShareContent=new QQShareContent();
        qqShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QQ");
        qqShareContent.setTitle("这是标题");
        qqShareContent.setShareMedia(resImage);
        qqShareContent.setTargetUrl("http://www.baidu.com");
        mController.setShareMedia(qqShareContent);

        TencentWbShareContent tencent=new TencentWbShareContent();
        tencent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-腾讯微博。http://www.baidu.com");
        mController.setShareMedia(tencent);

        SinaShareContent sinaContent=new SinaShareContent();
        sinaContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-新浪微博。http://www.baidu.com");
        sinaContent.setShareImage(new UMImage(mineLayout.getContext(), R.drawable.actionbar_back_indicator));
        mController.setShareMedia(sinaContent);

//        TwitterShareContent twitterShareContent=new TwitterShareContent();
//        twitterShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-TWITTER。http://www.baidu.com");
//        twitterShareContent.setShareMedia(new UMImage(mineLayout.getContext(), new File("/storage/sdcard0/emoji.gif")));
//        mController.setShareMedia(twitterShareContent);

//        GooglePlusShareContent googlePlusShareContent=new GooglePlusShareContent();
//        googlePlusShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-G+。http://www.baidu.com");
//        googlePlusShareContent.setShareMedia(resImage);
//        mController.setShareMedia(googlePlusShareContent);

    }
    
    private void addQQQZonePlatform() {
        String appId="100424468";
        String appKey="c7394704798a158208a74ab60104f0ba";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler=new UMQQSsoHandler((Activity)mineLayout.getContext(), appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.baidu.com");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler=new QZoneSsoHandler((Activity)mineLayout.getContext(), appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }
}