package com.consultation.app.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultation.app.R;

public class ConsultationListWaitFragment extends Fragment{

    private View consultationListWaitFragment; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    consultationListWaitFragment = inflater.inflate(R.layout.consulation_list_wait_layout, container, false);
		initLayout();
		return consultationListWaitFragment;
	}
	
	private void initLayout(){

	}
}