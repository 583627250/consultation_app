package com.consultation.app.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.action.ViewBaseAction;
import com.consultation.app.adapter.ChooseTextAdapter;
import com.consultation.app.adapter.ChooseTextMiddleAdapter;
import com.consultation.app.util.SelectHospitalDB;

public class LeftFilterView extends LinearLayout implements ViewBaseAction, View.OnClickListener {

	private ListView provinceListView;
	private ListView cityListView;
	private ListView hospitalListView;

	private TextView tvLeftView;
	private TextView tvMiddleView;
	private TextView tvRightView;
	
	private SelectHospitalDB selectHospitalDB;

	private ArrayList<String> provinces = new ArrayList<String>();
	private ArrayList<String> citys = new ArrayList<String>();
	private ArrayList<String> hospitals = new ArrayList<String>();
//	private LinkedList<String> childrenItem = new LinkedList<String>();
//	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
	private ChooseTextAdapter provinceListViewAdapter;
	private ChooseTextMiddleAdapter cityListViewAdapter;
	private ChooseTextAdapter hospitalListViewAdapter;
	private OnItemSelectListener mOnSelectListener;
	private int tProvincePosition = 0;
	private int tCityPosition = 0;
	private int tHospitalPosition = 0;
	private String showString = "不限";

	private enum showSubwayOrBcd {
		SUBWAY, BCD
	}

	private showSubwayOrBcd displayItem = showSubwayOrBcd.BCD;

	public LeftFilterView(Context context) {
		super(context);
		init(context);
	}

	public LeftFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void add(Cursor cursor){
        selectHospitalDB.insert("北京市","西城区","协和医院");
        selectHospitalDB.insert("北京市","西城区","儿童医院");
        selectHospitalDB.insert("北京市","海淀区","空军医院");
        selectHospitalDB.insert("北京市","昌平区","积水潭医院医院");
        selectHospitalDB.insert("安徽省","合肥市","省立儿童医");
        selectHospitalDB.insert("安徽省","合肥市","安医附院");
        selectHospitalDB.insert("安徽省","合肥市","妇幼保健院");
        selectHospitalDB.insert("安徽省","六安市","人民医院");
        selectHospitalDB.insert("安徽省","六安市","妇幼保健院");
        cursor.requery();
    }
	
	private ArrayList<String> getCitys(String provice){
	    ArrayList<String> temp = new ArrayList<String>();
        Cursor cursorCitys = selectHospitalDB.selectByProvince(provice);
        for(int j=0; j < cursorCitys.getCount(); j++) {
            cursorCitys.moveToPosition(j);
            temp.add(cursorCitys.getString(2));
        }
        return temp;
	}
	
	private ArrayList<String> getHospitals(String city){
        ArrayList<String> temp = new ArrayList<String>();
        Cursor cursorCitys = selectHospitalDB.selectByCity(city);
        for(int j=0; j < cursorCitys.getCount(); j++) {
            cursorCitys.moveToPosition(j);
            temp.add(cursorCitys.getString(3));
        }
        return temp;
    }

	private void init(final Context context) {
	    selectHospitalDB = new SelectHospitalDB(context);   
	    Cursor cursor = selectHospitalDB.selectAll();
//	    add(cursor);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.choose_view_left, this, true);
		provinceListView = (ListView) findViewById(R.id.listView);
		cityListView = (ListView) findViewById(R.id.listView2);
		hospitalListView = (ListView) findViewById(R.id.listView3);

		tvLeftView = (TextView) findViewById(R.id.left_tv);
		tvLeftView.setTextSize(17);
		tvLeftView.setOnClickListener(this);
		tvMiddleView = (TextView) findViewById(R.id.middle_tv);
		tvMiddleView.setTextSize(17);
		tvMiddleView.setOnClickListener(this);
		tvRightView = (TextView) findViewById(R.id.right_tv);
		tvRightView.setTextSize(17);
		tvRightView.setOnClickListener(this);

		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_left));
		
        List<String> province = new ArrayList<String>(); 
        for(int i=0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            province.add(cursor.getString(1));
        }
        for(String string:province){
            if(!provinces.contains(string)){  
                provinces.add(string);  
            }
        }  

		provinceListViewAdapter = new ChooseTextAdapter(context, provinces, R.drawable.choose_eara_item_selector);
		provinceListViewAdapter.setSelectedPositionNoNotify(tProvincePosition);
		provinceListView.setAdapter(provinceListViewAdapter);
		provinceListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				if (position < provinces.size()) {
					citys.clear();
					for(String string:getCitys(provinces.get(position))){
		                if(!citys.contains(string)){  
		                    citys.add(string);  
		                }
		            }
					cityListViewAdapter.setSelectedPositionNoNotify(tCityPosition);
					cityListViewAdapter.notifyDataSetChanged();
					hospitals.clear();
					for(String string:getHospitals(citys.get(tCityPosition))){
                        if(!hospitals.contains(string)){  
                            hospitals.add(string);
                        }
                    }
                    hospitalListViewAdapter.notifyDataSetChanged();
				}
			}
		});
		if (tProvincePosition < provinces.size()){
		    for(String string:getCitys(provinces.get(tProvincePosition))){
	            if(!citys.contains(string)){  
	                citys.add(string);  
	            }
	        }
		}
		cityListViewAdapter = new ChooseTextMiddleAdapter(context, citys, R.drawable.choose_plate_middle_item_selector);
		cityListViewAdapter.setSelectedPositionNoNotify(tCityPosition);
		cityListView.setAdapter(cityListViewAdapter);
		cityListViewAdapter.setOnItemClickListener(new ChooseTextMiddleAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, final int position) {
			    if (position < citys.size()) {
                    hospitals.clear();
                    for(String string:getHospitals(citys.get(position))){
                        if(!hospitals.contains(string)){  
                            hospitals.add(string);
                        }
                    }
                    hospitalListViewAdapter.notifyDataSetChanged();
                }
			}
		});
		if (tCityPosition < citys.size()){
		    for(String string:getHospitals(citys.get(tCityPosition))){
                if(!hospitals.contains(string)){  
                    hospitals.add(string);
                }
            }
		}
		hospitalListViewAdapter = new ChooseTextAdapter(context, hospitals, R.drawable.choose_plate_item_selector);
		hospitalListViewAdapter.setSelectedPositionNoNotify(tHospitalPosition);
		hospitalListView.setAdapter(hospitalListViewAdapter);
		hospitalListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, final int position) {
                 showString = hospitals.get(position);
                 if (mOnSelectListener != null) {
                     mOnSelectListener.getValue(showString);
                 }
            }
        });
        if (tHospitalPosition < hospitals.size())
			showString = hospitals.get(tHospitalPosition);
		if (showString.contains("不限")) {
			showString = showString.replace("不限", "");
		}
		setDefaultSelect();
	}

	public void setDefaultSelect() {
		provinceListView.setSelection(tProvincePosition);
		cityListView.setSelection(tCityPosition);
		hospitalListView.setSelection(tHospitalPosition);
	}

	public String getShowText() {
		return showString;
	}

	public void setOnSelectListener(OnItemSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnItemSelectListener {
		public void getValue(String showText);
	}

	@Override
	public void hideMenu() {

	}

	@Override
	public void showMenu() {

	}

	@Override
	public void onClick(View v) {
		if (v == tvLeftView) {
			if (displayItem == showSubwayOrBcd.BCD) {

			} else {

			}
		} else if (v == tvMiddleView) {
//			hospitalListViewAdapter = new ChooseTextAdapter(getContext(), provinces, R.drawable.choose_eara_item_selector);
//			hospitalListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
//			provinceListView.setAdapter(hospitalListViewAdapter);
//			hospitalListViewAdapter.notifyDataSetInvalidated();
//			hospitalListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(View view, int position) {
//					if (position < children.size()) {
//						childrenItem.clear();
//						childrenItem.addAll(children.get(position));
//						cityListViewAdapter.notifyDataSetChanged();
//					}
//				}
//			});
//			if (tEaraPosition < children.size())
//				childrenItem.addAll(children.get(tEaraPosition));
//			cityListViewAdapter = new ChooseTextAdapter(getContext(), childrenItem, R.drawable.choose_plate_item_selector);
//			cityListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
//			cityListView.setAdapter(cityListViewAdapter);
//			cityListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {
//
//				@Override
//				public void onItemClick(View view, final int position) {
//					showString = childrenItem.get(position);
//					if (mOnSelectListener != null) {
//						mOnSelectListener.getValue(showString);
//					}
//
//				}
//			});
//			if (tBlockPosition < childrenItem.size())
//				showString = childrenItem.get(tBlockPosition);
//			if (showString.contains("不限")) {
//				showString = showString.replace("不限", "");
//			}
//			setDefaultSelect();
		} else if(v == tvRightView){
		    
		}
	}

}
