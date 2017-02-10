package com.mnipshagen.planning_machine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipsh on 09/02/2017.
 */

public class Fragment_Overview extends Fragment {

    public Fragment_Overview () { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.overview, container, false);

        // upper screen half
        ((TextView) v.findViewById(R.id.overviewUpperText)).setText("Overall Grade");

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(8f, "Green"));
        entries.add(new PieEntry(8f, "Blue"));
        entries.add(new PieEntry(4f, "Gray"));
        PieDataSet pieSet = new PieDataSet(entries, "Credits");
        pieSet.setColors(new int[] {R.color.markCompleted, R.color.markInProgress, R.color.markedMarked});
        PieData pieData = new PieData(pieSet);

        ((com.github.mikephil.charting.charts.PieChart)
                v.findViewById(R.id.overviewPieChart)).setData(pieData);
        (v.findViewById(R.id.overviewPieChart)).invalidate();

        // lower screen half
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.lower_recycler);

        List<Module> moduleData = Module.prepareModules();

        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        Adapter_Overview adapter = new Adapter_Overview(getActivity(), moduleData);
        rv.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);

        return v;
    }
}
