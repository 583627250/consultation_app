package com.consultation.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.fragment.ExpertConsultationFragment;
import com.consultation.app.fragment.KnowledgeFragment;
import com.consultation.app.fragment.MineFragment;
import com.consultation.app.fragment.PatientConsultationFragment;
import com.consultation.app.fragment.PrimaryConsultationFragment;
import com.consultation.app.fragment.SpecialistFragment;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint("HandlerLeak")
public class HomeActivity extends FragmentActivity implements OnClickListener {

    /*
     * 会诊、专家、知识、我的的Fragment
     */
    private PatientConsultationFragment patientConsultationFragment;

    private ExpertConsultationFragment expertConsultationFragment;

    private PrimaryConsultationFragment primaryConsultationFragment;

    private SpecialistFragment specialistFragment;

    private KnowledgeFragment knowledgeFragment;

    private MineFragment mineFragment;

    /*
     * 会诊、专家、知识、我的的fragment的布局
     */
    private View consultationLayout;

    private View specialistLayout;

    private View knowledgeLayout;

    private View mineLayout;

    /*
     * 会诊、专家、知识、我的的tab的图标
     */
    private ImageView consultationImage;

    private ImageView specialistImage;

    private ImageView knowledgeImage;

    private ImageView mineImage;

    /*
     * 会诊、专家、知识、我的的tab的文本
     */
    private TextView consultationText;

    private TextView specialistText;

    private TextView knowledgeText;

    private TextView mineText;

    /*
     * 对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    private SharePreferencesEditor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_layout);
        editor=new SharePreferencesEditor(HomeActivity.this);
        initViews(); // 初始化界面，并设置四个tab的监听
        fragmentManager=getSupportFragmentManager();
        setTabSelection(2); // 第一次启动时开启第2个tab
    }

    /*
     * 根据传入的index，来设置开启的tab页面
     * @param index index代表对应的下标，0对应会诊，1对应专家，2对应知识，3对应我的
     */
    private void setTabSelection(int index) {
        // 清理之前的所有状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        // 隐藏所有的fragment，防止有多个界面显示在界面上
        hideFragments(transaction);
        switch(index) {
            case 0:
                consultationImage.setImageResource(R.drawable.consultation_selected);
                consultationText.setTextColor(Color.parseColor("#2CB67A"));

//                if(null != editor.get("userTp", "") && !"".equals(editor.get("userTp", ""))) {
//                    int type=Integer.parseInt(editor.get("userTp", ""));
//                    switch(type) {
//                        case 0:
//                            if(patientConsultationFragment == null) {
//                                patientConsultationFragment=PatientConsultationFragment.getInstance(HomeActivity.this);
//                                transaction.add(R.id.content_layout, patientConsultationFragment);
//                            } else {
//                                transaction.show(patientConsultationFragment);
//                            }
//                            break;
//                        case 1:
//                            if(primaryConsultationFragment == null) {
//                                primaryConsultationFragment=PrimaryConsultationFragment.getInstance(HomeActivity.this);
//                                transaction.add(R.id.content_layout, primaryConsultationFragment);
//                            } else {
//                                transaction.show(primaryConsultationFragment);
//                            }
//                            break;
//                        case 2:
//                            if(expertConsultationFragment == null) {
//                                expertConsultationFragment=ExpertConsultationFragment.getInstance(HomeActivity.this);
//                                transaction.add(R.id.content_layout, expertConsultationFragment);
//                            } else {
//                                transaction.show(expertConsultationFragment);
//                            }
//                            break;
//
//                        default:
//                            break;
//                    }
//                } else {
//                    if(patientConsultationFragment == null) {
//                        patientConsultationFragment=PatientConsultationFragment.getInstance(HomeActivity.this);
//                        transaction.add(R.id.content_layout, patientConsultationFragment);
//                    } else {
//                        transaction.show(patientConsultationFragment);
//                    }
//                }

                // if(patientConsultationFragment == null){
                // patientConsultationFragment = PatientConsultationFragment.getInstance(HomeActivity.this);
                // transaction.add(R.id.content_layout, patientConsultationFragment);
                // } else {
                // transaction.show(patientConsultationFragment);
                // }

                if(primaryConsultationFragment == null) {
                    primaryConsultationFragment=PrimaryConsultationFragment.getInstance(HomeActivity.this);
                    transaction.add(R.id.content_layout, primaryConsultationFragment);
                } else {
                    transaction.show(primaryConsultationFragment);
                }

                // if(expertConsultationFragment == null){
                // expertConsultationFragment = ExpertConsultationFragment.getInstance(HomeActivity.this);
                // transaction.add(R.id.content_layout, expertConsultationFragment);
                // } else {
                // transaction.show(expertConsultationFragment);
                // }

                break;
            case 1:
                specialistImage.setImageResource(R.drawable.specialist_selected);
                specialistText.setTextColor(Color.parseColor("#2CB67A"));
                if(specialistFragment == null) {
                    specialistFragment=SpecialistFragment.getInstance(HomeActivity.this);
                    transaction.add(R.id.content_layout, specialistFragment);
                } else {
                    transaction.show(specialistFragment);
                }
                break;
            case 2:
                knowledgeImage.setImageResource(R.drawable.knowledge_selected);
                knowledgeText.setTextColor(Color.parseColor("#2CB67A"));
                if(knowledgeFragment == null) {
                    knowledgeFragment=KnowledgeFragment.getInstance(HomeActivity.this);
                    transaction.add(R.id.content_layout, knowledgeFragment);
                } else {
                    transaction.show(knowledgeFragment);
                }
                break;
            case 3:
            default:
                mineImage.setImageResource(R.drawable.mine_selected);
                mineText.setTextColor(Color.parseColor("#2CB67A"));
                if(mineFragment == null) {
                    mineFragment=MineFragment.getInstance(HomeActivity.this);
                    transaction.add(R.id.content_layout, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /*
     * 隐藏所有的fragment
     * @param transaction 用于对fragment进行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if(patientConsultationFragment != null) {
            transaction.hide(patientConsultationFragment);
        }
        if(primaryConsultationFragment != null) {
            transaction.hide(primaryConsultationFragment);
        }
        if(expertConsultationFragment != null) {
            transaction.hide(expertConsultationFragment);
        }
        if(specialistFragment != null) {
            transaction.hide(specialistFragment);
        }
        if(knowledgeFragment != null) {
            transaction.hide(knowledgeFragment);
        }
        if(mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    /*
     * 清理之前的所有状态
     */
    private void clearSelection() {
        consultationImage.setImageResource(R.drawable.consultation_unselected);
        consultationText.setTextColor(Color.parseColor("#7D7D7D"));
        specialistImage.setImageResource(R.drawable.specialist_unselected);
        specialistText.setTextColor(Color.parseColor("#7D7D7D"));
        knowledgeImage.setImageResource(R.drawable.knowledge_unselected);
        knowledgeText.setTextColor(Color.parseColor("#7D7D7D"));
        mineImage.setImageResource(R.drawable.mine_unselected);
        mineText.setTextColor(Color.parseColor("#7D7D7D"));
    }

    /*
     * 初始化界面，并设置四个tab的监听
     */
    private void initViews() {
        consultationLayout=findViewById(R.id.consultation_layout);
        specialistLayout=findViewById(R.id.specialist_layout);
        knowledgeLayout=findViewById(R.id.knowledge_layout);
        mineLayout=findViewById(R.id.mine_layout);

        consultationImage=(ImageView)findViewById(R.id.consultation_image);
        specialistImage=(ImageView)findViewById(R.id.specialist_image);
        knowledgeImage=(ImageView)findViewById(R.id.knowledge_image);
        mineImage=(ImageView)findViewById(R.id.mine_image);

        consultationText=(TextView)findViewById(R.id.consultation_text);
        consultationText.setTextSize(14);
        specialistText=(TextView)findViewById(R.id.specialist_text);
        specialistText.setTextSize(14);
        knowledgeText=(TextView)findViewById(R.id.knowledge_text);
        knowledgeText.setTextSize(14);
        mineText=(TextView)findViewById(R.id.mine_text);
        mineText.setTextSize(14);

        consultationLayout.setOnClickListener(this);
        specialistLayout.setOnClickListener(this);
        knowledgeLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
    }

    /*
     * 点击四个tab时的监听
     * @param v 四个控件的view
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.consultation_layout:
                // 点击消息tab，选中第一个tab
                if(ClientUtil.isLogin()) {
                    setTabSelection(0);
                } else {
                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            switch(statusCode) {
                                case ConsultionStatusCode.SUCCESS:
                                    setTabSelection(3);
                                    break;

                                case ConsultionStatusCode.USER_LOGIN_SUCCESS:
                                    setTabSelection(0);
                                    break;

                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                        }
                    });
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
                break;
            case R.id.specialist_layout:
                // 点击联系人tab，选中第二个tab
                setTabSelection(1);
                break;
            case R.id.knowledge_layout:
                // 点击动态tab，选中第三个tab
                setTabSelection(2);
                break;
            case R.id.mine_layout:
                // 点击设置tab，选中第四个tab
                setTabSelection(3);
                // if(isLogin){
                // setTabSelection(3);
                // }else{
                // startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                // LoginActivity.setHandler(new ConsultationCallbackHandler() {
                //
                // @Override
                // public void onSuccess(String rspContent, int statusCode) {
                // isLogin = true;
                // setTabSelection(3);
                // }
                //
                // @Override
                // public void onFailure(ConsultationCallbackException exp) {
                //
                // }
                // });
                // }
                break;
            default:
                break;
        }
    }

    private static boolean isExit=false;

    Handler mHandler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit=false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if(!isExit) {
            isExit=true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
        }
    }
}
