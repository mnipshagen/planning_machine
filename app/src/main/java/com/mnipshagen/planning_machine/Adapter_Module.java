package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Module extends RecyclerView.Adapter <Adapter_Module.ViewHolder> {

    private Context mContext;
    private Adapter_Module.MyCursorAdapter mCursorAdapter;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView state;
        public TextView name, ects, grade;

        public ViewHolder (View view) {
            super(view);
            state = (ImageView) view.findViewById(R.id.state);
            name = (TextView) view.findViewById(R.id.name);
            ects = (TextView) view.findViewById(R.id.ects);
            grade = (TextView) view.findViewById(R.id.grade);
        }
    }

    public static class MyCursorAdapter extends CursorAdapter {

        public MyCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_entry, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

    public Adapter_Module(Context context, Cursor data) {
        mContext = context;
        mCursorAdapter = new MyCursorAdapter(mContext, data, 0);
    }

    @Override
    public Adapter_Module.ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder (Adapter_Module.ViewHolder viewHolder, int position) {
        Cursor cursor = mCursorAdapter.getCursor();
        cursor.moveToPosition(position);
        ImageView state = viewHolder.state;
        TextView name, ects, grade;
        name = viewHolder.name;
        ects = viewHolder.ects;
        grade = viewHolder.grade;

        state.setImageResource(R.drawable.inprogress);
        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE)));
        ects.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS)));
        grade.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_GRADE)));
    }

    @Override
    public int getItemCount () {
        return mCursorAdapter.getCount();
    }
}
