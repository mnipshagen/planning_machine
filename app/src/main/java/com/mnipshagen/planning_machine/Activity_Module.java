package com.mnipshagen.planning_machine;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This activity is called when a module inside the overview activity is called
 * It calls the database table "courses" and collects all entries which have the
 * corresponding module code
 */

public class Activity_Module extends Activity_Base implements LoaderManager.LoaderCallbacks<Cursor> {
    //TODO use cursor loader

    // holding a database reference
    private ContentResolver mCR;
    // the cursor which holds the course data
    private Cursor courses;
    private Adapter_Module adapter;
    // the module code of the active module
    private String module_code;

    private int comp_credits;
    private int optcomp_credits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        Intent intent = getIntent();

        /* collect the data that was handed over from 'overview' */
        // the code of the active module
        module_code = intent.getStringExtra("Module");
        // the credits necessary to complete the compulsory part of the module
        comp_credits = intent.getIntExtra("compECTS", 0);
        // to complete the optional compulsory part
        optcomp_credits = intent.getIntExtra("optcompECTS", 0);
        // and the name of the module
        final String name = intent.getStringExtra("Name");
        // set the name as the title of the activity
        setActionBarTitle(name);


        initGraph();
        /* And now to the lower part! */
        // find recycler view
        final RecyclerView rv = (RecyclerView) findViewById(R.id.moduleRecycler);
        // initialise the cursor if it holds no data
        if(courses == null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
        if (mCR == null) {
            mCR = getContentResolver();
        }
        // make it pretty!
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        // the adapter to handle the data
        adapter = new Adapter_Module(courses,this);
        rv.setAdapter(adapter);
        // and now display it!
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        // when we touch the course entries something should happen
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        //TODO REMOVE cursor. USE cursor loader
                        final Dialog dialog = new Dialog(Activity_Module.this);
                        dialog.setContentView(R.layout.course);

                        int oldpos = courses.getPosition();
                        courses.moveToPosition(position);
                        final long id = courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID));
                        courses.moveToPosition(oldpos);
                        final Cursor c = mCR.query(DataProvider.COURSES_DB_URI, null, SQL_Database.COURSES_COLUMN_ID + "=" + id, null, null);
                        c.moveToFirst();
                        TextView course_name = (TextView) dialog.findViewById(R.id.course_name);
                        final String c_name = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE));
                        course_name.setText(c_name);
                        final TextView grade = (TextView) dialog.findViewById(R.id.course_grade);
                        grade.setText(String.format("%.2f", c.getDouble(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE))));
                        grade.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment_Dialogs.changeGrade(c_name, id, Activity_Module.this);
                                c.requery();
                                grade.setText(String.format("%.2f", c.getDouble(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE))));
                            }
                        });
                        TextView ects = (TextView) dialog.findViewById(R.id.course_ects);
                        String credit = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS)) + " ECTS";
                        ects.setText(credit);
                        TextView typein = (TextView) dialog.findViewById(R.id.course_typein);
                        String tmp = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TYPE));
                        tmp = ModuleTools.courseTypeConv(tmp);
                        String tmp2 = ModuleTools.codeToName(c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_MODULE)), Activity_Module.this);
                        tmp = tmp.concat(" in " + tmp2);
                        typein.setText(tmp);
                        ImageView state = (ImageView) dialog.findViewById(R.id.course_state);
                        int st = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_STATE));
                        switch (st) {
                            case 0:
                                state.setImageResource(R.color.markMarked);
                                break;
                            case 1:
                                state.setImageResource(R.color.markInProgress);
                                break;
                            case 2:
                                state.setImageResource(R.color.markCompleted);
                                break;
                        }
                        TextView pm = (TextView) dialog.findViewById(R.id.course_pm);
                        if(c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_INFIELD_TYPE)).equals("PM")) {
                            pm.setText("Course is compulsory!");
                        } else {
                            pm.setText("Course is not Compulsory");
                        }
                        TextView term = (TextView) dialog.findViewById(R.id.course_term);
                        String year = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_YEAR));
                        String t = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TERM));
                        t = t.concat(" " + year);
                        term.setText(t);

                        Button but_move = (Button) dialog.findViewById(R.id.course_butt_move);
                        String movable = "Movable to:\n";
                        String[] possibleCourses = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_FIELDS_STR)).concat(", Open Studies").split(",");
                        List<String> possibleCodes = new ArrayList<>(Arrays.asList(ModuleTools.getModuleCodes(possibleCourses)));
                        possibleCodes.remove(module_code);
                        String[] modNames = ModuleTools.codesToNames(possibleCodes, Activity_Module.this);
                        StringBuilder sbuilder = new StringBuilder();
                        for (String s : modNames) {
                            sbuilder.append(s);
                            sbuilder.append(", ");
                        }
                        sbuilder.delete(sbuilder.lastIndexOf(","),sbuilder.length());
                        movable = movable.concat(sbuilder.toString());
                        TextView move = (TextView) dialog.findViewById(R.id.course_moveto);
                        move.setText(movable);
                        final String movable_button = movable;
                        but_move.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] entries = movable_button.substring(movable_button.indexOf(":")+2).split(",");
                                Fragment_Dialogs.moveCourse(c_name, entries, id, Activity_Module.this);
                                rv.getAdapter().notifyItemRemoved(position);
                                dialog.dismiss();
                            }
                        });

                        TextView desc = (TextView) dialog.findViewById(R.id.course_description);
                        String description = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE_DESC));
                        description = description.length()==0? "No description available" : description;
                        desc.setText(description);
                        TextView info = (TextView) dialog.findViewById(R.id.course_infodump);
                        String infodump = "Taught by " + c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TEACHERS_STR)) + "\n" +
                                            "StudIP code: " + c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_CODE)) + "\n" +
                                            "IKW code: " + c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE_ID));
                        info.setText(infodump);
                        Button dismiss = (Button) dialog.findViewById(R.id.course_butt_dismiss);
                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                c.close();
                            }
                        });
                        dialog.show();
                    }

                    @Override
                    public void onLongItemClick(final View view, int position) {
                        int oldpos = courses.getPosition();
                        courses.moveToPosition(position);
                        final String name = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE));
                        final long id = courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID));
                        final String fieldsSTR = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_FIELDS_STR));
                        courses.moveToPosition(oldpos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Module.this);
                        builder .setTitle(name)
                                .setItems(R.array.course_interaction, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AlertDialog.Builder builder;
                                        switch (which) {
                                            // set grade
                                            case 0:
                                               Fragment_Dialogs.changeGrade(name, id, Activity_Module.this);
                                               break;
                                            // change state
                                            case 1:
                                                builder = new AlertDialog.Builder(Activity_Module.this);
                                                builder.setTitle("Set state of " + name)
                                                        .setItems(R.array.statelist, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                switch(which) {
                                                                    // passed
                                                                    case 0:
                                                                        ModuleTools.setCoursePassed(id, Activity_Module.this);
                                                                        break;
                                                                    // in progress
                                                                    case 1:
                                                                        ModuleTools.setCourseInProgress(id, Activity_Module.this);
                                                                        break;
                                                                    // marked
                                                                    case 2:
                                                                        ModuleTools.setCourseMarked(id, Activity_Module.this);
                                                                        break;
                                                                }
                                                            }
                                                        });
                                                builder.show();
                                                break;
                                            // move to
                                            case 2:
                                                List<String> codes = new ArrayList<>(Arrays.asList(ModuleTools.getModuleCodes(fieldsSTR.split(","))));
                                                codes.add("OPEN");
                                                codes.remove(module_code);
                                                String[] fields = ModuleTools.codesToNames(codes, Activity_Module.this);

                                                Fragment_Dialogs.moveCourse(name, fields, id, Activity_Module.this);
                                                break;
                                            // remove
                                            case 3:
                                                ModuleTools.removeCourse(id, Activity_Module.this);
                                                Snackbar.make(findViewById(R.id.moduleRecycler), name + " was removed.", Snackbar.LENGTH_LONG);
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    }
                }));
    }


    private void initGraph() {
        //TODO include listener for adapter
        float[] res = ModuleTools.refreshModule(module_code, this);
        int achv_credits = (int) res[0];
        int ip_credits = (int) res[1];
        float grade = res[2];

         /* Setting up the PieChart */
        // the list holds all entries of the chart
        List<PieEntry> entries = new ArrayList<>();
        // will hold the colours to use
        int[] col;

        // if in progress + achieved credits are not enough to fulfill the compulsory part
        // the missing credits until compulsory is fulfilled are displayed in orange
        if(ip_credits + achv_credits < comp_credits) {
            int todo_ects = optcomp_credits-comp_credits;
            entries.add(new PieEntry(todo_ects, String.valueOf(todo_ects)));
            int todo_comp = comp_credits - achv_credits - ip_credits;
            entries.add(new PieEntry(todo_comp, String.valueOf(todo_comp)));
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markBachelor),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)  };
        }
        // if compulsory credits are achieved, then we have no need for the orange part
        // and the ects still to do are only depending on the achieved and in progress credits
        else {
            int todo_ects = optcomp_credits - achv_credits - ip_credits;
            entries.add(new PieEntry(todo_ects, String.valueOf(todo_ects)));
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)  };
        }
        // add in progress and achieved credits to chart
        entries.add(new PieEntry(ip_credits, String.valueOf(ip_credits)));
        entries.add(new PieEntry(achv_credits, String.valueOf(achv_credits)));
        // create the pieSet from the entries created above
        PieDataSet pieSet = new PieDataSet(entries, "Credits towards Module completion");
        // we display the formatted information on the chart and as such do not draw the actual
        // values. (no need for two times the same information)
        pieSet.setDrawValues(false);
        // set the colours to it
        pieSet.setColors(col);
        // and create a chart data set out of our pie information
        PieData pieData = new PieData(pieSet);

        // find our graph and set up a reference
        com.github.mikephil.charting.charts.PieChart graph =
                (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.modulePieChart);
        // attach the data to the pie chart and format the chart
        graph.setData(pieData);
        graph.setCenterText(String.format("%.2f", grade));
        // no description and no legend needed
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
        // right now the chart has no interaction
        graph.setRotationEnabled(false);
        graph.setHighlightPerTapEnabled(false);
        graph.setEntryLabelColor(R.color.half_black);
        graph.invalidate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] courseData = {
                SQL_Database.COURSES_COLUMN_ID,
                SQL_Database.COURSES_COLUMN_COURSE,
                SQL_Database.COURSES_COLUMN_ECTS,
                SQL_Database.COURSES_COLUMN_GRADE,
                SQL_Database.COURSES_COLUMN_STATE,
                SQL_Database.COURSES_COLUMN_MODULE,
                SQL_Database.COURSE_COLUMN_FIELDS_STR
        };
        String courseSelection = SQL_Database.COURSES_COLUMN_MODULE + " = " + "'" + module_code + "'";
        Loader<Cursor> loader = new CursorLoader(this, DataProvider.COURSES_DB_URI, courseData, courseSelection, null, SQL_Database.COURSES_COLUMN_STATE);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        courses = data;
        adapter.changeCursor(data);
        initGraph();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(courses != null) courses.close();
        adapter.changeCursor(null);
    }
}
