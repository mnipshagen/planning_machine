package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nipsh on 08/02/2017.
 */

public class SQL_Database extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "planning_machine.s3db";
    final static String COURSE_TABLE_NAME = "course_db";
    final static String COURSE_COLUMN_ID = "id";
    final static String COURSE_COLUMN_COURSE_ID = "course_id";
    final static String COURSE_COLUMN_COURSE = "course";
    final static String COURSE_COLUMN_COURSE_DESC = "course_desc";
    final static String COURSE_COLUMN_ECTS = "ects";
    final static String COURSE_COLUMN_TERM = "term";
    final static String COURSE_COLUMN_YEAR = "year";
    final static String COURSE_COLUMN_CODE = "code";
    final static String COURSE_COLUMN_TYPE = "type";
    final static String COURSE_COLUMN_INFIELD_TYPE = "course_in_field_type";
    final static String COURSE_COLUMN_TEACHERS = "teachers";
    final static String COURSE_COLUMN_TEACHERS_STR = "teachers_str";
    final static String COURSE_COLUMN_FIELDS = "fields";
    final static String COURSE_COLUMN_FIELDS_STR = "fields_str";
    final static String COURSE_COLUMN_SINGLE_FIELD = "singleField";

    final static String MODULE_TABLE_NAME = "modules";
    final static String MODULE_COLUMN_ID = "id";
    final static String MODULE_COLUMN_NAME = "name";
    final static String MODULE_COLUMN_CODE = "code";
    final static String MODULE_COLUMN_ECTS_COMP = "ects_comp";
    final static String MODULE_COLUMN_ECTS_OPTCOMP = "ects_optcomp";
    final static String MODULE_COLUMN_COURSES = "courses";

    final static String COURSES_TABLE_NAME = "courses";
    final static String COURSES_COLUMN_ID = "id";
    final static String COURSES_COLUMN_COURSE_ID = "course_id";
    final static String COURSES_COLUMN_COURSE = "course";
    final static String COURSES_COLUMN_MODULE = "module";
    final static String COURSES_COLUMN_COURSE_DESC = "desc";
    final static String COURSES_COLUMN_ECTS = "ects";
    final static String COURSES_COLUMN_TERM = "term";
    final static String COURSES_COLUMN_YEAR = "year";
    final static String COURSES_COLUMN_CODE = "code";
    final static String COURSES_COLUMN_TYPE = "type";
    final static String COURSES_COLUMN_INFIELD_TYPE = "course_in_field_type";
    final static String COURSES_COLUMN_TEACHERS = "teachers";
    final static String COURSES_COLUMN_TEACHERS_STR = "teachers_str";
    final static String COURSES_COLUMN_FIELDS = "fields";
    final static String COURSES_COLUMN_FIELDS_STR = "fields_str";
    final static String COURSES_COLUMN_SINGLE_FIELD = "singleField";


    Context context;

    public SQL_Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE course_db " +
                "(id integer primary key autoincrement," +
                    "course_id integer," +
                    "course text," +
                    "course_desc text," +
                    "ects integer," +
                    "term text" +
                    "year integer" +
                    "code text" +
                    "type text" +
                    "course_in_field_type text" +
                    "teachers text" + //TODO array
                    "teachers_str text" +
                    "fields text" + //TODO array
                    "fields_str text" +
                    "singleField integer)"
        );
        db.execSQL(
                "CREATE TABLE modules " +
                "(id integer primary key autoincrement," +
                    "name text," +
                    "code text," +
                    "ects_comp integer" +
                    "ects_optcomp integer" +
                    "courses text" //TODO array/link ?
        );
        db.execSQL(
                "CREATE TABLE courses " +
                    "(id integer primary key autoincrement," +
                        "course_id integer," +
                        "course text," +
                        "module text" +
                        "course_desc text," +
                        "ects integer," +
                        "term text" +
                        "year integer" +
                        "code text" +
                        "type text" +
                        "course_in_field_type text" +
                        "teachers text" + //TODO array
                        "teachers_str text" +
                        "fields text" + //TODO array
                        "fields_str text" +
                        "singleField integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int j) {

    }
}
