package com.consultation.app.activity;

import java.io.File;
import java.util.ArrayList;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.consultation.app.model.CaseModel;
import com.consultation.app.model.ItemModel;
import com.consultation.app.model.OptionsModel;
import com.consultation.app.model.TitleModel;
import com.consultation.app.service.OpenApiService;
import com.consultation.app.util.BitmapCache;
import com.consultation.app.util.ClientUtil;
import com.consultation.app.util.CommonUtil;
import com.consultation.app.util.SharePreferencesEditor;

@SuppressLint({"UseSparseArrays", "HandlerLeak"})
public class CaseTestActivity extends CaseBaseActivity implements OnLongClickListener {

    private TextView title_text, back_text;

    private LinearLayout back_layout;

    private ListView listView;

    private LinearLayout rightLayout, rightTextLayout;

    private MyAdapter myAdapter;

    private List<String> leftList=new ArrayList<String>();
    
    private Map<String, Object> views=new HashMap<String, Object>();

    private List<Integer> ids=new ArrayList<Integer>();

    private Map<Integer, List<LinearLayout>> maps=new HashMap<Integer, List<LinearLayout>>();

    private ViewHolder holder;

    private int currentPosition=-1;
    
    private int page;

    private Context context;

    private RequestQueue mQueue;

    private ImageLoader mImageLoader;

    private int width;

    private int height;

    private TextView textAdd, tip;

    private View currentView;

    private List<TitleModel> titleModels=new ArrayList<TitleModel>();

    private Button saveBtn, saveTextBtn;

    private boolean isAdd=false;

    private String caseId, imageString, departmentId="";

    private SharePreferencesEditor editor;

    private Map<Integer, List<String>> pathMap=new HashMap<Integer, List<String>>();

    private Map<Integer, List<String>> idMap=new HashMap<Integer, List<String>>();
    
    private Map<Integer, List<String>> bigPathMap=new HashMap<Integer, List<String>>();

    private String content="";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_case_add_test_layout);
        context=this;
        editor=new SharePreferencesEditor(context);
        WindowManager wm=this.getWindowManager();
        width=wm.getDefaultDisplay().getWidth();
        height=wm.getDefaultDisplay().getHeight();
        caseId=getIntent().getStringExtra("caseId");
        departmentId=getIntent().getStringExtra("departmentId");
        content=getIntent().getStringExtra("content");
        page=getIntent().getIntExtra("page", -1);
        imageString=getIntent().getStringExtra("imageString");
        mQueue=Volley.newRequestQueue(context);
        mImageLoader=new ImageLoader(mQueue, new BitmapCache());
        initData();
        initView();
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("检验");
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
                bundle.putBoolean("isAdd", isAdd);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        rightLayout=(LinearLayout)findViewById(R.id.test_right_layout);
        rightTextLayout=(LinearLayout)findViewById(R.id.test_right_text_layout);
        
        textAdd=(TextView)findViewById(R.id.test_add_image_text);
        textAdd.setText(Html.fromHtml("<u>" + "添加" + "</u>"));
        textAdd.setGravity(Gravity.CENTER_VERTICAL);
        textAdd.setTextColor(Color.BLUE);
        textAdd.setTextSize(17);
        textAdd.setVisibility(View.INVISIBLE);
        textAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CaseTestActivity.this, AddPatientPicActivity.class), 0);
            }
        });

        tip=(TextView)findViewById(R.id.test_image_tip);
        tip.setTextSize(14);
        tip.setVisibility(View.INVISIBLE);

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.test_left_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentView != null) {
                    currentView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                currentView=view;
                view.setBackgroundColor(Color.parseColor("#E4E4E4"));
                currentPosition=position;
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
                tip.setVisibility(View.VISIBLE);
                textAdd.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                showRightLayout(currentPosition);
            }
        });

        saveBtn=(Button)findViewById(R.id.test_image_btn_save);
        saveBtn.setTextSize(17);
        saveBtn.setVisibility(View.INVISIBLE);
        saveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pathMap.get(currentPosition) == null || pathMap.get(currentPosition).size() == 0) {
                    Toast.makeText(context, "请添加图片", Toast.LENGTH_LONG).show();
                    return;
                }
                List<String> temp=new ArrayList<String>();
                for(int i=0; i < pathMap.get(currentPosition).size(); i++) {
                    if(!pathMap.get(currentPosition).get(i).startsWith("http:")) {
                        temp.add(pathMap.get(currentPosition).get(i));
                    }
                }
                if(temp == null || temp.size() == 0) {
                    Toast.makeText(context, "请添加新图片", Toast.LENGTH_LONG).show();
                    return;
                }
                File[] files=new File[temp.size()];
                for(int i=0; i < temp.size(); i++) {
                    File file=new File(temp.get(i));
                    files[i]=file;
                }
                Map<String, String> params=new HashMap<String, String>();
                params.put("case_id", caseId);
                params.put("test_name", leftList.get(currentPosition));
                params.put("accessToken", ClientUtil.getToken());
                params.put("uid", editor.get("uid", ""));
                CommonUtil.showLoadingDialog(context);
                OpenApiService.getInstance(context).getUploadFiles(ClientUtil.GET_UPLOAD_IMAGES_URL, context,
                    new ConsultationCallbackHandler() {

                        @Override
                        public void onSuccess(String rspContent, int statusCode) {
                            CommonUtil.closeLodingDialog();
                            isAdd=true;
                            Toast.makeText(context, "图片上传成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(ConsultationCallbackException exp) {
                            Toast.makeText(context, "图片上传失败，请重新上传", Toast.LENGTH_LONG).show();
                            CommonUtil.closeLodingDialog();
                        }
                    }, files, params);
            }
        });
        saveBtn.setOnTouchListener(new ButtonListener().setImage(getResources().getDrawable(R.drawable.login_register_btn_shape),
            getResources().getDrawable(R.drawable.login_register_press_btn_shape)).getBtnTouchListener());
    
        saveTextBtn=(Button)findViewById(R.id.test_text_btn_save);
        saveTextBtn.setTextSize(20);
        saveTextBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存数据
                saveData();
            }
        });
        currentPosition = 0;
        if(ids.contains(0)) {
            // 显示界面，并影藏其他界面
            showRightLayout(0, 1);
        } else {
            // 创建界面
            showRightLayout(0, 0);
            ids.add(0);
        }
        for(int i=0; i < leftList.size(); i++) {
            if(i != 0) {
                showRightLayout(i, 2);
            }
        }
        tip.setVisibility(View.VISIBLE);
        textAdd.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        showRightLayout(currentPosition);
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
                            rightTextLayout.addView(TextViewLayout);
                        }
                        for(int k=0; k < subItemModels.size(); k++) {
                            ItemModel subItemModel=subItemModels.get(k);
                            if(null != subItemModel.getType() && subItemModel.getType().equals("Edit")) {
                                EditText input=new EditText(context);
                                views.put(subItemModel.getId(), input);
                                LinearLayout editTextLayout=
                                    createEditText(subItemModel.getFirstStr(), subItemModel.getLastStr(), input,
                                        subItemModel.getValue(), subItemModel.getDataType(),0);
                                layouts.add(editTextLayout);
                                maps.put(position, layouts);
                                rightTextLayout.addView(editTextLayout);
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
                                rightTextLayout.addView(spinnerLayout);
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
                                rightTextLayout.addView(radioButtonLayout);
                            }
                        }
                    } else {
                        if(null != itemModel.getType() && itemModel.getType().equals("Edit")) {
                            EditText input=new EditText(context);
                            views.put(itemModel.getId(), input);
                            LinearLayout editTextLayout=
                                createEditText(itemModel.getFirstStr(), itemModel.getLastStr(), input, itemModel.getValue(),
                                    itemModel.getDataType(),0);
                            layouts.add(editTextLayout);
                            maps.put(position, layouts);
                            rightTextLayout.addView(editTextLayout);
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
                            rightTextLayout.addView(spinnerLayout);
                            if(null != itemModel.getItemModels() && itemModel.getItemModels().size() != 0) {
                                for(int j=0; j < itemModel.getItemModels().size(); j++) {
                                    ItemModel suItemModel3=itemModel.getItemModels().get(j);
                                    EditText input=new EditText(context);
                                    views.put(suItemModel3.getId(), input);
                                    LinearLayout editTextLayout=
                                        createEditText(suItemModel3.getFirstStr(), suItemModel3.getLastStr(), input,
                                            suItemModel3.getValue(), suItemModel3.getDataType(),1);
                                    layouts.add(editTextLayout);
                                    rightTextLayout.addView(editTextLayout);
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
                            LinearLayout radioButtonLayout=createRadioButton(itemModel.getFirstStr(), date, rBtns, values);
                            layouts.add(radioButtonLayout);
                            rightLayout.addView(radioButtonLayout);
                            if(null != itemModel.getItemModels() && itemModel.getItemModels().size() != 0) {
                                for(int j=0; j < itemModel.getItemModels().size(); j++) {
                                    ItemModel suItemModel3=itemModel.getItemModels().get(j);
                                    EditText input=new EditText(context);
                                    views.put(suItemModel3.getId(), input);
                                    LinearLayout editTextLayout=
                                        createEditText(suItemModel3.getFirstStr(), suItemModel3.getLastStr(), input,
                                            suItemModel3.getValue(), suItemModel3.getDataType(),1);
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
        int itemId = 0;
        if(id.split("\\.")[2].contains("-")){
            itemId = Integer.parseInt(id.split("\\.")[2].split("-")[0]) - 1;
        }else{
            itemId = Integer.parseInt(id.split("\\.")[2]) - 1;
        }
        ItemModel model=list.get(itemId);
        if(type == 0) {
            if(id.split("\\.").length == 3) {
                if(id.split("\\.")[2].contains("-")){
                    for(int i=0; i < model.getItemModels().size(); i++) {
                        if(model.getItemModels().get(i).getId().equals(id)) {
                            model.getItemModels().get(i).setValue(value);
                        }
                    }
                }else{
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

    private void ModleToXml() {
        CaseModel caseModel;
        if(null != content && !"".equals(content) && !"null".equals(content)) {
            caseModel=caseList.get(0);
        } else if(ClientUtil.getCaseParams().size()!=0 && ClientUtil.getCaseParams().getValue(page+"") != null && !"".equals(ClientUtil.getCaseParams().getValue(page+""))){
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
                            stringBuffer.append(" Type=\"" + itemModel.getType() + "\"");
                            stringBuffer.append(">");
                            for(int k=0; k < optionsModels.size(); k++) {
                                stringBuffer.append("<Options ID=\"" + optionsModels.get(k).getId() + "\" Checked=\""
                                    + optionsModels.get(k).getChecked() + "\">");
                                stringBuffer.append(optionsModels.get(k).getName() + "</Options>");
                            }
                            List<ItemModel> subItemModels=itemModel.getItemModels();
                            for(int z=0; z < subItemModels.size(); z++) {
                                ItemModel subItemModel3 = subItemModels.get(z);
                                stringBuffer.append("<SubItem ID=\"" + subItemModel3.getId() + "\" Name=\"" + subItemModel3.getName() + "\" ");
                                if(subItemModel3.getFirstStr() != null && !"".equals(subItemModel3.getFirstStr())) {
                                    stringBuffer.append("FirstStr=\"" + subItemModel3.getFirstStr() + "\" ");
                                }
                                if(subItemModel3.getLastStr() != null && !"".equals(subItemModel3.getLastStr())) {
                                    stringBuffer.append("LastStr=\"" + subItemModel3.getLastStr() + "\" ");
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
                                    if(!"".equals(subItemModel3.getValue()) && !"null".equals(subItemModel3.getValue()) && null != subItemModel3.getValue()) {
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
    
    private LinearLayout createEditText(String name, String company, EditText input, String value, String dataType, int isTop) {
        LinearLayout layout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if(isTop == 0) {
            layoutParams.topMargin=height / 50;
        }else{
            layoutParams.leftMargin=width / 40;
            layoutParams.bottomMargin=height / 50;
        }
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);

        LayoutParams inputParams=new LayoutParams(LayoutParams.MATCH_PARENT, height / 20);
        inputParams.gravity=Gravity.CENTER;
        inputParams.leftMargin=width / 30;
        
        if(name != null && !"".equals(name) && !"null".equals(name)) {
            TextView textName=new TextView(context);
            LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textNameParams.weight = 1;
            textName.setLayoutParams(textNameParams);
            textName.setText(name);
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextColor(Color.parseColor("#414141"));
            textName.setTextSize(16);
            layout.addView(textName);
            inputParams.leftMargin=width / 40;
        }

        input.setLayoutParams(inputParams);
        inputParams.weight = 2;
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
            LayoutParams textCompanyParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            textCompanyParams.leftMargin=width / 40;
            textCompanyParams.rightMargin=width / 40;
            textCompanyParams.weight = 2;
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
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=height / 50;
        layoutParams.bottomMargin=height / 50;
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(layoutParams);

        TextView textName=new TextView(context);
        LayoutParams textNameParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            bundle.putBoolean("isAdd", isAdd);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showRightLayout(int position) {
        rightLayout.removeAllViews();
        List<String> imagePaths=pathMap.get(currentPosition);
        if(null != imagePaths && imagePaths.size() != 0) {
            LinearLayout rowsLayout=new LinearLayout(context);
            RelativeLayout relativeLayout=new RelativeLayout(context);
            for(int i=0; i < imagePaths.size(); i++) {
                if(i % 2 == 0) {
                    rowsLayout=createLinearLayout();
                    rightLayout.addView(rowsLayout);
                }
                relativeLayout=createImage(imagePaths.get(i), i,bigPathMap.get(currentPosition).get(i));
                rowsLayout.addView(relativeLayout);
            }
        }
    }

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout=new LinearLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_VERTICAL;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, height / 100, 0, height / 100);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private RelativeLayout createImage(String path, int id, final String bigPath) {
        RelativeLayout relativeLayout=new RelativeLayout(context);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity=Gravity.CENTER;
        layoutParams.leftMargin=width / 55;
        layoutParams.weight=1;
        relativeLayout.setLayoutParams(layoutParams);
        ImageView imageView=new ImageView(context);
        imageView.setId(id);
        imageView.setOnLongClickListener(this);
        imageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
             // 展示大图片
                BigImageActivity.setViewData(bigPath);
                startActivity(new Intent(CaseTestActivity.this, BigImageActivity.class));
            }
        });
        LayoutParams imageViewParams=new LayoutParams(width / 15 * 4, width / 15 * 4);
        imageView.setLayoutParams(imageViewParams);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        if(!"null".equals(path) && !"".equals(path)) {
            if(path.startsWith("http:")) {
                imageView.setTag(path);
                imageView.setImageResource(R.anim.loading_anim_test);
                ImageListener listener=
                    ImageLoader.getImageListener(imageView, 0, android.R.drawable.ic_menu_delete);
                mImageLoader.get(path, listener, 200, 200);
            } else {
                Bitmap bitmap=CommonUtil.readBitMap(200, path);
                imageView.setImageBitmap(bitmap);
            }
        }
        relativeLayout.addView(imageView);
        return relativeLayout;
    }

    private void initData() {
        if(null != content && !"".equals(content) && !"null".equals(content)) {
            // CommonUtil.appendToFile(content, new File(Environment.getExternalStorageDirectory() + File.separator + "text.txt"));
            XMLCaseDatas(content);
            titleModels=caseList.get(0).getTitleModels();
        } else if(ClientUtil.getCaseParams().size()!=0 && ClientUtil.getCaseParams().getValue(page+"") != null && !"".equals(ClientUtil.getCaseParams().getValue(page+""))){
            XMLCaseDatas(ClientUtil.getCaseParams().getValue(page+""));
            titleModels=caseList.get(0).getTitleModels();
        } else {
            initCaseDatas(departmentId + "case.xml");
            titleModels=caseList.get(page).getTitleModels();
        }
        for(int i=0; i < titleModels.size(); i++) {
            leftList.add(titleModels.get(i).getTitle());
        }
        if(null != imageString && !"".equals(imageString)) {
            try {
                JSONArray jsonArray=new JSONArray(imageString);
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject imageFilesObject=jsonArray.getJSONObject(i);
                    int index=leftList.indexOf(imageFilesObject.getString("test_name"));
                    List<String> paths=pathMap.get(index);
                    List<String> bigPaths=bigPathMap.get(index);
                    List<String> ids=idMap.get(index);
                    if(null == paths) {
                        paths=new ArrayList<String>();
                        paths.add(imageFilesObject.getString("little_pic_url"));
                        pathMap.put(index, paths);
                    } else {
                        paths.add(imageFilesObject.getString("little_pic_url"));
                    }
                    if(null == ids) {
                        ids=new ArrayList<String>();
                        ids.add(imageFilesObject.getString("id"));
                        idMap.put(index, ids);
                    } else {
                        ids.add(imageFilesObject.getString("id"));
                    }
                    if(null == bigPaths) {
                        bigPaths=new ArrayList<String>();
                        bigPaths.add(imageFilesObject.getString("pic_url"));
                        bigPathMap.put(index, bigPaths);
                    } else {
                        bigPaths.add(imageFilesObject.getString("pic_url"));
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode) {
            case 0:
                if(data != null) {
                    String photoPath=data.getStringExtra("bitmap");
                    List<String> paths=pathMap.get(currentPosition);
                    List<String> bigPaths=bigPathMap.get(currentPosition);
                    if(null == paths) {
                        paths=new ArrayList<String>();
                        paths.add(photoPath);
                        pathMap.put(currentPosition, paths);
                    } else {
                        if(!paths.contains(photoPath)) {
                            paths.add(photoPath);
                        }
                    }
                    if(null == bigPaths) {
                        bigPaths=new ArrayList<String>();
                        bigPaths.add(photoPath);
                        bigPathMap.put(currentPosition, bigPaths);
                    } else {
                        if(!bigPaths.contains(photoPath)) {
                            bigPaths.add(photoPath);
                        }
                    }
                    showRightLayout(currentPosition);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    Map<String, String> parmas=new HashMap<String, String>();
                    parmas.put("id", idMap.get(currentPosition).get(data.getIntExtra("index", 0)));
                    parmas.put("accessToken", ClientUtil.getToken());
                    parmas.put("uid", editor.get("uid", ""));
                    CommonUtil.showLoadingDialog(CaseTestActivity.this);
                    OpenApiService.getInstance(CaseTestActivity.this).getDeleteCaseImage(mQueue, parmas,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String arg0) {
                                CommonUtil.closeLodingDialog();
                                try {
                                    JSONObject responses=new JSONObject(arg0);
                                    if(responses.getInt("rtnCode") == 1) {
                                        pathMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                                        idMap.get(currentPosition).remove(data.getIntExtra("index", 0));
                                        showRightLayout(currentPosition);
                                    } else if(responses.getInt("rtnCode") == 10004) {
                                        Toast.makeText(CaseTestActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
                                            .show();
                                        LoginActivity.setHandler(new ConsultationCallbackHandler() {

                                            @Override
                                            public void onSuccess(String rspContent, int statusCode) {
                                            }

                                            @Override
                                            public void onFailure(ConsultationCallbackException exp) {
                                            }
                                        });
                                        startActivity(new Intent(CaseTestActivity.this, LoginActivity.class));
                                    } else {
                                        Toast.makeText(CaseTestActivity.this, responses.getString("rtnMsg"), Toast.LENGTH_SHORT)
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
                                Toast.makeText(CaseTestActivity.this, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                convertView=LayoutInflater.from(CaseTestActivity.this).inflate(R.layout.search_recommend_list_item, null);
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

    @Override
    public boolean onLongClick(View v) {
        Intent intent=new Intent(CaseTestActivity.this, DialogNewActivity.class);
        intent.putExtra("flag", 0);
        intent.putExtra("index", v.getId());
        intent.putExtra("titleText", "删除该图片?");
        startActivityForResult(intent, 1);
        return false;
    }
}
