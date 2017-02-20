package com.mnipshagen.planning_machine;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
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
import android.widget.Spinner;
import android.widget.Switch;

/**
 * Created by nipsh on 19/02/2017.
 */

public class Activity_Search_Card extends Activity_Base {
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
        Adapter_SearchCard adapter = new Adapter_SearchCard(null, rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv,
                        new RecyclerItemClickListener.OnItemClickListener() {

                            @Override
                            public void onItemClick(final View view, int position) {
                                final Adapter_SearchCard.ViewHolder vh
                                        = (Adapter_SearchCard.ViewHolder) rv.findViewHolderForLayoutPosition(position);
                                ValueAnimator valueAnimator;
                                if (vh.originalHeight == 0) {
                                    vh.originalHeight = vh.itemView.getHeight();
                                }

                                if (vh.expanded_content.getVisibility() == View.GONE) {
                                    vh.expanded_content.setVisibility(View.VISIBLE);
                                    vh.itemView.setActivated(true);
                                    TransitionManager.beginDelayedTransition(rv);
                                    valueAnimator = ValueAnimator.ofInt(vh.originalHeight, vh.originalHeight + (int) (vh.originalHeight * 2.0));
                                } else {
                                    vh.itemView.setActivated(false);
                                    valueAnimator = ValueAnimator.ofInt(vh.originalHeight + (int) (vh.originalHeight * 2.0), vh.originalHeight);
                                    Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
                                    a.setDuration(300);
                                    // Set a listener to the animation and configure onAnimationEnd
                                    a.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {}

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            vh.expanded_content.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {}
                                    });

                                    // Set the animation on the custom view
                                    vh.expanded_content.startAnimation(a);
                                }
                                valueAnimator.setDuration(200);
                                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        view.requestLayout();
                                    }
                                });
                                valueAnimator.start();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {}
                        }
                )
        );
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
        ((AppBarLayout)findViewById(R.id.search_app_bar)).setExpanded(false);
        // and start the background search. background to keep the UI thread from being stuck by it
        Async_Search search = new Async_Search(this);
        search.execute(columns, new String[]{selection});
    }
    /**
     * once the async search is done this is called
     * @param results the cursor which holds the results
     */
    public void onSearchCompleted(Cursor results) {
        ((Adapter_SearchCard) rv.getAdapter()).changeCursor(results);
    }
}
