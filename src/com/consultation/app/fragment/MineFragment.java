package com.consultation.app.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.activity.JionInDoctorActivity;
import com.consultation.app.activity.PayActivity;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.util.CommonUtil;

public class MineFragment extends Fragment {
    
    private View mineLayout;

    private TextView header_text;

    private ImageView myPhotoImage, myAccountImage, messageImage, jionImage, messageCountImage;

    private TextView realName, department, hospital, myInfo, myAccount, blance, myMessages, myJoin;

    private LinearLayout info, account, message, jion;
    
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mineLayout=inflater.inflate(R.layout.mine_layout, container, false);
        initLayout();
        return mineLayout;
    }
    
    public static MineFragment getInstance(Context ctx) { 
        context = ctx;
        return new MineFragment();
    } 

    private void initLayout() {
        header_text=(TextView)mineLayout.findViewById(R.id.header_text);
        header_text.setText("个人信息");
        header_text.setTextSize(20);

        messageCountImage=(ImageView)mineLayout.findViewById(R.id.mine_info_message_count_image);
//        LayoutParams messageCountBtnParams=
//            new LayoutParams((int)SizeUtil.getImageSize(((Activity)context).getContext(), 50), (int)SizeUtil.getImageSize(
//                ((Activity)context).getContext(), 50));
//        messageCountBtnParams.gravity = Gravity.CENTER;
//        messageCountImage.setLayoutParams(messageCountBtnParams);
        messageCountImage.setImageResource(CommonUtil.getResourceId(context, "drawable", "message_count_" + 2));

        myPhotoImage=(ImageView)mineLayout.findViewById(R.id.mine_info_imageView);
//        LayoutParams photoParams=
//            new LayoutParams((int)SizeUtil.getImageSize(((Activity)context).getContext(), 135), (int)SizeUtil.getImageSize(
//                ((Activity)context).getContext(), 135));
//        photoParams.gravity = Gravity.CENTER;
//        myPhotoImage.setLayoutParams(photoParams);
        myPhotoImage.setImageResource(R.drawable.splash_image);

        myAccountImage=(ImageView)mineLayout.findViewById(R.id.mine_account_imageView);
//        LayoutParams myAccountParams=
//            new LayoutParams((int)SizeUtil.getImageSize(((Activity)context).getContext(), 50), (int)SizeUtil.getImageSize(
//                ((Activity)context).getContext(), 50));
//        myAccountParams.gravity = Gravity.CENTER;
//        myAccountImage.setLayoutParams(myAccountParams);

        messageImage=(ImageView)mineLayout.findViewById(R.id.mine_message_imageView);
//        LayoutParams messageParams=
//            new LayoutParams((int)SizeUtil.getImageSize(((Activity)context).getContext(), 50), (int)SizeUtil.getImageSize(
//                ((Activity)context).getContext(), 50));
//        messageParams.gravity = Gravity.CENTER;
//        messageImage.setLayoutParams(messageParams);

        jionImage=(ImageView)mineLayout.findViewById(R.id.mine_join_imageView);
//        LayoutParams jionParams=
//            new LayoutParams((int)SizeUtil.getImageSize(((Activity)context).getContext(), 50), (int)SizeUtil.getImageSize(
//                ((Activity)context).getContext(), 50));
//        jionParams.gravity = Gravity.CENTER;
//        jionImage.setLayoutParams(jionParams);

        realName=(TextView)mineLayout.findViewById(R.id.mine_info_realname);
        realName.setTextSize(17);

        department=(TextView)mineLayout.findViewById(R.id.mine_info_department);
        department.setTextSize(14);

        hospital=(TextView)mineLayout.findViewById(R.id.mine_info_hospital);
        hospital.setTextSize(14);

        myInfo=(TextView)mineLayout.findViewById(R.id.mine_info_more_text);
        myInfo.setTextSize(14);

        myAccount=(TextView)mineLayout.findViewById(R.id.mine_info_account_text);
        myAccount.setTextSize(17);

        blance=(TextView)mineLayout.findViewById(R.id.mine_info_balance_text);
        blance.setTextSize(14);

        myMessages=(TextView)mineLayout.findViewById(R.id.mine_info_message_text);
        myMessages.setTextSize(17);

        myJoin=(TextView)mineLayout.findViewById(R.id.mine_info_jion_text);
        myJoin.setTextSize(17);

        info=(LinearLayout)mineLayout.findViewById(R.id.mine_my_info_layout);
        account=(LinearLayout)mineLayout.findViewById(R.id.mine_my_account_layout);
        message=(LinearLayout)mineLayout.findViewById(R.id.mine_my_message_layout);
        jion=(LinearLayout)mineLayout.findViewById(R.id.mine_my_jion_layout);
        account.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // 充值
                PayActivity.setHandler(new ConsultationCallbackHandler() {
                    
                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        blance.setText("余额："+rspContent+"元");
                    }
                    
                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                        
                    }
                });
                startActivity(new Intent(context, PayActivity.class));
            }
        });
        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 详细资料
                Toast.makeText(context, "详细资料", Toast.LENGTH_SHORT).show();
            }
        });
        message.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 显示消息
                messageCountImage.setVisibility(View.GONE);
            }
        });
        jion.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 到加入医生界面
                startActivity(new Intent(context, JionInDoctorActivity.class));
            }
        });
    }
}