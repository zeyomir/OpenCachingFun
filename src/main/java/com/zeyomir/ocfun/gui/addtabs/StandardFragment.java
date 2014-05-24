package com.zeyomir.ocfun.gui.addtabs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.gui.Add;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.RadioButton;

public class StandardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("standard", "on create view");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_standard, container, false);
    }

    @Override
    public void onStart() {
        Log.i("standard", "on start");
        super.onStart();
        Add a = ((Add) getActivity());
        a.setCurrentFragmentTag(R.id.add_standard);
        ((RadioButton) a.findViewById(R.id.radioButton1)).setChecked(true);
    }
}
