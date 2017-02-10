package com.mnipshagen.planning_machine;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Activity_Main extends AppCompatActivity {

    // this will reference our great DrawerLayout
    private DrawerLayout mDrawerLayout;
    // and this will be our toolbar made actionbar
    Toolbar toolbar;
    // setting up our Databse
    SQL_Database mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // do what is to do
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up reference the drawer xml
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        // initialise toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDB = new SQL_Database(this);

        // since the app was just started up, initialise Overview
        init(1);
        // and now set up all there is to set up for the nav drawer
        initNavigationDrawer();
    }

    private void initNavigationDrawer() {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

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

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void init(int init) {
        //TODO implement nav calls
        Fragment content = null;

        switch (init) {
            // Overview was selected
            case 1:
                content = new Fragment_Overview();
                ((TextView) findViewById(R.id.title)).setText(R.string.title_overview);
                break;
            // Search was selected
            case 2:
                ((TextView) findViewById(R.id.title)).setText(R.string.title_search);
                break;
            // Settings was selected
            case 3:
                Toast.makeText(getApplicationContext(),
                        "Settings Selected",
                        Toast.LENGTH_SHORT).show();
                break;
            // IKW Webpage was selected
            case 4:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ikw.uos.de"));
                startActivity(browserIntent);
                break;
            // About was selected
            case 5:
                Toast.makeText(getApplicationContext(),
                        "About Selected",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "How.... How did you do this?!",
                        Toast.LENGTH_LONG).show();
                break;
        }
        if (content != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, content).commit();
        }
    }

}