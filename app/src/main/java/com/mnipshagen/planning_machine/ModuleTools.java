package com.mnipshagen.planning_machine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

import java.util.List;

/**
 * A class to handle several interactions in modules and c.
 */

public class ModuleTools {

    public static final int NO_GRADE = 99;

    private static final Uri courses_table = DataProvider.COURSES_DB_URI;
    private static final Uri module_table = DataProvider.MODULE_DB_URI;
    private static final String col_state = SQL_Database.COURSES_COLUMN_STATE;
    private static final String col_id = SQL_Database.COURSES_COLUMN_ID;
    private static final String col_grade = SQL_Database.COURSES_COLUMN_GRADE;
    private static final String col_mod = SQL_Database.COURSES_COLUMN_MODULE;

    public static void setCoursePassed(long id, Context context){
        stateChange(2, 0.0, id, context);
    }
    public static void setCourseMarked(long id, Context context){
        stateChange(0, 0.0, id, context);
    }
    public static void setCourseInProgress(long id, Context context){
        stateChange(1, 0.0, id, context);
    }
    private static void stateChange(int state, double grade, long id, Context context) {
        ContentValues cv = new ContentValues();
        cv.put(col_grade, grade);
        cv.put(col_state, state);

        ContentResolver mCR = context.getContentResolver();
        Cursor c = mCR.query(courses_table, new String[]{col_mod}, col_id + "=" + id, null, null);
        c.moveToFirst();
        String m_code = c.getString(c.getColumnIndexOrThrow(col_mod));
        c.close();
        mCR.update(courses_table, cv, col_id + "=" + id, null);
//        refreshModule(m_code, context);
    }

    public static void setCourseGrade(long id, double grade, Context context){
        stateChange(2, grade, id, context);
    }

    public static void removeCourse(long id, Context context) {
        ContentResolver mCR = context.getContentResolver();
        Cursor c = mCR.query(courses_table, new String[]{col_mod}, col_id + "=" + id, null, null);
        c.moveToFirst();
        String m_code = c.getString(c.getColumnIndexOrThrow(col_mod));
        c.close();
        mCR.delete(courses_table, col_id + "=" + id , null);
//        refreshModule(m_code, context);
    }

    public static void moveCourse(String code, long id, Context context) {
        ContentResolver mCR = context.getContentResolver();
        Cursor c = mCR.query(courses_table, new String[]{col_mod}, col_id + "=" + id, null, null);
        c.moveToFirst();
        String m_code = c.getString(c.getColumnIndexOrThrow(col_mod));
        c.close();
        ContentValues cv = new ContentValues();
        cv.put(col_mod, code);
        mCR.update(courses_table, cv, col_id + "=" + id, null);
//        refreshModule(m_code, context);
//        refreshModule(code, context);
    }

    public static String courseTypeConv(String type) {
        String converted;
        switch (type) {
            case "L":
                converted = "Lecture"; break;
            case "Lecture":
                converted = "L"; break;
            case "B":
                converted = "Blockcourse"; break;
            case "Blockcourse":
                converted = "B"; break;
            case "S":
                converted = "Seminar"; break;
            case "Seminar":
                converted = "S"; break;
            case "C":
                converted = "Colloquium"; break;
            case "Colloquium":
                converted = "C"; break;
            default:
                converted = "Unknown (" + type +") "; break;
        }
        return converted;
    }

    public static void refreshAllModules(Context context) {
        String [] codes = {"KI", "KNP", "CL", "INF", "MAT", "NI", "NW", "PHIL", "LOG", "SD", "OPEN"};
        for (String s: codes) {
            refreshModule(s, context);
        }
    }

    public static double[] refreshModule(String module_code, Context context){
        double[] f = getOverallCredits(module_code, context);
        int achv_credits = (int) f[0];
        int ip_credits = (int) f[1];
        double grade = f[2];

        ContentValues values = new ContentValues();
        values.put(SQL_Database.MODULE_COLUMN_ECTS, achv_credits);
        values.put(SQL_Database.MODULE_COLUMN_IPECTS, ip_credits);
        values.put(SQL_Database.MODULE_COLUMN_GRADE, grade);
        context.getContentResolver().update(module_table, values, SQL_Database.MODULE_COLUMN_CODE + " = '" + module_code + "'", null);

        return new double[] {achv_credits, ip_credits, grade};
    }



    public static double[] getOverallCredits(String module_code, Context context) {
        ContentResolver mCR = context.getContentResolver();

        String[] courseData = {
                SQL_Database.COURSES_COLUMN_ECTS,
                SQL_Database.COURSES_COLUMN_GRADE,
                SQL_Database.COURSES_COLUMN_STATE
        };
        String courseSelection = SQL_Database.COURSES_COLUMN_MODULE + " = " + "'" + module_code + "'";

        Cursor c = mCR.query(courses_table, courseData, courseSelection, null, SQL_Database.COURSES_COLUMN_STATE);

        int achv_credits = 0;
        int ip_credits = 0;
        double grade = 0.f;
        int count = 0;

        c.moveToPosition(-1);
        while(c.moveToNext()) {
            int state = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_STATE));
            int ects = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS));
            float g = c.getFloat(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE));
            if (state == 1) {
                ip_credits += ects;
            } else if (state == 2) {
                achv_credits += ects;
                if (g != NO_GRADE) {
                    grade = grade + (g*ects);
                    count += ects;
                }

            }
        }
        if(count != 0) {
            grade /= (float)count;
        }

        c.close();

        return new double[] {achv_credits, ip_credits, grade};
    }

    public static int[] getCompAchvEcts(String module_code, Context context) {
        ContentResolver cr = context.getContentResolver();
        String [] columns = {SQL_Database.COURSES_COLUMN_ECTS, SQL_Database.COURSES_COLUMN_STATE,
                            SQL_Database.COURSES_COLUMN_INFIELD_TYPE};
        String selection = SQL_Database.COURSES_COLUMN_MODULE + "='" + module_code + "'";

        Cursor c = cr.query(courses_table, columns, selection, null, null);

        int achv = 0;
        int ip = 0;

        c.moveToPosition(-1);
        while(c.moveToNext()) {
            int state = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_STATE));
            int ects = c.getInt(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS));
            String type = c.getString(c.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_INFIELD_TYPE));
            if (type != null && type.equals("PM")) {
                switch (state) {
                    case 2: achv += ects; break;
                    case 1: ip += ects; break;
                }
            }
        }
        c.close();

        return new int[] {achv, ip};
    }

    public static String[] getModuleCodes(String[] m) {
        String[] codes = new String[m.length];
        for(int i = 0; i < m.length; i++) {
            codes[i] = getModuleCode(m[i]);
        }
        return codes;
    }

    public static String getModuleCode(String m) {
        String code;
        if (m.contains("ficial")) {
            code = "KI";
        }else if (m.contains("roinfo")) {
            code = "NI";
        }else if (m.contains("psych")) {
            code = "KNP";
        }else if (m.contains("ingui")) {
            code = "CL";
        }else if (m.contains("bio") || m.contains("rosci")) {
            code = "NW";
        }else if (m.contains("sophy")) {
            code = "PHIL";
        }else if (m.contains("ompute")) {
            code = "INF";
        }else if (m.contains("athem")) {
            code = "MAT";
        }else if(m.contains("ogi")){
            code = "LOG";
        } else if(m.contains("atisti")){
            code = "SD";
        }else {
            code = "OPEN";
        }
        return code;
    }

    public static String[] codesToNames(List<String> codes, Context context) {
        String[] names = new String[codes.size()];
        for(int i = 0; i < codes.size(); i++){
            names[i] = codeToName(codes.get(i), context);
        }
        return names;
    }

    public static String codeToName(String code, Context context){
        Resources res = context.getResources();
        switch(code){
            case "KI": return res.getString(R.string.KI_title);
            case "NI": return res.getString(R.string.NI_title);
            case "KNP": return res.getString(R.string.KNP_title);
            case "CL": return res.getString(R.string.CL_title);
            case "NW": return res.getString(R.string.NW_title);
            case "PHIL": return res.getString(R.string.PHIL_title);
            case "INF": return res.getString(R.string.INF_title);
            case "MAT": return res.getString(R.string.MAT_title);
            case "LOG": return "Logic";
            case "SD" : return "Statistics and Dataanalysis";
            case "OPEN": return "Open Studies";
            default: return "Unknown ModuleCode";
        }
    }

    public static int codeToListID(String code) {
        switch (code) {
            case "KI":
                return 1;
            case "KNP":
                return 2;
            case "CL":
                return 3;
            case "INF":
                return 4;
            case "MAT":
                return 5;
            case "NI":
                return 6;
            case "NW":
                return 7;
            case "PHIL":
                return 8;
            case "LOG":
                return 9;
            case "SD":
                return 10;
            default:
                return 0;
        }
    }

    public static boolean toggleSignificant(String code, Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(module_table,
                            new String[] {SQL_Database.MODULE_COLUMN_SIGNIFICANT},
                            SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                            null, null);
        Cursor s = cr.query(module_table,
                            new String[] {SQL_Database.MODULE_COLUMN_CODE},
                            SQL_Database.MODULE_COLUMN_SIGNIFICANT + "= 1",
                            null, null);
        Log.v("ModuleTools:", "ToogleSignificance Cursor Dump!: " + DatabaseUtils.dumpCursorToString(s));
        c.moveToFirst();

        ContentValues cv = new ContentValues();
        int sign = c.getInt(0);
        switch (sign) {
            case 0:
                if (s.getCount() >= 5) {
                    return false;
                }
                Log.v("ModuleTools", "AND MAKE IT RELEVANT");
                cv.put(SQL_Database.MODULE_COLUMN_SIGNIFICANT,1);
                cr.update(module_table,cv,SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",null);
                c.close();
                s.close();
                return true;
            case 1:
                Log.v("ModuleTools", "not relevant anymore");
                cv.put(SQL_Database.MODULE_COLUMN_SIGNIFICANT, 0);
                cr.update(module_table,cv,SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",null);
                c.close();
                s.close();
                return true;
            default:
                c.close();
                s.close();
                return false;
        }
    }
}
