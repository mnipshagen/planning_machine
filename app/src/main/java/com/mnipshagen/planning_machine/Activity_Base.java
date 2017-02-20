package com.mnipshagen.planning_machine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A base activity which wraps the drawer around all activities.
 */

public class Activity_Base extends AppCompatActivity{
    // this will store the reference to the drawer layout
    protected DrawerLayout mDrawerLayout;
    // and this to the content frame which holds the actual activities
    protected FrameLayout content;

    // The reference to the toolbar, to initialise the action bar
    protected Toolbar toolbar;
    // Holding a reference to the title so that we can change it dynamically
    protected TextView title;

    /**
     * This will intercept the setContentView method called upon creation of an activity and
     * inflate the drawerlayout and initialise the actionbar, then inflating the content frame
     * with the activity layout referenced by layoutResID
     * {@link super.setContentView}
     * @param layoutResID the ID to the layout of the displaying activity
     */
    @Override
    public void setContentView(int layoutResID) {
        // inflate the drawerlayout
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_wrapper, null);
        content = (FrameLayout) mDrawerLayout.findViewById(R.id.content);
        // inflate the content frame with the activity layout
        getLayoutInflater().inflate(layoutResID, content, true);
        super.setContentView(mDrawerLayout);

        // initialise action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.title);
        // initialise the navigation drawer
        initNavigationDrawer();
    }

//    @Override
//    public void onBackPressed() {
//        if(!this.getClass().getSimpleName().equals("Activity_Overview")){
//            init(1);
//        }
//        else {
//            super.onBackPressed();
//        }
//    }

    /**
     * takes care of initialising the navigation drawer, with an listener for item clicks,
     * which initialises the chosen activity
     */
    private void initNavigationDrawer() {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.drawer_drawer);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        // depending on what was clicked, the drawer builds the corresponding intent
                        // and then closes the drawer
                        switch (id) {
                            case R.id.nav_overview:
                                init(1);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_search:
                                init(2);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_settings:
                                init(3);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_webpage:
                                init(4);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_about:
                                init(5);
                                mDrawerLayout.closeDrawers();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(),
                                        "Damn, what did you do?!",
                                        Toast.LENGTH_LONG).show();
                                mDrawerLayout.closeDrawers();
                                break;
                        }
                        return true;
                    }
                });
        // here we set the upper header part of the navigation drawer
        // TODO initialise user information or whatever should be displayed there
        View header = mNavigationView.getHeaderView(0);
        TextView head_text = (TextView) header.findViewById(R.id.header_text);
        head_text.setText("xmuster@uni-osnabrueck.de");

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,           /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,    /* "open drawer" description for accessibility */
                R.string.drawer_close)  /* "close drawer" description for accessibility */ {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * starts the activity which was chosen in the drawer
     * @param init the chosen item
     */
    private void init(int init) {
        Intent start = null;

        switch (init) {
            // Activity_Overview was selected
            case 1:
                if(!this.getClass().getSimpleName().equals("Activity_Overview")) {
                    start = new Intent(this, Activity_Overview.class);
                }
                break;
            // Search was selected
            case 2:
                if(!this.getClass().getSimpleName().equals("Activity_Search")) {
                    start = new Intent(this, Activity_Search_Card.class);
                }
                break;
            // Settings was selected
            case 3:
                if(!this.getClass().getSimpleName().equals("Activity_Settings")){}
                Toast.makeText(getApplicationContext(),
                        "Settings Selected",
                        Toast.LENGTH_SHORT).show();
                start = new Intent(this, Activity_Overview.class);
                break;
            // IKW Webpage was selected
            case 4:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ikw.uos.de"));
                startActivity(browserIntent);
                //TODO call activity
                break;
            // About was selected
            case 5:
                if(!this.getClass().getSimpleName().equals("Activity_About")) {
                    start = new Intent(this, Activity_About.class);
                }
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "How.... How did you do this?!",
                        Toast.LENGTH_LONG).show();
                break;
        }
        if (start != null) {
            startActivity(start);
        } else {
            //TODO do something when error happens
        }
    }

    /**
     * A way to set the title of the actionbar
     * (though title.setText() could be called from all activities)
     * @param t the resource id pointing to the string
     */
    protected void setActionBarTitle(int t) {
        title.setText(t);
    }
    /**
     * A way to set the title of the actionbar
     * Should not be used in final release!! All strings should be handled by the resources
     * (though title.setText() could be called from all activities)
     * @param s the string to set the title to
     */
    protected void setActionBarTitle(String s) {
        title.setText(s);
    }
}
