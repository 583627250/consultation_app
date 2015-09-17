package com.consultation.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultation.app.R;
import com.consultation.app.model.ContactTo;
import com.consultation.app.util.Trans2PinYin;

@SuppressLint("DefaultLocale")
public class ContactActivity extends Activity {

    private static final String[] PHONES_PROJECTION=new String[]{Phone.DISPLAY_NAME, Phone.NUMBER};

    private static final int PHONES_DISPLAY_NAME_INDEX=0;

    private static final int PHONES_NUMBER_INDEX=1;

    private List<ContactTo> contacts=new ArrayList<ContactTo>();

    private List<ContactTo> temp=new ArrayList<ContactTo>();

    private MyAdapter myAdapter;

    private ViewHolder holder;

    private ListView listView;

    private TextView title_text;

    private LinearLayout back_layout;

    private TextView back_text;

    private Button button;

    private EditText editText;
    
    private ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);
        initData();
        initView();
    }

    private void initData() {
        // 获取联系人数据
        ContentResolver resolver=ContactActivity.this.getContentResolver();
        Cursor phoneCursor=resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if(phoneCursor != null) {
            while(phoneCursor.moveToNext()) {
                String phoneNumber=phoneCursor.getString(PHONES_NUMBER_INDEX);
                if(TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName=phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                ContactTo contactTo=new ContactTo();
                contactTo.setCheck(false);
                contactTo.setName(contactName);
                contactTo.setPhone(phoneNumber);
                contacts.add(contactTo);
                temp.add(contactTo);
            }
            phoneCursor.close();
        }
    }

    private void initView() {
        title_text=(TextView)findViewById(R.id.header_text);
        title_text.setText("联系人");
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
        
        delete = (ImageView)findViewById(R.id.contact_image_delete);
        delete.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText=(EditText)findViewById(R.id.contact_edit);
        editText.setTextSize(18);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getList(s.toString());
            }
        });

        button=(Button)findViewById(R.id.contact_btn);
        button.setTextSize(18);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                StringBuffer buffer=new StringBuffer();
                for(int i=0; i < contacts.size(); i++) {
                    if(temp.get(i).isCheck()) {
                        buffer.append(temp.get(i).getPhone()).append(" ");
                    }
                }
                if("".equals(buffer.toString()) || buffer.toString() == null) {
                    Toast.makeText(ContactActivity.this, "请选择联系人", Toast.LENGTH_LONG).show();
                    return;
                }
                String str=buffer.substring(0, buffer.length() - 1);
                intent.putExtra("phone", str);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        myAdapter=new MyAdapter();
        listView=(ListView)findViewById(R.id.contact_listView);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void getList(String name) {
        temp.clear();
        if("".equals(name) || name == null) {
            for(int i=0; i < contacts.size(); i++) {
                temp.add(contacts.get(i));
            }
        } else {
            for(int i=0; i < contacts.size(); i++) {
                String strPinYin=Trans2PinYin.trans2PinYin(contacts.get(i).getName());
                if(contacts.get(i).getName().contains(name) || strPinYin.startsWith(name.toLowerCase())) {
                    temp.add(contacts.get(i));
                }
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    private class ViewHolder {

        TextView phone;

        TextView name;

        CheckBox ischeck;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return temp.size();
        }

        @Override
        public Object getItem(int location) {
            return temp.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                holder=new ViewHolder();
                convertView=LayoutInflater.from(ContactActivity.this).inflate(R.layout.contact_list_item, null);
                holder.phone=(TextView)convertView.findViewById(R.id.contact_number);
                holder.name=(TextView)convertView.findViewById(R.id.contact_name);
                holder.ischeck=(CheckBox)convertView.findViewById(R.id.contact_isCheck);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            holder.phone.setText(temp.get(position).getPhone());
            holder.phone.setTextSize(20);
            holder.name.setText(temp.get(position).getName());
            holder.name.setTextSize(20);
            holder.ischeck.setChecked(temp.get(position).isCheck());
            holder.ischeck.setTextSize(20);
            holder.ischeck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    temp.get(position).setCheck(isChecked);
                }
            });
            return convertView;
        }
    }

}
