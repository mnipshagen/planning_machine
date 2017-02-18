package com.mnipshagen.planning_machine;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * A content provider, granting access to the database
 * not used at the moment. but kept in in case needed in the future
 */

public class CourseProvider extends ContentProvider {

    private SQL_Database data;

    private static final int COURSES = 10;
    private static final int COURSES_ID = 20;

    private static final String AUTHORITY = "com.mnipshagen.planning_machine.CourseProvider";
    private static final String BASE_PATH = "courseDB";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/courseDB";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/course";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, COURSES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COURSES_ID);
    }

    @Override
    public boolean onCreate() {
        data = SQL_Database.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(data.COURSE_TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch(uriType) {
            case COURSES:
                break;
            case COURSES_ID:
                queryBuilder.appendWhere(data.COURSE_COLUMN_ID + "=" + uri.getLastPathSegment());
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
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = data.getWritableDatabase();
        long id;

        switch (uriType) {
            case COURSES:
                id = db.insert(data.COURSE_TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = data.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case COURSES:
                rowsDeleted = db.delete(data.COURSE_TABLE_NAME, selection, selectionArgs);
                break;
            case COURSES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(data.COURSE_TABLE_NAME, data.COURSE_COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(data.COURSE_TABLE_NAME, data.COURSE_COLUMN_ID + "=" + id + "and" + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = data.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case COURSES:
                rowsUpdated = db.update(data.COURSE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case COURSES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(data.COURSE_TABLE_NAME, values, data.COURSE_COLUMN_ID + "=" +id, null);
                } else {
                    rowsUpdated = db.update(data.COURSE_TABLE_NAME, values, data.COURSE_COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { data.COURSE_COLUMN_ID, data.COURSE_COLUMN_COURSE_ID,
            data.COURSE_COLUMN_COURSE, data.COURSE_COLUMN_COURSE_DESC, data.COURSE_COLUMN_ECTS,
            data.COURSE_COLUMN_TERM, data.COURSE_COLUMN_YEAR, data.COURSE_COLUMN_CODE,
            data.COURSE_COLUMN_TYPE, data.COURSE_COLUMN_INFIELD_TYPE, data.COURSE_COLUMN_TEACHERS,
            data.COURSE_COLUMN_TEACHERS_STR, data.COURSE_COLUMN_FIELDS, data.COURSE_COLUMN_FIELDS_STR,
            data.COURSE_COLUMN_SINGLE_FIELD};
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
