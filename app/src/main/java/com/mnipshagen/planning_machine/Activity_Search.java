package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.mnipshagen.planning_machine.DataProviding.Async_Search;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

/**
 * Created by nipsh on 19/02/2017.
 */

public class Activity_Search extends Activity_Base {
    // hold a reference to our recycler view
    private RecyclerView rv;
    private Spinner moduleList;
    private EditText courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.search_app_bar);
        setActionBarTitle(R.string.title_search);

        moduleList = (Spinner) findViewById(R.id.searchSpinnerModule);
        courseTitle = (EditText) findViewById(R.id.searchCourseTitle);

        Bundle args = getIntent().getExtras();
        Boolean start = false;
        if (args != null) {
            courseTitle.setText(args.getString("course_title"));
            String module_code = args.getString("module_code");
            moduleList.setSelection(ModuleTools.codeToListID(module_code));
            start = args.getBoolean("start");
        }

        // when enter is pressed in the course search field, start the search
        findViewById(R.id.searchCourseTitle).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
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
        Adapter_Search adapter = new Adapter_Search(null, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setOnFlingListener(new RecyclerView.OnFlingListener() {
//            @Override
//            public boolean onFling(int velocityX, int velocityY) {
//                if (velocityY < 0)
//                    mAppBar.setExpanded(true);
//                return false;
//            }
//        });

        // on fab click start the search
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });

        if(start) {startSearch();}
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

        Switch switchComp = (Switch) findViewById(R.id.searchSwitchCompulsory);

        // save module which we want to search for and course name
        String module = moduleList.getSelectedItem().toString();
        String course = courseTitle.getText().toString().replace(" ","%");
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

        if( moduleList.getSelectedItemPosition() != 0) {
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

//        CollapsingToolbarLayout mToolbar = (CollapsingToolbarLayout) findViewById(R.id.search_toolbar);
//        AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.search_app_bar);
//
//        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
//        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
//
//        Log.v("SEARCH", "Last visible item pos: " + ((LinearLayoutManager)rv.getLayoutManager()).findLastCompletelyVisibleItemPosition());
//        Log.v("SEARCH", "Cursor count: " + results.getCount());
//
//        if (((LinearLayoutManager)rv.getLayoutManager()).findLastCompletelyVisibleItemPosition() == results.getCount() - 1) {
//            appBarLayoutParams.setBehavior(null);
//            toolbarLayoutParams.setScrollFlags(0);
//        } else {
//            mAppBar.setExpanded(false);
//            toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
//            appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
//        }
//
//        mToolbar.setLayoutParams(toolbarLayoutParams);
//        mAppBar.setLayoutParams(appBarLayoutParams);
    }
}
