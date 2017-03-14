package com.mnipshagen.planning_machine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.ArrayList;
import java.util.List;

/**
 * The Activity corresponding to the 'About' drawer option.
 * Of no use so far. Only for test & debug purposes.
 */

public class Activity_About extends Activity_Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setActionBarTitle("About aka debug for now");

        ((TextView)findViewById(R.id.about_description)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.about_github_link)).setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.about_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setInterpolator(new OvershootInterpolator());
                anim.setDuration(1200);

                final ImageView icon = (ImageView) findViewById(R.id.about_icon);
                icon.startAnimation(anim);
            }
        });

        Libs libs = new Libs(this);
        List<Library> libraryList = new ArrayList<>();

        Library sqlite = new Library();
        sqlite.setAuthor("Jeff Gilfelt");
        sqlite.setAuthorWebsite("https://github.com/jgilfelt");
        sqlite.setLibraryName("SQLite Asset-helper");
        sqlite.setLibraryDescription("An Android helper class to manage database creation and version management using an application's raw asset files.");

        libraryList.add(libs.getLibrary("MPAndroidChart"));
        libraryList.add(sqlite);
        libraryList.add(libs.getLibrary("Android Floating Action Button"));
        libraryList.add(libs.getLibrary("AboutLibraries"));

        RecyclerView card_host = (RecyclerView) findViewById(R.id.about_library_card_host);
        card_host.setLayoutManager(new LinearLayoutManager(this));
        card_host.setAdapter(new Adapter_About(libraryList, this));
    }
}
