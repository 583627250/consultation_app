package com.consultation.app.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultation.app.R;

public class ConsultationListHistoryFragment extends Fragment{

    private View consultationListHistoryFragment; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    consultationListHistoryFragment = inflater.inflate(R.layout.consulation_list_history_layout, container, false);
		initLayout();
		return consultationListHistoryFragment;
	}
	
	private void initLayout(){

	}
}