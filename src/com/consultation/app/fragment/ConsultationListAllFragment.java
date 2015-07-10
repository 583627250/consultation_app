package com.consultation.app.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultation.app.R;

public class ConsultationListAllFragment extends Fragment{

    private View consultationListAllFragment; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    consultationListAllFragment = inflater.inflate(R.layout.consulation_list_all_layout, container, false);
		initLayout();
		return consultationListAllFragment;
	}
	
	private void initLayout(){

	}
}