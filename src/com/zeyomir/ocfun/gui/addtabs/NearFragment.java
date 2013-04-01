package com.zeyomir.ocfun.gui.addtabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.gui.Add;

public class NearFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.add_near, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		Add a = ((Add) getActivity());
		a.setCurrentFragmentTag(R.id.add_near);
		((RadioButton) a.findViewById(R.id.radioButton1)).setChecked(true);
	}
}
