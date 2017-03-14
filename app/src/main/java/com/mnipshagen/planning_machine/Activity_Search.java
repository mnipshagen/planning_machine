package com.mnipshagen.planning_machine;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

/**
 * Created by nipsh on 19/02/2017.
 */

public class Activity_Search extends Activity_Base implements LoaderManager.LoaderCallbacks<Cursor> {
    // hold a reference to our recycler view
    private RecyclerView rv;
    private Spinner moduleList;
    private EditText courseTitle;
    private boolean isExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.search_app_bar);
        setActionBarTitle(R.string.title_search);
        isExpanded = false;

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

        final ImageButton expand = (ImageButton) findViewById(R.id.search_expand);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Spinner terms = (Spinner) findViewById(R.id.searchSpinnerTerm);
                final TextInputLayout taughtBy = (TextInputLayout) findViewById(R.id.searchTaughtByLayout);
                final LinearLayout years = (LinearLayout) findViewById(R.id.yearLayout);

                if(!isExpanded) {
                    isExpanded = true;

                    Animation a = new AlphaAnimation(0f, 1f);
                    a.setDuration(300);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            terms.setVisibility(View.VISIBLE);
                            taughtBy.setVisibility(View.VISIBLE);
                            years.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mAppBar.startAnimation(a);

                    ValueAnimator va;
                    int oldH = mAppBar.getHeight();
                    int newH = (int) (mAppBar.getHeight() * 1.8);
                    va = ValueAnimator.ofInt(oldH, newH);
                    va.setDuration(300);
                    va.setInterpolator(new AccelerateDecelerateInterpolator());
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mAppBar.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                            mAppBar.requestLayout();
                        }
                    });
                    va.start();
                } else {
                    isExpanded = false;

                    Animation a = new AlphaAnimation(1f, 0f);
                    a.setDuration(300);
                    a.setFillBefore(true);
                    a.setFillAfter(true);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            terms.setVisibility(View.GONE);
                            taughtBy.setVisibility(View.GONE);
                            years.setVisibility(View.GONE);
                            mAppBar.clearAnimation();
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    mAppBar.startAnimation(a);

                    ValueAnimator va;
                    int oldH = mAppBar.getHeight();
                    int newH = (int) (mAppBar.getHeight() / 1.8);
                    va = ValueAnimator.ofInt(oldH, newH);
                    va.setDuration(300);
                    va.setInterpolator(new AccelerateDecelerateInterpolator());
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mAppBar.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                            mAppBar.requestLayout();
                        }
                    });
                    va.start();
                }

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
        Spinner terms = (Spinner) findViewById(R.id.searchSpinnerTerm);
        EditText taughtBy = (EditText) findViewById(R.id.searchTaughtBy);
        EditText yearFrom = (EditText) findViewById(R.id.searchYearFrom);
        EditText yearTo = (EditText) findViewById(R.id.searchYearTo);

        // save module which we want to search for and course name
        String module = moduleList.getSelectedItem().toString();
        String course = courseTitle.getText().toString().replace(" ","%");
        String term = terms.getSelectedItem().toString();
        String teachers = taughtBy.getText().toString().replace(" ","%").replace(",","%");
        String yearStart = yearFrom.getText().toString();
        String yearEnd = yearTo.getText().toString();

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

        if (terms.getSelectedItemPosition() != 0) {
            if( !selection.equals("")) {
                selection = selection.concat(" AND ");
            }
            selection = selection.concat(SQL_Database.COURSE_COLUMN_TERM + " = '" + term + "'");
        }

        if (!teachers.equals("")) {
            if( !selection.equals("")) {
                selection = selection.concat(" AND ");
            }
            selection = selection.concat(SQL_Database.COURSE_COLUMN_TEACHERS_STR + " LIKE '%" + teachers + "%'");
        }

        if (!yearStart.equals("")){
            if( !selection.equals("")) {
                selection = selection.concat(" AND ");
            }
            selection = selection.concat(SQL_Database.COURSE_COLUMN_YEAR + ">= ' " + yearStart + "'");
        }

        if (!yearEnd.equals("")){
            if( !selection.equals("")) {
                selection = selection.concat(" AND ");
            }
            selection = selection.concat(SQL_Database.COURSE_COLUMN_YEAR + "<= '" + yearEnd + "'");
        }
        Log.v("SEARCHY SEARCH", "We built this selection: " + selection);

        Bundle args = new Bundle();
        args.putString("selection", selection);
        getSupportLoaderManager().destroyLoader(0);
        getSupportLoaderManager().initLoader(0, args, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                String selection = "";
                if (args != null) {
                    selection = args.getString("selection");
                }
                String[] columns = SQL_Database.COURSE_COLUMNS;

                return new CursorLoader(this, DataProvider.COURSE_DB_URI,
                                        columns,
                                        selection,
                                        null,
                                        SQL_Database.COURSE_COLUMN_YEAR + " DESC");
            default:
                throw new IllegalArgumentException("unknown cursor id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case 0:
                ((Adapter_Search) rv.getAdapter()).changeCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case 0:
                ((Adapter_Search) rv.getAdapter()).changeCursor(null);
                break;
        }
    }
}
