package com.mnipshagen.planning_machine;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class Fragment_Overview extends Fragment {

    public Fragment_Overview () { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.overview, container, false);

        // upper screen half
        //noinspection ResourceType
        ((TextView) v.findViewById(R.id.overviewUpperText))
                .setText("ColorString: " + getResources().getString(R.color.markCompleted)
                        + "\nColor ID: " + R.color.markCompleted
                        + "\nColorRBG: " + Color.rgb(92,184,92)
                        + "\nColorCol: " + getResources().getColor(R.color.markCompleted));

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(8f, "8"));
        entries.add(new PieEntry(4f, "4"));
        entries.add(new PieEntry(8f, "8"));
        PieDataSet pieSet = new PieDataSet(entries, "");
        pieSet.setDrawValues(false);
        int[] col = {   getResources().getColor(R.color.markCompleted),
                        getResources().getColor(R.color.markMarked),
                        getResources().getColor(R.color.markInProgress) };
        pieSet.setColors(col);
        PieData pieData = new PieData(pieSet);
        com.github.mikephil.charting.charts.PieChart graph =
                (com.github.mikephil.charting.charts.PieChart) v.findViewById(R.id.overviewPieChart);

        graph.setData(pieData);
        graph.setDrawHoleEnabled(false);
        // angle to -90-angle of green area, so green starts to draw at the top to the left
        graph.setRotationAngle(-90-(8f/20*360));
        Description desc = new Description();
        desc.setText("");
        graph.setDescription(desc);
        graph.getLegend().setEnabled(false);

        // lower screen half
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.overviewRecycler);

        SQLiteDatabase db = new SQL_Database(getActivity()).getReadableDatabase();
        String[] columns = {
                SQL_Database.MODULE_COLUMN_ID,
                SQL_Database.MODULE_COLUMN_NAME,
                SQL_Database.MODULE_COLUMN_CODE,
                SQL_Database.MODULE_COLUMN_ECTS,
                SQL_Database.MODULE_COLUMN_GRADE
        };

        final Cursor cursor = db.query(
                SQL_Database.MODULE_TABLE_NAME,
                columns, null, null,
                null, null, null);


        Log.v("Cursor",DatabaseUtils.dumpCursorToString(cursor));

        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        Adapter_Overview adapter = new Adapter_Overview(getActivity(), cursor);
        rv.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), rv ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                cursor.moveToPosition(position);
                String code = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_CODE));

                Bundle args = new Bundle();
                args.putString("Module", code);
                Fragment content = new Fragment_Module();
                content.setArguments(args);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().addToBackStack("Call Module").replace(R.id.content, content).commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        return v;
    }

}
