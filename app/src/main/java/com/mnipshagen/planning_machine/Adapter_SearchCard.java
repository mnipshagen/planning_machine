package com.mnipshagen.planning_machine;

import android.animation.ValueAnimator;
import android.database.Cursor;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
        private boolean isViewExpanded = false;
        public int originalHeight = 0;

        public ViewHolder (View view) {
            super(view);
//            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.result_title);
            ects = (TextView) view.findViewById(R.id.result_ects);
            studipCode = (TextView) view.findViewById(R.id.result_studip);
            termYear = (TextView) view.findViewById(R.id.result_time);
            fields = (TextView) view.findViewById(R.id.result_subtitle);
            lecturers = (TextView) view.findViewById(R.id.result_taughtby);
            description = (TextView) view.findViewById(R.id.result_description);
            expanded_content = (LinearLayout) view.findViewById(R.id.result_expaned_layout);
        }

//        @Override
//        public void onClick(final View v) {
//
//            if (originalHeight == 0) {
//                originalHeight = v.getHeight();
//
//                ValueAnimator valueAnimator;
//                if (!isViewExpanded) {
//                    expanded_content.setVisibility(View.VISIBLE);
//                    isViewExpanded = true;
//                    valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * 2.0)); // These values in this method can be changed to expand however much you like
//                    Log.v("isViewExpanded", "TRUMP SAGT WROOONG");
//                } else {
//                    isViewExpanded = false;
//                    valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * 2.0), originalHeight);
//
//                    Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
//
//                    a.setDuration(200);
//                    Log.v("isViewExpanded", "TRUMP SAGT EVERYONE KNOWS IT");
//                    // Set a listener to the animation and configure onAnimationEnd
//                    a.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            expanded_content.setVisibility(View.GONE);
//                            expanded_content.setEnabled(false);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//
//                    // Set the animation on the custom view
//                    expanded_content.startAnimation(a);
//                }
//                valueAnimator.setDuration(200);
//                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        Integer value = (Integer) animation.getAnimatedValue();
//                        v.getLayoutParams().height = value;
//                        v.requestLayout();
//                    }
//                });
//                valueAnimator.start();
//
//            }
//        }
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
    public void onBindViewHolder (final ViewHolder viewHolder, int position) {
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

//        final boolean isExpanded = viewHolder.getAdapterPosition()==mExpandedPosition;
//        viewHolder.expanded_content.setVisibility(isExpanded?View.VISIBLE:View.GONE);
//        viewHolder.itemView.setActivated(isExpanded);
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int oldExpanded = mExpandedPosition;
//                mExpandedPosition = isExpanded ? -1:viewHolder.getAdapterPosition();
//                TransitionManager.beginDelayedTransition(rv);
//                notifyItemChanged(oldExpanded);
//                notifyItemChanged(viewHolder.getAdapterPosition());
//            }
//        });

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
