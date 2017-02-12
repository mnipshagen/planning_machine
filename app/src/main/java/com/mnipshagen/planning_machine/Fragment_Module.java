package com.mnipshagen.planning_machine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipsh on 09/02/2017.
 */

public class Fragment_Module extends Fragment {

    public Fragment_Module() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String module_code = args.getString("Module");
        int title_id = getActivity().getResources().getIdentifier(module_code+"_title","string",getActivity().getPackageName());

        ((Activity_Main)getActivity()).setActionBarTitle(title_id);

        View v = inflater.inflate(R.layout.module, container, false);

        SQLiteDatabase db = new SQL_Database(getActivity()).getReadableDatabase();
        String[] moduleData = {
                SQL_Database.MODULE_COLUMN_NAME,
                SQL_Database.MODULE_COLUMN_CODE,
                SQL_Database.MODULE_COLUMN_ECTS_COMP,
                SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP,
                SQL_Database.MODULE_COLUMN_ECTS,
                SQL_Database.MODULE_COLUMN_GRADE
        };
        String moduleSelection = SQL_Database.MODULE_COLUMN_CODE + " = " + "'" + module_code + "'";

        Cursor module = db.query(SQL_Database.MODULE_TABLE_NAME, moduleData, moduleSelection, null, null, null, null);

        String[] courseData = {
                SQL_Database.COURSES_COLUMN_ID,
                SQL_Database.COURSES_COLUMN_COURSE,
                SQL_Database.COURSES_COLUMN_ECTS,
                SQL_Database.COURSES_COLUMN_GRADE
        };
        String courseSelection = SQL_Database.COURSES_COLUMN_MODULE + " = " + "'" + module_code + "'";

        Cursor courses = db.query(SQL_Database.COURSES_TABLE_NAME, courseData, courseSelection, null, null, null, null);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1, ""));
        entries.add(new PieEntry(2, ""));
        entries.add(new PieEntry(3, ""));
        PieDataSet pieSet = new PieDataSet(entries, "");
        pieSet.setDrawValues(false);
        int[] col =
                {
                        getResources().getColor(R.color.markCompleted),
                        getResources().getColor(R.color.markMarked),
                        getResources().getColor(R.color.markInProgress)
                };
        pieSet.setColors(col);
        PieData pieData = new PieData(pieSet);

        com.github.mikephil.charting.charts.PieChart graph =
                (com.github.mikephil.charting.charts.PieChart) v.findViewById(R.id.modulePieChart);
        graph.setData(pieData);
        graph.setDrawHoleEnabled(false);
        // angle to -90-angle of green area, so green starts to draw at the top to the left
        graph.setRotationAngle(-90-(8f/20*360));
        Description desc = new Description();
        desc.setText("");
        graph.setDescription(desc);
        graph.getLegend().setEnabled(false);

        ((TextView)v.findViewById(R.id.moduleGrade)).setText(String.format("%.2f", 0.015));

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.moduleRecycler);

        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        Adapter_Module adapter = new Adapter_Module(getActivity(), courses);
        rv.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);

        return v;
    }
}
