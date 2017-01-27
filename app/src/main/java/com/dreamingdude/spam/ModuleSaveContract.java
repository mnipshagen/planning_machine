package com.dreamingdude.spam;

import android.provider.BaseColumns;

/**
 * Created by Moritz on 14.10.2016.
 */

public class ModuleSaveContract {

    private ModuleSaveContract(){}

    public static class ModuleEntry implements BaseColumns{
        public static final String TABLE_NAME = "modules";
        public static final String COLUMN_NAME_ORDER = "order";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_COMPCRED = "compcred";
        public static final String COLUMN_NAME_OPTCOMPCRED = "optcompcred";
        public static final String COLUMN_NAME_ACHCRED = "achcred";
        public static final String COLUMN_NAME_AVGGRADE = "avggrade";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ModuleEntry.TABLE_NAME + " (" +
                    ModuleEntry._ID + " INTEGER PRIMARY KEY," +
                    ModuleEntry.COLUMN_NAME_ORDER + TEXT_TYPE + COMMA_SEP +
                    ModuleEntry.COLUMN_NAME_NAME + TEXT_TYPE + " )";
}
