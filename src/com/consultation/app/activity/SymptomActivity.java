package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.model.CaseModel;
import com.consultation.app.model.ItemModel;
import com.consultation.app.model.OptionsModel;
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

    private Map<Integer, Boolean> isNomalMap=new HashMap<Integer, Boolean>();

    private List<Integer> ids=new ArrayList<Integer>();

    private List<Integer> xbsIds=new ArrayList<Integer>();

    private Map<Integer, List<LinearLayout>> maps=new HashMap<Integer, List<LinearLayout>>();

    private List<TitleModel> titleModels;

    private int page;

    private String titleText;

    private boolean isAdd=false;

    private String content="";

    private String departmentId="";

    private RadioButton noamal, abnomal;

    private int firstCheck;

    private String secondCheck;

    private RadioGroup radioGroup;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_symptom_layout);
        context=this;
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        page=getIntent().getIntExtra("page", -1);
        titleText=getIntent().getStringExtra("titleText");
        content=getIntent().getStringExtra("content");
        departmentId=getIntent().getStringExtra("departmentId");
        firstCheck=getIntent().getIntExtra("firstCheck", -1);
        secondCheck=getIntent().getStringExtra("secondCheck");
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText(titleText);
        title_text.setTextSize(20);

        TextView header_right=(TextView)findViewById(R.id.header_right);
        header_right.setText("保存");
        header_right.setVisibility(View.VISIBLE);
        header_right.setTextSize(18);
        header_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存数据
                saveData(false);
                // InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                // if(imm.isActive()) {
                // imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                // }
            }
        });

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        radioGroup=(RadioGroup)findViewById(R.id.syamptom_select_isNormal_radioGroup);

        noamal=(RadioButton)findViewById(R.id.syamptom_select_normal);
        noamal.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // 关闭
                    if(page == 0) {
                        showRightLayout(xbsIds.get(currentPosition), 2);
                        isNomalMap.put(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId().split("\\.")[1]) - 1,
                            true);
                    } else {
                        showRightLayout(currentPosition, 2);
                        isNomalMap.put(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1, true);
                    }
                }
            }
        });
        abnomal=(RadioButton)findViewById(R.id.syamptom_select_abnormal);
        abnomal.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // 展开
                    if(page == 0) {
                        if(ids.contains(xbsIds.get(currentPosition))) {
                            // 显示界面，并影藏其他界面
                            showRightLayout(xbsIds.get(currentPosition), 1);
                        } else {
                            // 创建界面
                            showRightLayout(xbsIds.get(currentPosition), 0);
                            ids.add(xbsIds.get(currentPosition));
                        }
                        isNomalMap.put(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId().split("\\.")[1]) - 1,
                            false);
                    } else {
                        if(ids.contains(currentPosition)) {
                            // 显示界面，并影藏其他界面
                            showRightLayout(currentPosition, 1);
                        } else {
                            // 创建界面
                            showRightLayout(currentPosition, 0);
                            ids.add(currentPosition);
                        }
                        isNomalMap.put(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1, false);
                    }
                }
            }
        });

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                saveData(true);
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isAdd", isAdd);
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
                if(page == 0) {
                    for(int i=0; i < leftList.size(); i++) {
                        if(i != position) {
                            showRightLayout(xbsIds.get(i), 2);
                        }
                    }
                } else {
                    for(int i=0; i < leftList.size(); i++) {
                        if(i != position) {
                            showRightLayout(i, 2);
                        }
                    }
                }
                if(page == 2) {
                    radioGroup.setVisibility(View.GONE);
                    if(ids.contains(currentPosition)) {
                        // 显示界面，并影藏其他界面
                        showRightLayout(currentPosition, 1);
                    } else {
                        // 创建界面
                        showRightLayout(currentPosition, 0);
                        ids.add(currentPosition);
                    }
                    isNomalMap.put(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1, false);
                } else if(page == 0) {
                    radioGroup.setVisibility(View.GONE);
                    // noamal.setChecked(isNomalMap.get(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId()
                    // .split("\\.")[1]) - 1));
                    // abnomal.setChecked(!isNomalMap.get(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId()
                    // .split("\\.")[1]) - 1));
                    // if(!isNomalMap.get(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId().split("\\.")[1]) -
                    // 1)) {
                    if(ids.contains(xbsIds.get(currentPosition))) {
                        // 显示界面，并影藏其他界面
                        showRightLayout(xbsIds.get(currentPosition), 1);
                    } else {
                        // 创建界面
                        showRightLayout(xbsIds.get(currentPosition), 0);
                        ids.add(xbsIds.get(currentPosition));
                    }
                    // }
                } else {
                    noamal.setChecked(isNomalMap.get(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1));
                    abnomal.setChecked(!isNomalMap.get(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1));
                    if(!isNomalMap.get(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1)) {
                        if(ids.contains(currentPosition)) {
                            // 显示界面，并影藏其他界面
                            showRightLayout(currentPosition, 1);
                        } else {
                            // 创建界面
                            showRightLayout(currentPosition, 0);
                            ids.add(currentPosition);
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
        });
        currentPosition=0;
        if(page == 2) {
            radioGroup.setVisibility(View.GONE);
            if(ids.contains(currentPosition)) {
                // 显示界面，并影藏其他界面
                showRightLayout(currentPosition, 1);
            } else {
                // 创建界面
                showRightLayout(currentPosition, 0);
                ids.add(currentPosition);
            }
            isNomalMap.put(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1, false);
        } else if(page == 0) {
            radioGroup.setVisibility(View.GONE);
            // noamal
            // .setChecked(isNomalMap.get(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId().split("\\.")[1]) -
            // 1));
            // abnomal
            // .setChecked(!isNomalMap.get(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId().split("\\.")[1]) -
            // 1));
            // if(!isNomalMap.get(Integer.parseInt(titleModels.get(xbsIds.get(currentPosition)).getId().split("\\.")[1]) - 1)) {
            if(ids.contains(xbsIds.get(currentPosition))) {
                // 显示界面，并影藏其他界面
                showRightLayout(xbsIds.get(currentPosition), 1);
            } else {
                // 创建界面
                showRightLayout(xbsIds.get(currentPosition), 0);
                ids.add(xbsIds.get(currentPosition));
            }
            // }
        } else {
            noamal.setChecked(isNomalMap.get(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1));
            abnomal.setChecked(!isNomalMap.get(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1));
            if(!isNomalMap.get(Integer.parseInt(titleModels.get(currentPosition).getId().split("\\.")[1]) - 1)) {
                if(ids.contains(currentPosition)) {
                    // 显示界面，并影藏其他界面
                    showRightLayout(currentPosition, 1);
                } else {
                    // 创建界面
                    showRightLayout(currentPosition, 0);
                    ids.add(currentPosition);
                }
            }
        }
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
                        if(null != itemModel.getFirstStr() && !"".equals(itemModel.getFirstStr())) {
                            LinearLayout TextViewLayout=createTextView(itemModel.getFirstStr());
                            layouts.add(TextViewLayout);
                            maps.put(position, layouts);
                            rightLayout.addView(TextViewLayout);
                        }
                        for(int k=0; k < subItemModels.size(); k++) {
                            ItemModel subItemModel=subItemModels.get(k);
                            if(null != subItemModel.getType() && subItemModel.getType().equals("Edit")) {
                                EditText input=new EditText(context);
                                views.put(subItemModel.getId(), input);
                                LinearLayout editTextLayout=
                                    createEditText(subItemModel.getFirstStr(), subItemModel.getLastStr(), input,
                                        subItemModel.getValue(), subItemModel.getDataType(), 0);
                                layouts.add(editTextLayout);
                                maps.put(position, layouts);
                                rightLayout.addView(editTextLayout);
                            } else if(null != subItemModel.getType() && subItemModel.getType().equals("CheckBoxList")) {
                                List<CheckBox> checkBoxs=new ArrayList<CheckBox>();
                                String[] data_list=new String[subItemModel.getOptionsModels().size()];
                                String[] values=new String[subItemModel.getOptionsModels().size()];
                                for(int j=0; j < subItemModel.getOptionsModels().size(); j++) {
                                    data_list[j]=subItemModel.getOptionsModels().get(j).getName();
                                    values[j]=subItemModel.getOptionsModels().get(j).getChecked();
                                    CheckBox box=(CheckBox)LayoutInflater.from(context).inflate(R.layout.my_check_box, null);
                                    checkBoxs.add(box);
                                    views.put(subItemModel.getOptionsModels().get(j).getId(), box);
                                }
                                LinearLayout spinnerLayout=
                                    createCheckBox(subItemModel.getFirstStr(), data_list, checkBoxs, values,
                                        subItemModel.getLastStr());
                                layouts.add(spinnerLayout);
                                maps.put(position, layouts);
                                rightLayout.addView(spinnerLayout);
                            } else if(null != subItemModel.getType() && subItemModel.getType().equals("RadioButtonGroup")) {
                                List<RadioButton> rBtns=new ArrayList<RadioButton>();
                                String[] date=new String[subItemModel.getOptionsModels().size()];
                                String[] values=new String[subItemModel.getOptionsModels().size()];
                                for(int j=0; j < subItemModel.getOptionsModels().size(); j++) {
                                    RadioButton rBtn=
                                        (RadioButton)LayoutInflater.from(context).inflate(R.layout.my_radiobutton, null);
                                    views.put(subItemModel.getOptionsModels().get(j).getId(), rBtn);
                                    rBtns.add(rBtn);
                                    date[j]=subItemModel.getOptionsModels().get(j).getName();
                                    values[j]=subItemModel.getOptionsModels().get(j).getChecked();
                                }
                                LinearLayout radioButtonLayout=
                                    createRadioButton(subItemModel.getFirstStr(), date, rBtns, values, subItemModel.getLastStr());
                                layouts.add(radioButtonLayout);
                                maps.put(position, layouts);
                                rightLayout.addView(radioButtonLayout);
                            }
                        }
                    } else {
                        if(null != itemModel.getType() && itemModel.getType().equals("Edit")) {
                            EditText input=new EditText(context);
                            views.put(itemModel.getId(), input);
                            LinearLayout editTextLayout=
                                createEditText(itemModel.getFirstStr(), itemModel.getLastStr(), input, itemModel.getValue(),
                                    itemModel.getDataType(), 0);
                            layouts.add(editTextLayout);
                            maps.put(position, layouts);
                            rightLayout.addView(editTextLayout);
                        } else if(null != itemModel.getType() && itemModel.getType().equals("CheckBoxList")) {
                            List<CheckBox> checkBoxs=new ArrayList<CheckBox>();
                            String[] data_list=new String[itemModel.getOptionsModels().size()];
                            String[] values=new String[itemModel.getOptionsModels().size()];
                            for(int j=0; j < itemModel.getOptionsModels().size(); j++) {
                                data_list[j]=itemModel.getOptionsModels().get(j).getName();
                                values[j]=itemModel.getOptionsModels().get(j).getChecked();
                                CheckBox box=(CheckBox)LayoutInflater.from(context).inflate(R.layout.my_check_box, null);
                                checkBoxs.add(box);
                                views.put(itemModel.getOptionsModels().get(j).getId(), box);
                            }
                            LinearLayout spinnerLayout=
                                createCheckBox(itemModel.getFirstStr(), data_list, checkBoxs, values, itemModel.getLastStr());
                            layouts.add(spinnerLayout);
                            rightLayout.addView(spinnerLayout);
                            if(null != itemModel.getItemModels() && itemModel.getItemModels().size() != 0) {
                                for(int j=0; j < itemModel.getItemModels().size(); j++) {
                                    ItemModel suItemModel3=itemModel.getItemModels().get(j);
                                    EditText input=new EditText(context);
                                    views.put(suItemModel3.getId(), input);
                                    LinearLayout editTextLayout=
                                        createEditText(suItemModel3.getFirstStr(), suItemModel3.getLastStr(), input,
                                            suItemModel3.getValue(), suItemModel3.getDataType(), 1);
                                    layouts.add(editTextLayout);
                                    rightLayout.addView(editTextLayout);
                                }
                            }
                            maps.put(position, layouts);
                        } else if(null != itemModel.getType() && itemModel.getType().equals("RadioButtonGroup")) {
                            List<RadioButton> rBtns=new ArrayList<RadioButton>();
                            String[] date=new String[itemModel.getOptionsModels().size()];
                            String[] values=new String[itemModel.getOptionsModels().size()];
                            for(int j=0; j < itemModel.getOptionsModels().size(); j++) {
                                RadioButton rBtn=(RadioButton)LayoutInflater.from(context).inflate(R.layout.my_radiobutton, null);
                                views.put(itemModel.getOptionsModels().get(j).getId(), rBtn);
                                rBtns.add(rBtn);
                                date[j]=itemModel.getOptionsModels().get(j).getName();
                                values[j]=itemModel.getOptionsModels().get(j).getChecked();
                            }
                            LinearLayout radioButtonLayout=
                                createRadioButton(itemModel.getFirstStr(), date, rBtns, values, itemModel.getLastStr());
                            layouts.add(radioButtonLayout);
                            rightLayout.addView(radioButtonLayout);
                            if(null != itemModel.getItemModels() && itemModel.getItemModels().size() != 0) {
                                for(int j=0; j < itemModel.getItemModels().size(); j++) {
                                    ItemModel suItemModel3=itemModel.getItemModels().get(j);
                                    EditText input=new EditText(context);
                                    views.put(suItemModel3.getId(), input);
                                    LinearLayout editTextLayout=
                                        createEditText(suItemModel3.getFirstStr(), suItemModel3.getLastStr(), input,
                                            suItemModel3.getValue(), suItemModel3.getDataType(), 1);
                                    layouts.add(editTextLayout);
                                    rightLayout.addView(editTextLayout);
                                }
                            }
                            // 再有一个LinearLayout 是垂直方向的 长是match宽是包含
                            maps.put(position, layouts);
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

    private void saveData(boolean isBack) {
        if(page == 0) {
            for(int i=0; i < titleModels.size(); i++) {
                if(i == firstCheck) {
                    titleModels.get(i).setType("Main");
                } else {
                    String[] tempSecondCheck;
                    if(secondCheck.contains(",")) {
                        tempSecondCheck=secondCheck.split(",");
                        for(int j=0; j < tempSecondCheck.length; j++) {
                            if(tempSecondCheck[j].equals(i + "")) {
                                titleModels.get(i).setType("Accompany");
                            }
                        }
                    } else {
                        if(secondCheck.equals(i + "")) {
                            titleModels.get(i).setType("Accompany");
                        }
                    }
                }
            }
        }
        for(Integer key: isNomalMap.keySet()) {
            titleModels.get(key).setIsNormal(String.valueOf(isNomalMap.get(key)));
        }
        for(String key: views.keySet()) {
            Object view=views.get(key);
            if(EditText.class.isInstance(view)) {
                if(null != ((EditText)view).getText().toString() && !"".equals(((EditText)view).getText().toString())) {
                    if(page == 0) {
                        setValue(key, ((EditText)view).getText().toString(), 0);
                        isAdd=true;
                    } else {
                        if(isNomalMap.containsKey(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                            if(!isNomalMap.get(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                                setValue(key, ((EditText)view).getText().toString(), 0);
                                isAdd=true;
                            }
                        }
                    }
                }
            } else if(RadioButton.class.isInstance(view)) {
                RadioButton radioButton=(RadioButton)view;
                if(radioButton.isChecked()) {
                    if(page == 0) {
                        setValue(key, "1", 1);
                        isAdd=true;
                    } else {
                        if(isNomalMap.containsKey(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                            if(!isNomalMap.get(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                                setValue(key, "1", 1);
                                isAdd=true;
                            }
                        }
                    }
                } else {
                    if(page == 0) {
                        setValue(key, "0", 1);
                    } else {
                        if(isNomalMap.containsKey(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                            if(!isNomalMap.get(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                                setValue(key, "0", 1);
                            }
                        }
                    }
                }
            } else if(CheckBox.class.isInstance(view)) {
                CheckBox box=(CheckBox)view;
                if(box.isChecked()) {
                    if(page == 0) {
                        setValue(key, "1", 2);
                        isAdd=true;
                    } else {
                        if(isNomalMap.containsKey(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                            if(!isNomalMap.get(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                                setValue(key, "1", 2);
                                isAdd=true;
                            }
                        }
                    }
                } else {
                    if(page == 0) {
                        setValue(key, "0", 2);
                    } else {
                        if(isNomalMap.containsKey(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                            if(!isNomalMap.get(Integer.parseInt(key.split("\\.")[1]) - 1)) {
                                setValue(key, "0", 2);
                            }
                        }
                    }
                }
            }
        }
        ModleToXml(isBack);
    }

    private void setValue(String id, String value, int type) {
        int titleIds=Integer.parseInt(id.split("\\.")[1]) - 1;
        List<ItemModel> list=titleModels.get(titleIds).getItemModels();
        int itemId=0;
        if(id.split("\\.")[2].contains("-")) {
            itemId=Integer.parseInt(id.split("\\.")[2].split("-")[0]) - 1;
        } else {
            itemId=Integer.parseInt(id.split("\\.")[2]) - 1;
        }
        ItemModel model=list.get(itemId);
        if(type == 0) {
            if(id.split("\\.").length == 3) {
                if(id.split("\\.")[2].contains("-")) {
                    for(int i=0; i < model.getItemModels().size(); i++) {
                        if(model.getItemModels().get(i).getId().equals(id)) {
                            model.getItemModels().get(i).setValue(value);
                        }
                    }
                } else {
                    if(model.getId().equals(id)) {
                        model.setValue(value);
                    }
                }
            } else if(id.split("\\.").length == 4) {
                if(model.getItemModels().get(Integer.parseInt(id.split("\\.")[3]) - 1).getId().equals(id)) {
                    model.getItemModels().get(Integer.parseInt(id.split("\\.")[3]) - 1).setValue(value);
                }
            }
        } else if(type == 1) {
            if(id.split("\\.").length == 3) {
                List<OptionsModel> list2=model.getOptionsModels();
                for(int k=0; k < list2.size(); k++) {
                    if(list2.get(k).getId().equals(id)) {
                        list2.get(k).setChecked(value);
                    }
                }
            } else if(id.split("\\.").length == 4) {
                if(model.getItemModels() != null && model.getItemModels().size() != 0) {
                    ItemModel model2=model.getItemModels().get(Integer.parseInt(id.split("\\.")[3].split("-")[0]) - 1);
                    if(model2 != null) {
                        List<OptionsModel> list3=model2.getOptionsModels();
                        if(list3 != null && list3.size() != 0) {
                            for(int k=0; k < list3.size(); k++) {
                                if(list3.get(k).getId().equals(id)) {
                                    list3.get(k).setChecked(value);
                                }
                            }
                        }
                    }
                }
            }
        } else if(type == 2) {
            if(id.split("\\.").length == 3) {
                List<OptionsModel> list2=model.getOptionsModels();
                for(int k=0; k < list2.size(); k++) {
                    if(list2.get(k).getId().equals(id)) {
                        list2.get(k).setChecked(value);
                    }
                }
            } else if(id.split("\\.").length == 4) {
                if(model.getItemModels() != null && model.getItemModels().size() != 0) {
                    ItemModel model2=model.getItemModels().get(Integer.parseInt(id.split("\\.")[3].split("-")[0]) - 1);
                    if(model2 != null) {
                        List<OptionsModel> list3=model2.getOptionsModels();
                        if(list3 != null && list3.size() != 0) {
                            for(int k=0; k < list3.size(); k++) {
                                if(list3.get(k).getId().equals(id)) {
                                    list3.get(k).setChecked(value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void ModleToXml(boolean isBack) {
        CaseModel caseModel;
        if(null != content && !"".equals(content) && !"null".equals(content)) {
            caseModel=caseList.get(0);
        } else if(ClientUtil.getCaseParams().size() != 0 && ClientUtil.getCaseParams().getValue(page + "") != null
            && !"".equals(ClientUtil.getCaseParams().getValue(page + ""))) {
            XMLCaseDatas(ClientUtil.getCaseParams().getValue(page + ""));
            caseModel=caseList.get(0);
        } else {
            caseModel=caseList.get(page);
        }
        StringBuilder stringBuffer=new StringBuilder();
        stringBuffer.append("<Root ID=\"" + caseModel.getId() + "\" Name=\"" + caseModel.getName() + "\" Level=\""
            + caseModel.getLevel() + "\" ChildCount=\"" + caseModel.getChildCount() + "\">");
        stringBuffer.append("<Title IsShow=\"" + caseModel.getIsShow() + "\">" + caseModel.getTitle() + "</Title>");
        for(int i=0; i < titleModels.size(); i++) {
            TitleModel titleModel=titleModels.get(i);
            List<ItemModel> itemModels=titleModel.getItemModels();
            stringBuffer.append("<Group ID=\"" + titleModel.getId() + "\" Name=\"" + titleModel.getName() + "\" Level=\""
                + titleModel.getLevel() + "\" ChildCount=\"" + titleModel.getChildCount() + "\"");
            if(titleModel.getType() != null && !"".equals(titleModel.getType())) {
                stringBuffer.append(" Type=\"" + titleModel.getType() + "\"");
            }
            if(titleModel.getPrefixP() != null && !"".equals(titleModel.getPrefixP())) {
                stringBuffer.append(" PrefixP=\"" + titleModel.getPrefixP() + "\"");
            }
            if(titleModel.getSuffixP() != null && !"".equals(titleModel.getSuffixP())) {
                stringBuffer.append(" SuffixP=\"" + titleModel.getSuffixP() + "\"");
            }
            if(titleModel.getSeperator() != null && !"".equals(titleModel.getSeperator())) {
                stringBuffer.append(" Seperator=\"" + titleModel.getSeperator() + "\"");
            }
            if(titleModel.getNoChecked() != null && !"".equals(titleModel.getNoChecked())) {
                stringBuffer.append(" NoChecked=\"" + titleModel.getNoChecked() + "\"");
            }
            stringBuffer.append(" IsNormal=\"" + titleModel.getIsNormal() + "\"");
            stringBuffer.append(">");
            stringBuffer.append("<Title IsShow=\"" + titleModel.getIsShow() +"\"");
            if(titleModel.getLineBreak() != null && !"".equals(titleModel.getLineBreak())) {
                stringBuffer.append(" LineBreak=\"" + titleModel.getLineBreak() + "\"");
            }
            stringBuffer.append(">" + titleModel.getTitle() + "</Title>");
            for(int j=0; j < itemModels.size(); j++) {
                ItemModel itemModel=itemModels.get(j);
                if(null != itemModel.getChildCount()) {
                    stringBuffer.append("<Item ID=\"" + itemModel.getId() + "\" Name=\"" + itemModel.getName() + "\" ");
                    if(itemModel.getFirstStr() != null && !"".equals(itemModel.getFirstStr())) {
                        stringBuffer.append("FirstStr=\"" + itemModel.getFirstStr() + "\" ");
                    }
                    if(itemModel.getLastStr() != null && !"".equals(itemModel.getLastStr())) {
                        stringBuffer.append("LastStr=\"" + itemModel.getLastStr() + "\" ");
                    }
                    if(itemModel.getPrefixP() != null && !"".equals(itemModel.getPrefixP())) {
                        stringBuffer.append("PrefixP=\"" + itemModel.getPrefixP() + "\" ");
                    }
                    if(itemModel.getSuffixP() != null && !"".equals(itemModel.getSuffixP())) {
                        stringBuffer.append("SuffixP=\"" + itemModel.getSuffixP() + "\" ");
                    }
                    if(itemModel.getSeperator() != null && !"".equals(itemModel.getSeperator())) {
                        stringBuffer.append("Seperator=\"" + itemModel.getSeperator() + "\" ");
                    }
                    if(itemModel.getNoChecked() != null && !"".equals(itemModel.getNoChecked())) {
                        stringBuffer.append("NoChecked=\"" + itemModel.getNoChecked() + "\" ");
                    }
                    stringBuffer.append("Level=\"" + itemModel.getLevel() + "\" ");
                    stringBuffer.append("ChildCount=\"" + itemModel.getChildCount() + "\"");
                    if(itemModel.getIsShow() != null && !"".equals(itemModel.getIsShow())) {
                        stringBuffer.append(" IsShow=\"" + itemModel.getIsShow() + "\">");
                    } else {
                        stringBuffer.append(">");
                    }
                    List<ItemModel> subItemModels=itemModel.getItemModels();
                    for(int k=0; k < subItemModels.size(); k++) {
                        ItemModel itemModel2=subItemModels.get(k);
                        stringBuffer.append("<SubItem ID=\"" + itemModel2.getId() + "\" Name=\"" + itemModel2.getName() + "\" ");
                        if(itemModel2.getFirstStr() != null && !"".equals(itemModel2.getFirstStr())) {
                            stringBuffer.append("FirstStr=\"" + itemModel2.getFirstStr() + "\" ");
                        }
                        if(itemModel2.getLastStr() != null && !"".equals(itemModel2.getLastStr())) {
                            stringBuffer.append("LastStr=\"" + itemModel2.getLastStr() + "\" ");
                        }
                        if(itemModel2.getPrefixP() != null && !"".equals(itemModel2.getPrefixP())) {
                            stringBuffer.append("PrefixP=\"" + itemModel2.getPrefixP() + "\" ");
                        }
                        if(itemModel2.getSuffixP() != null && !"".equals(itemModel2.getSuffixP())) {
                            stringBuffer.append("SuffixP=\"" + itemModel2.getSuffixP() + "\" ");
                        }
                        if(itemModel2.getSeperator() != null && !"".equals(itemModel2.getSeperator())) {
                            stringBuffer.append("Seperator=\"" + itemModel2.getSeperator() + "\" ");
                        }
                        if(itemModel2.getNoChecked() != null && !"".equals(itemModel2.getNoChecked())) {
                            stringBuffer.append("NoChecked=\"" + itemModel2.getNoChecked() + "\" ");
                        }
                        stringBuffer.append("Level=\"" + itemModel2.getLevel() + "\"");
                        if(itemModel2.getIsShow() != null && !"".equals(itemModel2.getIsShow())) {
                            stringBuffer.append(" IsShow=\"" + itemModel2.getIsShow() + "\"");
                        }
                        if(itemModel2.getInput() != null && !"".equals(itemModel2.getInput())) {
                            stringBuffer.append(" Input=\"" + itemModel2.getInput() + "\"");
                        }
                        if(itemModel2.getType() != null && itemModel2.getType().equals("Edit")) {
                            stringBuffer.append(" Type=\"" + itemModel2.getType() + "\"");
                            if(itemModel2.getDataType() != null && !"".equals(itemModel2.getDataType())) {
                                stringBuffer.append(" DataType=\"" + itemModel2.getDataType() + "\"");
                            }
                            if(!"".equals(itemModel2.getValue()) && !"null".equals(itemModel2.getValue())
                                && null != itemModel2.getValue()) {
                                stringBuffer.append(" Value=\"" + itemModel2.getValue() + "\"/>");
                            } else {
                                stringBuffer.append(" Value=\"" + "\"/>");
                            }
                        } else {
                            List<OptionsModel> optionsModels=itemModel2.getOptionsModels();
                            if(null != optionsModels && optionsModels.size() != 0) {
                                stringBuffer.append(" Type=\"" + itemModel2.getType() + "\"");
                                stringBuffer.append(">");
                                for(int x=0; x < optionsModels.size(); x++) {
                                    stringBuffer.append("<Options ID=\"" + optionsModels.get(x).getId() + "\" Checked=\""
                                        + optionsModels.get(x).getChecked() + "\">");
                                    stringBuffer.append(optionsModels.get(x).getName() + "</Options>");
                                }
                                stringBuffer.append("</SubItem>");
                            } else {
                                stringBuffer.append("/>");
                            }
                        }
                    }
                    stringBuffer.append("</Item>");
                } else {
                    stringBuffer.append("<Item ID=\"" + itemModel.getId() + "\" Name=\"" + itemModel.getName() + "\" ");
                    if(itemModel.getFirstStr() != null && !"".equals(itemModel.getFirstStr())) {
                        stringBuffer.append("FirstStr=\"" + itemModel.getFirstStr() + "\" ");
                    }
                    if(itemModel.getLastStr() != null && !"".equals(itemModel.getLastStr())) {
                        stringBuffer.append("LastStr=\"" + itemModel.getLastStr() + "\" ");
                    }
                    if(itemModel.getPrefixP() != null && !"".equals(itemModel.getPrefixP())) {
                        stringBuffer.append("PrefixP=\"" + itemModel.getPrefixP() + "\" ");
                    }
                    if(itemModel.getSuffixP() != null && !"".equals(itemModel.getSuffixP())) {
                        stringBuffer.append("SuffixP=\"" + itemModel.getSuffixP() + "\" ");
                    }
                    if(itemModel.getSeperator() != null && !"".equals(itemModel.getSeperator())) {
                        stringBuffer.append("Seperator=\"" + itemModel.getSeperator() + "\" ");
                    }
                    if(itemModel.getNoChecked() != null && !"".equals(itemModel.getNoChecked())) {
                        stringBuffer.append("NoChecked=\"" + itemModel.getNoChecked() + "\" ");
                    }
                    stringBuffer.append("Level=\"" + itemModel.getLevel() + "\"");
                    if(itemModel.getIsShow() != null && !"".equals(itemModel.getIsShow())) {
                        stringBuffer.append(" IsShow=\"" + itemModel.getIsShow() + "\"");
                    }
                    if(itemModel.getInput() != null && !"".equals(itemModel.getInput())) {
                        stringBuffer.append(" Input=\"" + itemModel.getInput() + "\"");
                    }
                    if(itemModel.getType() != null && itemModel.getType().equals("Edit")) {
                        stringBuffer.append(" Type=\"" + itemModel.getType() + "\"");
                        if(itemModel.getDataType() != null && !"".equals(itemModel.getDataType())) {
                            stringBuffer.append(" DataType=\"" + itemModel.getDataType() + "\"");
                        }
                        if(!"".equals(itemModel.getValue()) && !"null".equals(itemModel.getValue()) && null != itemModel.getValue()) {
                            stringBuffer.append(" Value=\"" + itemModel.getValue() + "\"/>");
                        } else {
                            stringBuffer.append(" Value=\"" + "\"/>");
                        }
                    } else {
                        List<OptionsModel> optionsModels=itemModel.getOptionsModels();
                        if(optionsModels.size() != 0 && null != optionsModels) {
                            stringBuffer.append(" Type=\"" + itemModel.getType() + "\"");
                            stringBuffer.append(">");
                            for(int k=0; k < optionsModels.size(); k++) {
                                stringBuffer.append("<Options ID=\"" + optionsModels.get(k).getId() + "\" Checked=\""
                                    + optionsModels.get(k).getChecked() + "\">");
                                stringBuffer.append(optionsModels.get(k).getName() + "</Options>");
                            }
                            List<ItemModel> subItemModels=itemModel.getItemModels();
                            for(int z=0; z < subItemModels.size(); z++) {
                                ItemModel subItemModel3=subItemModels.get(z);
                                stringBuffer.append("<SubItem ID=\"" + subItemModel3.getId() + "\" Name=\""
                                    + subItemModel3.getName() + "\" ");
                                if(subItemModel3.getFirstStr() != null && !"".equals(subItemModel3.getFirstStr())) {
                                    stringBuffer.append("FirstStr=\"" + subItemModel3.getFirstStr() + "\" ");
                                }
                                if(subItemModel3.getLastStr() != null && !"".equals(subItemModel3.getLastStr())) {
                                    stringBuffer.append("LastStr=\"" + subItemModel3.getLastStr() + "\" ");
                                }
                                if(subItemModel3.getPrefixP() != null && !"".equals(subItemModel3.getPrefixP())) {
                                    stringBuffer.append("PrefixP=\"" + subItemModel3.getPrefixP() + "\" ");
                                }
                                if(subItemModel3.getSuffixP() != null && !"".equals(subItemModel3.getSuffixP())) {
                                    stringBuffer.append("SuffixP=\"" + subItemModel3.getSuffixP() + "\" ");
                                }
                                if(subItemModel3.getSeperator() != null && !"".equals(subItemModel3.getSeperator())) {
                                    stringBuffer.append("Seperator=\"" + subItemModel3.getSeperator() + "\" ");
                                }
                                if(subItemModel3.getNoChecked() != null && !"".equals(subItemModel3.getNoChecked())) {
                                    stringBuffer.append("NoChecked=\"" + subItemModel3.getNoChecked() + "\" ");
                                }
                                stringBuffer.append("Level=\"" + subItemModel3.getLevel() + "\"");
                                if(subItemModel3.getIsShow() != null && !"".equals(subItemModel3.getIsShow())) {
                                    stringBuffer.append(" IsShow=\"" + subItemModel3.getIsShow() + "\"");
                                }
                                if(subItemModel3.getInput() != null && !"".equals(subItemModel3.getInput())) {
                                    stringBuffer.append(" Input=\"" + subItemModel3.getInput() + "\"");
                                }
                                if(subItemModel3.getType() != null && subItemModel3.getType().equals("Edit")) {
                                    stringBuffer.append(" Type=\"" + subItemModel3.getType() + "\"");
                                    if(subItemModel3.getDataType() != null && !"".equals(subItemModel3.getDataType())) {
                                        stringBuffer.append(" DataType=\"" + subItemModel3.getDataType() + "\"");
                                    }
                                    if(!"".equals(subItemModel3.getValue()) && !"null".equals(subItemModel3.getValue())
                                        && null != subItemModel3.getValue()) {
                                        stringBuffer.append(" Value=\"" + subItemModel3.getValue() + "\"/>");
                                    } else {
                                        stringBuffer.append(" Value=\"" + "\"/>");
                                    }
                                }
                            }
                            stringBuffer.append("</Item>");
                        } else {
                            stringBuffer.append("/>");
                        }
                    }
                }
            }
            stringBuffer.append("</Group>");
        }
        stringBuffer.append("</Root>");
        // CommonUtil.appendToFile(stringBuffer.toString(), new File(Environment.getExternalStorageDirectory() + File.separator
        // + "text1.txt"));
        ClientUtil.getCaseParams().add(String.valueOf(page), stringBuffer.toString());
        if(!isBack){
            Toast.makeText(context, "已保存到本地", Toast.LENGTH_SHORT).show();
        }
        // Intent intent=new Intent();
        // Bundle bundle=new Bundle();
        // bundle.putBoolean("isAdd", isAdd);
        // intent.putExtras(bundle);
        // setResult(Activity.RESULT_OK, intent);
        // finish();
    }

    private void initData() {
        if(null != content && !"".equals(content) && !"null".equals(content) && content.startsWith("<Root")) {
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
        if(page == 0) {
            for(int i=0; i < titleModels.size(); i++) {
                if(i == firstCheck) {
                    leftList.add(titleModels.get(i).getTitle());
                    xbsIds.add(i);
                    isNomalMap.put(Integer.parseInt(titleModels.get(i).getId().split("\\.")[1]) - 1,
                        Boolean.parseBoolean(titleModels.get(i).getIsNormal()));
                } else {
                    String[] tempSecondCheck;
                    if(secondCheck.contains(",")) {
                        tempSecondCheck=secondCheck.split(",");
                        for(int j=0; j < tempSecondCheck.length; j++) {
                            if(tempSecondCheck[j].equals(i + "")) {
                                leftList.add(titleModels.get(i).getTitle());
                                xbsIds.add(i);
                                isNomalMap.put(Integer.parseInt(titleModels.get(i).getId().split("\\.")[1]) - 1,
                                    Boolean.parseBoolean(titleModels.get(i).getIsNormal()));
                            }
                        }
                    } else {
                        if(secondCheck.equals(i + "")) {
                            leftList.add(titleModels.get(i).getTitle());
                            xbsIds.add(i);
                            isNomalMap.put(Integer.parseInt(titleModels.get(i).getId().split("\\.")[1]) - 1,
                                Boolean.parseBoolean(titleModels.get(i).getIsNormal()));
                        }
                    }
                }
            }
        } else {
            for(int i=0; i < titleModels.size(); i++) {
                leftList.add(titleModels.get(i).getTitle());
                isNomalMap.put(Integer.parseInt(titleModels.get(i).getId().split("\\.")[1]) - 1,
                    Boolean.parseBoolean(titleModels.get(i).getIsNormal()));
            }
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

    private LinearLayout createEditText(String name, String company, EditText input, String value, String dataType, int isTop) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if(isTop == 0) {
            layoutParams.topMargin=height / 50;
        } else {
            layoutParams.leftMargin=width / 40;
            layoutParams.bottomMargin=height / 50;
        }
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        if(name != null && !"".equals(name) && !"null".equals(name)){
            if(name.equals("其它") || name.equals("服用") || name.equals("手术") || name.equals("疼痛放射至")){
                layout.setOrientation(LinearLayout.VERTICAL);
            }
        }
        layout.setLayoutParams(layoutParams);
        LayoutParams inputParams=new LayoutParams(width / 4, LayoutParams.WRAP_CONTENT);
        inputParams.gravity=Gravity.CENTER;
        inputParams.leftMargin=width / 30;
        if(name != null && !"".equals(name) && !"null".equals(name)) {
            TextView textName=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textNameParams.leftMargin=width / 40;
            if(name.equals("其它") || name.equals("服用") || name.equals("手术") || name.equals("疼痛放射至")){
                textNameParams.bottomMargin=height / 50;
            }
            textName.setLayoutParams(textNameParams);
            textName.setText(name);
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextColor(Color.parseColor("#414141"));
            textName.setTextSize(16);
            layout.addView(textName);
            inputParams.leftMargin=width / 40;
        }
        input.setLayoutParams(inputParams);
        input.setGravity(Gravity.CENTER);
        input.setPadding(0, height / 300, 0, 0);
        input.setBackgroundResource(R.drawable.edit_bg);
        if(page == 2) {
            LayoutParams inputParams1=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            inputParams1.leftMargin=width / 30;
            inputParams1.rightMargin=width / 30;
            input.setLayoutParams(inputParams1);
            input.setGravity(Gravity.LEFT);
            input.setPadding(height / 300, height / 300, height / 300, height / 300);
            input.setBackgroundResource(R.drawable.edit_big_bg);
        } else if(name != null && !"".equals(name) && !"null".equals(name)){
            if(name.equals("其它") || name.equals("服用") || name.equals("手术") || name.equals("疼痛放射至")){
                LayoutParams inputParams1=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                inputParams1.leftMargin=width / 30;
                inputParams1.rightMargin=width / 30;
                input.setLayoutParams(inputParams1);
                input.setGravity(Gravity.LEFT);
                input.setPadding(height / 300, height / 300, height / 300, height / 300);
                input.setBackgroundResource(R.drawable.edit_big_bg);
            }
        }
        input.setTextColor(Color.parseColor("#414141"));
        input.setTextSize(16);
        if(dataType.equals("Number")) {
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        if(value != null && !"".equals(value) && !"null".equals(value)) {
            input.setText(value);
        }
        layout.addView(input);

        if(company != null && !"".equals(company) && !"null".equals(company)) {
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
        }
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

    private LinearLayout createRadioButton(String name, String[] selectTexts, final List<RadioButton> radioButtons, String[] value,
        String lastStr) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);

        if(name != null && !"".equals(name) && !"null".equals(name)) {
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
            button.setId(i);
            button.setText(selectTexts[i]);
            button.setPadding(10, 10, 10, 10);
            button.setGravity(Gravity.CENTER_VERTICAL);
            if(value[i].equals("1")) {
                button.setChecked(true);
            } else {
                button.setChecked(false);
            }
            // button.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // if(((RadioButton)v).isChecked()) {
            // for(int j=0; j < radioButtons.size(); j++) {
            // radioButtons.get(j).setChecked(false);
            // }
            // } else {
            // for(int j=0; j < radioButtons.size(); j++) {
            // radioButtons.get(j).setChecked(false);
            // }
            // ((RadioButton)v).setChecked(true);
            // }
            // }
            // });
            // button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            //
            // @Override
            // public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // if(isChecked) {
            // for(int j=0; j < radioButtons.size(); j++) {
            // if(j == radioButtons.get(j).getId()) {
            // radioButtons.get(j).setChecked(isChecked);
            // }
            // }
            // } else {
            // for(int j=0; j < radioButtons.size(); j++) {
            // if(j == radioButtons.get(j).getId()) {
            // radioButtons.get(j).setChecked(isChecked);
            // }else{
            // radioButtons.get(j).setChecked(!isChecked);
            // }
            // }
            // }
            // }
            // });
            button.setLayoutParams(radioButtonParams);
            group.addView(button);
            // layout.addView(button);
        }
        layout.addView(group);
        if(lastStr != null && !"".equals(lastStr) && !"null".equals(lastStr)) {
            TextView lastStrText=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            textNameParams.leftMargin=width / 40;
            lastStrText.setLayoutParams(textNameParams);
            lastStrText.setText(lastStr);
            lastStrText.setGravity(Gravity.CENTER_VERTICAL);
            lastStrText.setTextColor(Color.parseColor("#414141"));
            lastStrText.setTextSize(16);
            layout.addView(lastStrText);
        }
        return layout;
    }

    private LinearLayout createCheckBox(String name, String[] data_list, List<CheckBox> checkBoxs, String[] value, String lastStr) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);

        if(null != name && !"".equals(name) && !"null".equals(name)) {
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

        LayoutParams viewParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        viewParams.leftMargin=width / 40;
        viewParams.gravity=Gravity.LEFT;

        for(int i=0; i < checkBoxs.size(); i++) {
            CheckBox box=checkBoxs.get(i);
            box.setLayoutParams(viewParams);
            box.setTextSize(16);
            box.setText(data_list[i]);
            if(value[i].equals("1")) {
                box.setChecked(true);
            }else{
                box.setChecked(false);
            }
            box.setTextColor(Color.parseColor("#414141"));
            layout.addView(box);
        }

        if(lastStr != null && !"".equals(lastStr) && !"null".equals(lastStr)) {
            TextView lastStrText=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            textNameParams.leftMargin=width / 40;
            lastStrText.setLayoutParams(textNameParams);
            lastStrText.setText(lastStr);
            lastStrText.setGravity(Gravity.CENTER_VERTICAL);
            lastStrText.setTextColor(Color.parseColor("#414141"));
            lastStrText.setTextSize(16);
            layout.addView(lastStrText);
        }
        return layout;
    }
}
