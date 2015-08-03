package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.util.AccountUtil;
import com.consultation.app.util.SharePreferencesEditor;

public class SelectPatientActivity extends Activity {

    private LinearLayout back_layout;

    private TextView back_text, search_text, hide_text;

    private EditText searchEditText;
    
    private ListView historyListView;
    
    private MyAdapter myAdapter;
    
    private List<String> historyList = new ArrayList<String>();
    
    private ViewHolder holder;
    
    private SharePreferencesEditor editor;
    
    private boolean isHasHistory = false;

    private TextView deleteText;
    
    private boolean isHave = false;
    
    private ImageView deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_recommend_list_search_layout);
        initDate();
        initView();
    }

    private void initDate() {
        editor = new SharePreferencesEditor(SelectPatientActivity.this);
        if(editor.get("select_patient_history",null) != null && !"".equals(editor.get("select_patient_history",null))){
            isHasHistory = true;
            String[] historys = editor.get("select_patient_history",null).split(",");
            for(String string: historys) {
                historyList.add(string);
            }
        }
    }

    private void initView() {
        back_layout=(LinearLayout)findViewById(R.id.header_layout_lift);
        back_layout.setVisibility(View.VISIBLE);
        hide_text=(TextView)findViewById(R.id.header_text);
        hide_text.setTextSize(20);
        search_text=(TextView)findViewById(R.id.header_right);
        search_text.setTextSize(18);
        back_text=(TextView)findViewById(R.id.header_text_lift);
        back_text.setTextSize(18);
        searchEditText=(EditText)findViewById(R.id.header_edit);
        searchEditText.setTextSize(16);
        searchEditText.setHint("请输入患者手机号码");
        searchEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        searchEditText.setHintTextColor(Color.parseColor("#D3D3D3"));
        deleteBtn = (ImageView)findViewById(R.id.header_image_delete);
        deleteBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });
        deleteText = (TextView)findViewById(R.id.recommend_search_history_listView_delete);
        deleteText.setTextSize(16);
        if(!isHasHistory){
            deleteText.setVisibility(View.GONE);
        }
        deleteText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.put("select_patient_history", "");
                historyList.clear();
                myAdapter.notifyDataSetChanged();
                isHasHistory = false;
                deleteText.setVisibility(View.GONE);
            }
        });
        
        
        back_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {     
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );   
                } 
                finish();
            }
        });
        search_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String editTextString = searchEditText.getText().toString().trim();
                if(AccountUtil.isPhoneNum(editTextString)){
                    Toast.makeText(SelectPatientActivity.this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(null != editTextString && !"".equals(editTextString)){
                    if(editor.get("select_patient_history",null) == null || "".equals(editor.get("select_patient_history",null))){
                        editor.put("select_patient_history", editTextString);
                    }else{
                        for(String temp: editor.get("select_patient_history",null).split(",")) {
                            if(temp.equals(editTextString)){
                                isHave = true;
                            }
                        }
                        if(!isHave){
                            editor.put("select_patient_history", editor.get("select_patient_history",null)+","+editTextString);
                        }
                        isHave = false;
                    }
                    Intent intent = new Intent(SelectPatientActivity.this, SelectPatientResultActivity.class);
                    intent.putExtra("nameString", editTextString);
                    startActivityForResult(intent, 0);
                    deleteText.setVisibility(View.VISIBLE);
                    historyList.add(editTextString);
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        myAdapter = new MyAdapter();
        historyListView=(ListView)findViewById(R.id.recommend_search_history_listView);
        historyListView.setAdapter(myAdapter);
        historyListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectPatientActivity.this, SelectPatientResultActivity.class);
                intent.putExtra("nameString", historyList.get(position));
                startActivityForResult(intent, 0);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            setResult(Activity.RESULT_OK, data);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private static class ViewHolder {

      TextView searchText;
  }

  private class MyAdapter extends BaseAdapter {

      @Override
      public int getCount() {
          return historyList.size();
      }

      @Override
      public Object getItem(int location) {
          return historyList.get(location);
      }

      @Override
      public long getItemId(int position) {
          return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
          if(convertView == null) {
              convertView=LayoutInflater.from(SelectPatientActivity.this).inflate(R.layout.search_recommend_list_item, null);
              holder=new ViewHolder();
              holder.searchText=(TextView)convertView.findViewById(R.id.search_recommend_list_item_text);
              convertView.setTag(holder);
          }else {
              holder=(ViewHolder)convertView.getTag();
          }
          holder.searchText.setTextSize(16);
          holder.searchText.setText(historyList.get(position));
          return convertView;
      }
  }
}
