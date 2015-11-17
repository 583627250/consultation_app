package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.fragment.ConsultationDiscussionFragment;
import com.consultation.app.fragment.ExpertConsultationAllFragment;
import com.consultation.app.fragment.MineFragment;
import com.consultation.app.fragment.PatientConsultationAllFragment;
import com.consultation.app.fragment.PrimaryConsultationAllFragment;
import com.consultation.app.fragment.SpecialistFragment;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.CaseCountBroadcastReceiver;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.MyBroadcastReceiver;
import com.consultation.app.util.SharePreferencesEditor;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.sso.UMSsoHandler;

@SuppressLint("HandlerLeak")
public class HomeActivity extends FragmentActivity implements OnClickListener {

    /*
     * 会诊、专家、知识、我的的Fragment
     */
    private PatientConsultationAllFragment patientConsultationAllFragment;

    private ExpertConsultationAllFragment expertConsultationAllFragment;

    private PrimaryConsultationAllFragment primaryConsultationAllFragment;

    private SpecialistFragment specialistFragment;

    private ConsultationDiscussionFragment consultationDiscussionFragment;

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

    private TextView count;

    private RequestQueue mQueue;

    private ImageView addCaseBtn;

    private RelativeLayout addCaseLayout;
    
    private int currentIndex;

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
        if(savedInstanceState != null){
            ClientUtil.setToken(savedInstanceState.getString("token"));
        }
        editor=new SharePreferencesEditor(HomeActivity.this);
        mQueue=Volley.newRequestQueue(this);
        initData();
        initViews(); // 初始化界面，并设置四个tab的监听
        fragmentManager=getSupportFragmentManager();
        if(savedInstanceState != null){
            setTabSelection(savedInstanceState.getInt("currentIndex")); // 第一次启动时开启第2个tab
        }else{
            setTabSelection(getIntent().getIntExtra("selectId", 0)); // 第一次启动时开启第2个tab
            currentIndex = getIntent().getIntExtra("selectId", 0);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("token", ClientUtil.getToken());
        outState.putInt("currentIndex", currentIndex);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onResume() {
        setTabSelection(currentIndex);
        super.onResume();
    }

    private void initData() {
        if(!ClientUtil.isLogin()) {
            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                @Override
                public void onSuccess(String rspContent, int statusCode) {
                    initData();
                    setTabSelection(getIntent().getIntExtra("selectId", 0));
                }

                @Override
                public void onFailure(ConsultationCallbackException exp) {
                }
            });
            Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
            intent.putExtra("flag", 1);
            startActivityForResult(intent, 0);
        } else {
            Map<String, String> parmasInit=new HashMap<String, String>();
            parmasInit.put("uid", editor.get("uid", ""));
            parmasInit.put("accessToken", ClientUtil.getToken());
            OpenApiService.getInstance(HomeActivity.this).getUserInfo(mQueue, parmasInit, new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            JSONObject jsonObject=new JSONObject(arg0);
                            JSONObject object=jsonObject.getJSONObject("user");
                            editor.put("userType", object.getString("tp"));
                            if(editor.get("userType", "").equals("1")) {
                                if(addCaseLayout == null){
                                    addCaseLayout=(RelativeLayout)findViewById(R.id.add_new_case_layout);
                                }
                                addCaseLayout.setVisibility(View.VISIBLE);
                            }else{
                                if(addCaseLayout == null){
                                    addCaseLayout=(RelativeLayout)findViewById(R.id.add_new_case_layout);
                                }
                                addCaseLayout.setVisibility(View.GONE);
                            }
                            if(null != editor.get("userType", "") && !"".equals(editor.get("userType", ""))) {
                                int type=Integer.parseInt(editor.get("userType", ""));
                                if(consultationText == null){
                                    consultationText=(TextView)findViewById(R.id.consultation_text);
                                    consultationText.setTextSize(14);
                                }
                                switch(type) {
                                    case 0:
                                        consultationText.setText("咨询");
                                        break;
                                    case 1:
                                        consultationText.setText("诊室");
                                        break;
                                    case 2:
                                        consultationText.setText("诊室");
                                        break;

                                    default:
                                        break;
                                }
                            }
                            if(!editor.get("userType", "").equals("0")) {
                                Map<String, String> parmas=new HashMap<String, String>();
                                parmas.put("accessToken", ClientUtil.getToken());
                                parmas.put("uid", editor.get("uid", ""));
                                parmas.put("userTp", editor.get("userType", ""));
                                OpenApiService.getInstance(HomeActivity.this).getReadTotalCount(mQueue, parmas, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String arg0) {
                                        try {
                                            JSONObject responses=new JSONObject(arg0);
                                            if(responses.getInt("rtnCode") == 1) {
                                                Message msg=new Message();
                                                msg.obj=responses.getInt("totalCount");
                                                handler.dispatchMessage(msg);
                                            }
                                        } catch(JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError arg0) {
                                    }
                                });
                            }
                        } else if(responses.getInt("rtnCode") == 10004) {
                            Toast.makeText(HomeActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                     initData();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
                            intent.putExtra("flag", 1);
                            startActivityForResult(intent, 0);
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    Toast.makeText(HomeActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            count=(TextView)findViewById(R.id.consulation_home_image_count);
            int countNumber=(Integer)msg.obj;
            if(countNumber != 0) {
                count.setVisibility(View.VISIBLE);
                count.setTextSize(12);
                count.setText(countNumber + "");
            }else{
                count.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            if(resultCode == Activity.RESULT_OK && requestCode == 0) {
                if(data.getExtras().getBoolean("logout")) {
                    finish();
                }
            }
        }
        UMSsoHandler ssoHandler=SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if(ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 根据传入的index，来设置开启的tab页面
     * @param index index代表对应的下标，0对应会诊，1对应专家，2对应知识，3对应我的
     */
    private void setTabSelection(int index) {
        if(!ClientUtil.isLogin()) {
            return;
        }
        // 清理之前的所有状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        // 隐藏所有的fragment，防止有多个界面显示在界面上
        hideFragments(transaction);
        switch(index) {
            case 0:
                currentIndex =0;
                consultationImage.setImageResource(R.drawable.consultation_selected);
                consultationText.setTextColor(Color.parseColor("#2CB67A"));
                if(null != editor.get("userType", "") && !"".equals(editor.get("userType", ""))) {
                    int type=Integer.parseInt(editor.get("userType", ""));
                    switch(type) {
                        case 0:
                            consultationText.setText("咨询");
                            if(patientConsultationAllFragment == null) {
                                patientConsultationAllFragment=PatientConsultationAllFragment.getInstance(HomeActivity.this);
                                transaction.add(R.id.content_layout, patientConsultationAllFragment);
                            } else {
                                transaction.show(patientConsultationAllFragment);
                                patientConsultationAllFragment.initData(2);
                            }
                            break;
                        case 1:
                            consultationText.setText("诊室");
                            if(primaryConsultationAllFragment == null) {
                                primaryConsultationAllFragment=PrimaryConsultationAllFragment.getInstance(HomeActivity.this);
                                transaction.add(R.id.content_layout, primaryConsultationAllFragment);
                            } else {
                                transaction.show(primaryConsultationAllFragment);
                                primaryConsultationAllFragment.initData(2);
                            }
                            break;
                        case 2:
                            consultationText.setText("诊室");
                            if(expertConsultationAllFragment == null) {
                                expertConsultationAllFragment=ExpertConsultationAllFragment.getInstance(HomeActivity.this);
                                transaction.add(R.id.content_layout, expertConsultationAllFragment);
                            } else {
                                transaction.show(expertConsultationAllFragment);
                                expertConsultationAllFragment.initData(2);
                            }
                            break;

                        default:
                            break;
                    }
                } else {
                    consultationText.setText("咨询");
                    if(patientConsultationAllFragment == null) {
                        patientConsultationAllFragment=PatientConsultationAllFragment.getInstance(HomeActivity.this);
                        transaction.add(R.id.content_layout, patientConsultationAllFragment);
                    } else {
                        transaction.show(patientConsultationAllFragment);
                        patientConsultationAllFragment.initData(2);
                    }
                }
                break;
            case 1:
                currentIndex =1;
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
                currentIndex =2;
                knowledgeImage.setImageResource(R.drawable.knowledge_selected);
                knowledgeText.setTextColor(Color.parseColor("#2CB67A"));
                if(consultationDiscussionFragment == null) {
                    consultationDiscussionFragment=ConsultationDiscussionFragment.getInstance(HomeActivity.this);
                    transaction.add(R.id.content_layout, consultationDiscussionFragment);
                } else {
                    transaction.show(consultationDiscussionFragment);
                    consultationDiscussionFragment.initData(2);
                }
                break;
            case 3:
                currentIndex =3;
                mineImage.setImageResource(R.drawable.mine_selected);
                mineText.setTextColor(Color.parseColor("#2CB67A"));
                if(mineFragment == null) {
                    mineFragment=MineFragment.getInstance(HomeActivity.this);
                    transaction.add(R.id.content_layout, mineFragment);
                } else {
                    transaction.show(mineFragment);
                    // 刷新界面的信息
                    mineFragment.initDate(2);
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
        if(patientConsultationAllFragment != null) {
            transaction.hide(patientConsultationAllFragment);
        }
        if(primaryConsultationAllFragment != null) {
            transaction.hide(primaryConsultationAllFragment);
        }
        if(expertConsultationAllFragment != null) {
            transaction.hide(expertConsultationAllFragment);
        }
        if(specialistFragment != null) {
            transaction.hide(specialistFragment);
        }
        if(consultationDiscussionFragment != null) {
            transaction.hide(consultationDiscussionFragment);
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
        addCaseBtn=(ImageView)findViewById(R.id.add_new_case_image);
        addCaseLayout=(RelativeLayout)findViewById(R.id.add_new_case_layout);
        if(editor.get("userType", "").equals("1")) {
            addCaseLayout.setVisibility(View.VISIBLE);
        }else{
            addCaseLayout.setVisibility(View.GONE);
        }
        addCaseBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(consultationLayout.getContext(), CreateCaseActivity.class));
            }
        });

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
        if(null != editor.get("userType", "") && !"".equals(editor.get("userType", ""))) {
            int type=Integer.parseInt(editor.get("userType", ""));
            switch(type) {
                case 1:
                    consultationText.setText("诊室");
                    break;
                case 2:
                    consultationText.setText("诊室");
                    break;

                default:
                    break;
            }
        }
        specialistText=(TextView)findViewById(R.id.specialist_text);
        specialistText.setTextSize(14);
        knowledgeText=(TextView)findViewById(R.id.knowledge_text);
        knowledgeText.setTextSize(14);
        mineText=(TextView)findViewById(R.id.mine_text);
        mineText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        consultationLayout.setOnClickListener(this);
        specialistLayout.setOnClickListener(this);
        knowledgeLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
        
        CaseCountBroadcastReceiver.setHander(new ConsultationCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                if(rspContent.equals("switch")){
                    initData();
                }else if(rspContent.equals("refresh") && currentIndex==0){
                    //刷新
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    parmas.put("userTp", editor.get("userType", ""));
                    OpenApiService.getInstance(HomeActivity.this).getReadTotalCount(mQueue, parmas, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Message msg=new Message();
                                    msg.obj=responses.getInt("totalCount");
                                    handler.dispatchMessage(msg);
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                        }
                    });
                }
            }
            
            @Override
            public void onFailure(ConsultationCallbackException exp) {
            }
        });
        
        MyBroadcastReceiver.setHander(new ConsultationCallbackHandler() {

            @Override
            public void onSuccess(String rspContent, int statusCode) {
                if(statusCode == 0){
                    setTabSelection(2);
                }else if(statusCode == 1){
                    setTabSelection(0);
                }
            }

            @Override
            public void onFailure(ConsultationCallbackException exp) {

            }
        });
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
                            initData();
                            if(editor.get("userType", "").equals("1")) {
                                addCaseLayout.setVisibility(View.VISIBLE);
                            }
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
                if(ClientUtil.isLogin()) {
                    setTabSelection(3);
                } else {
                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            setTabSelection(3);
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                        }
                    });
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
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
