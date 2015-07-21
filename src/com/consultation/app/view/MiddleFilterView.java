package com.consultation.app.view;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
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

public class MiddleFilterView extends LinearLayout implements ViewBaseAction, View.OnClickListener {

	private ListView regionListView;
	private ListView plateListView;

	private TextView tvLeftView;
	private TextView tvRightView;

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

	public MiddleFilterView(Context context) {
		super(context);
		init(context);
	}

	public MiddleFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(final Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.choose_view_midd, this, true);
		regionListView = (ListView) findViewById(R.id.listView);
		plateListView = (ListView) findViewById(R.id.listView2);

		tvLeftView = (TextView) findViewById(R.id.left_tv);
		tvLeftView.setTextSize(17);
		tvLeftView.setOnClickListener(this);
		tvRightView = (TextView) findViewById(R.id.right_tv);
		tvLeftView.setTextSize(17);
		tvRightView.setOnClickListener(this);

		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_mid));

		for (int i = 0; i < 8; i++) {
			groups.add(i + "行");
			LinkedList<String> tItem = new LinkedList<String>();
			for (int j = 0; j < 12; j++) {

				tItem.add(i + "行" + j + "列");

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
