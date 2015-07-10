package com.consultation.app.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.consultation.app.R;
import com.consultation.app.action.ViewBaseAction;
import com.consultation.app.adapter.ChooseTextAdapter;
import com.consultation.app.util.SelectHospitalDB;

public class LeftFilterView extends LinearLayout implements ViewBaseAction, View.OnClickListener {

	private ListView regionListView;
	private ListView plateListView;

	private TextView tvLeftView;
	private TextView tvRightView;
	
	private SelectHospitalDB selectHospitalDB;

	private ArrayList<String> groups = new ArrayList<String>();
	private LinkedList<String> childrenItem = new LinkedList<String>();
	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
	private ChooseTextAdapter plateListViewAdapter;
	private ChooseTextAdapter earaListViewAdapter;
	private OnItemSelectListener mOnSelectListener;
	private int tEaraPosition = 0;
	private int tBlockPosition = 0;
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

	public void updateShowText(Context context, String showArea, String showBlock) {
		if (showArea == null || showBlock == null) {
			return;
		}
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).equals(showArea)) {
				earaListViewAdapter.setSelectedPosition(i);
				childrenItem.clear();
				if (i < children.size()) {
					childrenItem.addAll(children.get(i));
				}
				tEaraPosition = i;
				break;
			}
		}
		for (int j = 0; j < childrenItem.size(); j++) {
			if (childrenItem.get(j).replace("不限", "").equals(showBlock.trim())) {
				plateListViewAdapter.setSelectedPosition(j);
				tBlockPosition = j;
				break;
			}
		}
		setDefaultSelect();
	}
	
	public void add(Cursor cursor){
        selectHospitalDB.insert("北京","协和医院");
        selectHospitalDB.insert("北京","儿童医院");
        selectHospitalDB.insert("北京","空军医院");
        selectHospitalDB.insert("合肥","省立儿童医院");
        selectHospitalDB.insert("合肥","安医附院");
        selectHospitalDB.insert("合肥","哈哈哈医院");
        selectHospitalDB.insert("六安","人民医院");
        selectHospitalDB.insert("六安","妇幼保健院");
        cursor.requery();
    }

	private void init(final Context context) {
	    selectHospitalDB = new SelectHospitalDB(context);   
	    Cursor cursor = selectHospitalDB.selectAll();
//	    add(cursor);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.choose_view_region, this, true);
		regionListView = (ListView) findViewById(R.id.listView);
		plateListView = (ListView) findViewById(R.id.listView2);

		tvLeftView = (TextView) findViewById(R.id.left_tv);
		tvLeftView.setTextSize(17);
		tvLeftView.setOnClickListener(this);
		tvRightView = (TextView) findViewById(R.id.right_tv);
		tvRightView.setTextSize(17);
		tvRightView.setOnClickListener(this);

		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_left));

		
        List<String> citys=new ArrayList<String>(); 
        for(int i=0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            citys.add(cursor.getString(1));
        }
        for(String city:citys){
            if(!groups.contains(city)){  
                groups.add(city);  
            }
        }  
		for (int i = 0; i < groups.size(); i++) {
		    Cursor cursorHospiatals = selectHospitalDB.selectByCity(groups.get(i));
		    LinkedList<String> tItem = new LinkedList<String>();
		    for(int j=0; j < cursorHospiatals.getCount(); j++) {
		        cursorHospiatals.moveToPosition(j);
	            tItem.add(cursorHospiatals.getString(2));
	        }
			children.put(i, tItem);
		}

		earaListViewAdapter = new ChooseTextAdapter(context, groups, R.drawable.choose_eara_item_selector);
		earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
		regionListView.setAdapter(earaListViewAdapter);
		earaListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				if (position < children.size()) {
					childrenItem.clear();
					childrenItem.addAll(children.get(position));
					plateListViewAdapter.notifyDataSetChanged();
				}
			}
		});
		if (tEaraPosition < children.size())
			childrenItem.addAll(children.get(tEaraPosition));
		plateListViewAdapter = new ChooseTextAdapter(context, childrenItem, R.drawable.choose_plate_item_selector);
		plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
		plateListView.setAdapter(plateListViewAdapter);
		plateListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, final int position) {

				showString = childrenItem.get(position);
				if (mOnSelectListener != null) {
					mOnSelectListener.getValue(showString);
				}

			}
		});
		if (tBlockPosition < childrenItem.size())
			showString = childrenItem.get(tBlockPosition);
		if (showString.contains("不限")) {
			showString = showString.replace("不限", "");
		}
		setDefaultSelect();

	}

	public void setDefaultSelect() {
		regionListView.setSelection(tEaraPosition);
		plateListView.setSelection(tBlockPosition);
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
		} else if (v == tvRightView) {
			earaListViewAdapter = new ChooseTextAdapter(getContext(), groups, R.drawable.choose_eara_item_selector);
			earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
			regionListView.setAdapter(earaListViewAdapter);
			earaListViewAdapter.notifyDataSetInvalidated();
			earaListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					if (position < children.size()) {
						childrenItem.clear();
						childrenItem.addAll(children.get(position));
						plateListViewAdapter.notifyDataSetChanged();
					}
				}
			});
			if (tEaraPosition < children.size())
				childrenItem.addAll(children.get(tEaraPosition));
			plateListViewAdapter = new ChooseTextAdapter(getContext(), childrenItem, R.drawable.choose_plate_item_selector);
			plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
			plateListView.setAdapter(plateListViewAdapter);
			plateListViewAdapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, final int position) {
					showString = childrenItem.get(position);
					if (mOnSelectListener != null) {
						mOnSelectListener.getValue(showString);
					}

				}
			});
			if (tBlockPosition < childrenItem.size())
				showString = childrenItem.get(tBlockPosition);
			if (showString.contains("不限")) {
				showString = showString.replace("不限", "");
			}
			setDefaultSelect();
		}
	}

}
