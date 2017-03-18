package com.mnipshagen.planning_machine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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

public class Utils {

    public static final int NO_GRADE = 99;
    public static final String SAVE_DATA = "data_storage";
    public static final String SAVE_PREFS = "preference_storage";

    public static final String SAVE_KEY_ORALS = "oral_exams";
    public static final String SAVE_KEY_SIGNIFICANTS = "significant_modules";

    private static final Uri courses_table = DataProvider.COURSES_DB_URI;
    private static final Uri module_table = DataProvider.MODULE_DB_URI;
    private static final String col_state = SQL_Database.COURSES_COLUMN_STATE;
    private static final String col_id = SQL_Database.COURSES_COLUMN_ID;
    private static final String col_grade = SQL_Database.COURSES_COLUMN_GRADE;
    private static final String col_mod = SQL_Database.COURSES_COLUMN_MODULE;

    public static void setCoursePassed(long id, Context context){
        stateChange(2, NO_GRADE, id, context);
    }
    public static void setCourseMarked(long id, Context context){
        stateChange(0, NO_GRADE, id, context);
    }
    public static void setCourseInProgress(long id, Context context){
        stateChange(1, NO_GRADE, id, context);
    }
    public static void setCourseGrade(long id, double grade, Context context){
        stateChange(2, grade, id, context);
    }
    private static void stateChange(int state, double grade, long id, Context context) {
        ContentValues cv = new ContentValues();
        cv.put(col_grade, grade);
        cv.put(col_state, state);

        ContentResolver mCR = context.getContentResolver();
        mCR.update(courses_table, cv, col_id + "=" + id, null);
    }

    public static void removeCourse(long id, Context context) {
        ContentResolver mCR = context.getContentResolver();
        mCR.delete(courses_table, col_id + "=" + id , null);
    }

    public static void moveCourse(String code, long id, Context context) {
        ContentResolver mCR = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(col_mod, code);
        mCR.update(courses_table, cv, col_id + "=" + id, null);
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

        Cursor c = context.getContentResolver().query(
                module_table,
                new String[] {SQL_Database.MODULE_COLUMN_STATE},
                SQL_Database.MODULE_COLUMN_CODE + "='" + module_code + "'",
                null, null
        );
        c.moveToFirst();
        int state = c.getInt(0);
        c.close();

        ContentValues values = new ContentValues();
        values.put(SQL_Database.MODULE_COLUMN_ECTS, achv_credits);
        values.put(SQL_Database.MODULE_COLUMN_IPECTS, ip_credits);
        if (state == 0) {
            values.put(SQL_Database.MODULE_COLUMN_GRADE, grade);
        }
        context.getContentResolver().update(
                module_table,
                values,
                SQL_Database.MODULE_COLUMN_CODE + " = '" + module_code + "'",
                null
        );

        return new double[] {achv_credits, ip_credits, grade};
    }



    public static double[] getOverallCredits(String module_code, Context context) {
        final ContentResolver mCR = context.getContentResolver();

        Cursor m = mCR.query(
                module_table,
                new String[] {SQL_Database.MODULE_COLUMN_STATE, SQL_Database.MODULE_COLUMN_GRADE},
                SQL_Database.MODULE_COLUMN_CODE + "='" + module_code + "'",
                null, null
        );
        m.moveToFirst();
        boolean oral = m.getInt(0) != 0;
        double m_grade = m.getDouble(1);
        m.close();

        String[] courseData = {
                SQL_Database.COURSES_COLUMN_ECTS,
                SQL_Database.COURSES_COLUMN_GRADE,
                SQL_Database.COURSES_COLUMN_STATE
        };
        String courseSelection = SQL_Database.COURSES_COLUMN_MODULE + " = " + "'" + module_code + "'";

        Cursor c = mCR.query(courses_table, courseData, courseSelection, null, SQL_Database.COURSES_COLUMN_STATE);

        int achv_credits = 0;
        int ip_credits = 0;
        double grade = 0.;
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
                if (g != NO_GRADE && !oral) {
                    grade = grade + (g*ects);
                    count += ects;
                }

            }
        }
        if(count != 0) {
            grade /= (float)count;
        }
        if (grade == 0.) {
            grade = NO_GRADE;
        }

        c.close();

        double[] res;
        if (oral) {
            res = new double[] {achv_credits, ip_credits, m_grade};
        } else {
            res = new double[] {achv_credits, ip_credits, grade};
        }

        return res;
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
            case "KI":      return 1;
            case "KNP":     return 2;
            case "CL":      return 3;
            case "INF":     return 4;
            case "MAT":     return 5;
            case "NI":      return 6;
            case "NW":      return 7;
            case "PHIL":    return 8;
            case "LOG":     return 9;
            case "SD":      return 10;
            default:        return 0;
        }
    }

    public static boolean setSignificant(String code, Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SAVE_DATA, Context.MODE_PRIVATE);
        Cursor c = context.getContentResolver().query(module_table,
                new String[] {SQL_Database.MODULE_COLUMN_SIGNIFICANT},
                SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                null, null);
        c.moveToFirst();
        int sign = c.getInt(0);
        if (sign != 0) return false;
        c.close();
        int signAmount = sharedPrefs.getInt(SAVE_KEY_SIGNIFICANTS, 0);
        if (signAmount >= 5) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(SQL_Database.MODULE_COLUMN_SIGNIFICANT,1);
        context.getContentResolver().update(module_table,cv,SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",null);
        signAmount++;
        sharedPrefs.edit().putInt(SAVE_KEY_SIGNIFICANTS, signAmount).apply();
        Log.v("Utils", code + " is now significant.");
        return true;
    }

    public static boolean setInsignificant(String code, Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SAVE_DATA, Context.MODE_PRIVATE);
        int signAmount = sharedPrefs.getInt(SAVE_KEY_SIGNIFICANTS, 0);

        Cursor c = context.getContentResolver().query(module_table,
                new String[] {SQL_Database.MODULE_COLUMN_SIGNIFICANT},
                SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                null, null);
        c.moveToFirst();
        int sign = c.getInt(0);
        if (sign == 0) return false;
        c.close();

        ContentValues cv = new ContentValues();
        cv.put(SQL_Database.MODULE_COLUMN_SIGNIFICANT, 0);
        context.getContentResolver().update(module_table,cv,SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",null);
        signAmount--;
        sharedPrefs.edit().putInt(SAVE_KEY_SIGNIFICANTS, signAmount).apply();
        Log.v("Utils", code + " is now insignificant.");
        return true;
    }

    public static boolean toggleSignificant(String code, Context context) {
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(module_table,
                            new String[] {SQL_Database.MODULE_COLUMN_SIGNIFICANT},
                            SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                            null, null);
        c.moveToFirst();

        int sign = c.getInt(0);
        c.close();
        switch (sign) {
            case 0:
                return setSignificant(code, context);
            case 1:
                return setInsignificant(code, context);
            default:
                return false;
        }
    }

    public static boolean toggleOral(String code, double grade, Context context) {
        final ContentResolver cr = context.getContentResolver();
        SharedPreferences sharedPrefs = context.getSharedPreferences(SAVE_DATA, Context.MODE_PRIVATE);
        int oralAmount = sharedPrefs.getInt(SAVE_KEY_ORALS, 0);

        Cursor c = cr.query(module_table,
                new String[] {SQL_Database.MODULE_COLUMN_STATE},
                SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                null, null);
        c.moveToFirst();

        ContentValues cv = new ContentValues();
        int sign = c.getInt(0);
        switch (sign) {
            case 0:
                if (oralAmount >= 2) {
                    return false;
                }
                Log.v("Utils", "PERFORMED ORAL");
                cv.put(SQL_Database.MODULE_COLUMN_STATE, 1);
                cv.put(SQL_Database.MODULE_COLUMN_GRADE, grade);
                cr.update(
                        module_table,
                        cv,
                        SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                        null
                );
                oralAmount++;
                sharedPrefs.edit().putInt(SAVE_KEY_ORALS, oralAmount).apply();
                c.close();
                return true;
            case 1:
                Log.v("Utils", "no oral after all");
                cv.put(SQL_Database.MODULE_COLUMN_STATE, 0);
                cr.update(
                        module_table,
                        cv,
                        SQL_Database.MODULE_COLUMN_CODE + "='" + code + "'",
                        null
                );
                oralAmount--;
                sharedPrefs.edit().putInt(SAVE_KEY_ORALS, oralAmount).apply();
                refreshModule(code, context);
                c.close();
                return true;
            default:
                c.close();
                return false;
        }
    }
}
