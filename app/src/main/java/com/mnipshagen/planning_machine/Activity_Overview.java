package com.mnipshagen.planning_machine;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

import java.util.ArrayList;
import java.util.List;

/**
 * The overview activity which is also the launch activity
 * Here all modules are displayed and the overall progress towards the bachelor
 */
public class Activity_Overview extends Activity_Base {

    // the bachelor credits and the bachelor thesis credits are fix.
    private final int BACHELOR_CREDITS = 180;
    private final int THESIS_CREDITS = 12;
    private Cursor cursor;
    private SQLiteDatabase db;
    private RecyclerView rv;

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
        Adapter_Overview adapter = new Adapter_Overview(null, this);
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
                        openModule(name, code, compECTS, optcompECTS);
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
        // initalise the database and fetch all the module data
        db = SQL_Database.getInstance(this).getReadableDatabase();
        String[] columns = {
                SQL_Database.MODULE_COLUMN_ID,
                SQL_Database.MODULE_COLUMN_NAME,
                SQL_Database.MODULE_COLUMN_CODE,
                SQL_Database.MODULE_COLUMN_ECTS,
                SQL_Database.MODULE_COLUMN_IPECTS,
                SQL_Database.MODULE_COLUMN_GRADE
        };
        // query the database
        cursor = db.query(
                SQL_Database.MODULE_TABLE_NAME,
                null, null, null,
                null, null, SQL_Database.MODULE_COLUMN_ID);
        ((Adapter_Overview) rv.getAdapter()).changeCursor(cursor);
        //set up the grade
        // TODO format and set up grade
        ((TextView) findViewById(R.id.overviewUpperText))
                .setText("Grade");

        // calculate the overall achieved and in progress credits
        // by going through the modules
        int ach_ects = 0;
        int ip_ects = 0;
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            ach_ects += cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS));
            ip_ects += cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_IPECTS));
        }

        /* Set up the pie chart */
        // the entries are t he thesis itself, the credits still to do, the in progress credits
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
        if(ach_ects == 0 ) {
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
        int[] col = {   getResources().getColor(R.color.markBachelor),
                getResources().getColor(R.color.markMarked),
                getResources().getColor(R.color.markInProgress),
                getResources().getColor(R.color.markCompleted)  };
        // apply the colours
        pieSet.setColors(col);
        // aaaand create the data from the set of entries
        PieData pieData = new PieData(pieSet);
        // set up graph reference
        com.github.mikephil.charting.charts.PieChart graph =
                (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.overviewPieChart);
        // and attach data
        graph.setData(pieData);
        // format it
        // no donut and no description and no legend and no interaction
        graph.setDrawHoleEnabled(false);
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
        graph.setRotationEnabled(false);
        graph.setHighlightPerTapEnabled(false);
        graph.setEntryLabelColor(R.color.half_black);
    }

    @Override
    public void onPause() {
        super.onPause();
        cursor.close();
        db.close();
    }

    /**
     * build the intent to display the module and start it
     * @param name name of the module
     * @param code the po code of the module
     * @param compECTS compulsory credits of module
     * @param optcompECTS optional compulsory credits
     */
    private void openModule(String name, String code, int compECTS, int optcompECTS) {
        Intent intent = new Intent(this, Activity_Module.class);
        intent.putExtra("Name",name);
        intent.putExtra("Module", code);
        intent.putExtra("compECTS", compECTS);
        intent.putExtra("optcompECTS", optcompECTS);
        startActivity(intent);
    }
}
