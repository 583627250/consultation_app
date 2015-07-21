package com.consultation.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultation.app.R;

public class PatientConsultationDiscussionFragment extends Fragment{

    private View consultationDiscussionFragment; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    consultationDiscussionFragment = inflater.inflate(R.layout.consulation_list_file_layout, container, false);
		initLayout();
		return consultationDiscussionFragment;
	}
	
	private void initLayout(){

	}
}