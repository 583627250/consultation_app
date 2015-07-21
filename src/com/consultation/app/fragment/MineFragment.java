package com.consultation.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.consultation.app.R;

public class MineFragment extends Fragment implements OnClickListener{
    
    private View mineLayout;

    private TextView header_text,phone_text,tip_text,updatePwd_text,pay_text,
                     share_text,invitation_text,set_text,feedback_text,update_text,
                     help_text,about_text;

    private LinearLayout info_layout,updatePwd_layout,pay_layout,share_layout,invitation_layout,
                         set_layout,feedback_layout,update_layout,help_layout,about_layout  ;
    
//    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mineLayout=inflater.inflate(R.layout.mine_layout, container, false);
        initLayout();
        return mineLayout;
    }
    
    public static MineFragment getInstance(Context ctx) { 
//        context = ctx;
        return new MineFragment();
    } 

    private void initLayout() {
        header_text=(TextView)mineLayout.findViewById(R.id.header_text);
        header_text.setText("我");
        header_text.setTextSize(20);


        phone_text=(TextView)mineLayout.findViewById(R.id.mine_info_phone_text);
        phone_text.setTextSize(20);

        tip_text=(TextView)mineLayout.findViewById(R.id.mine_info_tip_text);
        tip_text.setTextSize(14);

        updatePwd_text=(TextView)mineLayout.findViewById(R.id.mine_info_update_pwd_text);
        updatePwd_text.setTextSize(18);

        pay_text=(TextView)mineLayout.findViewById(R.id.mine_info_pay_text);
        pay_text.setTextSize(18);

        share_text=(TextView)mineLayout.findViewById(R.id.mine_info_share_text);
        share_text.setTextSize(18);

        invitation_text=(TextView)mineLayout.findViewById(R.id.mine_info_invitation_text);
        invitation_text.setTextSize(18);

        set_text=(TextView)mineLayout.findViewById(R.id.mine_info_set_text);
        set_text.setTextSize(18);

        feedback_text=(TextView)mineLayout.findViewById(R.id.mine_info_feedback_text);
        feedback_text.setTextSize(18);
        
        update_text=(TextView)mineLayout.findViewById(R.id.mine_info_update_text);
        update_text.setTextSize(18);
        
        help_text=(TextView)mineLayout.findViewById(R.id.mine_info_help_text);
        help_text.setTextSize(18);
       
        about_text=(TextView)mineLayout.findViewById(R.id.mine_info_about_text);
        about_text.setTextSize(18);

        info_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_info_layout);
        info_layout.setOnClickListener(this);
        
        updatePwd_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_update_pwd_layout);
        updatePwd_layout.setOnClickListener(this);
        
        pay_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_pay_layout);
        pay_layout.setOnClickListener(this);
        
        share_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_share_layout);
        share_layout.setOnClickListener(this);
        
        invitation_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_invitation_layout);
        invitation_layout.setOnClickListener(this);
        
        set_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_set_layout);
        set_layout.setOnClickListener(this);
        
        feedback_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_feedback_layout);
        feedback_layout.setOnClickListener(this);
        
        update_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_update_layout);
        update_layout.setOnClickListener(this);
        
        help_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_help_layout);
        help_layout.setOnClickListener(this);
        
        about_layout=(LinearLayout)mineLayout.findViewById(R.id.mine_my_about_layout);
        about_layout.setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.mine_my_info_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_update_pwd_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_pay_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_share_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_invitation_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_set_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_feedback_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_update_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_help_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;
            case R.id.mine_my_about_layout:
//                Toast.makeText(context, "hahah", 500).show();
                break;

            default:
                break;
        }
    }
}