package com.zeyomir.ocfun.gui.addtabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.gui.Add;

public class StandardFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("standard","on create view");
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.add_standard, container, false);
	}

	@Override
	public void onStart() {
		Log.i("standard","on start");
		super.onStart();
		Add a = ((Add) getActivity());
		a.setCurrentFragmentTag(R.id.add_standard);
		((RadioButton) a.findViewById(R.id.radioButton1)).setChecked(true);
	}
}
