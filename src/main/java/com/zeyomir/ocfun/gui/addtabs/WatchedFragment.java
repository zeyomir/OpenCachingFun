package com.zeyomir.ocfun.gui.addtabs;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.gui.Add;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

public class WatchedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_watched, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Add a = ((Add) getActivity());
        a.setCurrentFragmentTag(R.id.add_watched);
    }
}
