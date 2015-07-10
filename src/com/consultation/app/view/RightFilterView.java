package com.consultation.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.consultation.app.R;
import com.consultation.app.action.ViewBaseAction;
import com.consultation.app.adapter.ChooseTextAdapter;

public class RightFilterView extends RelativeLayout implements ViewBaseAction {

	private ListView mListView;
	private String[] items = null;
	private OnSelectListener mOnSelectListener;
	private ChooseTextAdapter adapter;
	private String showText = "item1";

	public String getShowText() {
		return showText;
	}

	public RightFilterView(Context context, String items[]) {
		super(context);
		this.items = items;
		init(context);
	}

	public RightFilterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RightFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.choose_view_distance, this, true);
		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_right));
		mListView = (ListView) findViewById(R.id.listView);
		adapter = new ChooseTextAdapter(context, items, R.drawable.choose_eara_item_selector);

		mListView.setAdapter(adapter);
		adapter.setOnItemClickListener(new ChooseTextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (mOnSelectListener != null) {
					showText = items[position];
					mOnSelectListener.getValue(items[position], items[position]);
				}
			}
		});
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String distance, String showText);
	}

	@Override
	public void hideMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showMenu() {
		// TODO Auto-generated method stub

	}
}
