package com.consultation.app.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultation.app.R;

public class ConsultationListDoingFragment extends Fragment{

    private View consultationListDoingFragment; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    consultationListDoingFragment = inflater.inflate(R.layout.consulation_list_doing_layout, container, false);
		initLayout();
		return consultationListDoingFragment;
	}
	
	private void initLayout(){

	}
}