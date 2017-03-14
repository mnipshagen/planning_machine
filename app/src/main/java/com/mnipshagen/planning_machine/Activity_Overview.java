package com.mnipshagen.planning_machine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

import java.util.ArrayList;
import java.util.List;

/**
 * The overview activity which is also the launch activity
 * Here all modules are displayed and the overall progress towards the bachelor
 */
public class Activity_Overview extends Activity_Base implements LoaderManager.LoaderCallbacks<Cursor> {

    // the bachelor credits and the bachelor thesis credits are fix.
    private final int BACHELOR_CREDITS = 180;
    private final int THESIS_CREDITS = 12;
    private Cursor cursor;
    private RecyclerView rv;
    private Adapter_Overview adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        setActionBarTitle(R.string.title_overview);

        /* The lower half is now */
        rv = (RecyclerView) findViewById(R.id.overviewRecycler);
        // dump the cursor into the console for debug reasons
        // Log.v("Cursor", DatabaseUtils.dumpCursorToString(cursor));

        // MAKE IT PRETTY MAKE IT NICE
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        // initialise the adapter
        adapter = new Adapter_Overview(cursor, this);
        rv.setAdapter(adapter);
        // and the layoutmanager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        // when we touch the module entries something should happen
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // get all the data for the module from the cursor and open the module
                        cursor.moveToPosition(position);
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_NAME));
                        String code = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_CODE));
                        int compECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_COMP));
                        int optcompECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP));
                        boolean significant = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_SIGNIFICANT)) == 1;
                        Intent intent = new Intent(Activity_Overview.this, Activity_Module.class);
                        intent.putExtra("Name",name);
                        intent.putExtra("Module", code);
                        intent.putExtra("compECTS", compECTS);
                        intent.putExtra("optcompECTS", optcompECTS);
                        intent.putExtra("significant", significant);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //TODO
                    }
                }));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cursor == null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    private void initGraph() {

        // calculate the overall achieved and in progress credits
        // by going through the modules
        int ach_ects = 0;
        int ip_ects = 0;
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            ach_ects += cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS));
            ip_ects += cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_IPECTS));
        }
        // find the 5 best grades! (for now until other algorithms are implemented)
        List<Float> grades = new ArrayList<>();
        List<Boolean> significance = new ArrayList<>();
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            float g = cursor.getFloat(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_GRADE));
            boolean sign = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_SIGNIFICANT)) == 1;
            sortIn(g, grades, sign, significance);
        }
        float grade = 0f;
        for(float g : grades) grade += g;
        grade /= (float) grades.size();

        /* Set up the pie chart */
        // the entries are the thesis itself, the credits still to do, the in progress credits
        // and the already achieved credits
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(THESIS_CREDITS, String.valueOf(THESIS_CREDITS)));
        int todo_ects = BACHELOR_CREDITS - ach_ects - ip_ects - THESIS_CREDITS;
        String todo_str = String.valueOf(todo_ects);
        if(todo_ects <= 0) {
            todo_ects = 0;
            todo_str = "";
        }
        if(ip_ects > (BACHELOR_CREDITS-THESIS_CREDITS-ach_ects)) {
            ip_ects = BACHELOR_CREDITS-THESIS_CREDITS-ach_ects;
        }
        String ip_str = String.valueOf(ip_ects);
        if(ip_ects <= 0) {
            ip_str="";
        }
        if(ach_ects > BACHELOR_CREDITS-THESIS_CREDITS) {
            ach_ects = BACHELOR_CREDITS-THESIS_CREDITS;
        }
        String ach_str = String.valueOf(ach_ects);
        if(ach_ects <= 0 ) {
            ach_str = "";
        }
        entries.add(new PieEntry(todo_ects, todo_str));
        entries.add(new PieEntry(ip_ects, ip_str));
        entries.add(new PieEntry(ach_ects, ach_str));
        // create dataset
        PieDataSet pieSet = new PieDataSet(entries, "Credits towards Bachelor");
        // we display the formatted description of each slice. no need for double information
        pieSet.setDrawValues(false);
        // ALL THE COLOURS OF THE WIND. or at least of spam
        int[] col = {
                getResources().getColor(R.color.markBachelor),
                getResources().getColor(R.color.markMarked),
                getResources().getColor(R.color.markInProgress),
                getResources().getColor(R.color.markCompleted)
        };
        // apply the colours
        pieSet.setColors(col);
        // aaaand create the data from the set of entries
        PieData pieData = new PieData(pieSet);
        // set up graph reference
        com.github.mikephil.charting.charts.PieChart graph =
                (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.overviewPieChart);
        // and attach data
        graph.setData(pieData);
        graph.setCenterText(String.format("%.2f", grade));
        // format it
        // no description and no legend and no interaction
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
        graph.setRotationEnabled(false);
        graph.setHighlightPerTapEnabled(false);
        graph.setEntryLabelColor(R.color.half_black);
    }

    private void sortIn(float g, List<Float> grades, boolean significant, List<Boolean> signs) {
        if (significant){
            for (int i=0; i < grades.size(); i++) {
                if (!signs.get(i)){
                    grades.set(i, g);
                    signs.set(i, true);
                    break;
                }
            }
        } else {
            if (g == 0f) return;
            for(int i=0; i< grades.size(); i++) {
                if (g < grades.get(i)) {
                    grades.add(i, g);
                    signs.add(i, false);
                    break;
                }
            }
            if (grades.size() > 5) grades.remove(grades.size() - 1);
            if (signs.size() > 5) signs.remove(grades.size() - 1);
            Log.v("OVERVIEW", "Sorting " + g + " brought us:" + grades.toString());
            Log.v("OVERVIEW", "Sorting " + g + " brought us:" + signs.toString());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                String[] projection = SQL_Database.MODULE_COLUMNS;
                return new CursorLoader(this, DataProvider.MODULE_DB_URI,
                                    projection, null, null,
                                    SQL_Database.MODULE_COLUMN_SIGNIFICANT + " DESC, " +
                                    SQL_Database.MODULE_COLUMN_GRADE + " ASC");
            case 1:
            default:
                throw new IllegalArgumentException("unknown cursor id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case 0:
                cursor = data;
                adapter.changeCursor(data);
                initGraph();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case 0:
                if (cursor != null) cursor.close();
                adapter.changeCursor(null);
                break;
        }
    }
}
