package com.mnipshagen.planning_machine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * A class to handle several interactions in modules and c.
 */

public class ModuleTools {

    private static final String table = SQL_Database.COURSES_TABLE_NAME;
    private static final String col_state = SQL_Database.COURSES_COLUMN_STATE;
    private static final String col_id = SQL_Database.COURSES_COLUMN_ID;
    private static final String col_grade = SQL_Database.COURSES_COLUMN_GRADE;
    private static final String col_mod = SQL_Database.COURSES_COLUMN_MODULE;

    public static void setCoursePassed(long id, Context context){
        sqliteStateChange(2, 0.0, id, context);
    }
    public static void setCourseMarked(long id, Context context){
        sqliteStateChange(0, 0.0, id, context);
    }
    public static void setCourseInProgress(long id, Context context){
        sqliteStateChange(1, 0.0, id, context);
    }
    private static void sqliteStateChange(int state, double grade, long id, Context context) {
        ContentValues cv = new ContentValues();
        cv.put(col_grade, grade);
        cv.put(col_state, state);

        SQLiteDatabase db = SQL_Database.getInstance(context).getWritableDatabase();
        db.update(table,cv, col_id + "=" + id, null);
        db.close();
    }

    public static void setCourseGrade(long id, double grade, Context context){
        sqliteStateChange(2, grade, id, context);
    }

    public static void removeCourse(long id, Context context) {
        SQLiteDatabase db = SQL_Database.getInstance(context).getWritableDatabase();
        db.delete(table, col_id + "=" + id , null);
        db.close();
    }

    public static float[] refreshModule(String module_code, Context context){
        SQLiteDatabase db = SQL_Database.getInstance(context).getWritableDatabase();

        String[] courseData = {
                SQL_Database.COURSES_COLUMN_ID,
                SQL_Database.COURSES_COLUMN_COURSE,
                SQL_Database.COURSES_COLUMN_ECTS,
                SQL_Database.COURSES_COLUMN_GRADE,
                SQL_Database.COURSES_COLUMN_STATE
        };
        String courseSelection = SQL_Database.COURSES_COLUMN_MODULE + " = " + "'" + module_code + "'";

        Cursor c = db.query(SQL_Database.COURSES_TABLE_NAME, courseData, courseSelection, null, null, null, SQL_Database.COURSES_COLUMN_STATE);

        int achv_credits = 0;
        int ip_credits = 0;
        float grade = 0.f;

        c.moveToPosition(-1);
        while(c.moveToNext()) {
            int state = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_STATE));
            int ects = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS));
            float g = c.getFloat(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE));
            if (state == 1) {
                ip_credits += ects;
            } else if (state == 2) {
                achv_credits += ects;
                grade = g==0.0? grade : grade+g;
            }
        }
        if(c.getCount()!=0) {
            grade /= c.getCount();
        }

        ContentValues values = new ContentValues();
        values.put(SQL_Database.MODULE_COLUMN_ECTS, achv_credits);
        values.put(SQL_Database.MODULE_COLUMN_IPECTS, ip_credits);
        values.put(SQL_Database.MODULE_COLUMN_GRADE, grade);
        db.update(SQL_Database.MODULE_TABLE_NAME, values, SQL_Database.MODULE_COLUMN_CODE + " = '" + module_code + "'", null);

        c.close();

        return new float[] {achv_credits, ip_credits, grade};
    }
    
    public static String getModuleCode(String m) {
        String code;
        if (m.contains("ficial")) {
            code = "KI";
        }else if (m.contains("roinfo")) {
            code = "NI";
        }else if (m.contains("psych")) {
            code = "KNP";
        }else if (m.contains("lingu")) {
            code = "CL";
        }else if (m.contains("bio")) {
            code = "NW";
        }else if (m.contains("sophy")) {
            code = "PHIL";
        }else if (m.contains("ompute")) {
            code = "INF";
        }else if (m.contains("athem")) {
            code = "MAT";
        }else {
            code = "OPEN";
        }
        return code;
    }
    
    public static String[] getModuleCodes(String[] m) {
        String[] codes = new String[m.length];
        for(int i = 0; i < m.length; i++) {
            codes[i] = getModuleCode(m[i]);
        }
        return codes;
    }

    public static void moveCourse(String code, long id, Context context) {
        SQLiteDatabase db = SQL_Database.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_mod, code);
        db.update(table, cv, col_id + "=" + id, null);
    }
}
