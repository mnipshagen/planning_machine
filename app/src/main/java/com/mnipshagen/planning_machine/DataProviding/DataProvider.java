package com.mnipshagen.planning_machine.DataProviding;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mnipshagen.planning_machine.ModuleTools;

import java.util.Arrays;
import java.util.HashSet;

/**
 * A content provider, granting access to the database
 * not used at the moment. but kept in in case needed in the future
 */

public class DataProvider extends ContentProvider {

    private SQL_Database data;

    private static final String AUTHORITY = "com.mnipshagen.planning_machine.DataProviding.DataProvider";
    private static final String COURSE_BASE_PATH = "courses_db";
    private static final String COURSES_BASE_PATH = "courses";
    private static final String MODULE_BASE_PATH = "modules";

    public static final Uri COURSE_DB_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_BASE_PATH);
    public static final Uri COURSES_DB_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_BASE_PATH);
    public static final Uri MODULE_DB_URI = Uri.parse("content://" + AUTHORITY + "/" + MODULE_BASE_PATH);

    private static final int COURSE_DB = 1;
    private static final int COURSE_DB_ID = 2;

    private static final int COURSES_SAVED = 6;
    private static final int COURSES_SAVED_ID = 7;

    private static final int MODULE_DB = 11;
    private static final int MODULE_DB_ID = 12;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, COURSE_BASE_PATH, COURSE_DB);
        sURIMatcher.addURI(AUTHORITY, COURSE_BASE_PATH + "/#", COURSE_DB_ID);

        sURIMatcher.addURI(AUTHORITY, COURSES_BASE_PATH, COURSES_SAVED);
        sURIMatcher.addURI(AUTHORITY, COURSES_BASE_PATH + "/#", COURSES_SAVED_ID);

        sURIMatcher.addURI(AUTHORITY, MODULE_BASE_PATH, MODULE_DB);
        sURIMatcher.addURI(AUTHORITY, MODULE_BASE_PATH + "/#", MODULE_DB_ID);
    }

    @Override
    public boolean onCreate() {
        data = SQL_Database.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch(uriType) {
            case COURSE_DB_ID:
                queryBuilder.appendWhere(SQL_Database.COURSE_COLUMN_ID + "=" + uri.getLastPathSegment());
            case COURSE_DB:
                checkColumns(projection,1);
                queryBuilder.setTables(SQL_Database.COURSE_TABLE_NAME);
                break;
            case COURSES_SAVED_ID:
                queryBuilder.appendWhere(SQL_Database.COURSES_COLUMN_ID + "=" + uri.getLastPathSegment());
            case COURSES_SAVED:
                checkColumns(projection,2);
                queryBuilder.setTables(SQL_Database.COURSES_TABLE_NAME);
                break;
            case MODULE_DB_ID:
                queryBuilder.appendWhere(SQL_Database.MODULE_COLUMN_ID + "=" + uri.getLastPathSegment());
            case MODULE_DB:
                checkColumns(projection,3);
                queryBuilder.setTables(SQL_Database.MODULE_TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }

        SQLiteDatabase db = data.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.v("Provider-Ins","Uri: " + uri.toString());
        Log.v("Provider-Ins","Content: " + values.toString());
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = data.getWritableDatabase();
        long id;
        Uri res_uri;

        switch (uriType) {
            case COURSE_DB:
                id = db.insert(SQL_Database.COURSE_TABLE_NAME, null, values);
                res_uri = Uri.parse(COURSE_BASE_PATH + "/" + id);
                break;
            case COURSES_SAVED:
                id = db.insert(SQL_Database.COURSES_TABLE_NAME, null, values);
                res_uri = Uri.parse(COURSES_BASE_PATH + "/" + id);
                String module_code = values.getAsString(SQL_Database.COURSES_COLUMN_MODULE);
                Log.v("DataProvider", "Updated Module: " + module_code);
                ModuleTools.refreshModule(module_code,getContext());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return res_uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = data.getWritableDatabase();
        int rowsDeleted;
        String id;

        switch (uriType) {
            case COURSE_DB:
                rowsDeleted = db.delete(SQL_Database.COURSE_TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_DB_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(SQL_Database.COURSE_TABLE_NAME, SQL_Database.COURSE_COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(SQL_Database.COURSE_TABLE_NAME, SQL_Database.COURSE_COLUMN_ID + "=" + id + "and" + selection, selectionArgs);
                }
                break;
            case COURSES_SAVED:
                rowsDeleted = db.delete(SQL_Database.COURSES_TABLE_NAME, selection, selectionArgs);
                break;
            case COURSES_SAVED_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(SQL_Database.COURSES_TABLE_NAME, SQL_Database.COURSES_COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(SQL_Database.COURSES_TABLE_NAME, SQL_Database.COURSES_COLUMN_ID + "=" + id + "and" + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = data.getWritableDatabase();
        String id;
        int rowsUpdated;

        switch (uriType) {
            case COURSE_DB:
                rowsUpdated = db.update(SQL_Database.COURSE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSE_DB_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(SQL_Database.COURSE_TABLE_NAME, values, SQL_Database.COURSE_COLUMN_ID + "=" +id, null);
                } else {
                    rowsUpdated = db.update(SQL_Database.COURSE_TABLE_NAME, values, SQL_Database.COURSE_COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case COURSES_SAVED:
                rowsUpdated = db.update(SQL_Database.COURSES_TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSES_SAVED_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(SQL_Database.COURSES_TABLE_NAME, values, SQL_Database.COURSES_COLUMN_ID + "=" +id, null);
                } else {
                    rowsUpdated = db.update(SQL_Database.COURSES_TABLE_NAME, values, SQL_Database.COURSES_COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case MODULE_DB:
                rowsUpdated = db.update(SQL_Database.MODULE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case MODULE_DB_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(SQL_Database.MODULE_TABLE_NAME, values, SQL_Database.COURSE_COLUMN_ID + "=" +id, null);
                } else {
                    rowsUpdated = db.update(SQL_Database.MODULE_TABLE_NAME, values, SQL_Database.COURSE_COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection, int table) {
        String[] available;
        switch(table) {
            case 1: available = SQL_Database.COURSE_COLUMNS;
                break;
            case 2: available = SQL_Database.COURSES_COLUMNS;
                break;
            case 3: available = SQL_Database.MODULE_COLUMNS;
                break;
            default: available = new String[] {""};
        }
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }
}
