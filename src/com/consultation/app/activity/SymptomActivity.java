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
import android.widget.Button;
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

    private List<Integer> ids=new ArrayList<Integer>();

    private Map<Integer, List<LinearLayout>> maps=new HashMap<Integer, List<LinearLayout>>();

    private List<TitleModel> titleModels;

    private int page;

    private Button saveBtn;

    private String titleText;

    private boolean isAdd=false;

    private String content="";

    private String departmentId="10503";
    
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
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText(titleText);
        title_text.setTextSize(20);

        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);

        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
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

        saveBtn=(Button)findViewById(R.id.syamptom_btn_save);
        saveBtn.setTextSize(20);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存数据
                saveData();
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
                        if(null != itemModel.getFirstStr() && !"".equals(itemModel.getFirstStr())){
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
                                        subItemModel.getValue(), subItemModel.getDataType());
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
                                LinearLayout spinnerLayout=createCheckBox(subItemModel.getFirstStr(), data_list, checkBoxs, values);
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
                                LinearLayout radioButtonLayout=createRadioButton(subItemModel.getFirstStr(), date, rBtns, values);
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
                                    itemModel.getDataType());
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
                            LinearLayout spinnerLayout=createCheckBox(itemModel.getFirstStr(), data_list, checkBoxs, values);
                            layouts.add(spinnerLayout);
                            maps.put(position, layouts);
                            rightLayout.addView(spinnerLayout);
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
                            LinearLayout radioButtonLayout=createRadioButton(itemModel.getFirstStr(), date, rBtns, values);
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

    private void saveData() {
        for(String key: views.keySet()) {
            Object view=views.get(key);
            if(EditText.class.isInstance(view)) {
                if(null != ((EditText)view).getText().toString() && !"".equals(((EditText)view).getText().toString())) {
                    setValue(key, ((EditText)view).getText().toString(), 0);
                    isAdd=true;
                }
            } else if(RadioButton.class.isInstance(view)) {
                RadioButton radioButton=(RadioButton)view;
                if(radioButton.isChecked()) {
                    setValue(key, "1", 1);
                    isAdd=true;
                } else {
                    setValue(key, "0", 1);
                }
            } else if(CheckBox.class.isInstance(view)) {
                CheckBox box=(CheckBox)view;
                if(box.isChecked()) {
                    setValue(key, "1", 2);
                    isAdd=true;
                } else {
                    setValue(key, "0", 2);
                }
            }
        }
        ModleToXml();
    }

    private void setValue(String id, String value, int type) {
        int titleIds=Integer.parseInt(id.split("\\.")[1]) - 1;
        List<ItemModel> list=titleModels.get(titleIds).getItemModels();
        ItemModel model=list.get(Integer.parseInt(id.split("\\.")[2]) - 1);
        if(type == 0) {
            if(id.split("\\.").length == 3) {
                if(model.getId().equals(id)) {
                    model.setValue(value);
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

    private void ModleToXml() {
        CaseModel caseModel;
        if(null != content && !"".equals(content) && !"null".equals(content)) {
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
                + titleModel.getLevel() + "\" ChildCount=\"" + titleModel.getChildCount() + "\">");
            stringBuffer.append("<Title IsShow=\"" + titleModel.getIsShow() + "\">" + titleModel.getTitle() + "</Title>");
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
                            stringBuffer.append(">");
                            for(int k=0; k < optionsModels.size(); k++) {
                                stringBuffer.append("<Options ID=\"" + optionsModels.get(k).getId() + "\" Checked=\""
                                    + optionsModels.get(k).getChecked() + "\">");
                                stringBuffer.append(optionsModels.get(k).getName() + "</Options>");
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
//         CommonUtil.appendToFile(stringBuffer.toString(), new File(Environment.getExternalStorageDirectory() + File.separator +
//         "text1.txt"));
        ClientUtil.getCaseParams().add(String.valueOf(page), stringBuffer.toString());
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putBoolean("isAdd", isAdd);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void initData() {
        if(null != content && !"".equals(content) && !"null".equals(content)) {
            // CommonUtil.appendToFile(content, new File(Environment.getExternalStorageDirectory() + File.separator + "text.txt"));
            XMLCaseDatas(content);
            titleModels=caseList.get(0).getTitleModels();
        } else {
            initCaseDatas(departmentId + "case.xml");
            titleModels=caseList.get(page).getTitleModels();
        }
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

    private LinearLayout createEditText(String name, String company, EditText input, String value, String dataType) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);

        LayoutParams inputParams=new LayoutParams(width / 4, height / 20);
        inputParams.gravity=Gravity.CENTER;
        inputParams.leftMargin=width / 30;
        if(name != null && !"".equals(name) && !"null".equals(name)) {
            TextView textName=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textNameParams.leftMargin=width / 40;
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
        input.setTextColor(Color.parseColor("#414141"));
        input.setPadding(0, height / 300, 0, 0);
        input.setBackgroundResource(R.drawable.edit_bg);
        input.setTextSize(16);
        if(dataType.equals("Number")) {
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
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

    private LinearLayout createRadioButton(String name, String[] selectTexts, final List<RadioButton> radioButtons, String[] value) {
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
            }
            button.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        for(int j=0; j < radioButtons.size(); j++) {
                            if(j == radioButtons.get(j).getId()) {
                                radioButtons.get(j).setChecked(isChecked);
                            } else {
                                radioButtons.get(j).setChecked(!isChecked);
                            }
                        }
                    }
                }
            });
            button.setLayoutParams(radioButtonParams);
            group.addView(button);
        }
        layout.addView(group);

        return layout;
    }

    private LinearLayout createCheckBox(String name, String[] data_list, List<CheckBox> checkBoxs, String[] value) {
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
            }
            box.setTextColor(Color.parseColor("#414141"));
            layout.addView(box);
        }
        return layout;
    }
}
