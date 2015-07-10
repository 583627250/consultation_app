package com.consultation.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultation.app.R;

public class ConsultationListFileFragment extends Fragment{

    private View consultationListFileFragment; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    consultationListFileFragment = inflater.inflate(R.layout.consulation_list_file_layout, container, false);
		initLayout();
		return consultationListFileFragment;
	}
	
	private void initLayout(){

	}
}