package com.mnipshagen.planning_machine;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity is called when a module inside the overview activity is called
 * It calls the database table "courses" and collects all entries which have the
 * corresponding module code
 */

public class Activity_Module extends Activity_Base {
    //TODO use cursor loader

    // holding a database reference
    private SQLiteDatabase db;
    // the cursor which holds the course data
    private Cursor courses;
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

        /* And now to the lower part! */
        // find recycler view
        final RecyclerView rv = (RecyclerView) findViewById(R.id.moduleRecycler);
        // initialise the cursor if it holds no data
        if(courses == null) {
            initCursor();
        }
        // make it pretty!
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        // the adapter to handle the data
        Adapter_Module adapter = new Adapter_Module(courses,this);
        rv.setAdapter(adapter);
        // and now display it!
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        // when we touch the course entries something should happen
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO
                        try {
                            int oldpos = courses.getPosition();
                            courses.moveToPosition(position);
                            final long id = courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID));
                            courses.moveToPosition(oldpos);
                            Cursor c = db.query(SQL_Database.COURSES_TABLE_NAME, null, SQL_Database.COURSES_COLUMN_ID + "=" + id, null, null, null, null);
                            c.moveToFirst();
                            View course = getLayoutInflater().inflate(R.layout.course, (ViewGroup) view.getParent(), false);
                            TextView name = (TextView) course.findViewById(R.id.course_name);
                            name.setText(c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE)));
                            TextView grade = (TextView) course.findViewById(R.id.course_grade);
                            grade.setText(String.format("%.2f", c.getDouble(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE))));
                            TextView ects = (TextView) course.findViewById(R.id.course_ects);
                            ects.setText(c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS)));
                            TextView typein = (TextView) course.findViewById(R.id.course_typein);
                            String tmp = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TYPE));
                            tmp = tmp.concat(" in " + c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_MODULE)));
                            typein.setText(tmp);
                            ImageView state = (ImageView) course.findViewById(R.id.course_state);
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
                            String movable = " Movable to:\n";
                            movable = movable.concat(c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_FIELDS_STR)));
                            TextView move = (TextView) course.findViewById(R.id.course_moveto);
                            move.setText(movable);
                            TextView desc = (TextView) course.findViewById(R.id.course_description);
                            desc.setText(c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE_DESC)));
                            TextView info = (TextView) course.findViewById(R.id.course_infodump);
                            info.setText("DUMP THE INFO");
                            c.close();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Module.this);
                            builder.setView(course)
                                    .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLongItemClick(final View view, int position) {
                        int oldpos = courses.getPosition();
                        courses.moveToPosition(position);
                        final String name = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE));
                        final long id = courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID));
                        final String currentMod = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_MODULE));
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
                                                builder = new AlertDialog.Builder(Activity_Module.this);
                                                builder.setTitle("Set Grade");
                                                final EditText input = new EditText(Activity_Module.this);
                                                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                builder.setView(input);
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (input.getText() != null){
                                                            double grade = 0.0;
                                                            boolean allowed = true;
                                                            try {
                                                                grade = Double.parseDouble(input.getText().toString().replace(',', '.'));
                                                                if (!(0.5 < grade) || !(grade <= 4.)) {
                                                                    Toast.makeText(Activity_Module.this, "Not a grade!", Toast.LENGTH_SHORT).show();
                                                                    allowed = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(Activity_Module.this, "That was not a recognisable number!", Toast.LENGTH_SHORT).show();
                                                                allowed = false;
                                                            }
                                                            if (allowed) {
                                                                ModuleTools.setCourseGrade(id, grade, Activity_Module.this);
                                                            }
                                                        } else {
                                                            Toast.makeText(Activity_Module.this, "No input detected.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.show();
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
                                                String[] fields1 = fieldsSTR.split(",");
                                                String[] codes1 = ModuleTools.getModuleCodes(fields1);
                                                String[] fields = new String[fields1.length - 1];
                                                if (fields.length == 0) {
                                                    TextView info = new TextView(Activity_Module.this);
                                                    info.setText("This course cannot be moved.");
                                                    builder = new AlertDialog.Builder(Activity_Module.this);
                                                    builder.setTitle("Move " + name)
                                                            .setView(info)
                                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .show();
                                                } else {
                                                    final String[] codes = new String[codes1.length - 1];
                                                    for (int i = 0; i < fields1.length; i++) {
                                                        if (codes1[i].equals(currentMod)) {
                                                            for (int j = 0; j < fields1.length; j++) {
                                                                if (j != i) {
                                                                    fields[j] = fields1[j];
                                                                    codes[j] = codes1[j];
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    final int[] selected = {0};
                                                    builder = new AlertDialog.Builder(Activity_Module.this);
                                                    builder.setTitle("Move " + name)
                                                            .setSingleChoiceItems(fields, 0, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    selected[0] = which;
                                                                }
                                                            })
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    int sel = selected[0];
                                                                    String code = codes[sel];
                                                                    ModuleTools.moveCourse(code, id, Activity_Module.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        // when we start the activity from cache, reinitialise cursor
        initCursor();
        initGraph();
    }

    @Override
    protected void onPause() {
        // when the activity leaves the active view, close the cursor to prevent memory leak
        courses.close();
        db.close();
        super.onPause();
    }

    private void initGraph() {
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

    /**
     * initialise the cursor to fetch and hold all courses of the module
     */
    private void initCursor() {
        db = SQL_Database.getInstance(this).getWritableDatabase();

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

        courses = db.query(SQL_Database.COURSES_TABLE_NAME, courseData, courseSelection, null, null, null, SQL_Database.COURSES_COLUMN_STATE);
    }
}
