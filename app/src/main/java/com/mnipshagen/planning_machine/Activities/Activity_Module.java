package com.mnipshagen.planning_machine.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.Adapters.Adapter_Module;
import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;
import com.mnipshagen.planning_machine.Dialogs.AddUnlistedCourseDialog;
import com.mnipshagen.planning_machine.Dialogs.SetGradeDialog;
import com.mnipshagen.planning_machine.DividerItemDecoration;
import com.mnipshagen.planning_machine.Fragment_Dialogs;
import com.mnipshagen.planning_machine.Utils;
import com.mnipshagen.planning_machine.R;
import com.mnipshagen.planning_machine.Adapters.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This activity is called when a module inside the overview activity is called
 * It calls the database table "courses" and collects all entries which have the
 * corresponding module code
 */

public class Activity_Module extends Activity_Base implements LoaderManager.LoaderCallbacks<Cursor>, SetGradeDialog.GradeDialogListener{
    private String LOGTAG = "ModActiv";

    // the cursor which holds the course data
    private Cursor courses;
    private Adapter_Module adapter;
    // the module code of the active module
    private String module_code;

    Dialog dialog;

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
        // and the significane
        boolean significant = intent.getBooleanExtra("significant", false);
        // was oral performed on this module?
        boolean oraled = intent.getBooleanExtra("oral", false);
        // and the name of the module
        final String module_name = intent.getStringExtra("Name");
        // set the name as the title of the activity
        setActionBarTitle(module_name);

        final FloatingActionMenu allFABS = (FloatingActionMenu) findViewById(R.id.moduleFaB);

        FloatingActionButton fab_addCourse = (FloatingActionButton) findViewById(R.id.moduleAddCourse);
        fab_addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFABS.close(true);
                final EditText input = new EditText(Activity_Module.this);
                input.setHint("Course title to search for");

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Module.this);
                builder.setTitle("Start search:")
                        .setView(input)
                        .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Activity_Module.this, Activity_Search.class);
                                intent.putExtra("course_title", input.getText().toString());
                                intent.putExtra("module_code", module_code);
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
        FloatingActionButton fab_remCourse = (FloatingActionButton) findViewById(R.id.moduleRemoveCourse);
        fab_remCourse.setEnabled(false);
        fab_remCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFABS.close(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Module.this);
                final ArrayList<Long> selected = new ArrayList<>();
                builder.setTitle("Remove Courses")
                        .setMultiChoiceItems(courses, SQL_Database.COURSE_COLUMN_ID, SQL_Database.COURSE_COLUMN_COURSE, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                courses.moveToPosition(which);
                                if(isChecked){
                                    selected.add(courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID)));
                                } else {
                                    selected.remove(courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID)));
                                }
                            }
                        })
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView text = new TextView(Activity_Module.this);
                                text.setText("Are you sure you want to remove " + selected.size() + " courses?");
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Module.this);
                                builder.setTitle("EX-TERMINATE")
                                        .setView(text)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                for (long id : selected) {
                                                    Utils.removeCourse(id, Activity_Module.this);
                                                }
                                            }
                                        })
                                        .show();
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
        FloatingActionButton fab_addUnlisted = (FloatingActionButton) findViewById(R.id.moduleAddUnlisted);
        fab_addUnlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFABS.close(true);
                AddUnlistedCourseDialog dialog = new AddUnlistedCourseDialog();
                Bundle args = new Bundle();
                args.putString("module_code", module_code);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "addUnlistedCourse");
            }
        });

        final boolean insignificant = module_code.equals("OPEN") || module_code.equals("LOG") || module_code.equals("SD");

        final ToggleButton markSignificant = (ToggleButton) findViewById(R.id.toggleSignificance);
        markSignificant.setChecked(significant);
        markSignificant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!insignificant) {
                    if (!Utils.toggleSignificant(module_code, Activity_Module.this)) {
                        Toast.makeText(Activity_Module.this, "Could not change state. Do you already have 5 applicable modules?", Toast.LENGTH_LONG).show();
                        markSignificant.toggle();
                    }
                } else {
                    Toast.makeText(Activity_Module.this, "This module cannot be marked applicable.", Toast.LENGTH_LONG).show();
                    markSignificant.toggle();
                }
            }
        });

        final ToggleButton markOral = (ToggleButton) findViewById(R.id.toggleOral);
        markOral.setChecked(oraled);
        markOral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!insignificant) {
                    if(markOral.isChecked()) {
                        SetGradeDialog setGrade = new SetGradeDialog();
                        Bundle args = new Bundle();
                        args.putString(SetGradeDialog.TITLE, module_name);
                        args.putLong(SetGradeDialog.ID, SetGradeDialog.NO_ID);
                        args.putBoolean(SetGradeDialog.IS_MODULE, true);
                        setGrade.setArguments(args);
                        setGrade.show(getSupportFragmentManager(), "setGrade");
                    } else {
                        Utils.toggleOral(module_code, Utils.NO_GRADE, Activity_Module.this);
                        initGraph();
                    }
                } else {
                    Toast.makeText(Activity_Module.this, "You cannot perform an oral examination in this module.", Toast.LENGTH_LONG).show();
                    markSignificant.toggle();
                }
            }
        });


        /* And now to the lower part! */
        // find recycler view
        final RecyclerView rv = (RecyclerView) findViewById(R.id.moduleRecycler);
        // initialise the cursor if it holds no data
        if(courses == null) {
            getSupportLoaderManager().initLoader(0, null, this);
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
                        dialog = new Dialog(Activity_Module.this);
                        dialog.setContentView(R.layout.course);

                        final int oldpos = courses.getPosition();
                        courses.moveToPosition(position);
                        final long id = courses.getLong(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID));

                        TextView course_name = (TextView) dialog.findViewById(R.id.course_name);
                        final String c_name = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE));
                        course_name.setText(c_name);

                        final TextView grade = (TextView) dialog.findViewById(R.id.course_grade);
                        double g = courses.getDouble(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE));
                        String gradeText =
                                 g == Utils.NO_GRADE? "--" : String.format("%.2f", g);
                        grade.setText(gradeText);
                        grade.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                SetGradeDialog setGrade = new SetGradeDialog();
                                Bundle args = new Bundle();
                                args.putString(SetGradeDialog.TITLE, c_name);
                                args.putLong(SetGradeDialog.ID, id);
                                args.putBoolean(SetGradeDialog.IS_MODULE, false);
                                setGrade.setArguments(args);
                                setGrade.show(getSupportFragmentManager(), "setGrade");
                            }
                        });

                        TextView ects = (TextView) dialog.findViewById(R.id.course_ects);
                        String credit = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS)) + " ECTS";
                        ects.setText(credit);

                        TextView typein = (TextView) dialog.findViewById(R.id.course_typein);
                        String tmp = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TYPE));
                        if (tmp != null) {
                            tmp = Utils.courseTypeConv(tmp);
                        } else {
                            tmp = "";
                        }
                        String tmp2 = Utils.codeToName(courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_MODULE)), Activity_Module.this);
                        tmp = tmp.concat(" in " + tmp2);
                        typein.setText(tmp);

                        ImageView state = (ImageView) dialog.findViewById(R.id.course_state);
                        int st = courses.getInt(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_STATE));
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
                        String compulsory = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_INFIELD_TYPE));
                        if(compulsory != null && compulsory.equals("PM")) {
                            pm.setText("Course is compulsory!");
                        } else {
                            pm.setText("Course is not Compulsory");
                        }
                        TextView term = (TextView) dialog.findViewById(R.id.course_term);
                        String year = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_YEAR));
                        String t = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TERM));
                        t = t.concat(" " + year);
                        term.setText(t);

                        Button but_move = (Button) dialog.findViewById(R.id.course_butt_move);
                        String movable = "Movable to:\n";
                        String[] possibleCourses = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_FIELDS_STR)).concat(", Open Studies").split(",");
                        List<String> possibleCodes = new ArrayList<>(Arrays.asList(Utils.getModuleCodes(possibleCourses)));
                        possibleCodes.remove(module_code);
                        String[] modNames = Utils.codesToNames(possibleCodes, Activity_Module.this);
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
                        Button but_remove = (Button) dialog.findViewById(R.id.course_butt_remove);
                        but_remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.removeCourse(id, Activity_Module.this);
                                dialog.dismiss();
                                Snackbar.make(findViewById(R.id.content), module_name + " was removed.", Snackbar.LENGTH_LONG);
                            }
                        });
                        Button but_changeState = (Button) dialog.findViewById(R.id.course_butt_changeState);
                        but_changeState.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Module.this);
                                builder.setTitle("Set state of " + module_name)
                                        .setItems(R.array.statelist, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch(which) {
                                                    // passed
                                                    case 0:
                                                        Utils.setCoursePassed(id, Activity_Module.this);
                                                        break;
                                                    // in progress
                                                    case 1:
                                                        Utils.setCourseInProgress(id, Activity_Module.this);
                                                        break;
                                                    // marked
                                                    case 2:
                                                        Utils.setCourseMarked(id, Activity_Module.this);
                                                        break;
                                                }
                                                grade.setText(String.format("%.2f", 0.));
                                            }
                                        });
                                builder.show();
                            }
                        });

                        TextView desc = (TextView) dialog.findViewById(R.id.course_description);
                        String description = courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE_DESC));
                        description = (description==null || description.length()==0)? "No description available" : description;
                        desc.setText(description);
                        TextView info = (TextView) dialog.findViewById(R.id.course_infodump);
                        String infodump = "Taught by " + courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_TEACHERS_STR)) + "\n" +
                                            "StudIP code: " + courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_CODE)) + "\n" +
                                            "IKW code: " + courses.getString(courses.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE_ID));
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
                                courses.moveToPosition(oldpos);
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
                                                SetGradeDialog setGrade = new SetGradeDialog();
                                                Bundle args = new Bundle();
                                                args.putString(SetGradeDialog.TITLE, name);
                                                args.putLong(SetGradeDialog.ID, id);
                                                args.putBoolean(SetGradeDialog.IS_MODULE, false);
                                                setGrade.setArguments(args);
                                                setGrade.show(getSupportFragmentManager(), "setGrade");
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
                                                                        Utils.setCoursePassed(id, Activity_Module.this);
                                                                        break;
                                                                    // in progress
                                                                    case 1:
                                                                        Utils.setCourseInProgress(id, Activity_Module.this);
                                                                        break;
                                                                    // marked
                                                                    case 2:
                                                                        Utils.setCourseMarked(id, Activity_Module.this);
                                                                        break;
                                                                }
                                                            }
                                                        });
                                                builder.show();
                                                break;
                                            // move to
                                            case 2:
                                                List<String> codes = new ArrayList<>(Arrays.asList(Utils.getModuleCodes(fieldsSTR.split(","))));
                                                codes.add("OPEN");
                                                codes.remove(module_code);
                                                String[] fields = Utils.codesToNames(codes, Activity_Module.this);

                                                Fragment_Dialogs.moveCourse(name, fields, id, Activity_Module.this);
                                                break;
                                            // remove
                                            case 3:
                                                Utils.removeCourse(id, Activity_Module.this);
                                                Snackbar.make(findViewById(R.id.content), name + " was removed.", Snackbar.LENGTH_LONG);
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    }
                }));
    }


    private void initGraph() {
        double[] res = Utils.getOverallCredits(module_code, this);
        int overall_achv = (int) res[0];
        int overall_ip = (int) res[1];
        double grade = res[2];

        int[] res2 = Utils.getCompAchvEcts(module_code, this);
        int comp_achv = res2[0];
        int comp_ip = res2[1];

        int opt_achv = overall_achv - comp_achv;
        int opt_ip = overall_ip - comp_ip;

        Log.v("Module", "Starting off" +
                "\nOverall Achvieved: " + overall_achv +
                "\nOverall in progress: " + overall_ip +
                "\nCompulsory achieved: " + comp_achv +
                "\nCompulsory in progress: " + comp_ip +
                "\nOptional achieved: " + opt_achv +
                "\nOptional in progress: " + opt_ip);

        boolean compComplete = false;
        boolean compInProg = false;
        boolean optComplete = false;
        boolean optInProg = false;

        if (comp_achv >= comp_credits){
            compComplete = true;
            opt_ip += comp_ip;
            comp_ip = 0;
            opt_achv += comp_credits - comp_achv;
            comp_achv = comp_credits;
            Log.v("Module", "compulsory achieved" +
                    "\nOverall Achvieved: " + overall_achv +
                    "\nOverall in progress: " + overall_ip +
                    "\nCompulsory achieved: " + comp_achv +
                    "\nCompulsory in progress: " + comp_ip +
                    "\nOptional achieved: " + opt_achv +
                    "\nOptional in progress: " + opt_ip);
        } else if (comp_achv  + comp_ip >= comp_credits) {
            compInProg = true;
            optInProg = true;
            opt_ip += comp_ip - (comp_credits - comp_achv);
            comp_ip = comp_credits - comp_achv;
            Log.v("Module", "Compulsory nearly achieved" +
                    "\nOverall Achvieved: " + overall_achv +
                    "\nOverall in progress: " + overall_ip +
                    "\nCompulsory achieved: " + comp_achv +
                    "\nCompulsory in progress: " + comp_ip +
                    "\nOptional achieved: " + opt_achv +
                    "\nOptional in progress: " + opt_ip);
        } else if (comp_achv > 0 || comp_ip > 0) {
            compInProg = true;
            Log.v("Module", "compulsory in progress" +
                    "\nOverall Achvieved: " + overall_achv +
                    "\nOverall in progress: " + overall_ip +
                    "\nCompulsory achieved: " + comp_achv +
                    "\nCompulsory in progress: " + comp_ip +
                    "\nOptional achieved: " + opt_achv +
                    "\nOptional in progress: " + opt_ip);
        }
        // TODO do not ignore overflowing credits
        int overflow;
        int optcomp_only = optcomp_credits - comp_credits;
        if (opt_achv >= optcomp_only) {
            optComplete = true;
            opt_achv = optcomp_only;
            Log.v("Module", "Oversaturated this thing" +
                    "\nOverall Achvieved: " + overall_achv +
                    "\nOverall in progress: " + overall_ip +
                    "\nCompulsory achieved: " + comp_achv +
                    "\nCompulsory in progress: " + comp_ip +
                    "\nOptional achieved: " + opt_achv +
                    "\nOptional in progress: " + opt_ip);
        } else if (opt_achv + opt_ip >= optcomp_only) {
            optInProg = true;
            opt_ip = optcomp_only - opt_achv;
            Log.v("Module", "module is nearly there" +
                    "\nOverall Achvieved: " + overall_achv +
                    "\nOverall in progress: " + overall_ip +
                    "\nCompulsory achieved: " + comp_achv +
                    "\nCompulsory in progress: " + comp_ip +
                    "\nOptional achieved: " + opt_achv +
                    "\nOptional in progress: " + opt_ip);
        } else if (opt_achv >0 || opt_ip >0) {
            optInProg = true;
            Log.v("Module", "well. we are getting there" +
                    "\nOverall Achvieved: " + overall_achv +
                    "\nOverall in progress: " + overall_ip +
                    "\nCompulsory achieved: " + comp_achv +
                    "\nCompulsory in progress: " + comp_ip +
                    "\nOptional achieved: " + opt_achv +
                    "\nOptional in progress: " + opt_ip);
        }

         /* Setting up the PieChart */
        // the list holds all entries of the chart
        List<PieEntry> entries = new ArrayList<>();
        // will hold the colours to use
        int[] col;

        if(compComplete && optComplete) {
            Log.v(LOGTAG, "Graph says: This course is completed.");
            col = new int[] {   getResources().getColor(R.color.markCompleted)  };
            entries.add(new PieEntry(optcomp_credits, String.valueOf(optcomp_credits)));
        } else if (compComplete && optInProg) {
            Log.v(LOGTAG, "Graph says: This course is in progress completed");
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)
            };
            int todo =  optcomp_only - opt_achv - opt_ip;
            entries.add(new PieEntry(todo, String.valueOf(todo)));
            entries.add(new PieEntry(opt_ip, String.valueOf(overall_ip)));
            entries.add(new PieEntry(comp_achv + opt_achv, String.valueOf(comp_achv+opt_achv)));
        } else if(compComplete) {
            Log.v(LOGTAG, "Graph says: This course is satisfied");
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)
            };
            entries.add(new PieEntry(optcomp_only, String.valueOf(optcomp_only)));
            entries.add(new PieEntry(comp_ip, String.valueOf(comp_ip)));
            entries.add(new PieEntry(comp_achv, String.valueOf(comp_achv)));
        } else if (compInProg && optComplete) {
            Log.v(LOGTAG, "Graph says: This course is completed, but on the wrong side.");
            col = new int[] {
                    getResources().getColor(R.color.markCompleted),
                    getResources().getColor(R.color.markBachelor),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)
            };
            entries.add(new PieEntry(optcomp_only, String.valueOf(optcomp_only)));
            int todo = comp_credits - comp_achv - comp_ip;
            entries.add(new PieEntry(todo, String.valueOf(todo)));
            entries.add(new PieEntry(comp_ip, String.valueOf(comp_ip)));
            entries.add(new PieEntry(comp_achv, String.valueOf(comp_achv)));
        } else if (compInProg && optInProg) {
            Log.v(LOGTAG, "Graph says: This course is in progress twice.");
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted),
                    getResources().getColor(R.color.markBachelor),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)
            };
            int todo = optcomp_only - opt_achv - opt_ip;
            entries.add(new PieEntry(todo, String.valueOf(todo)));
            entries.add(new PieEntry(opt_ip, String.valueOf(opt_ip)));
            entries.add(new PieEntry(opt_achv, String.valueOf(opt_achv)));
            int compTodo = comp_credits - comp_achv - comp_ip;
            entries.add(new PieEntry(compTodo, String.valueOf(compTodo)));
            entries.add(new PieEntry(comp_ip, String.valueOf(comp_ip)));
            entries.add(new PieEntry(comp_achv, String.valueOf(comp_achv)));
        } else if (compInProg) {
            Log.v(LOGTAG, "Graph says: This course is in little progress");
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted)
            };
            entries.add(new PieEntry(optcomp_only, String.valueOf(optcomp_only)));
            entries.add(new PieEntry(comp_ip, String.valueOf(comp_ip)));
            entries.add(new PieEntry(comp_achv, String.valueOf(comp_achv)));
        } else if (optComplete) {
            Log.v(LOGTAG, "Graph says: No compuslory. But all optional.");
            col = new int[] {
                    getResources().getColor(R.color.markCompleted),
                    getResources().getColor(R.color.markBachelor)
            };
            entries.add(new PieEntry(optcomp_only, String.valueOf(optcomp_only)));
            entries.add(new PieEntry(comp_credits, String.valueOf(comp_credits)));
        } else if (optInProg) {
            Log.v(LOGTAG, "Graph says: No compulsory and a bit optional.");
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markInProgress),
                    getResources().getColor(R.color.markCompleted),
                    getResources().getColor(R.color.markBachelor)
            };
            int todo = optcomp_only - opt_achv - opt_ip;
            entries.add(new PieEntry(todo, String.valueOf(todo)));
            entries.add(new PieEntry(opt_ip, String.valueOf(opt_ip)));
            entries.add(new PieEntry(opt_achv, String.valueOf(opt_achv)));
            entries.add(new PieEntry(comp_credits, String.valueOf(comp_credits)));
        } else {
            Log.v(LOGTAG, "Graph says: There is no course.");
            col = new int[] {
                    getResources().getColor(R.color.markMarked),
                    getResources().getColor(R.color.markBachelor)
            };
            entries.add(new PieEntry(optcomp_only, String.valueOf(optcomp_only)));
            entries.add(new PieEntry(comp_credits, String.valueOf(comp_credits)));
        }

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
        String gradeText = String.format("%.2f", grade);
        if (grade == Utils.NO_GRADE){
            gradeText = "--";
        }
        graph.setCenterText(gradeText);
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
        switch (id) {
            case 0:
                String[] courseData = SQL_Database.COURSES_COLUMNS;
                String courseSelection = SQL_Database.COURSES_COLUMN_MODULE + " = " + "'" + module_code + "'";
                return new CursorLoader(this, DataProvider.COURSES_DB_URI, courseData, courseSelection, null, SQL_Database.COURSES_COLUMN_STATE + " DESC");
            case 1:
                if (args != null) {
                    String c_id = Long.toString(args.getLong("id"));
                    String[] col = {SQL_Database.COURSES_COLUMN_GRADE};
                    String selection = SQL_Database.COURSES_COLUMN_ID + "=" + c_id;
                    return new CursorLoader(this, DataProvider.COURSES_DB_URI, col, selection, null, null);
                } else {
                    throw new IllegalArgumentException("No ID was given.");
                }
            default:
                throw new IllegalArgumentException("unknown cursor id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case 0:
                courses = data;
                adapter.changeCursor(data);
                initGraph();
                if(data.getCount() != 0) {
                    findViewById(R.id.moduleRemoveCourse).setEnabled(true);
                } else{
                    findViewById(R.id.moduleRemoveCourse).setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case 0:
                if (courses != null) courses.close();
                adapter.changeCursor(null);
                break;
        }
    }

    @Override
    public void onGradeDialogPositiveClick(DialogFragment d, Double g, boolean is_module) {
        if(is_module) {
            ((com.github.mikephil.charting.charts.PieChart)
                    findViewById(R.id.modulePieChart)).setCenterText(String.format("%.2f", g));
            findViewById(R.id.modulePieChart).invalidate();
        } else {
            TextView grade = (TextView) dialog.findViewById(R.id.course_grade);
            grade.setText(String.format("%.2f", g));
        }
    }
}
