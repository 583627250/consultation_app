package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.model.ItemModel;
import com.consultation.app.model.TitleModel;
import com.consultation.app.util.ClientUtil;

@SuppressLint("UseSparseArrays")
public class SymptomActivity extends CaseBaseActivity {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ListView listView;

    private LinearLayout rightLayout;

    private MyAdapter myAdapter;

    private List<String> leftList=new ArrayList<String>();

    private ViewHolder holder;

    private int currentPosition=-1;

    private Context context;

    private int width;

    private int height;

    private Map<String, Object> views=new HashMap<String, Object>();

    private List<Integer> ids=new ArrayList<Integer>();

    private Map<Integer, List<LinearLayout>> maps=new HashMap<Integer, List<LinearLayout>>();

    private boolean isSave=false;

    private List<TitleModel> titleModels;
    
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_symptom_layout);
        context=this;
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("症状");
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
//        save_text=(TextView)findViewById(R.id.header_right);
//        save_text.setVisibility(View.VISIBLE);
//        save_text.setTextSize(18);
//        save_text.setBackgroundResource(R.drawable.symptom_save_text_selector);
//        save_text.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 保存
//                saveData();
//            }
//        });

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                if(!isSave) {
                    if(saveData()) {
                        bundle.putBoolean("isAdd", true);
                    } else {
                        bundle.putBoolean("isAdd", false);
                    }
                }
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        rightLayout=(LinearLayout)findViewById(R.id.syamptom_right_layout);

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.syamptom_left_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                if(ids.contains(position)) {
                    // 显示界面，并影藏其他界面
                    showRightLayout(position, 1);
                } else {
                    // 创建界面
                    showRightLayout(position, 0);
                    ids.add(position);
                }
                for(int i=0; i < leftList.size(); i++) {
                    if(i != position) {
                        showRightLayout(i, 2);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
        });
        
        saveBtn = (Button)findViewById(R.id.syamptom_btn_save);
        saveBtn.setTextSize(20);
        saveBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // 保存数据
            }
        });
    }

    private void showRightLayout(int position, int status) {
        List<ItemModel> itemModels=titleModels.get(position).getItemModels();
        for(int i=0; i < itemModels.size(); i++) {
            ItemModel itemModel=itemModels.get(i);
            List<LinearLayout> layouts;
            if(maps.containsKey(position)) {
                layouts=maps.get(position);
            } else {
                layouts=new ArrayList<LinearLayout>();
            }
            switch(status) {
                case 0:
                    if(null == itemModel.getType() || "".equals(itemModel.getType())) {
                        List<ItemModel> subItemModels=itemModel.getItemModels();
                        LinearLayout TextViewLayout=createTextView(itemModel.getFirstStr());
                        layouts.add(TextViewLayout);
                        maps.put(position, layouts);
                        rightLayout.addView(TextViewLayout);
                        for(int k=0; k < subItemModels.size(); k++) {
                            ItemModel subItemModel=subItemModels.get(k);
                            if(subItemModel.getType().equals("Edit")) {
                                EditText input=new EditText(context);
                                views.put(subItemModel.getId(), input);
                                LinearLayout editTextLayout=
                                    createEditText(subItemModel.getFirstStr(), subItemModel.getLastStr(), input);
                                layouts.add(editTextLayout);
                                maps.put(position, layouts);
                                rightLayout.addView(editTextLayout);
                            } else if(subItemModel.getType().equals("CheckBoxList")) {
                                List<CheckBox> checkBoxs=new ArrayList<CheckBox>();
                                String[] data_list=new String[subItemModel.getOptionsModels().size()];
                                for(int j=0; j < subItemModel.getOptionsModels().size(); j++) {
                                    data_list[j]=subItemModel.getOptionsModels().get(j).getName();
                                    CheckBox box = new CheckBox(context);
                                    checkBoxs.add(box);
                                    views.put(subItemModel.getOptionsModels().get(j).getId(), box);
                                }
                                LinearLayout spinnerLayout=createCheckBox(subItemModel.getFirstStr(), data_list, checkBoxs);
                                layouts.add(spinnerLayout);
                                maps.put(position, layouts);
                                rightLayout.addView(spinnerLayout);
                            } else if(subItemModel.getType().equals("RadioButtonGroup")) {
                                List<RadioButton> rBtns=new ArrayList<RadioButton>();
                                String[] date=new String[subItemModel.getOptionsModels().size()];
                                for(int j=0; j < subItemModel.getOptionsModels().size(); j++) {
                                    RadioButton rBtn=new RadioButton(context);
                                    views.put(subItemModel.getOptionsModels().get(j).getId(), rBtn);
                                    rBtns.add(rBtn);
                                    date[j]=subItemModel.getOptionsModels().get(j).getName();
                                }
                                LinearLayout radioButtonLayout=createRadioButton(subItemModel.getFirstStr(), date, rBtns);
                                layouts.add(radioButtonLayout);
                                maps.put(position, layouts);
                                rightLayout.addView(radioButtonLayout);
                            }
                        }
                    } else {
                        if(itemModel.getType().equals("Edit")) {
                            EditText input=new EditText(context);
                            views.put(itemModel.getId(), input);
                            LinearLayout editTextLayout=createEditText(itemModel.getFirstStr(), itemModel.getLastStr(), input);
                            layouts.add(editTextLayout);
                            maps.put(position, layouts);
                            rightLayout.addView(editTextLayout);
                        } else if(itemModel.getType().equals("CheckBoxList")) {
                            List<CheckBox> checkBoxs=new ArrayList<CheckBox>();
                            String[] data_list=new String[itemModel.getOptionsModels().size()];
                            for(int j=0; j < itemModel.getOptionsModels().size(); j++) {
                                data_list[j]=itemModel.getOptionsModels().get(j).getName();
                                CheckBox box = new CheckBox(context);
                                checkBoxs.add(box);
                                views.put(itemModel.getOptionsModels().get(j).getId(), box);
                            }
                            LinearLayout spinnerLayout=createCheckBox(itemModel.getFirstStr(), data_list, checkBoxs);
                            layouts.add(spinnerLayout);
                            maps.put(position, layouts);
                            rightLayout.addView(spinnerLayout);
                        } else if(itemModel.getType().equals("RadioButtonGroup")) {
                            List<RadioButton> rBtns=new ArrayList<RadioButton>();
                            String[] date=new String[itemModel.getOptionsModels().size()];
                            for(int j=0; j < itemModel.getOptionsModels().size(); j++) {
                                RadioButton rBtn=new RadioButton(context);
                                views.put(itemModel.getOptionsModels().get(j).getId(), rBtn);
                                rBtns.add(rBtn);
                                date[j]=itemModel.getOptionsModels().get(j).getName();
                            }
                            LinearLayout radioButtonLayout=createRadioButton(itemModel.getFirstStr(), date, rBtns);
                            layouts.add(radioButtonLayout);
                            maps.put(position, layouts);
                            rightLayout.addView(radioButtonLayout);
                        }
                    }
                    break;
                case 1:
                    for(LinearLayout layout: layouts) {
                        layout.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    for(LinearLayout layout: layouts) {
                        layout.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private boolean saveData() {
        boolean isAdd=false;
        for(String key: views.keySet()) {
            Object view=views.get(key);
            if(EditText.class.isInstance(view)) {
                if(null != ((EditText)view).getText().toString() && !"".equals(((EditText)view).getText().toString())) {
                    ClientUtil.getCaseParams().add(key, ((EditText)view).getText().toString());
                    isAdd=true;
                }
            } else if(RadioButton.class.isInstance(view)) {
                RadioButton radioButton=(RadioButton)view;
                if(radioButton.isChecked()) {
                    if(null != radioButton.getText().toString() && !"".equals(radioButton.getText().toString())) {
                        ClientUtil.getCaseParams().add(key, radioButton.getText().toString());
                    }
                }
            } else if(TextView.class.isInstance(view)) {
                if(null != ((TextView)view).getText().toString() && !"".equals(((TextView)view).getText().toString())) {
                    ClientUtil.getCaseParams().add(key, ((TextView)view).getText().toString());
                }
            }
        }
        isSave=true;
        return isAdd;
    }

    private void initData() {
        initCaseDatas();
        titleModels=caseList.get(0).getTitleModels();
        for(int i=0; i < titleModels.size(); i++) {
            leftList.add(titleModels.get(i).getTitle());
        }
    }

    private static class ViewHolder {

        TextView textName;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return leftList.size();
        }

        @Override
        public Object getItem(int location) {
            return leftList.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView=LayoutInflater.from(SymptomActivity.this).inflate(R.layout.search_recommend_list_item, null);
                holder=new ViewHolder();
                holder.textName=(TextView)convertView.findViewById(R.id.search_recommend_list_item_text);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.textName.setTextSize(16);
            holder.textName.setText(leftList.get(position));
            convertView.setBackgroundColor(Color.WHITE);
            if(position == currentPosition) {
                convertView.setBackgroundColor(Color.parseColor("#E4E4E4"));
            }
            return convertView;
        }
    }

    private LinearLayout createEditText(String name, String company, EditText input) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);

        if(name != null && !"".equals(name)) {
            TextView textName=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textNameParams.leftMargin=width / 40;
            textName.setLayoutParams(textNameParams);
            textName.setText(name);
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextColor(Color.parseColor("#414141"));
            textName.setTextSize(16);
            layout.addView(textName);
        }

        LayoutParams inputParams=new LayoutParams(width / 4, height / 20);
        inputParams.gravity=Gravity.CENTER;
        inputParams.leftMargin=width / 40;
        input.setLayoutParams(inputParams);
        input.setGravity(Gravity.CENTER);
        input.setTextColor(Color.parseColor("#414141"));
        input.setPadding(0, height / 300, 0, 0);
        input.setBackgroundResource(R.drawable.symptom_edit_shape);
        input.setTextSize(16);
        layout.addView(input);

        TextView textCompany=new TextView(context);
        LayoutParams textCompanyParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textCompanyParams.leftMargin=width / 40;
        textCompanyParams.rightMargin=width / 40;
        textCompany.setLayoutParams(textCompanyParams);
        textCompany.setText(company);
        textCompany.setGravity(Gravity.CENTER_VERTICAL);
        textCompany.setTextColor(Color.parseColor("#414141"));
        textCompany.setTextSize(16);
        layout.addView(textCompany);
        return layout;
    }

    private LinearLayout createTextView(String name) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);

        TextView textName=new TextView(context);
        LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textNameParams.leftMargin=width / 40;
        textName.setLayoutParams(textNameParams);
        textName.setText(name);
        textName.setGravity(Gravity.CENTER_VERTICAL);
        textName.setTextColor(Color.parseColor("#414141"));
        textName.setTextSize(16);
        layout.addView(textName);
        return layout;
    }

    private LinearLayout createRadioButton(String name, String[] selectTexts, final List<RadioButton> radioButtons) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);

        if(name != null && !"".equals(name)) {
            TextView textName=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            textNameParams.leftMargin=width / 40;
            textName.setLayoutParams(textNameParams);
            textName.setText(name);
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextColor(Color.parseColor("#414141"));
            textName.setTextSize(16);
            layout.addView(textName);
        }

        RadioGroup group=new RadioGroup(context);
        LayoutParams viewParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        viewParams.leftMargin=width / 40;
        group.setLayoutParams(viewParams);
        group.setOrientation(LinearLayout.VERTICAL);
        group.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams radioButtonParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        for(int i=0; i < radioButtons.size(); i++) {
            RadioButton button=radioButtons.get(i);
            button.setTextColor(Color.parseColor("#414141"));
            button.setTextSize(16);
            button.setText(selectTexts[i]);
            button.setPadding(0, 0, 10, 0);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setLayoutParams(radioButtonParams);
            group.addView(button);
        }
        layout.addView(group);

        return layout;
    }
    
    private LinearLayout createCheckBox(String name, String[] data_list, List<CheckBox> checkBoxs) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);
        
        if(null != name && !"".equals(name)){
            TextView textName=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            textNameParams.leftMargin=width / 40;
            textName.setLayoutParams(textNameParams);
            textName.setText(name);
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextColor(Color.parseColor("#414141"));
            textName.setTextSize(16);
            layout.addView(textName);
        }

        LayoutParams viewParams=new LayoutParams(width / 3, height / 18);
        viewParams.leftMargin=width / 40;
        viewParams.gravity=Gravity.LEFT;
        
        for(int i=0; i < checkBoxs.size(); i++) {
            CheckBox box = checkBoxs.get(i);
            box.setLayoutParams(viewParams);
            box.setTextSize(16);
            box.setText(data_list[i]);
            box.setTextColor(Color.parseColor("#414141"));
            layout.addView(box);
        }
        return layout;
    }
}
