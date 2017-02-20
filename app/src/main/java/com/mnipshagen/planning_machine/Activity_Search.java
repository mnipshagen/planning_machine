//package com.mnipshagen.planning_machine;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Switch;
//
///**
// * The activity to search for coursed in the database
// */
//public class Activity_Search extends Activity_Base {
//    // hold a reference to our recycler view
//    private RecyclerView rv;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//        setActionBarTitle(R.string.title_search);
//
//        // when enter is pressed in the course search field, start the search
//        findViewById(R.id.searchCourseTitle).setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                switch(keyCode) {
//                    case KeyEvent.KEYCODE_ENTER:
//                        startSearch();
//                        break;
//                    default:
//                        return false;
//                }
//                return true;
//            }
//        });
//
//
//        rv = (RecyclerView) findViewById(R.id.searchRecycler);
//        // make it pretty make it nice
//        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        rv.setItemAnimator(new DefaultItemAnimator());
//        // for now the recycler view holds no data and we have no cursor for it
//        Adapter_Search adapter = new Adapter_Search(null);
//        rv.setAdapter(adapter);
//        rv.setLayoutManager(new LinearLayoutManager(this));
//        // on touchy touchy display dialog with more information or the direct way to add the course
//        rv.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Adapter_Search.ViewHolder vh = (Adapter_Search.ViewHolder) rv.findViewHolderForLayoutPosition(position);
//                        // TODO
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, final int position) {
//                        // get the viewholder of the clicked item
//                        Adapter_Search.ViewHolder vh = (Adapter_Search.ViewHolder) rv.findViewHolderForLayoutPosition(position);
//
//                        final String[] modules = vh.fields.getText().toString().split(",");
//                        modules[0] = modules[0].substring(3);
//
//                        // display a dialog to add the course to the module
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                        builder.setTitle("Add to which Module?")
//                                .setSingleChoiceItems(modules, 0, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // when one was selected, write it into the database
//                                        // get database
//                                        SQLiteDatabase db = SQL_Database.getInstance(getParent()).getWritableDatabase();
//                                        // and get the adapter from the recycler view to collect the sql id from it
//                                        Adapter_Search adapter
//                                                = (Adapter_Search) ((RecyclerView) getParent().findViewById(R.id.searchRecycler)).getAdapter();
//                                        // collect the "_id"
//                                        long rowID = adapter.getItemId(position);
//                                        // all the columns we need to copy
//                                        String columns =    SQL_Database.COURSES_COLUMN_COURSE_ID + "," +
//                                                SQL_Database.COURSE_COLUMN_COURSE + "," +
//                                                SQL_Database.COURSE_COLUMN_COURSE_DESC + "," +
//                                                SQL_Database.COURSE_COLUMN_ECTS + "," +
//                                                SQL_Database.COURSE_COLUMN_TERM + "," +
//                                                SQL_Database.COURSE_COLUMN_YEAR + "," +
//                                                SQL_Database.COURSE_COLUMN_CODE + "," +
//                                                SQL_Database.COURSE_COLUMN_TYPE + "," +
//                                                SQL_Database.COURSE_COLUMN_INFIELD_TYPE + "," +
//                                                SQL_Database.COURSE_COLUMN_TEACHERS + "," +
//                                                SQL_Database.COURSE_COLUMN_TEACHERS_STR + "," +
//                                                SQL_Database.COURSE_COLUMN_FIELDS + "," +
//                                                SQL_Database.COURSE_COLUMN_FIELDS_STR + "," +
//                                                SQL_Database.COURSE_COLUMN_SINGLE_FIELD;
//                                        // we need to encode the module name to its corresponding code
//                                        // using contains as there might be blanks and (PM) inside module name
//                                        String module;
//                                        if (modules[which].contains("ficial")) {
//                                            module = "KI";
//                                        }else if (modules[which].contains("roinfo")) {
//                                            module = "NI";
//                                        }else if (modules[which].contains("psych")) {
//                                            module = "KNP";
//                                        }else if (modules[which].contains("lingu")) {
//                                            module = "CL";
//                                        }else if (modules[which].contains("bio")) {
//                                            module = "NW";
//                                        }else if (modules[which].contains("sophy")) {
//                                            module = "PHIL";
//                                        }else if (modules[which].contains("ompute")) {
//                                            module = "INF";
//                                        }else if (modules[which].contains("athem")) {
//                                            module = "MAT";
//                                        }else {
//                                            module = "OPEN";
//                                        }
//
//                                        db.execSQL("INSERT INTO " + SQL_Database.COURSES_TABLE_NAME +
//                                                "(" + columns + ") SELECT " + columns + " FROM " +
//                                                SQL_Database.COURSE_TABLE_NAME + " WHERE _id = " + rowID);
//                                        db.execSQL("UPDATE " + SQL_Database.COURSES_TABLE_NAME + " SET " +
//                                                SQL_Database.COURSES_COLUMN_MODULE + "= '" + module +
//                                                "' WHERE _id = (SELECT MAX(_id) FROM courses)");
//                                    }
//                                });
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                    }
//                }));
//        // on fab click start the search
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_FAB);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startSearch();
//            }
//        });
//    }
//
//    /**
//     * search the database with all the filters applied
//     */
//    private void startSearch() {
//        //findViewById(R.id.searchCourseTitle).clearFocus();
//        // hide the keyboard when search is started
//        if (this.getCurrentFocus() != null) {
//            InputMethodManager imm =
//                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//        }
//
//        Spinner spinner = (Spinner) findViewById(R.id.searchSpinnerModule);
//        Switch switchComp = (Switch) findViewById(R.id.searchSwitchCompulsory);
//        EditText courseTitle = (EditText) findViewById(R.id.searchCourseTitle);
//
//        // save module which we want to search for and course name
//        String module = spinner.getSelectedItem().toString();
//        String course = courseTitle.getText().toString();
//        // all the columns to search for
//        String[] columns = {
//                SQL_Database.COURSE_COLUMN_ID,
//                SQL_Database.COURSE_COLUMN_COURSE,
//                SQL_Database.COURSE_COLUMN_COURSE_DESC,
//                SQL_Database.COURSE_COLUMN_ECTS,
//                SQL_Database.COURSE_COLUMN_TERM,
//                SQL_Database.COURSE_COLUMN_YEAR,
//                SQL_Database.COURSE_COLUMN_CODE,
//                SQL_Database.COURSE_COLUMN_TYPE,
//                SQL_Database.COURSE_COLUMN_INFIELD_TYPE,
//                SQL_Database.COURSE_COLUMN_TEACHERS_STR,
//                SQL_Database.COURSE_COLUMN_FIELDS_STR   };
//
//        // selection string dependent on the filters
//        String selection = "";
//
//        if( !course.equals("") ){
//            selection = selection.concat(SQL_Database.COURSE_COLUMN_COURSE + " LIKE '%" + course + "%'");
//        }
//
//        if( spinner.getSelectedItemPosition() != 0) {
//            if( !selection.equals("") ) {
//                selection = selection.concat(" AND ");
//            }
//            selection = selection.concat(SQL_Database.COURSE_COLUMN_FIELDS_STR + " LIKE '%" + module + "%'");
//        }
//
//        if( switchComp.isChecked() ) {
//            if( !selection.equals("") ) {
//                selection = selection.concat(" AND ");
//            }
//            selection = selection.concat(SQL_Database.COURSE_COLUMN_INFIELD_TYPE + " = 'PM'");
//        }
//        ((AppBarLayout)findViewById(R.id.search_app_bar)).setExpanded(false);
//        // and start the background search. background to keep the UI thread from being stuck by it
//        Async_Search search = new Async_Search(this);
//        search.execute(columns, new String[]{selection});
//
//    }
//
//    /**
//     * once the async search is done this is called
//     * @param results the cursor which holds the results
//     */
//    public void onSearchCompleted(Cursor results) {
//        ((Adapter_Search) rv.getAdapter()).changeCursor(results);
//    }
//}
