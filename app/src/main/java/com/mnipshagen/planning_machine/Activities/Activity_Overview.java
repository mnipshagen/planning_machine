package com.mnipshagen.planning_machine.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.Adapters.Adapter_Overview;
import com.mnipshagen.planning_machine.Adapters.Adapter_Overview_SignificantChoice;
import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;
import com.mnipshagen.planning_machine.Utils;
import com.mnipshagen.planning_machine.R;
import com.mnipshagen.planning_machine.Adapters.RecyclerCursorAdapter;
import com.mnipshagen.planning_machine.Adapters.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The overview activity which is also the launch activity
 * Here all modules are displayed and the overall progress towards the bachelor
 */
public class Activity_Overview extends Activity_Base implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor cursor;
    private RecyclerView rv;
    private RecyclerCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        setActionBarTitle(R.string.title_overview);

        TextView ects_thesis, ects_orals;
        ects_thesis = (TextView) findViewById(R.id.overview_thesis);
        ects_thesis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "You will be able to fill in your thesis results here. This time is not now. But it will come. And it will be aweomse.", Snackbar.LENGTH_SHORT);
            }
        });
        ects_orals = (TextView) findViewById(R.id.overview_orals);
        ects_orals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = getContentResolver().query(
                        DataProvider.MODULE_DB_URI,
                        new String[] {SQL_Database.MODULE_COLUMN_NAME},
                        SQL_Database.MODULE_COLUMN_STATE + "<> 0",
                        null, null);
                c.moveToPosition(-1);
                String[] names = new String[c.getCount()];
                int i = 0;
                while (c.moveToNext()) {
                    names[i++] = c.getString(0);
                }
                if (i != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Overview.this);
                    builder.setTitle("Your oral exams")
                            .setItems(names, null)
                            .show();
                }
            }
        });

        /* The lower half is now */
        rv = (RecyclerView) findViewById(R.id.overviewRecycler);

        // MAKE IT PRETTY MAKE IT NICE
//        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        // initialise the adapter
        adapter = new Adapter_Overview(cursor, this);
        rv.setAdapter(adapter);
        // and the layoutmanager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        // when we touch the module entries something should happen
        final RecyclerItemClickListener mOnTouchListener
                = new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // get all the data for the module from the cursor and open the module
                cursor.moveToPosition(position);
                String name = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_CODE));
                int compECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_COMP));
                int optcompECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP));
                boolean significant = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_SIGNIFICANT)) != 0;
                boolean oraled = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_STATE)) != 0;
                Intent intent = new Intent(Activity_Overview.this, Activity_Module.class);
                intent.putExtra("Name",name);
                intent.putExtra("Module", code);
                intent.putExtra("compECTS", compECTS);
                intent.putExtra("optcompECTS", optcompECTS);
                intent.putExtra("significant", significant);
                intent.putExtra("oral", oraled);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                //TODO
            }
        });
        rv.addOnItemTouchListener(mOnTouchListener);

        final FloatingActionMenu allFABS = (FloatingActionMenu) findViewById(R.id.overviewFAB);
        FloatingActionButton addCourse = (FloatingActionButton) findViewById(R.id.overviewAddCourse);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFABS.close(true);
                final EditText input = new EditText(Activity_Overview.this);
                input.setHint("Course title to search for");

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Overview.this);
                builder.setTitle("Start search:")
                        .setView(input)
                        .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Activity_Overview.this, Activity_Search.class);
                                intent.putExtra("course_title", input.getText().toString());
                                intent.putExtra("module_code", "");
                                intent.putExtra("start", true);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
        FloatingActionButton changeGradeCalc = (FloatingActionButton) findViewById(R.id.overviewChangeGradeCalc);
        changeGradeCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFABS.close(true);
                Toast.makeText(Activity_Overview.this,
                            "Choose from different ways to calculate the grade. ... Once they are implemented.",
                            Toast.LENGTH_SHORT)
                        .show();
            }
        });
        FloatingActionButton setSignificant = (FloatingActionButton) findViewById(R.id.overviewSetSignificantModules);
        setSignificant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFABS.close(true);
                rv.removeOnItemTouchListener(mOnTouchListener);
                final Adapter_Overview_SignificantChoice a = new Adapter_Overview_SignificantChoice(cursor, Activity_Overview.this);
                rv.setAdapter(a);
                allFABS.setVisibility(View.GONE);
                CoordinatorLayout coord = (CoordinatorLayout) findViewById(R.id.overview_coordinator);
                final FloatingActionButton done = new FloatingActionButton(Activity_Overview.this);
                done.setImageResource(R.drawable.ic_check);
                CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.BOTTOM;
                done.setLayoutParams(lp);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.markThem();
                        rv.setAdapter(adapter);
                        rv.addOnItemTouchListener(mOnTouchListener);
                        done.setVisibility(View.GONE);
                        allFABS.setVisibility(View.VISIBLE);
                    }
                });
                coord.addView(done);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().destroyLoader(0);
        getSupportLoaderManager().initLoader(0, null, this);
        if(cursor != null && !cursor.isClosed())
            Log.v("OVERVIEW", "On Resume: " + DatabaseUtils.dumpCursorToString(cursor));
        else
            Log.v("OVERVIEW", "On Resume: Cursor is null or closed.");
    }

    private void initAppBar() {
        final int THESIS_CREDITS = 12;
        final int BACHELOR_CREDITS = 180;

        TextView ects_thesis, ects_orals;
        ects_thesis = (TextView) findViewById(R.id.overview_thesis);
        Spanned text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml("+ <b>0</b> from",Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            text = Html.fromHtml("+ <b>0</b> from");
        }
        ects_thesis.setText(text);
        ects_orals = (TextView) findViewById(R.id.overview_orals);
        SharedPreferences sharedPrefs = getSharedPreferences(Utils.SAVE_DATA, MODE_PRIVATE);
        int oralAmount = sharedPrefs.getInt(Utils.SAVE_KEY_ORALS, 0) * 3;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml("+ <b>" + oralAmount +"</b> from",Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            text = Html.fromHtml("+ <b>" + oralAmount + "</b> from");
        }
        ects_orals.setText(text);

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

        int todo_ects = BACHELOR_CREDITS - ach_ects - ip_ects - THESIS_CREDITS - oralAmount;
        String todo_str = String.valueOf(todo_ects);
        if(todo_ects <= 0) {
            todo_ects = 0;
            todo_str = "";
        }
        if(ip_ects > (BACHELOR_CREDITS - THESIS_CREDITS -ach_ects)) {
            ip_ects = BACHELOR_CREDITS - THESIS_CREDITS -ach_ects;
        }
        String ip_str = String.valueOf(ip_ects);
        if(ip_ects <= 0) {
            ip_str="";
        }
        if(ach_ects > BACHELOR_CREDITS - THESIS_CREDITS) {
            ach_ects = BACHELOR_CREDITS - THESIS_CREDITS;
        }
        String ach_str = String.valueOf(ach_ects);
        if(ach_ects <= 0 ) {
            ach_str = "";
        }
        String oralAmount_str = String.valueOf(oralAmount);
        if(oralAmount <= 0) {
            oralAmount_str = "";
        }
        entries.add(new PieEntry(todo_ects, todo_str));
        entries.add(new PieEntry(ip_ects, ip_str));
        entries.add(new PieEntry(oralAmount, oralAmount_str));
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
                getResources().getColor(R.color.markOrals),
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

        graph.invalidate();
    }

    private void sortIn(float g, List<Float> grades, boolean significant, List<Boolean> signs) {
        if (grades.size() != signs.size()) {
            throw new RuntimeException("ToggleSignificant: More or less grades than states.");
        }
        if (grades.size() > 5 || signs.size() > 5) {
            throw new RuntimeException("ToggleSignificant: More than 5 modules. Should not work");
        }
        if (g == Utils.NO_GRADE) return;
        if (grades.size() < 5) {
            int i = 0;
            while (i < grades.size() && grades.get(i) > g) {
                i++;
            }
            grades.add(i, g);
            signs.add(i,significant);
        }else {
            for (int i = 4; i >= 0; i--) {
                if (significant && !signs.get(i)) {
                    grades.set(i, g);
                    signs.set(i, true);
                    break;
                } else if (g < grades.get(i) && !significant && !signs.get(i)){
                    grades.set(i, g);
                    signs.set(i, false);
                    break;
                }
            }
        }

        if (grades.size() > 5) grades.remove(grades.size() - 1);
        if (signs.size() > 5) signs.remove(grades.size() - 1);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                String[] projection = SQL_Database.MODULE_COLUMNS;
                return new CursorLoader(this, DataProvider.MODULE_DB_URI,
                                    projection, null, null,
                                    SQL_Database.MODULE_COLUMN_SIGNIFICANT + " DESC, " +
                                    SQL_Database.MODULE_COLUMN_ID + " ASC");
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
                ((RecyclerCursorAdapter)rv.getAdapter()).changeCursor(cursor);
                initAppBar();
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
