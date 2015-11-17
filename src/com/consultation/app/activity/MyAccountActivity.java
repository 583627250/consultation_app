package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint("HandlerLeak")
public class MyAccountActivity extends Activity implements OnClickListener{

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private TextView blanceTitle,withdrawalsTitle,incomeTitle,txText,czText,
                     czjl,zfjl,srjl,txjl,blanceAmont,srAmount,txAmount;
    
    private LinearLayout czLayout,txLayout,czjlLayout,zfjlLayout,srjlLayout,txjlLayout;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_acount_layout);
        editor=new SharePreferencesEditor(this);
        mQueue=Volley.newRequestQueue(this);
        initDate();
        initView();
    }

    private void initDate() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("uid", editor.get("uid", ""));
        parmas.put("accessToken", ClientUtil.getToken());
        CommonUtil.showLoadingDialog(this);
        OpenApiService.getInstance(this).getMyAcountInfo(mQueue, parmas, new Response.Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                CommonUtil.closeLodingDialog();
                try {
                    JSONObject responses=new JSONObject(arg0);
                    if(responses.getInt("rtnCode") == 1) {
                        Message msg=new Message();
                        msg.obj=responses;
                        handler.dispatchMessage(msg);
                    } else if(responses.getInt("rtnCode") == 10004) {
                        Toast.makeText(MyAccountActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                            @Override
                            public void onSuccess(String rspContent, int statusCode) {
                                initDate();
                            }

                            @Override
                            public void onFailure(ConsultationCallbackException exp) {
                            }
                        });
                        startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(MyAccountActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject info=(JSONObject)msg.obj;
            try {
                blanceAmont.setText(info.getString("current_balance"));
                if(editor.get("userType", "").equals("0")){
                    srAmount.setText(info.getString("total_topup"));
                    txAmount.setText(info.getString("total_payment"));
                }else{
                    srAmount.setText(info.getString("total_income"));
                    txAmount.setText(info.getString("total_draw"));
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("我的账户");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                finish();
            }
        });
        
        blanceTitle = (TextView)findViewById(R.id.my_acount_blance_title_text);
        blanceTitle.setTextSize(19);
        incomeTitle = (TextView)findViewById(R.id.my_acount_info_title_income_text);
        incomeTitle.setTextSize(19);
        withdrawalsTitle = (TextView)findViewById(R.id.my_acount_info_title_withdrawals_text);
        withdrawalsTitle.setTextSize(19);
        
        txText = (TextView)findViewById(R.id.my_acount_chongzhi_text);
        txText.setTextSize(19);
        czText = (TextView)findViewById(R.id.my_acount_tixian_text);
        czText.setTextSize(19);
        
        czjl = (TextView)findViewById(R.id.my_acount_chongzhijilu_text);
        czjl.setTextSize(19);
        zfjl = (TextView)findViewById(R.id.my_acount_payjilu_text);
        zfjl.setTextSize(19);
        srjl = (TextView)findViewById(R.id.my_acount_incomejilu_text);
        srjl.setTextSize(19);
        txjl = (TextView)findViewById(R.id.my_acount_tixianjilu_text);
        txjl.setTextSize(19);
        
        
        blanceAmont = (TextView)findViewById(R.id.my_acount_blance_text);
        blanceAmont.setTextSize(17);
        srAmount = (TextView)findViewById(R.id.my_acount_info_income_text);
        srAmount.setTextSize(17);
        txAmount = (TextView)findViewById(R.id.my_acount_info_withdrawals_text);
        txAmount.setTextSize(17);
        
        czLayout = (LinearLayout)findViewById(R.id.my_acount_chongzhi_layout);
        czLayout.setOnClickListener(this);
        txLayout = (LinearLayout)findViewById(R.id.my_acount_tixian_layout);
        txLayout.setOnClickListener(this);
        
        czjlLayout = (LinearLayout)findViewById(R.id.my_acount_chongzhijilu_layout);
        czjlLayout.setOnClickListener(this);
        zfjlLayout = (LinearLayout)findViewById(R.id.my_acount_payjilu_layout);
        zfjlLayout.setOnClickListener(this);
        
        srjlLayout = (LinearLayout)findViewById(R.id.my_acount_incomejilu_layout);
        srjlLayout.setOnClickListener(this);
        txjlLayout = (LinearLayout)findViewById(R.id.my_acount_tixianjilu_layout);
        txjlLayout.setOnClickListener(this);
        
        if(editor.get("userType", "").equals("0")){
            incomeTitle.setText("总充值(元)");
            withdrawalsTitle.setText("总消费(元)");
            txLayout.setVisibility(View.GONE);
            LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.my_acount_line1);
            linearLayout1.setVisibility(View.GONE);
            LinearLayout linearLayout2 = (LinearLayout)findViewById(R.id.my_acount_line2);
            linearLayout2.setVisibility(View.VISIBLE);
            srjlLayout.setVisibility(View.GONE);
            txjlLayout.setVisibility(View.GONE);
        }else{
            LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.my_acount_line1);
            linearLayout1.setVisibility(View.VISIBLE);
            LinearLayout linearLayout2 = (LinearLayout)findViewById(R.id.my_acount_line2);
            linearLayout2.setVisibility(View.GONE);
            czLayout.setVisibility(View.GONE);
            czjlLayout.setVisibility(View.GONE);
            zfjlLayout.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.my_acount_chongzhi_layout:
                //充值界面
                Intent payIntent = new Intent(MyAccountActivity.this, PayActivity.class);
                startActivityForResult(payIntent,0);
                break;
            case R.id.my_acount_tixian_layout:
                //提现界面
                Intent txIntent = new Intent(MyAccountActivity.this, PayActivity.class);
                startActivityForResult(txIntent,1);
                break;
            case R.id.my_acount_chongzhijilu_layout:
                //充值记录界面
                Intent intent = new Intent(MyAccountActivity.this, RechargeRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.my_acount_payjilu_layout:
                //支付记录界面
                Intent intentPayRecord = new Intent(MyAccountActivity.this, PayRecordActivity.class);
                startActivity(intentPayRecord);
                break;
            case R.id.my_acount_incomejilu_layout:
                //收入记录界面
                Intent intentIncomRecord = new Intent(MyAccountActivity.this, IncomeRecordActivity.class);
                startActivity(intentIncomRecord);
                break;
            case R.id.my_acount_tixianjilu_layout:
                //提现记录界面
                Intent intentWithdrawalsRecord = new Intent(MyAccountActivity.this, WithdrawalsRecordActivity.class);
                startActivity(intentWithdrawalsRecord);
                break;

            default:
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode) {
                case 0:
                    //支付充值
                    initDate();
                    break;
                case 1:
                    //提现
                    initDate();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
