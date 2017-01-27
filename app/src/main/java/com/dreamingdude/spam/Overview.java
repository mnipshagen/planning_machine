package com.dreamingdude.spam;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class Overview extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private List<Module> moduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // always call super
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // so it says Overview in the Actionbar
        setTitle(R.string.title_overview);

        // initialise toolabr and make it an awesome action bar!
        Toolbar toolbar = (Toolbar) findViewById(R.id.overview_toolbar);
        setSupportActionBar(toolbar);

        // set up recycler view to dynamically setup the cards
        recyclerView = (RecyclerView) findViewById(R.id.overview_recycler_view);

        // set up the list of all Modules and put them into the adapter of RecylcerView
        moduleList = new ArrayList<>();
        adapter = new ModuleAdapter(this, moduleList);

        // initialise layout manager as gridmanager to display cards in grids
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        // make it fancy
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // fill Module list and update Adapter
        prepareModules();

        // redundant
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Set up nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.overview_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // ehm??
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // initialise nav view
        NavigationView navigationView = (NavigationView) findViewById(R.id.overview_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Fills up the module list for fancy information and updates the adapter
     */
    private void prepareModules(){
        Module m = new Module("Computational Linguistics", "CL", 8, 12);
        moduleList.add(m);

        m = new Module("Computer Science", "INF", 9, 9);
        moduleList.add(m);

        m = new Module("Cognitive (Neuro-)Psychology", "KNP", 8, 8);
        moduleList.add(m);

        m = new Module("Artificial Intelligence", "KI", 8, 12);
        moduleList.add(m);

        m = new Module("Mathematics", "MAT", 9, 9);
        moduleList.add(m);

        m = new Module("Neuroinformatics", "NI", 12, 12);
        moduleList.add(m);

        m = new Module("Neuroscience", "NW", 8, 12);
        moduleList.add(m);

        m = new Module("Philosophy on Mind and Cognition", "PHIL", 10, 8);
        moduleList.add(m);

        m = new Module("Logic", "LOG", 6, 6);
        moduleList.add(m);

        m = new Module("Statistics", "SD", 8, 8);
        moduleList.add(m);

        m = new Module("Foundations of Cognitive Science", "PWB", 3, 3);
        moduleList.add(m);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.overview_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {

        } else if (id == R.id.nav_search) {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.overview_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
