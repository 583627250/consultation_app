package com.consultation.app.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.consultation.app.ConsultionStatusCode;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;
import com.umeng.message.UmengRegistrar;

public class LoginActivity extends Activity {

    private TextView title_text;

    private TextView userName_text;

    private TextView pwd_text;

    private TextView verification_code_text, verification_text;

    // private ImageView verification_image;

    private Button register_btn, getVerification_btn, login_btn;

    // private LinearLayout code_layout;

    private static ConsultationCallbackHandler handler;

    private EditText account, password, verification_edit;

    private TextView forgetPwd, noAcount;

    // private int loginCount=0;

    private RequestQueue mQueue;

    private SharePreferencesEditor editor;

    // private ImageLoader mImageLoader;

    private int flag, times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mQueue=Volley.newRequestQueue(LoginActivity.this);
        // mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        editor=new SharePreferencesEditor(LoginActivity.this);
        flag=getIntent().getIntExtra("flag", -1);
        init();
    }

    public static void setHandler(ConsultationCallbackHandler h) {
        handler=h;
    }

    private void init() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("用户登陆");
        title_text.setTextSize(20);

        forgetPwd=(TextView)findViewById(R.id.login_forget_pwd_text);
        forgetPwd.setTextSize(17);
        forgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetPwd.getPaint().setAntiAlias(true);
        forgetPwd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 去忘记密码
                startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class));
            }
        });

        noAcount=(TextView)findViewById(R.id.login_no_acount_text);
        noAcount.setTextSize(16);

        verification_text=(TextView)findViewById(R.id.login_code_phone_text);
        verification_text.setTextSize(18);

        verification_edit=(EditText)findViewById(R.id.login_code_phone_input_edit);
        verification_edit.setTextSize(18);

        // back_layout = (LinearLayout)findViewById(R.id.header_layout_lift);
        // back_layout.setVisibility(View.VISIBLE);
        // back_text = (TextView)findViewById(R.id.header_text_lift);
        // back_text.setTextSize(18);
        // back_layout.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        // if (imm.isActive()) {
        // imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );
        // }
        // finish();
        // }
        // });

        userName_text=(TextView)findViewById(R.id.login_username_text);
        userName_text.setTextSize(18);

        pwd_text=(TextView)findViewById(R.id.login_pwd_text);
        pwd_text.setTextSize(18);

        verification_code_text=(TextView)findViewById(R.id.login_code_text);
        verification_code_text.setTextSize(18);

        // verification_image=(ImageView)findViewById(R.id.login_code_imageView);

        account=(EditText)findViewById(R.id.login_username_input_edit);
        account.setTextSize(18);
        password=(EditText)findViewById(R.id.login_pwd_input_edit);
        password.setTextSize(18);
//        code=(EditText)findViewById(R.id.login_code_input_edit);
//        code.setTextSize(18);

        // code_layout=(LinearLayout)findViewById(R.id.login_code_layout);

        // verification_image.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // verification_image.setBackgroundResource(R.anim.loading_anim);
        // AnimationDrawable animation=(AnimationDrawable)verification_image.getBackground();
        // animation.start();
        // ImageListener listener=ImageLoader.getImageListener(verification_image, 0, android.R.drawable.ic_delete);
        // mImageLoader.get(
        // ClientUtil.GET_LOGIN_IMAGE_URL + "??mobile_ph=" + account.getText().toString() + "&ts="
        // + System.currentTimeMillis(), listener);
        // }
        // });

        login_btn=(Button)findViewById(R.id.login_btn_login);
        login_btn.setTextSize(20);

        register_btn=(Button)findViewById(R.id.login_btn_register);
        register_btn.setTextSize(20);

        getVerification_btn=(Button)findViewById(R.id.login_phone_get_btn);
        getVerification_btn.setTextSize(14);
        getVerification_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 获取验证码
                if(null == account.getText().toString() || "".equals(account.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountUtil.isPhoneNum(account.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "手机号输入有误，请重输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("mobile_ph", account.getText().toString());
                CommonUtil.showLoadingDialog(LoginActivity.this);
                OpenApiService.getInstance(LoginActivity.this).getLoginVerification(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    getVerification_btn.setEnabled(false);
                                    Toast.makeText(LoginActivity.this, "验证码请求成功", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            times=30;
                                            while(times >= 0) {
                                                try {
                                                    Message msg=new Message();
                                                    msg.what=times;
                                                    h.sendMessage(msg);
                                                    Thread.sleep(1000);
                                                    times--;
                                                } catch(InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).start();
                                } else {
                                    Toast.makeText(LoginActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(LoginActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });
        getVerification_btn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());

        login_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 登陆
                if(account.getText().toString() == null || "".equals(account.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "手机号不能位空,请输入手机号!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(verification_edit.getText().toString() == null || "".equals(verification_edit.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "验证码不能位空，请输入验证码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountUtil.isPhoneNum(account.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "手机号输入有误,请输入正确的手机号!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString() == null || "".equals(password.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "密码不能位空,请输入密码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().length() < 6) {
                    Toast.makeText(LoginActivity.this, "密码格式不正确,请输入6-20位字母或数字的密码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if(loginCount > 3) {
                // if(code.getText().toString() == null || "".equals(code.getText().toString())) {
                // Toast.makeText(LoginActivity.this, "验证码不能位空,请输入验证码!", Toast.LENGTH_SHORT).show();
                // return;
                // }
                // }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("mobile_ph", account.getText().toString());
                parmas.put("pwd", password.getText().toString());
                parmas.put("device_token", UmengRegistrar.getRegistrationId(LoginActivity.this));
                parmas.put("device_tp", "Android");
                parmas.put("device_tp", "Android");
                // if(loginCount > 3) {
                parmas.put("smsVerifyCode", verification_edit.getText().toString());
                // }
                CommonUtil.showLoadingDialog(LoginActivity.this);
                ClientUtil.setToken("");
                editor.put("refreshToken", "");
                OpenApiService.getInstance(LoginActivity.this).getLogin(mQueue, parmas, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        CommonUtil.closeLodingDialog();
                        try {
                            JSONObject responses=new JSONObject(arg0);
                            if(responses.getInt("rtnCode") == 1) {
                                editor.put("uid", responses.getString("uid"));
                                editor.put("userType", responses.getString("userTp"));
                                editor.put("refreshToken", responses.getString("refreshToken"));
                                editor.put("real_name", responses.getString("real_name"));
                                editor.put("icon_url", responses.getString("icon_url"));
                                editor.put("phone", account.getText().toString());
                                ClientUtil.setToken(responses.getString("accessToken"));
                                handler.onSuccess("登陆成功", ConsultionStatusCode.USER_LOGIN_SUCCESS);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                                // loginCount++;
                                // if(loginCount == 3) {
                                // code_layout.setVisibility(View.VISIBLE);
                                // Map<String, String> imageParmas=new HashMap<String, String>();
                                // imageParmas.put("mobile_ph", account.getText().toString());
                                // imageParmas.put("ts", System.currentTimeMillis() + "");
                                // ImageListener listener=
                                // ImageLoader.getImageListener(verification_image, 0, android.R.drawable.ic_delete);
                                // mImageLoader.get(ClientUtil.GET_LOGIN_IMAGE_URL + "??mobile_ph=" + account.getText().toString()
                                // + "&ts=" + System.currentTimeMillis(), listener);
                                // }
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        CommonUtil.closeLodingDialog();
                        Toast.makeText(LoginActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        login_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());
        register_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 注册
                RegisterActivity.setHandler(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        handler.onSuccess("注册", ConsultionStatusCode.SUCCESS);
                        finish();
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                    }
                });
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        register_btn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
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
        if(keyCode == KeyEvent.KEYCODE_BACK && flag == 1) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler h=new Handler() {

        public void dispatchMessage(Message msg) {
            if(msg.what == 0) {
                getVerification_btn.setEnabled(true);
                getVerification_btn.setText("获取验证码");
            } else {
                getVerification_btn.setText(msg.what + "s后重新发送");
            }
        };
    };

    private void exit() {
        if(!isExit) {
            isExit=true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            bundle.putBoolean("logout", true);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
