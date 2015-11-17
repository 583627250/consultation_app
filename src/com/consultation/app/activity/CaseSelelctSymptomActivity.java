package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.model.SymptomTo;
import com.consultation.app.model.TitleModel;
import com.consultation.app.util.ClientUtil;

public class CaseSelelctSymptomActivity extends CaseBaseActivity {

    private ListView mainListView, accompanyListView;

    private List<SymptomTo> mainList=new ArrayList<SymptomTo>();

    private List<SymptomTo> accompanyList=new ArrayList<SymptomTo>();
    
    private List<SymptomTo> accompanyTempList=new ArrayList<SymptomTo>();

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ViewHolder mainHolder;

    private MyAdapter myMainAdapter, myAccompanyAdapter;

    private TextView mainText, accompanyText;

    private Button nextBtn;

    private int firstCheck=-1;

    private List<Integer> secondCheck=new ArrayList<Integer>();

    private List<TitleModel> titleModels;

    private String content="";

    private String departmentId="";

    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_select_symptom_layout);
        page=getIntent().getIntExtra("page", -1);
        content=getIntent().getStringExtra("content");
        departmentId=getIntent().getStringExtra("departmentId");
        initData();
        initView();
    }

    private void initData() {
        if(null != content && !"".equals(content) && !"null".equals(content)) {
            XMLCaseDatas(content);
            titleModels=caseList.get(0).getTitleModels();
            if(ClientUtil.getCaseParams().size() != 0 && ClientUtil.getCaseParams().getValue(page + "") != null
                && !"".equals(ClientUtil.getCaseParams().getValue(page + ""))) {
                XMLCaseDatas(ClientUtil.getCaseParams().getValue(page + ""));
                titleModels=caseList.get(0).getTitleModels();
            }
        } else if(ClientUtil.getCaseParams().size() != 0 && ClientUtil.getCaseParams().getValue(page + "") != null
            && !"".equals(ClientUtil.getCaseParams().getValue(page + ""))) {
            XMLCaseDatas(ClientUtil.getCaseParams().getValue(page + ""));
            titleModels=caseList.get(0).getTitleModels();
        } else {
            initCaseDatas(departmentId + "case.xml");
            titleModels=caseList.get(page).getTitleModels();
        }
        for(int i=0; i < titleModels.size(); i++) {
            SymptomTo symptomTo=new SymptomTo();
            if(null != titleModels.get(i).getType() && titleModels.get(i).getType().equals("Main")) {
                symptomTo.setCheckMain(true);
                firstCheck=i;
            } else {
                symptomTo.setCheckMain(false);
            }
            if(null != titleModels.get(i).getType() && titleModels.get(i).getType().equals("Accompany")) {
                symptomTo.setCheckAccompany(true);
                secondCheck.add(i);
            } else {
                symptomTo.setCheckAccompany(false);
            }
            symptomTo.setName(titleModels.get(i).getTitle());
            symptomTo.setId(i);
            mainList.add(symptomTo);
            accompanyList.add(symptomTo);
            accompanyTempList.add(symptomTo);
        }
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("选择症状");
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

        mainText=(TextView)findViewById(R.id.case_select_symptom_main_text);
        mainText.setTextSize(18);
        accompanyText=(TextView)findViewById(R.id.case_select_symptom_accompany_text);
        accompanyText.setTextSize(18);

        myMainAdapter=new MyAdapter(1);
        mainListView=(ListView)findViewById(R.id.case_select_symptom_main_listView);
        mainListView.setAdapter(myMainAdapter);
        setListViewHeightBasedOnChildren(mainListView);

        myAccompanyAdapter=new MyAdapter(2);
        accompanyListView=(ListView)findViewById(R.id.case_select_symptom_accompany_listView);
        accompanyListView.setAdapter(myAccompanyAdapter);
        setListViewHeightBasedOnChildren(accompanyListView);

        nextBtn=(Button)findViewById(R.id.case_select_symptom_btn_submit);
        nextBtn.setTextSize(20);
        nextBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(firstCheck == -1) {
                    Toast.makeText(CaseSelelctSymptomActivity.this, "请选择主要症状", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent=new Intent(CaseSelelctSymptomActivity.this, SymptomActivity.class);
                intent.putExtra("page", 0);
                intent.putExtra("titleText", "现病史");
                if(!"null".equals(content) && !"".equals(content) && null != content) {
                    intent.putExtra("content", content.split("&")[0]);
                } else {
                    intent.putExtra("content", "");
                }
                intent.putExtra("departmentId", departmentId);
                intent.putExtra("firstCheck", firstCheck);
                StringBuffer buffer=new StringBuffer();
                for(int i=0; i < secondCheck.size(); i++) {
                    if(i == 0) {
                        buffer.append(secondCheck.get(i));
                    } else {
                        buffer.append(",").append(secondCheck.get(i));
                    }
                }
                if(secondCheck.size() == 0){
                    intent.putExtra("secondCheck", "");
                }else{
                    intent.putExtra("secondCheck", buffer.toString());
                }
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            if(resultCode == Activity.RESULT_OK) {
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isAdd", data.getExtras().getBoolean("isAdd"));
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private static class ViewHolder {

        TextView title;

        CheckBox isCheck;
    }

    private class MyAdapter extends BaseAdapter {

        private int model;

        public MyAdapter(int tempModel) {
            model=tempModel;
        }

        @Override
        public int getCount() {
            if(model == 1) {
                return mainList.size();
            }
            return accompanyTempList.size();
        }

        @Override
        public Object getItem(int location) {
            if(model == 1) {
                return mainList.get(location);
            }
            return accompanyTempList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=
                    LayoutInflater.from(CaseSelelctSymptomActivity.this).inflate(R.layout.case_select_symptom_list_item, null);
                mainHolder=new ViewHolder();
                mainHolder.title=(TextView)convertView.findViewById(R.id.case_select_name);
                mainHolder.isCheck=(CheckBox)convertView.findViewById(R.id.case_select_isCheck);
                convertView.setTag(mainHolder);
            } else {
                mainHolder=(ViewHolder)convertView.getTag();
            }
            if(model == 1) {
                mainHolder.title.setTextSize(18);
                mainHolder.title.setText(mainList.get(position).getName());
                mainHolder.isCheck.setTextSize(18);
                mainHolder.isCheck.setChecked(mainList.get(position).isCheckMain());
                mainHolder.isCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            mainIsCheck(position);
                        }
                    }
                });
            } else {
                mainHolder.title.setTextSize(18);
                mainHolder.title.setText(accompanyTempList.get(position).getName());
                mainHolder.isCheck.setTextSize(18);
                mainHolder.isCheck.setChecked(accompanyTempList.get(position).isCheckAccompany());
                mainHolder.isCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            secondCheck.add(accompanyTempList.get(position).getId());
                        } else {
                            if(secondCheck.contains(accompanyTempList.get(position).getId())) {
                                secondCheck.remove(Integer.valueOf(accompanyTempList.get(position).getId()));
                            }
                        }
                    }
                });
            }
            return convertView;
        }
    }

    private void mainIsCheck(int index) {
        accompanyTempList.clear();
        for(int i=0; i < accompanyList.size(); i++) {
            accompanyTempList.add(accompanyList.get(i));
        }
        for(int i=0; i < mainList.size(); i++) {
            if(i != index) {
                mainList.get(i).setCheckMain(false);
            } else {
                mainList.get(i).setCheckMain(true);
                accompanyTempList.remove(i);
                firstCheck=index;
            }
        }
        myMainAdapter.notifyDataSetChanged();
        myAccompanyAdapter.notifyDataSetChanged();
    }
}
