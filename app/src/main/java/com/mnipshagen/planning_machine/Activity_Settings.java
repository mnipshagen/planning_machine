package com.mnipshagen.planning_machine;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

import java.util.Random;

public class Activity_Settings extends Activity_Base {
    final private String bio = "NW";
    final private String ai = "KI";
    final private String ninf = "NI";
    final private String cnp = "KNP";
    final private String ling = "CL";
    final private String phi = "PHIL";
    final private String inf = "INF";
    final private String math = "MAT";
    final private String open = "OPEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setActionBarTitle("Settings");
        final int[] courses = {2544, 2542, 2491, 2492, 2497, 2568, 2567, 2574, 2573, 2566, 2695, 2696, 2630, 2631, 2632, 2629, 2635, 2636};
        final String[] module = {bio, open, math, inf, open, ling, ai, phi, phi, bio, cnp, cnp, open, open, open, ninf, bio, bio};
        final float[] grades = {1.0f, 1.3f, 1.7f, 2.0f, 2.3f, 2.7f, 3.0f, 3.3f};

        Button reset = (Button) findViewById(R.id.settings_reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rnd = new Random();
                Cursor c = getContentResolver().query(
                        DataProvider.COURSES_DB_URI,
                        new String[] {SQL_Database.COURSES_COLUMN_ID},
                        null, null, null
                );
                c.moveToPosition(-1);
                while(c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ID));
                    getContentResolver().delete(DataProvider.COURSES_DB_URI, SQL_Database.COURSES_COLUMN_ID + " = " + id, null);
                }
                c.close();
                if (courses.length != module.length)
                    throw new RuntimeException("Something does not add up here");

                String[] columns = {
                        SQL_Database.COURSES_COLUMN_COURSE_ID,
                        SQL_Database.COURSE_COLUMN_COURSE,
                        SQL_Database.COURSE_COLUMN_COURSE_DESC,
                        SQL_Database.COURSE_COLUMN_ECTS,
                        SQL_Database.COURSE_COLUMN_TERM,
                        SQL_Database.COURSE_COLUMN_YEAR,
                        SQL_Database.COURSE_COLUMN_CODE,
                        SQL_Database.COURSE_COLUMN_TYPE,
                        SQL_Database.COURSE_COLUMN_INFIELD_TYPE,
                        SQL_Database.COURSE_COLUMN_TEACHERS,
                        SQL_Database.COURSE_COLUMN_TEACHERS_STR,
                        SQL_Database.COURSE_COLUMN_FIELDS,
                        SQL_Database.COURSE_COLUMN_FIELDS_STR,
                        SQL_Database.COURSE_COLUMN_SINGLE_FIELD
                };

                for (int i = 0; i < courses.length; i++) {
                    int idx = rnd.nextInt(grades.length);
                    int course_id = courses[i];
                    String module_code = module[i];
                    float grade = grades[idx];

                    ContentValues cv = new ContentValues();

                    Cursor db = getContentResolver().query(
                            DataProvider.COURSE_DB_URI,
                            columns,
                            SQL_Database.COURSE_COLUMN_COURSE_ID + "=" + course_id,
                            null, null
                    );
                    db.moveToFirst();
                    cv.put(columns[0], db.getInt(0));
                    cv.put(columns[1], db.getString(1));
                    cv.put(columns[2], db.getString(2));
                    cv.put(columns[3], db.getInt(3));
                    cv.put(columns[4], db.getString(4));
                    cv.put(columns[5], db.getInt(5));
                    cv.put(columns[6], db.getString(6));
                    cv.put(columns[7], db.getString(7));
                    cv.put(columns[8], db.getString(8));
                    cv.put(columns[9], db.getString(9));
                    cv.put(columns[10], db.getString(10));
                    cv.put(columns[11], db.getString(11));
                    cv.put(columns[12], db.getString(12));
                    cv.put(columns[13], db.getString(13));

                    cv.put(SQL_Database.COURSES_COLUMN_MODULE, module_code);
                    cv.put(SQL_Database.COURSES_COLUMN_GRADE, grade);
                    cv.put(SQL_Database.COURSES_COLUMN_STATE, 2);

                    getContentResolver().insert(DataProvider.COURSES_DB_URI, cv);

                }


            }
        });
    }
}
