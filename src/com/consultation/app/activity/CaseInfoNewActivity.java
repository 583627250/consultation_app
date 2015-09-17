package com.consultation.app.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.consultation.app.R;
import com.consultation.app.exception.ConsultationCallbackException;
import com.consultation.app.listener.ButtonListener;
import com.consultation.app.listener.ConsultationCallbackHandler;
import com.consultation.app.model.CaseContentTo;
import com.consultation.app.model.CaseTo;
import com.consultation.app.model.DiscussionTo;
import com.consultation.app.model.ImageFilesTo;
import com.consultation.app.model.PatientCaseTo;
import com.consultation.app.model.UserTo;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.ActivityList;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.CaseBroadcastReceiver;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint({"SimpleDateFormat", "HandlerLeak"})
public class CaseInfoNewActivity extends Activity {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private String caseId;

    private SharePreferencesEditor editor;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private CaseTo caseTo;

    private ListView discussionListView;

    private MyAdapter myAdapter=new MyAdapter();

    private ViewHolder holder;

    private ArrayList<DiscussionTo> discussionList=new ArrayList<DiscussionTo>();

    private boolean havaCase=false, isXml=false, havaImage;

    private TextView titleTitle, xbsTitle, zljgTitle, jwsTitle, jzsTitle, tgjcTitle, fzjcTitle, xqTitle, zxfsTitle, ztTitle,
            bhyyTitle, bltlTitle;

    private TextView infoNameText, infoSexText, infoAgeText, titleText, xbsText, zljgText, jwsText, jzsText, tgjcText, xqText,
            zxfsText, ztText, bhyyText;

    private LinearLayout bhyyLayout, discussionLayout, showNewLayout, showDiscussionLayout, showFinshLayout, starLayout,
            examineLayout, expertDisLayout, expertDiscussionLayout, imageLayout;

    private ScrollView scrollView;

    private Button submit_btn, update_btn, evaluation_btn, discussion_send_btn, discussion_more_btn, acceptance_btn,
            not_accepted_btn, expert_send_btn, expert_submit_btn;

    private TextView evaluation_tip;

    private EditText evaluation_edit, discussion_edit, expert_dis_edit;

    private RatingBar evaluation_ratingBar;

    private TextView expertTitle, bcbsText, wsjcText, zdyjText, jyssText;

    private EditText bcbsEdit, wsjcEdit, zdyjEdit, jyssEdit;

    private String imageString="";

    private int width;

    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_info_new_layout);
        caseId=getIntent().getStringExtra("caseId");
        editor=new SharePreferencesEditor(CaseInfoNewActivity.this);
        ActivityList.getInstance().setActivitys("CaseInfoNewActivity", this);
        mQueue=Volley.newRequestQueue(CaseInfoNewActivity.this);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        initData();
        initView();
    }

    private void initData() {
        Map<String, String> parmas=new HashMap<String, String>();
        parmas.put("id", caseId);
        parmas.put("accessToken", ClientUtil.getToken());
        parmas.put("uid", editor.get("uid", ""));
        CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
        OpenApiService.getInstance(CaseInfoNewActivity.this).getPatientCaseListInfo(mQueue, parmas,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    try {
                        JSONObject responses=new JSONObject(arg0);
                        if(responses.getInt("rtnCode") == 1) {
                            caseTo=new CaseTo();
                            JSONObject patientCaseObject=responses.getJSONObject("patientCase");
                            PatientCaseTo patientCaseTo=new PatientCaseTo();
                            patientCaseTo.setId(patientCaseObject.getString("id"));
                            patientCaseTo.setTitle(patientCaseObject.getString("title"));
                            patientCaseTo.setProblem(patientCaseObject.getString("problem"));
                            String consult_fee=patientCaseObject.getString("consult_fee");
                            if(consult_fee.equals("null")) {
                                patientCaseTo.setConsult_fee("0");
                            } else {
                                patientCaseTo.setConsult_fee(consult_fee);
                            }
                            patientCaseTo.setConsult_tp(patientCaseObject.getString("consult_tp"));
                            patientCaseTo.setDepart_id(patientCaseObject.getString("depart_id"));
                            patientCaseTo.setStatus(patientCaseObject.getString("status"));
                            if(patientCaseObject.getString("status").equals("30")
                                || patientCaseObject.getString("status").equals("21")) {
                                caseTo.setHandleReason(responses.getString("handleReason"));
                            }
                            patientCaseTo.setStatus_desc(patientCaseObject.getString("status_desc"));
                            patientCaseTo.setPatient_name(patientCaseObject.getString("patient_name"));
                            patientCaseTo.setPatient_userid(patientCaseObject.getString("patient_userid"));
                            patientCaseTo.setOpinion(patientCaseObject.getString("opinion"));
                            patientCaseTo.setDoctor_name(patientCaseObject.getString("doctor_name"));
                            patientCaseTo.setDoctor_userid(patientCaseObject.getString("doctor_userid"));
                            patientCaseTo.setExpert_name(patientCaseObject.getString("expert_name"));
                            patientCaseTo.setExpert_userid(patientCaseObject.getString("expert_userid"));
                            String createTime=patientCaseObject.getString("create_time");
                            if(createTime.equals("null")) {
                                patientCaseTo.setCreate_time("0");
                            } else {
                                patientCaseTo.setCreate_time(createTime);
                            }
                            UserTo userTo=new UserTo();
                            JSONObject userObject=patientCaseObject.getJSONObject("user");
                            userTo.setSex(userObject.getString("sex"));
                            userTo.setBirth_year(userObject.getString("birth_year"));
                            userTo.setBirth_month(userObject.getString("birth_month"));
                            userTo.setBirth_day(userObject.getString("birth_day"));
                            patientCaseTo.setUserTo(userTo);
                            caseTo.setPatientCase(patientCaseTo);
                            if(!responses.getString("caseContent").equals("null")) {
                                havaCase=true;
                                CaseContentTo caseContentTo=new CaseContentTo();
                                JSONObject caseContentJsonObject=responses.getJSONObject("caseContent");
                                caseContentTo.setCaseId(caseContentJsonObject.getString("case_id"));
                                caseContentTo.setFill_tp(caseContentJsonObject.getString("fill_tp"));
                                caseContentTo.setContent_zs_xml(caseContentJsonObject.getString("content_zs_xml"));
                                if(!caseContentJsonObject.getString("content_zs_xml").equals("null")) {
                                    caseContentTo.setContent_zs_xml(caseContentJsonObject.getString("content_zs_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_zs_txt").equals("null")) {
                                    caseContentTo.setContent_zs_txt(caseContentJsonObject.getString("content_zs_txt"));
                                }
                                if(!caseContentJsonObject.getString("content_tz_xml").equals("null")) {
                                    caseContentTo.setContent_tz_xml(caseContentJsonObject.getString("content_tz_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_tz_txt").equals("null")) {
                                    caseContentTo.setContent_tz_txt(caseContentJsonObject.getString("content_tz_txt"));
                                }
                                if(!caseContentJsonObject.getString("content_zljg_xml").equals("null")) {
                                    caseContentTo.setContent_zljg_xml(caseContentJsonObject.getString("content_zljg_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_zljg_txt").equals("null")) {
                                    caseContentTo.setContent_zljg_txt(caseContentJsonObject.getString("content_zljg_txt"));
                                }
                                if(!caseContentJsonObject.getString("content_jws_xml").equals("null")) {
                                    caseContentTo.setContent_jws_xml(caseContentJsonObject.getString("content_jws_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_jws_txt").equals("null")) {
                                    caseContentTo.setContent_jws_txt(caseContentJsonObject.getString("content_jws_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_jzs_xml").equals("null")) {
                                    caseContentTo.setContent_jzs_xml(caseContentJsonObject.getString("content_jzs_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_jzs_txt").equals("null")) {
                                    caseContentTo.setContent_jzs_txt(caseContentJsonObject.getString("content_jzs_txt"));
                                }
                                if(!caseContentJsonObject.getString("content_jy_xml").equals("null")) {
                                    caseContentTo.setContent_jy_xml(caseContentJsonObject.getString("content_jy_xml"));
                                }
                                if(!caseContentJsonObject.getString("content_jy_txt").equals("null")) {
                                    caseContentTo.setContent_jy_txt(caseContentJsonObject.getString("content_jy_txt"));
                                }
                                caseTo.setCaseContentTo(caseContentTo);
                            }
                            if(!responses.getString("caseFiles").equals("null")) {
                                havaImage=true;
                                imageString=responses.getString("caseFiles");
                                ArrayList<ImageFilesTo> imageFilesTos=new ArrayList<ImageFilesTo>();
                                JSONArray imageFilesArray=responses.getJSONArray("caseFiles");
                                for(int i=0; i < imageFilesArray.length(); i++) {
                                    ImageFilesTo imageFilesTo=new ImageFilesTo();
                                    JSONObject imageFilesObject=imageFilesArray.getJSONObject(i);
                                    imageFilesTo.setCase_id(imageFilesObject.getString("case_id"));
                                    imageFilesTo.setId(imageFilesObject.getString("id"));
                                    imageFilesTo.setPic_url(imageFilesObject.getString("pic_url"));
                                    imageFilesTo.setLittle_pic_url(imageFilesObject.getString("little_pic_url"));
                                    imageFilesTo.setTest_name(imageFilesObject.getString("test_name"));
                                    imageFilesTos.add(imageFilesTo);
                                }
                                caseTo.setImageFilesTos(imageFilesTos);
                            }
                            if(!responses.getString("caseDiscusss").equals("null")) {
                                JSONArray discussionJSONArray=responses.getJSONArray("caseDiscusss");
                                for(int i=0; i < discussionJSONArray.length(); i++) {
                                    DiscussionTo discussionTo=new DiscussionTo();
                                    JSONObject discussionObject=discussionJSONArray.getJSONObject(i);
                                    discussionTo.setId(discussionObject.getString("id"));
                                    discussionTo.setContent(discussionObject.getString("content"));
                                    String createTime1=discussionObject.getString("create_time");
                                    if(createTime1.equals("null")) {
                                        discussionTo.setCreate_time(0);
                                    } else {
                                        discussionTo.setCreate_time(Long.parseLong(createTime1));
                                    }
                                    discussionTo.setCase_id(discussionObject.getString("case_id"));
                                    discussionTo.setAt_userid(discussionObject.getString("at_userid"));
                                    discussionTo.setAt_username(discussionObject.getString("at_username"));
                                    discussionTo.setDiscusser(discussionObject.getString("discusser"));
                                    discussionTo.setDiscusser_userid(discussionObject.getString("discusser_userid"));
                                    discussionTo.setIs_view(discussionObject.getString("is_view"));
                                    discussionTo.setHave_photos(discussionObject.getString("have_photos"));
                                    JSONObject userObject1=discussionObject.getJSONObject("user");
                                    UserTo userTo1=new UserTo();
                                    userTo1.setIcon_url(userObject1.getString("icon_url"));
                                    userTo1.setUser_name(userObject1.getString("real_name"));
                                    userTo1.setTp(userObject1.getString("tp"));
                                    discussionTo.setUserTo(userTo1);
                                    if(discussionTo.getHave_photos().equals("1")) {
                                        ImageFilesTo filesTo=new ImageFilesTo();
                                        List<ImageFilesTo> list=new ArrayList<ImageFilesTo>();
                                        if(discussionObject.getString("cdFiles") != null
                                            && !"".equals(discussionObject.getString("cdFiles"))
                                            && !"null".equals(discussionObject.getString("cdFiles"))) {
                                            JSONArray jsonArray=discussionObject.getJSONArray("cdFiles");
                                            for(int j=0; j < jsonArray.length(); j++) {
                                                JSONObject jsonObject=jsonArray.getJSONObject(j);
                                                filesTo.setCase_id(jsonObject.getString("case_id"));
                                                filesTo.setPic_url(jsonObject.getString("pic_url"));
                                                filesTo.setLittle_pic_url(jsonObject.getString("little_pic_url"));
                                                filesTo.setTest_name(jsonObject.getString("test_name"));
                                                list.add(filesTo);
                                            }
                                            discussionTo.setImageFilesTos(list);
                                        }
                                    }
                                    discussionList.add(discussionTo);
                                }
                                caseTo.setDiscussionTos(discussionList);
                            }
                            handler.sendEmptyMessage(0);
                        } else if(responses.getInt("rtnCode") == 10004) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                            LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                @Override
                                public void onSuccess(String rspContent, int statusCode) {
                                    initData();
                                }

                                @Override
                                public void onFailure(ConsultationCallbackException exp) {
                                }
                            });
                            startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                        } else {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    CommonUtil.closeLodingDialog();
                    Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });
    }

    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            scrollView.scrollTo(0, 0);
            setListViewHeightBasedOnChildren(discussionListView);
            AssetManager assetManager=getAssets();
            try {
                for(String str: assetManager.list("")) {
                    if(str.endsWith("case.xml")) {
                        if(str.equals(caseTo.getPatientCase().getDepart_id() + "case.xml")) {
                            isXml=true;
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            infoNameText.setText(caseTo.getPatientCase().getPatient_name());
            if(caseTo.getPatientCase().getUserTo().getSex().equals("0")) {
                infoSexText.setText("女");
            } else {
                infoSexText.setText("男");
            }
            Date date=new Date();
            int currentYear=date.getYear() + 1900;
            infoAgeText.setText((currentYear - Integer.parseInt(caseTo.getPatientCase().getUserTo().getBirth_year())) + "岁");
            titleText.setText(caseTo.getPatientCase().getTitle());
            if(havaCase) {
                xbsText.setText(caseTo.getCaseContentTo().getContent_zs_txt());
                zljgText.setText(caseTo.getCaseContentTo().getContent_zljg_txt());
                jwsText.setText(caseTo.getCaseContentTo().getContent_jws_txt());
                jzsText.setText(caseTo.getCaseContentTo().getContent_jzs_txt());
                tgjcText.setText(caseTo.getCaseContentTo().getContent_jy_txt());
            }
            xqText.setText(caseTo.getPatientCase().getProblem());
            zxfsText.setText(caseTo.getPatientCase().getConsult_tp());
            ztText.setText(caseTo.getPatientCase().getStatus());
            if(havaImage) {
                // 显示图片
                System.out.println(caseTo.getImageFilesTos().size());
                if(isXml) {
                    // 显示 标题加图片
                    showImageLayout(caseTo.getImageFilesTos());
                } else {
                    // 显示图片
                    showImageLayout(caseTo.getImageFilesTos());
                }
            }
            if(caseTo.getPatientCase().getStatus().equals("已驳回") || caseTo.getPatientCase().getStatus().equals("已拒绝")) {
                bhyyTitle.setVisibility(View.VISIBLE);
                bhyyLayout.setVisibility(View.VISIBLE);
                bhyyText.setVisibility(View.VISIBLE);
                bhyyText.setText(caseTo.getHandleReason());
            }
            if(caseTo.getPatientCase().getStatus().equals("讨论中") || caseTo.getPatientCase().getStatus().equals("已完成")
                || caseTo.getPatientCase().getStatus().equals("已归档")) {
                bltlTitle.setVisibility(View.VISIBLE);
                discussionListView.setVisibility(View.VISIBLE);
                discussionLayout.setVisibility(View.VISIBLE);
            }
            if(caseTo.getPatientCase().getConsult_tp().equals("明确诊断")) {
                if(editor.get("userType", "").equals("1")) {
                    if(caseTo.getPatientCase().getStatus().equals("新建") || caseTo.getPatientCase().getStatus().equals("已驳回")
                        || caseTo.getPatientCase().getStatus().equals("拒受理")) {
                        showNewLayout.setVisibility(View.VISIBLE);
                        showDiscussionLayout.setVisibility(View.GONE);
                        showFinshLayout.setVisibility(View.GONE);
                    } else if(caseTo.getPatientCase().getStatus().equals("讨论中")) {
                        showNewLayout.setVisibility(View.GONE);
                        showDiscussionLayout.setVisibility(View.VISIBLE);
                        showFinshLayout.setVisibility(View.GONE);
                    } else if(caseTo.getPatientCase().getStatus().equals("完成")) {
                        showNewLayout.setVisibility(View.GONE);
                        showDiscussionLayout.setVisibility(View.GONE);
                        showFinshLayout.setVisibility(View.VISIBLE);
                    }
                } else if(editor.get("userType", "").equals("2")) {
                    if(caseTo.getPatientCase().getStatus().equals("已审核")) {
                        examineLayout.setVisibility(View.VISIBLE);
                        expertDisLayout.setVisibility(View.GONE);
                    } else if(caseTo.getPatientCase().getStatus().equals("讨论中")) {
                        examineLayout.setVisibility(View.GONE);
                        expertDisLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else if(caseTo.getPatientCase().getConsult_tp().equals("公开讨论")) {
                if(editor.get("userType", "").equals("1")) {
                    if(caseTo.getPatientCase().getStatus().equals("新建") || caseTo.getPatientCase().getStatus().equals("已驳回")
                        || caseTo.getPatientCase().getStatus().equals("拒受理")) {
                        showNewLayout.setVisibility(View.VISIBLE);
                        showDiscussionLayout.setVisibility(View.GONE);
                        showFinshLayout.setVisibility(View.GONE);
                    } else if(caseTo.getPatientCase().getStatus().equals("讨论中")) {
                        showNewLayout.setVisibility(View.GONE);
                        showDiscussionLayout.setVisibility(View.VISIBLE);
                        showFinshLayout.setVisibility(View.GONE);
                    }
                } else if(editor.get("userType", "").equals("2")) {
                    if(caseTo.getPatientCase().getStatus().equals("讨论中")) {
                        // 专家讨论的界面
                        expertDiscussionLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            CommonUtil.closeLodingDialog();
        }
    };

    private void showImageLayout(ArrayList<ImageFilesTo> imageFilesTos) {
        imageLayout.removeAllViews();
        if(null != imageFilesTos && imageFilesTos.size() != 0) {
            LinearLayout rowsLayout=new LinearLayout(CaseInfoNewActivity.this);
            LinearLayout relativeLayout=new LinearLayout(CaseInfoNewActivity.this);
            for(int i=0; i < imageFilesTos.size(); i++) {
                if(i % 3 == 0) {
                    rowsLayout=createLinearLayout();
                    imageLayout.addView(rowsLayout);
                }
                relativeLayout=createImage(imageFilesTos.get(i).getLittle_pic_url(), imageFilesTos.get(i).getPic_url());
                rowsLayout.addView(relativeLayout);
            }
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout=new LinearLayout(CaseInfoNewActivity.this);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_VERTICAL;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, height / 100, 0, height / 100);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private LinearLayout createImage(String path, final String bigPath) {
        LinearLayout relativeLayout=new LinearLayout(CaseInfoNewActivity.this);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 55;
        layoutParams.rightMargin=width / 55;
        relativeLayout.setLayoutParams(layoutParams);
        ImageView imageView=new ImageView(CaseInfoNewActivity.this);
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 展示大图片
                BigImageActivity.setViewData(mImageLoader, bigPath);
                startActivity(new Intent(CaseInfoNewActivity.this, BigImageActivity.class));
            }
        });
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setTag(path);
        if(!"null".equals(path) && !"".equals(path)) {
            ImageListener listener=
                ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_delete);
            mImageLoader.get(path, listener, 200, 200);
        }
        relativeLayout.addView(imageView);
        return relativeLayout;
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("病例摘要");
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

        scrollView=(ScrollView)findViewById(R.id.case_info_all_scrollView);

        bhyyLayout=(LinearLayout)findViewById(R.id.case_info_new_bhyy_layout);
        discussionLayout=(LinearLayout)findViewById(R.id.case_info_new_discussion_layout);
        showNewLayout=(LinearLayout)findViewById(R.id.case_info_new_show_new_layout);
        showDiscussionLayout=(LinearLayout)findViewById(R.id.case_info_new_show_discussion_layout);
        showFinshLayout=(LinearLayout)findViewById(R.id.case_info_new_show_finsh_layout);
        starLayout=(LinearLayout)findViewById(R.id.case_info_new_evaluation_stars_layout);
        examineLayout=(LinearLayout)findViewById(R.id.case_info_new_show_examine_layout);
        expertDiscussionLayout=(LinearLayout)findViewById(R.id.case_info_new_show_expert_discussion_layout);
        expertDisLayout=(LinearLayout)findViewById(R.id.case_info_new_show_expert_dis_layout);
        imageLayout=(LinearLayout)findViewById(R.id.case_info_new_title_fzjc_layout);

        titleTitle=(TextView)findViewById(R.id.case_info_new_title_zs_text);
        titleTitle.setTextSize(18);

        xbsTitle=(TextView)findViewById(R.id.case_info_new_title_xbs_text);
        xbsTitle.setTextSize(18);

        zljgTitle=(TextView)findViewById(R.id.case_info_new_title_zljg_text);
        zljgTitle.setTextSize(18);

        jwsTitle=(TextView)findViewById(R.id.case_info_new_title_jws_text);
        jwsTitle.setTextSize(18);

        jzsTitle=(TextView)findViewById(R.id.case_info_new_title_jzs_text);
        jzsTitle.setTextSize(18);

        tgjcTitle=(TextView)findViewById(R.id.case_info_new_title_tgjy_text);
        tgjcTitle.setTextSize(18);

        fzjcTitle=(TextView)findViewById(R.id.case_info_new_title_fzjc_text);
        fzjcTitle.setTextSize(18);

        xqTitle=(TextView)findViewById(R.id.case_info_new_title_xq_text);
        xqTitle.setTextSize(18);

        zxfsTitle=(TextView)findViewById(R.id.case_info_new_title_zxfs_text);
        zxfsTitle.setTextSize(18);

        ztTitle=(TextView)findViewById(R.id.case_info_new_title_zt_text);
        ztTitle.setTextSize(18);

        bhyyTitle=(TextView)findViewById(R.id.case_info_new_title_bhyy_text);
        bhyyTitle.setTextSize(18);

        bltlTitle=(TextView)findViewById(R.id.case_info_new_title_tl_text);
        bltlTitle.setTextSize(18);

        infoNameText=(TextView)findViewById(R.id.case_info_new_patient_name_text);
        infoNameText.setTextSize(17);

        infoSexText=(TextView)findViewById(R.id.case_info_new_patient_sex_text);
        infoSexText.setTextSize(17);

        infoAgeText=(TextView)findViewById(R.id.case_info_new_patient_age_text);
        infoAgeText.setTextSize(17);

        titleText=(TextView)findViewById(R.id.case_info_new_zs_text);
        titleText.setTextSize(17);

        xbsText=(TextView)findViewById(R.id.case_info_new_xbs_text);
        xbsText.setTextSize(17);

        zljgText=(TextView)findViewById(R.id.case_info_new_zljg_text);
        zljgText.setTextSize(17);

        jwsText=(TextView)findViewById(R.id.case_info_new_jws_text);
        jwsText.setTextSize(17);

        jzsText=(TextView)findViewById(R.id.case_info_new_jzs_text);
        jzsText.setTextSize(17);

        tgjcText=(TextView)findViewById(R.id.case_info_new_tgjy_text);
        tgjcText.setTextSize(17);

        xqText=(TextView)findViewById(R.id.case_info_new_xq_text);
        xqText.setTextSize(17);

        zxfsText=(TextView)findViewById(R.id.case_info_new_zxfs_text);
        zxfsText.setTextSize(17);

        ztText=(TextView)findViewById(R.id.case_info_new_zt_text);
        ztText.setTextSize(17);

        bhyyText=(TextView)findViewById(R.id.case_info_new_bhyy_text);
        bhyyText.setTextSize(17);

        expertTitle=(TextView)findViewById(R.id.case_info_new_title_zjtlk_text);
        expertTitle.setTextSize(18);
        bcbsText=(TextView)findViewById(R.id.case_info_new_title_zjtlk_bcbs_text);
        bcbsText.setTextSize(18);
        wsjcText=(TextView)findViewById(R.id.case_info_new_title_zjtlk_wsjc_text);
        wsjcText.setTextSize(17);
        zdyjText=(TextView)findViewById(R.id.case_info_new_title_zjtlk_zdyj_text);
        zdyjText.setTextSize(17);
        jyssText=(TextView)findViewById(R.id.case_info_new_title_zjtlk_jyss_text);
        jyssText.setTextSize(17);

        bcbsEdit=(EditText)findViewById(R.id.case_info_new_title_zjtlk_bcbs_input_edit);
        bcbsEdit.setTextSize(17);
        wsjcEdit=(EditText)findViewById(R.id.case_info_new_title_zjtlk_wsjc_input_edit);
        wsjcEdit.setTextSize(17);
        zdyjEdit=(EditText)findViewById(R.id.case_info_new_title_zjtlk_zdyj_input_edit);
        zdyjEdit.setTextSize(17);
        jyssEdit=(EditText)findViewById(R.id.case_info_new_title_zjtlk_jyss_input_edit);
        jyssEdit.setTextSize(17);

        discussionListView=(ListView)findViewById(R.id.case_info_new_discussion_listView);
        discussionListView.setAdapter(myAdapter);

        submit_btn=(Button)findViewById(R.id.case_info_new_btn_submit);
        submit_btn.setTextSize(18);
        submit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 提交申请
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("case_id", caseId);
                parmas.put("is_submit", "1");
                if(isXml) {
                    parmas.put("fill_tp", "2");
                } else {
                    parmas.put("fill_tp", "1");
                }
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                OpenApiService.getInstance(CaseInfoNewActivity.this).getCaseSaveInfo(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(CaseInfoNewActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        submit_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        update_btn=(Button)findViewById(R.id.case_info_new_btn_update);
        update_btn.setTextSize(18);
        update_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 修改病例
                Intent intent=new Intent(CaseInfoNewActivity.this, CreateCaseActivity.class);
                intent.putExtra("caseId", caseId);
                intent.putExtra("departmentId", caseTo.getPatientCase().getDepart_id());
                intent.putExtra("expertId", caseTo.getPatientCase().getExpert_userid());
                intent.putExtra("expertName", caseTo.getPatientCase().getExpert_name());
                intent.putExtra("patientId", caseTo.getPatientCase().getPatient_userid());
                intent.putExtra("patientName", caseTo.getPatientCase().getPatient_name());
                intent.putExtra("consultType", caseTo.getPatientCase().getConsult_tp());
                intent.putExtra("titles", caseTo.getPatientCase().getTitle());
                intent.putExtra("problem", caseTo.getPatientCase().getProblem());
                intent.putExtra("imageString", imageString);
                intent.putExtra("isUpdate", true);
                StringBuffer buffer=new StringBuffer();
                if(havaCase) {
                    if(caseTo.getCaseContentTo().getFill_tp().equals("1")) {
                        // txt
                        buffer.append(caseTo.getCaseContentTo().getContent_zs_txt());
                        buffer.append(caseTo.getCaseContentTo().getContent_tz_txt()).append("&");
                        buffer.append(caseTo.getCaseContentTo().getContent_jy_txt()).append("&");
                        buffer.append(caseTo.getCaseContentTo().getContent_zljg_txt()).append("&");
                        buffer.append(caseTo.getCaseContentTo().getContent_jws_txt()).append("&");
                        buffer.append(caseTo.getCaseContentTo().getContent_jzs_txt()).append("&");
                        intent.putExtra("content", buffer.toString());
                    } else if(caseTo.getCaseContentTo().getFill_tp().equals("2")) {
                        // xml
                        buffer.append(caseTo.getCaseContentTo().getContent_zs_xml()).append(",");
                        buffer.append(caseTo.getCaseContentTo().getContent_tz_xml()).append(",");
                        buffer.append(caseTo.getCaseContentTo().getContent_jy_xml()).append(",");
                        buffer.append(caseTo.getCaseContentTo().getContent_zljg_xml()).append(",");
                        buffer.append(caseTo.getCaseContentTo().getContent_jws_xml()).append(",");
                        buffer.append(caseTo.getCaseContentTo().getContent_jzs_xml()).append(",");
                        intent.putExtra("content", buffer.toString());
                    }
                }
                startActivity(intent);
                CaseBroadcastReceiver.setHander(new ConsultationCallbackHandler() {

                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        initData();
                        initView();
                    }

                    @Override
                    public void onFailure(ConsultationCallbackException exp) {
                    }
                });

            }
        });
        update_btn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        evaluation_btn=(Button)findViewById(R.id.case_info_new_evaluation_btn);
        evaluation_btn.setTextSize(17);
        evaluation_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 初级医师评价
                if(null == evaluation_edit.getText().toString() || "".equals(evaluation_edit.getText().toString())) {
                    Toast.makeText(CaseInfoNewActivity.this, "请输入评价内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((int)evaluation_ratingBar.getRating() == 0) {
                    Toast.makeText(CaseInfoNewActivity.this, "请选择评价星级", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("comment_userid", editor.get("uid", ""));
                parmas.put("commenter", editor.get("real_name", "医生"));
                parmas.put("doctor_userid", caseTo.getPatientCase().getExpert_userid());
                parmas.put("star_value", (int)(evaluation_ratingBar.getRating()) * 10 + "");
                parmas.put("comment_desc", evaluation_edit.getText().toString());
                parmas.put("case_id", caseId);
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                OpenApiService.getInstance(CaseInfoNewActivity.this).getSaveDoctorComment(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    finish();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        evaluation_btn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        evaluation_ratingBar=(RatingBar)findViewById(R.id.case_info_new_evalution_feedback_ratingBar);

        evaluation_tip=(TextView)findViewById(R.id.case_info_new_evaluation_stars_text);
        evaluation_tip.setTextSize(17);

        evaluation_edit=(EditText)findViewById(R.id.case_info_new_evaluation_input_edit);
        evaluation_edit.setTextSize(17);
        evaluation_edit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    starLayout.setVisibility(View.VISIBLE);
                } else {
                    starLayout.setVisibility(View.GONE);
                }
            }
        });

        discussion_edit=(EditText)findViewById(R.id.case_info_new_show_discussion_input_edit);
        discussion_edit.setTextSize(17);

        expert_dis_edit=(EditText)findViewById(R.id.case_info_new_expert_discussion_input_edit);
        expert_dis_edit.setTextSize(17);

        discussion_send_btn=(Button)findViewById(R.id.case_info_new_show_discussion_send_btn);
        discussion_send_btn.setTextSize(17);
        discussion_send_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 初级医师讨论
                if(null == discussion_edit.getText().toString() || "".equals(discussion_edit.getText().toString())) {
                    Toast.makeText(CaseInfoNewActivity.this, "请输入讨论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("case_id", caseId);
                parmas.put("discusser_userid", editor.get("uid", ""));
                parmas.put("discusser", editor.get("real_name", "医生"));
                parmas.put("content", discussion_edit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                OpenApiService.getInstance(CaseInfoNewActivity.this).getSendDiscussionCase(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    DiscussionTo discussionTo=new DiscussionTo();
                                    discussionTo.setCreate_time(System.currentTimeMillis());
                                    discussionTo.setCase_id(caseId);
                                    discussionTo.setDiscusser(editor.get("real_name", "医生"));
                                    discussionTo.setDiscusser_userid(editor.get("uid", ""));
                                    discussionTo.setContent(discussion_edit.getText().toString());
                                    discussionTo.setIs_view("1");
                                    discussionTo.setHave_photos("0");
                                    UserTo userTo=new UserTo();
                                    userTo.setTp(editor.get("userType", ""));
                                    userTo.setIcon_url(editor.get("icon_url", ""));
                                    userTo.setUser_name(editor.get("real_name", ""));
                                    discussionTo.setUserTo(userTo);
                                    ImageFilesTo filesTo=new ImageFilesTo();
                                    filesTo.setCase_id(caseId);
                                    filesTo.setPic_url("");
                                    filesTo.setTest_name("");
                                    List<ImageFilesTo> list=new ArrayList<ImageFilesTo>();
                                    list.add(filesTo);
                                    discussionTo.setImageFilesTos(list);
                                    discussionList.add(discussionTo);
                                    myAdapter.notifyDataSetChanged();
                                    discussionListView.setSelection(discussionList.size());
                                    discussion_edit.setText("");
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        expert_send_btn=(Button)findViewById(R.id.case_info_new_expert_discussion_btn);
        expert_send_btn.setTextSize(17);
        expert_send_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 专家讨论
                if(null == discussion_edit.getText().toString() || "".equals(discussion_edit.getText().toString())) {
                    Toast.makeText(CaseInfoNewActivity.this, "请输入讨论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("case_id", caseId);
                parmas.put("discusser_userid", editor.get("uid", ""));
                parmas.put("discusser", editor.get("real_name", "专家"));
                parmas.put("content", discussion_edit.getText().toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                OpenApiService.getInstance(CaseInfoNewActivity.this).getSendDiscussionCase(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    DiscussionTo discussionTo=new DiscussionTo();
                                    discussionTo.setCreate_time(System.currentTimeMillis());
                                    discussionTo.setCase_id(caseId);
                                    discussionTo.setDiscusser(editor.get("real_name", "医生"));
                                    discussionTo.setDiscusser_userid(editor.get("uid", ""));
                                    discussionTo.setContent(discussion_edit.getText().toString());
                                    discussionTo.setIs_view("1");
                                    discussionTo.setHave_photos("0");
                                    UserTo userTo=new UserTo();
                                    userTo.setTp(editor.get("userType", ""));
                                    userTo.setIcon_url(editor.get("icon_url", ""));
                                    userTo.setUser_name(editor.get("real_name", ""));
                                    discussionTo.setUserTo(userTo);
                                    ImageFilesTo filesTo=new ImageFilesTo();
                                    filesTo.setCase_id(caseId);
                                    filesTo.setPic_url("");
                                    filesTo.setTest_name("");
                                    List<ImageFilesTo> list=new ArrayList<ImageFilesTo>();
                                    list.add(filesTo);
                                    discussionTo.setImageFilesTos(list);
                                    discussionList.add(discussionTo);
                                    myAdapter.notifyDataSetChanged();
                                    discussionListView.setSelection(discussionList.size());
                                    discussion_edit.setText("");
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        discussion_more_btn=(Button)findViewById(R.id.case_info_new_show_discussion_more_btn);
        discussion_more_btn.setTextSize(17);
        discussion_more_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 初级医师更多，根据咨询类型 明确诊断的多一个转住院或手术的功能
                if(caseTo.getPatientCase().getConsult_tp().equals("明确诊断")) {
                    // 上传图片、转手术或住院、完成，点击完成时，弹出取消、确认框
                    Intent intent=new Intent();
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("btn1", "上传图片");
                    intent.putExtra("btn2", "转手术或住院");
                    intent.putExtra("btn3", "完成");
                    intent.putExtra("btnCount", 3);
                    startActivityForResult(intent, 1);
                } else if(caseTo.getPatientCase().getConsult_tp().equals("公开讨论")) {
                    // 上传图片、完成，点击完成时，弹出取消、确认框
                    Intent intent=new Intent();
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("btn1", "上传图片");
                    intent.putExtra("btn3", "完成");
                    intent.putExtra("btnCount", 2);
                    startActivityForResult(intent, 2);
                }
            }
        });

        acceptance_btn=(Button)findViewById(R.id.case_info_new_btn_examine_ok);
        acceptance_btn.setTextSize(18);
        acceptance_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 受理
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("case_id", caseId);
                parmas.put("accept", "1");
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                OpenApiService.getInstance(CaseInfoNewActivity.this).getExpertAccept(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    initData();
                                    initView();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        acceptance_btn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        not_accepted_btn=(Button)findViewById(R.id.case_info_new_btn_examine_no);
        not_accepted_btn.setTextSize(18);
        not_accepted_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 不受理
                Intent intent=new Intent();
                intent.putExtra("flag", 1);
                intent.putExtra("titleText", "请输入不受理原因");
                startActivityForResult(intent, 0);
            }
        });
        not_accepted_btn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

        expert_submit_btn=(Button)findViewById(R.id.case_info_new_expert_dis_btn);
        expert_submit_btn.setTextSize(18);
        expert_submit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 专家框提交
                if((null == bcbsEdit.getText().toString() || "".equals(bcbsEdit.getText().toString()))
                    && (null == wsjcEdit.getText().toString() || "".equals(wsjcEdit.getText().toString()))
                    && (null == zdyjEdit.getText().toString() || "".equals(zdyjEdit.getText().toString()))
                    && (null == jyssEdit.getText().toString() || "".equals(jyssEdit.getText().toString()))) {
                    Toast.makeText(CaseInfoNewActivity.this, "请输入讨论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer sb=new StringBuffer();
                if(!(null == bcbsEdit.getText().toString() || "".equals(bcbsEdit.getText().toString()))) {
                    sb.append("补充病史：").append(bcbsEdit.getText().toString()).append("\r\n");
                }
                if(!(null == wsjcEdit.getText().toString() || "".equals(wsjcEdit.getText().toString()))) {
                    sb.append("完善检查：").append(wsjcEdit.getText().toString()).append("\r\n");
                }
                if(!(null == zdyjEdit.getText().toString() || "".equals(zdyjEdit.getText().toString()))) {
                    sb.append("诊断建议：").append(zdyjEdit.getText().toString()).append("\r\n");
                }
                if(!(null == jyssEdit.getText().toString() || "".equals(jyssEdit.getText().toString()))) {
                    sb.append("建议术式：").append(jyssEdit.getText().toString()).append("\r\n");
                }
                Map<String, String> parmas=new HashMap<String, String>();
                parmas.put("case_id", caseId);
                parmas.put("discusser_userid", editor.get("uid", ""));
                parmas.put("discusser", editor.get("real_name", "专家"));
                parmas.put("content", sb.toString());
                parmas.put("accessToken", ClientUtil.getToken());
                parmas.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                OpenApiService.getInstance(CaseInfoNewActivity.this).getSendDiscussionCase(mQueue, parmas,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            CommonUtil.closeLodingDialog();
                            try {
                                JSONObject responses=new JSONObject(arg0);
                                if(responses.getInt("rtnCode") == 1) {
                                    Toast.makeText(CaseInfoNewActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if(responses.getInt("rtnCode") == 10004) {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                    LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                        @Override
                                        public void onSuccess(String rspContent, int statusCode) {
                                            initData();
                                        }

                                        @Override
                                        public void onFailure(ConsultationCallbackException exp) {
                                        }
                                    });
                                    startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                        .show();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            CommonUtil.closeLodingDialog();
                            Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        expert_submit_btn.setOnTouchListener(new ButtonListener().setImage(
            getResources().getDrawable(R.drawable.login_login_btn_shape),
            getResources().getDrawable(R.drawable.login_login_btn_press_shape)).getBtnTouchListener());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 0:
                    // 不受理
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("case_id", caseId);
                    parmas.put("accept", "0");
                    parmas.put("accept_desc", data.getStringExtra("contentString"));
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
                    OpenApiService.getInstance(CaseInfoNewActivity.this).getExpertAccept(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        finish();
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                                initData();
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseInfoNewActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseInfoNewActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError arg0) {
                                CommonUtil.closeLodingDialog();
                                Toast.makeText(CaseInfoNewActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    break;
                case 1:
                    if(data.getStringExtra("status").equals("3")) {
                        finish();
                    } else if(data.getStringExtra("status").equals("2")) {
                        finish();
                    } else if(data.getStringExtra("status").equals("1")) {
                        uploadImage(data.getStringExtra("path"));
                    }
                    break;
                case 2:
                    if(data.getStringExtra("status").equals("3")) {
                        finish();
                    } else if(data.getStringExtra("status").equals("1")) {
                        uploadImage(data.getStringExtra("path"));
                    }
                    break;

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(final String photoPath) {
        File[] files=new File[1];
        File file=new File(photoPath);
        files[0]=file;
        Map<String, String> params=new HashMap<String, String>();
        params.put("case_id", caseId);
        params.put("discusser_userid", editor.get("uid", ""));
        params.put("discusser", editor.get("real_name", "医生"));
        params.put("accessToken", ClientUtil.getToken());
        params.put("uid", editor.get("uid", ""));
        CommonUtil.showLoadingDialog(CaseInfoNewActivity.this);
        OpenApiService.getInstance(CaseInfoNewActivity.this).getUploadFiles(ClientUtil.GET_DISCUSSION_CASE_IMAGE_URL,
            CaseInfoNewActivity.this, new ConsultationCallbackHandler() {

                @Override
                public void onSuccess(String rspContent, int statusCode) {
                    CommonUtil.closeLodingDialog();
                    DiscussionTo discussionTo=new DiscussionTo();
                    discussionTo.setCreate_time(System.currentTimeMillis());
                    discussionTo.setCase_id(caseId);
                    discussionTo.setDiscusser(editor.get("real_name", "医生"));
                    discussionTo.setDiscusser_userid(editor.get("uid", ""));
                    discussionTo.setIs_view("1");
                    discussionTo.setHave_photos("1");
                    UserTo userTo=new UserTo();
                    userTo.setTp(editor.get("userType", ""));
                    userTo.setIcon_url(editor.get("icon_url", ""));
                    userTo.setUser_name(editor.get("real_name", ""));
                    discussionTo.setUserTo(userTo);
                    ImageFilesTo filesTo=new ImageFilesTo();
                    filesTo.setCase_id(caseId);
                    filesTo.setPic_url(photoPath);
                    filesTo.setTest_name("");
                    List<ImageFilesTo> list=new ArrayList<ImageFilesTo>();
                    list.add(filesTo);
                    discussionTo.setImageFilesTos(list);
                    discussionList.add(discussionTo);
                    myAdapter.notifyDataSetChanged();
                    discussionListView.setSelection(discussionList.size());
                }

                @Override
                public void onFailure(ConsultationCallbackException exp) {
                    CommonUtil.closeLodingDialog();
                    Toast.makeText(CaseInfoNewActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                }
            }, files, params);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight=0;
        for(int i=0; i < listAdapter.getCount(); i++) {
            View listItem=listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height=totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private class ViewHolder {

        TextView contents;

        TextView name;

        ImageView photo;

        TextView title;

        TextView create_time;

        ImageView imageView;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return discussionList.size();
        }

        @Override
        public Object getItem(int location) {
            return discussionList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                if(discussionList.get(position).getDiscusser_userid().equals(editor.get("uid", ""))) {
                    convertView=
                        LayoutInflater.from(CaseInfoNewActivity.this).inflate(R.layout.discussion_case_mine_list_item, null);
                } else {
                    convertView=LayoutInflater.from(CaseInfoNewActivity.this).inflate(R.layout.discussion_case_list_item, null);
                }
                holder.contents=(TextView)convertView.findViewById(R.id.discussion_case_item_content);
                holder.create_time=(TextView)convertView.findViewById(R.id.discussion_case_item_createTime);
                holder.name=(TextView)convertView.findViewById(R.id.discussion_case_item_name);
                holder.imageView=(ImageView)convertView.findViewById(R.id.discussion_case_item_content_imageView);
                holder.name=(TextView)convertView.findViewById(R.id.discussion_case_item_name);
                holder.title=(TextView)convertView.findViewById(R.id.discussion_case_item_title);
                holder.photo=(ImageView)convertView.findViewById(R.id.discussion_case_item_photo);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            if(discussionList.get(position).getHave_photos().equals("1")) {
                holder.contents.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
                final String imgUrl=discussionList.get(position).getImageFilesTos().get(0).getLittle_pic_url();
                final String bigImgUrl=discussionList.get(position).getImageFilesTos().get(0).getPic_url();
                if(!"null".equals(imgUrl) && !"".equals(imgUrl)) {
                    if(imgUrl.startsWith("http://")) {
                        holder.imageView.setTag(imgUrl);
                            ImageListener listener=
                                ImageLoader.getImageListener(holder.imageView, android.R.drawable.ic_menu_rotate,
                                    android.R.drawable.ic_menu_delete);
                            mImageLoader.get(imgUrl, listener, 200, 200);
                    } else {
                        Bitmap bitmap=CommonUtil.readBitMap(200, imgUrl);
                        holder.imageView.setImageBitmap(bitmap);
                    }
                    holder.imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            BigImageActivity.setViewData(mImageLoader, bigImgUrl);
                            startActivity(new Intent(CaseInfoNewActivity.this, BigImageActivity.class));
                        }
                    });
                }
            } else {
                holder.contents.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.GONE);
                holder.contents.setText(discussionList.get(position).getContent());
                holder.contents.setTextSize(18);
            }
            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd  HH:mm");
            String sd=sdf.format(new Date(discussionList.get(position).getCreate_time()));
            holder.create_time.setText(sd);
            holder.create_time.setTextSize(15);
            holder.title.setText("初级医师");
            holder.title.setBackgroundResource(R.drawable.discussion_title_shape);
            if(discussionList.get(position).getUserTo().getTp().equals("2")) {
                holder.title.setText("专家");
                holder.title.setBackgroundResource(R.drawable.discussion_mine_title_shape);
            }
            holder.title.setTextSize(15);
            holder.name.setText(discussionList.get(position).getDiscusser());
            holder.name.setTextSize(15);
            final String imgUrl=discussionList.get(position).getUserTo().getIcon_url();
            holder.photo.setTag(imgUrl);
            int photoId=0;
            if(discussionList.get(position).getUserTo().getTp().equals("1")) {
                photoId=R.drawable.photo_primary;
            } else if(discussionList.get(position).getUserTo().getTp().equals("2")) {
                photoId=R.drawable.photo_expert;
            }
            holder.photo.setImageResource(photoId);
            if(!"null".equals(imgUrl) && !"".equals(imgUrl)) {
                ImageListener listener=ImageLoader.getImageListener(holder.photo, photoId, photoId);
                mImageLoader.get(imgUrl, listener);
            }
            return convertView;
        }
    }
}
