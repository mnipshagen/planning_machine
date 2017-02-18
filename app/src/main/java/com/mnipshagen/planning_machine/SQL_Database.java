package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * The SQL handler for our database
 */
// TODO split into two databases, one for the courseDB and one for modules and courses to avoid
// TODO   the databases being unavailable while one of the tables is queried
public class SQL_Database extends SQLiteAssetHelper {
    // we implement it as a singleton, since we can only have one active connection anyway
    private static SQL_Database sInstance;

    // the database version to make "on upgrade" available
    private final static int DATABASE_VERSION = 1;
    // the database file name
    private final static String DB_NAME = "planning_machine.db";

    // the table "course_db" and all its columns
    final static String COURSE_TABLE_NAME = "course_db";
    final static String COURSE_COLUMN_ID = "_id";
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

    // the table "modules" and all its columns
    final static String MODULE_TABLE_NAME = "modules";
    final static String MODULE_COLUMN_ID = "_id";
    final static String MODULE_COLUMN_NAME = "name";
    final static String MODULE_COLUMN_CODE = "code";
    final static String MODULE_COLUMN_ECTS_COMP = "ects_comp";
    final static String MODULE_COLUMN_ECTS_OPTCOMP = "ects_optcomp";
    final static String MODULE_COLUMN_ECTS = "ects_current";
    final static String MODULE_COLUMN_IPECTS = "ects_inprogress";
    final static String MODULE_COLUMN_GRADE = "grade";
    final static String MODULE_COLUMN_COURSES = "courses";

    // the table "courses" and all its columns, are mostly duplicates of "course_db"
    // added columns for module, grade, state, etc...
    final static String COURSES_TABLE_NAME = "courses";
    final static String COURSES_COLUMN_ID = "_id";
    final static String COURSES_COLUMN_COURSE_ID = "course_id";
    final static String COURSES_COLUMN_COURSE = "course";
    final static String COURSES_COLUMN_MODULE = "module";
    final static String COURSES_COLUMN_GRADE = "grade";
    final static String COURSES_COLUMN_STATE = "state";
    final static String COURSES_COLUMN_COURSE_DESC = "course_desc";
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

    /**
     * call the instance of the database handler and initialise it if not yet done
     * @param context the context from which it is called
     * @return the SQL_Database handler
     */
    public static synchronized SQL_Database getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQL_Database(context);
        }
        return sInstance;
    }

    // use the constructor from "SQLiteAssetHelper" (see github) to create the databse from the asset
    private SQL_Database(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }
/*
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + COURSE_TABLE_NAME + " (" +
                    COURSE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COURSE_COLUMN_COURSE_ID + " INTEGER," +
                    COURSE_COLUMN_COURSE + " TEXT," +
                    COURSE_COLUMN_COURSE_DESC + " TEXT," +
                    COURSE_COLUMN_ECTS + " INTEGER," +
                    COURSE_COLUMN_TERM + " TEXT," +
                    COURSE_COLUMN_YEAR + " INTEGER," +
                    COURSE_COLUMN_CODE + " TEXT," +
                    COURSE_COLUMN_TYPE + " TEXT," +
                    COURSE_COLUMN_INFIELD_TYPE + " TEXT," +
                    COURSE_COLUMN_TEACHERS + " TEXT," + //TODO array
                    COURSE_COLUMN_TEACHERS_STR + " TEXT," +
                    COURSE_COLUMN_FIELDS + "TEXT," + //TODO array
                    COURSE_COLUMN_FIELDS_STR + "TEXT," +
                    COURSE_COLUMN_SINGLE_FIELD + " INTEGER)"
        );
        db.execSQL(
                "CREATE TABLE " + MODULE_TABLE_NAME +
                " (" +
                    MODULE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MODULE_COLUMN_NAME + " TEXT," +
                    MODULE_COLUMN_CODE + " TEXT," +
                    MODULE_COLUMN_ECTS_COMP + " INTEGER," +
                    MODULE_COLUMN_ECTS_OPTCOMP + " INTEGER," +
                    MODULE_COLUMN_ECTS + " INTEGER," +
                    MODULE_COLUMN_GRADE + " TEXT," +
                    MODULE_COLUMN_COURSES + " TEXT)" //TODO array/link ?
        );
        db.execSQL(
                "CREATE TABLE " + COURSES_TABLE_NAME +
                    " ( " +
                        COURSES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COURSES_COLUMN_COURSE_ID + " INTEGER," +
                        COURSES_COLUMN_COURSE + " TEXT" +
                        COURSES_COLUMN_CODE + " TEXT," +
                        COURSES_COLUMN_MODULE + " TEXT," +
                        COURSES_COLUMN_GRADE + " TEXT," +
                        COURSES_COLUMN_STATE + " INTEGER" +
                        COURSES_COLUMN_COURSE_DESC + " TEXT," +
                        COURSES_COLUMN_ECTS + " INTEGER," +
                        COURSES_COLUMN_TERM + " TEXT," +
                        COURSES_COLUMN_YEAR + " INTEGER," +
                        COURSES_COLUMN_CODE + " TEXT," +
                        COURSES_COLUMN_TYPE + " TEXT," +
                        COURSES_COLUMN_INFIELD_TYPE + " TEXT," +
                        COURSES_COLUMN_TEACHERS + " TEXT," + //TODO array
                        COURSES_COLUMN_TEACHERS_STR + " TEXT," +
                        COURSES_COLUMN_FIELDS + " TEXT," + //TODO array
                        COURSES_COLUMN_FIELDS_STR + " TEXT," +
                        COURSES_COLUMN_SINGLE_FIELD + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int j) {
        database.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + MODULE_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE_NAME);
        onCreate(database);
    }*/
}
