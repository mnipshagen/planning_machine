package com.mnipshagen.planning_machine.DataProviding;

import android.content.ContentResolver;
import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * The SQL handler for our database
 */
// TODO split into two databases, one for the courseDB and one for modules and courses to avoid
// TODO   the databases being unavailable while one of the tables is queried
public class SQL_Database extends SQLiteAssetHelper {
    // we implement it as a singleton, since we can only have one active connection anyway
    private static SQL_Database sInstance = null;
    private static ContentResolver mCr = null;

    // the database version to make "on upgrade" available
    private final static int DATABASE_VERSION = 1;
    // the database file name
    private final static String DB_NAME = "planning_machine.db";

    // the table "course_db" and all its columns
    public final static String COURSE_TABLE_NAME = "course_db";
    public final static String COURSE_COLUMN_ID = "_id";
    public final static String COURSE_COLUMN_COURSE_ID = "course_id";
    public final static String COURSE_COLUMN_COURSE = "course";
    public final static String COURSE_COLUMN_COURSE_DESC = "course_desc";
    public final static String COURSE_COLUMN_ECTS = "ects";
    public final static String COURSE_COLUMN_TERM = "term";
    public final static String COURSE_COLUMN_YEAR = "year";
    public final static String COURSE_COLUMN_CODE = "code";
    public final static String COURSE_COLUMN_TYPE = "type";
    public final static String COURSE_COLUMN_INFIELD_TYPE = "course_in_field_type";
    public final static String COURSE_COLUMN_TEACHERS = "teachers";
    public final static String COURSE_COLUMN_TEACHERS_STR = "teachers_str";
    public final static String COURSE_COLUMN_FIELDS = "fields";
    public final static String COURSE_COLUMN_FIELDS_STR = "fields_str";
    public final static String COURSE_COLUMN_SINGLE_FIELD = "singleField";
    public final static String[] COURSE_COLUMNS = {COURSE_COLUMN_ID, COURSE_COLUMN_COURSE_ID, COURSE_COLUMN_COURSE,
            COURSE_COLUMN_COURSE_DESC, COURSE_COLUMN_ECTS, COURSE_COLUMN_TERM, COURSE_COLUMN_YEAR,
            COURSE_COLUMN_CODE, COURSE_COLUMN_TYPE, COURSE_COLUMN_INFIELD_TYPE, COURSE_COLUMN_TEACHERS,
            COURSE_COLUMN_TEACHERS_STR, COURSE_COLUMN_FIELDS, COURSE_COLUMN_FIELDS_STR, COURSE_COLUMN_SINGLE_FIELD};

    // the table "modules" and all its columns
    public final static String MODULE_TABLE_NAME = "modules";
    public final static String MODULE_COLUMN_ID = "_id";
    public final static String MODULE_COLUMN_NAME = "name";
    public final static String MODULE_COLUMN_CODE = "code";
    public final static String MODULE_COLUMN_ECTS_COMP = "ects_comp";
    public final static String MODULE_COLUMN_ECTS_OPTCOMP = "ects_optcomp";
    public final static String MODULE_COLUMN_ECTS = "ects_current";
    public final static String MODULE_COLUMN_IPECTS = "ects_inprogress";
    public final static String MODULE_COLUMN_GRADE = "grade";
    public final static String[] MODULE_COLUMNS = {MODULE_COLUMN_ID, MODULE_COLUMN_NAME, MODULE_COLUMN_CODE,
            MODULE_COLUMN_ECTS_COMP, MODULE_COLUMN_ECTS_OPTCOMP, MODULE_COLUMN_ECTS, MODULE_COLUMN_IPECTS,
            MODULE_COLUMN_GRADE};

    // the table "courses" and all its columns, are mostly duplicates of "course_db"
    // added columns for module, grade, graph, etc...
    public final static String COURSES_TABLE_NAME = "courses";
    public final static String COURSES_COLUMN_ID = "_id";
    public final static String COURSES_COLUMN_COURSE_ID = "course_id";
    public final static String COURSES_COLUMN_COURSE = "course";
    public final static String COURSES_COLUMN_MODULE = "module";
    public final static String COURSES_COLUMN_GRADE = "grade";
    public final static String COURSES_COLUMN_STATE = "state";
    public final static String COURSES_COLUMN_COURSE_DESC = "course_desc";
    public final static String COURSES_COLUMN_ECTS = "ects";
    public final static String COURSES_COLUMN_TERM = "term";
    public final static String COURSES_COLUMN_YEAR = "year";
    public final static String COURSES_COLUMN_CODE = "code";
    public final static String COURSES_COLUMN_TYPE = "type";
    public final static String COURSES_COLUMN_INFIELD_TYPE = "course_in_field_type";
    public final static String COURSES_COLUMN_TEACHERS = "teachers";
    public final static String COURSES_COLUMN_TEACHERS_STR = "teachers_str";
    public final static String COURSES_COLUMN_FIELDS = "fields";
    public final static String COURSES_COLUMN_FIELDS_STR = "fields_str";
    public final static String COURSES_COLUMN_SINGLE_FIELD = "singleField";
    public final static String[] COURSES_COLUMNS = {COURSES_COLUMN_ID, COURSES_COLUMN_COURSE_ID,
            COURSES_COLUMN_COURSE, COURSES_COLUMN_MODULE, COURSES_COLUMN_GRADE, COURSES_COLUMN_STATE,
            COURSES_COLUMN_COURSE_DESC, COURSES_COLUMN_ECTS, COURSES_COLUMN_TERM, COURSES_COLUMN_YEAR,
            COURSES_COLUMN_CODE, COURSES_COLUMN_TYPE, COURSES_COLUMN_INFIELD_TYPE, COURSES_COLUMN_TEACHERS,
            COURSES_COLUMN_TEACHERS_STR, COURSES_COLUMN_FIELDS, COURSES_COLUMN_FIELDS_STR, COURSES_COLUMN_SINGLE_FIELD};

    /**
     * call the instance of the database handler and initialise it if not yet done
     * @param context the context from which it is called
     * @return the SQL_Database handler
     */
    public static synchronized SQL_Database getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQL_Database(context);
        }
        if (mCr == null) {
            mCr = context.getContentResolver();
        }
        return sInstance;
    }

    // use the constructor from "SQLiteAsse tHelper" (see github) to create the databse from the asset
    private SQL_Database(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

}
