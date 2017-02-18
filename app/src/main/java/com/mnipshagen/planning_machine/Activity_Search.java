package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

/**
 * The activity to search for coursed in the database
 */
public class Activity_Search extends Activity_Base {
    // hold a reference to our recycler view
    private RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setActionBarTitle(R.string.title_search);

        // when enter is pressed in the course search field, start the search
        findViewById(R.id.searchCourseTitle).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch(keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        startSearch();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


        rv = (RecyclerView) findViewById(R.id.searchRecycler);
        // make it pretty make it nice
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        // for now the recycler view holds no data and we have no cursor for it
        Adapter_Search adapter = new Adapter_Search(null);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        // on touchy touchy display dialog with more information or the direct way to add the course
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Adapter_Search.ViewHolder vh = (Adapter_Search.ViewHolder) rv.findViewHolderForLayoutPosition(position);
                        // TODO
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // get the viewholder of the clicked item
                        Adapter_Search.ViewHolder vh = (Adapter_Search.ViewHolder) rv.findViewHolderForLayoutPosition(position);
                        // display a dialog to add the course to the module
                        DialogFragment addToModule = new Dialog_AddCourseToModule();
                        // give it the position of the course and all modules it fits into
                        Bundle args = new Bundle();
                        args.putInt("Position", position);
                        args.putString("ModuleList",vh.fields.getText().toString());
                        // and apply it to the dialog
                        addToModule.setArguments(args);
                        addToModule.show(getSupportFragmentManager(), "Add to Module");
                    }
                }));
        // on fab click start the search
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });
    }

    /**
     * search the database with all the filters applied
     */
    private void startSearch() {
        //findViewById(R.id.searchCourseTitle).clearFocus();
        // hide the keyboard when search is started
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

        Spinner spinner = (Spinner) findViewById(R.id.searchSpinnerModule);
        Switch switchComp = (Switch) findViewById(R.id.searchSwitchCompulsory);
        EditText courseTitle = (EditText) findViewById(R.id.searchCourseTitle);

        // save module which we want to search for and course name
        String module = spinner.getSelectedItem().toString();
        String course = courseTitle.getText().toString();
        // all the columns to search for
        String[] columns = {
                SQL_Database.COURSE_COLUMN_ID,
                SQL_Database.COURSE_COLUMN_COURSE,
                SQL_Database.COURSE_COLUMN_COURSE_DESC,
                SQL_Database.COURSE_COLUMN_ECTS,
                SQL_Database.COURSE_COLUMN_TERM,
                SQL_Database.COURSE_COLUMN_YEAR,
                SQL_Database.COURSE_COLUMN_CODE,
                SQL_Database.COURSE_COLUMN_TYPE,
                SQL_Database.COURSE_COLUMN_INFIELD_TYPE,
                SQL_Database.COURSE_COLUMN_TEACHERS_STR,
                SQL_Database.COURSE_COLUMN_FIELDS_STR   };

        // selection string dependent on the filters
        String selection = "";

        if( !course.equals("") ){
            selection = selection.concat(SQL_Database.COURSE_COLUMN_COURSE + " LIKE '%" + course + "%'");
        }

        if( spinner.getSelectedItemPosition() != 0) {
            if( !selection.equals("") ) {
                selection = selection.concat(" AND ");
            }
            selection = selection.concat(SQL_Database.COURSE_COLUMN_FIELDS_STR + " LIKE '%" + module + "%'");
        }

        if( switchComp.isChecked() ) {
            if( !selection.equals("") ) {
                selection = selection.concat(" AND ");
            }
            selection = selection.concat(SQL_Database.COURSE_COLUMN_INFIELD_TYPE + " = 'PM'");
        }
        // and start the background search. background to keep the UI thread from being stuck by it
        Async_Search search = new Async_Search(this);
        search.execute(columns, new String[]{selection});

    }

    /**
     * once the async search is done this is called
     * @param results the cursor which holds the results
     */
    public void onSearchCompleted(Cursor results) {
        ((Adapter_Search) rv.getAdapter()).changeCursor(results);
    }
}
