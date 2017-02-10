package com.mnipshagen.planning_machine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nipsh on 02/02/2017.
 */

public class SearchUpperFragment extends Fragment {

    public SearchUpperFragment() { }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_upper_fragment, container, false);

        return v;
    }
}
