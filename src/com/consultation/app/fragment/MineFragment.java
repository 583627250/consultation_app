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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.consultation.app.activity.InvitationActivity;
import com.consultation.app.activity.LoginActivity;
import com.consultation.app.activity.MyInfoActivity;
import com.consultation.app.activity.MyInfoSetActivity;
import com.consultation.app.activity.SelectHeadPicActivity;
import com.consultation.app.activity.UpdateMyInfoActivity;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint("HandlerLeak")
public class MineFragment extends Fragment implements OnClickListener {

    private View mineLayout;

    private TextView header_text, header_right, myInfo_text, pay_text, blance_text, share_text, invitation_text, jion_text,
            feedback_text, help_text;

    private LinearLayout info_layout, pay_layout, share_layout, invitation_layout, jion_layout, feedback_layout, help_layout;

    private ImageView photos;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private UserTo userTo;

    private ImageLoader mImageLoader;

    private long blance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mineLayout=inflater.inflate(R.layout.mine_layout, container, false);
        editor=new SharePreferencesEditor(mineLayout.getContext());
        mQueue=Volley.newRequestQueue(mineLayout.getContext());
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initLayout();
        initDate();
        return mineLayout;
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("accessToken", ClientUtil.getToken());
        CommonUtil.showLoadingDialog(mineLayout.getContext());
        OpenApiService.getInstance(mineLayout.getContext()).getUserInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                System.out.println(arg0);
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        userTo=new UserTo();
                        JSONObject jsonObject=new JSONObject(arg0);
                        JSONObject object=jsonObject.getJSONObject("user");
                        userTo.setIcon_url(object.getString("icon_url"));
                        Message msg=new Message();
                        msg.obj=object.getString("icon_url");
                        if(object.getJSONObject("userBalance").getString("current_balance").equals("null")){
                            blance = 0;
                        }else{
                            blance=object.getJSONObject("userBalance").getLong("current_balance");
                        }
                        handler.dispatchMessage(msg);
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(mineLayout.getContext(), responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
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
                CommonUtil.closeLodingDialog();
                Toast.makeText(mineLayout.getContext(), "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String icon_url=(String)msg.obj;
            if(!"null".equals(icon_url) && !"".equals(icon_url) && null != icon_url) {
                ImageListener listener=ImageLoader.getImageListener(photos, R.drawable.photo, R.drawable.photo);
                mImageLoader.get(icon_url, listener);
                photos.setImageBitmap(CommonUtil.drawableToRoundBitmap(photos.getDrawable(), 15));
            }
            blance_text.setText("余额" + blance + "元");
        }
    };

    public static MineFragment getInstance(Context ctx) {
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

        photos=(ImageView)mineLayout.findViewById(R.id.mine_info_imageView);
        photos.setOnClickListener(this);
        photos.setImageBitmap(CommonUtil.drawableToRoundBitmap(photos.getDrawable(), 15));

        myInfo_text=(TextView)mineLayout.findViewById(R.id.mine_info_my_text);
        myInfo_text.setTextSize(18);

        pay_text=(TextView)mineLayout.findViewById(R.id.mine_info_pay_text);
        pay_text.setTextSize(18);

        blance_text=(TextView)mineLayout.findViewById(R.id.mine_info_blance_text);
        blance_text.setTextSize(18);

        jion_text=(TextView)mineLayout.findViewById(R.id.mine_info_jion_text);
        jion_text.setTextSize(18);

        share_text=(TextView)mineLayout.findViewById(R.id.mine_info_share_text);
        share_text.setTextSize(18);

        invitation_text=(TextView)mineLayout.findViewById(R.id.mine_info_invitation_text);
        invitation_text.setTextSize(18);

        feedback_text=(TextView)mineLayout.findViewById(R.id.mine_info_feedback_text);
        feedback_text.setTextSize(18);

        help_text=(TextView)mineLayout.findViewById(R.id.mine_info_help_text);
        help_text.setTextSize(18);

        info_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_info_icon_layout);
        info_layout.setOnClickListener(this);

        pay_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_pay_layout);
        pay_layout.setOnClickListener(this);

        jion_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_jion_layout);
        jion_layout.setOnClickListener(this);
        LinearLayout line=(LinearLayout)mineLayout.findViewById(R.id.mine_my_jion_layout_line);
        if(!editor.get("userType", "").equals("0")) {
            line.setVisibility(View.GONE);
            jion_layout.setVisibility(View.GONE);
        }

        share_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_share_layout);
        share_layout.setOnClickListener(this);

        invitation_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_invitation_layout);
        invitation_layout.setOnClickListener(this);
        if(editor.get("userType", "").equals("0")) {
            invitation_layout.setVisibility(View.GONE);
        }

        feedback_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_feedback_layout);
        feedback_layout.setOnClickListener(this);

        help_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_help_layout);
        help_layout.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 0:
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
                            OpenApiService.getInstance(mineLayout.getContext()).getUploadFiles(ClientUtil.GET_UPLOAD_IMAGES_URL,
                                mineLayout.getContext(), new ConsultationCallbackHandler() {

                                    @Override
                                    public void onSuccess(String rspContent, int statusCode) {
                                        CommonUtil.closeLodingDialog();
                                        System.out.println(rspContent);
                                        photos.setImageBitmap(photo);
                                        photos.setImageBitmap(CommonUtil.drawableToRoundBitmap(photos.getDrawable(), 15));
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
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.mine_my_info_icon_layout:
                Intent intent=new Intent(mineLayout.getContext(), MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_info_imageView:
                startActivityForResult(new Intent(mineLayout.getContext(), SelectHeadPicActivity.class), 0);
                break;
            case R.id.mine_my_pay_layout:
                // startActivityForResult(new Intent(mineLayout.getContext(), SelectHeadPicActivity.class), 1);
                break;
            case R.id.mine_my_share_layout:

                break;
            case R.id.mine_my_jion_layout:
                startActivity(new Intent(mineLayout.getContext(), UpdateMyInfoActivity.class));
                break;
            case R.id.mine_my_invitation_layout:
                startActivity(new Intent(mineLayout.getContext(), InvitationActivity.class));
                break;
            case R.id.mine_my_feedback_layout:
                startActivity(new Intent(mineLayout.getContext(), FeedBackActivity.class));
                break;
            case R.id.mine_my_help_layout:
                break;
            default:
                break;
        }
    }
}