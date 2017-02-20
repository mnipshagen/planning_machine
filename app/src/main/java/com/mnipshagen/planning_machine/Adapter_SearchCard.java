package com.mnipshagen.planning_machine;

import android.database.Cursor;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_SearchCard extends RecyclerCursorAdapter<Adapter_SearchCard.ViewHolder>{

    private RecyclerView rv;
    private int mExpandedPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ects, studipCode, termYear, lecturers, fields, description;
        public LinearLayout expanded_content;

        public ViewHolder (View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.result_title);
            ects = (TextView) view.findViewById(R.id.result_ects);
            studipCode = (TextView) view.findViewById(R.id.result_studip);
            termYear = (TextView) view.findViewById(R.id.result_time);
            fields = (TextView) view.findViewById(R.id.result_subtitle);
            lecturers = (TextView) view.findViewById(R.id.result_taughtby);
            description = (TextView) view.findViewById(R.id.result_description);
            expanded_content = (LinearLayout) view.findViewById(R.id.result_expaned_layout);
        }
    }

    public Adapter_SearchCard(Cursor cursor, RecyclerView rv) {
        super(cursor);
        this.rv = rv;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_card_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder viewHolder, final int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        TextView name, ects, studipCode, termYear, lecturers, fields, description;

        name = viewHolder.name;
        ects = viewHolder.ects;
        studipCode = viewHolder.studipCode;
        termYear = viewHolder.termYear;
        lecturers = viewHolder.lecturers;
        fields = viewHolder.fields;
        description = viewHolder.description;

        final boolean isExpanded = position==mExpandedPosition;
        viewHolder.expanded_content.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        viewHolder.itemView.setActivated(isExpanded);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(rv);
                notifyItemChanged(position);
            }
        });

        String termandyear =
                String.format("%s%s",
                    mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TERM)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_YEAR))
                            .substring(2));

        name.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE)));
        String credits =
                mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_ECTS)) + " ECTS";
        ects.setText(credits);
        studipCode.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_CODE)));
        termYear.setText(termandyear);
        String lectureInfo = "";
        String courseType = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TYPE));
        courseType = courseType==null ? "":courseType;
        switch(courseType) {
            case "L":
                lectureInfo = "Lecture in ";
                break;
            case "B":
                lectureInfo = "Blockcourse in ";
                break;
            case "S":
                lectureInfo = "Seminar in ";
                break;
            case "C":
                lectureInfo = "Colloquium in ";
                break;
            default:
                lectureInfo = "Unknown CourseType in ";
                break;
        }
        lectureInfo = lectureInfo.concat(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)));
        fields.setText(lectureInfo);
        lecturers.setText("taught by " + mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TEACHERS_STR)));
        description.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE_DESC)));
    }
}
