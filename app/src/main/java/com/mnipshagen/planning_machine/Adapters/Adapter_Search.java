package com.mnipshagen.planning_machine.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mnipshagen.planning_machine.Activities.Activity_Base;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;
import com.mnipshagen.planning_machine.Dialogs.AddCourseDialog;
import com.mnipshagen.planning_machine.ModuleTools;
import com.mnipshagen.planning_machine.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Search extends RecyclerCursorAdapter<Adapter_Search.ViewHolder> {

    private Context mContext;
    private List<Integer> expanded;
    private List<Integer> heights;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ects, studipCode, termYear, lecturers, fields, description;
        public LinearLayout expanded_content, descriptionLayout;
        public int originalHeight = 0;
        public Button addCourse, addAndOpenModule;
        public ImageButton desc;

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
            addCourse = (Button) view.findViewById(R.id.result_addToModule);
            addAndOpenModule = (Button) view.findViewById(R.id.result_addAndGoToModule);
            desc = (ImageButton) view.findViewById(R.id.result_expandDescription);
            descriptionLayout = (LinearLayout) view.findViewById(R.id.result_descriptionContainer);
        }

        public void onBindData(final Context mContext, final Cursor mCursor) {
            if (expanded.contains(getAdapterPosition())) {
                expanded_content.setVisibility(View.VISIBLE);
                itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                originalHeight = heights.get(expanded.indexOf(getAdapterPosition()));
            } else {
                expanded_content.setVisibility(View.GONE);
                itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            String termandyear =
                    String.format("%s%s",
                            mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TERM)),
                            mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_YEAR))
                                    .substring(2));
            termYear.setText(termandyear);

            name.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE)));

            String credits =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_ECTS)) + " ECTS";
            ects.setText(credits);

            studipCode.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_CODE)));

            String lectureInfo;
            String courseType = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TYPE));
            courseType = courseType==null ? "":courseType;
            courseType = ModuleTools.courseTypeConv(courseType);
            lectureInfo = courseType.concat(" in ");
            lectureInfo = lectureInfo.concat(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)));
            fields.setText(lectureInfo);

            lecturers.setText("taught by " + mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TEACHERS_STR)));
            String descr = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE_DESC));
            if (descr == null || descr.equals("") || descr.equals("null")) {
                descr = "No description available.";
            }
            description.setText(descr);

            addCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = mCursor.getPosition();
                    mCursor.moveToPosition(getAdapterPosition());
                    final String[] modules = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)).split(",");
                    mCursor.moveToPosition(pos);
                    long rowID = getItemId();

                    // display a dialog to add the course to the module
                    AddCourseDialog dialog = new AddCourseDialog();
                    Bundle args = new Bundle();
                    args.putStringArray("modules", modules);
                    args.putBoolean("go", false);
                    args.putLong("rowID", rowID);
                    dialog.setArguments(args);
                    FragmentManager fm = ((Activity_Base) mContext).getSupportFragmentManager();
                    dialog.show(fm, "add course");
                }
            });

            addAndOpenModule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = mCursor.getPosition();
                    mCursor.moveToPosition(getAdapterPosition());
                    final String[] modules = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)).split(",");
                    mCursor.moveToPosition(pos);
                    long rowID = getItemId();

                    // display a dialog to add the course to the module
                    AddCourseDialog dialog = new AddCourseDialog();
                    Bundle args = new Bundle();
                    args.putStringArray("modules", modules);
                    args.putBoolean("go", true);
                    args.putLong("rowID", rowID);
                    dialog.setArguments(args);
                    FragmentManager fm = ((Activity_Base) mContext).getSupportFragmentManager();
                    dialog.show(fm, "add course");
                }
            });

            desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    DisplayMetrics dm = new DisplayMetrics();
                    ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                    final float yDpi = dm.ydpi;
                    ValueAnimator valueAnimator;

                    if(descriptionLayout.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        // SHRINK IT
                        valueAnimator = ValueAnimator.ofInt(itemView.getHeight(), itemView.getHeight() - (int)(80*yDpi/160));
                        Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
                        a.setDuration(300);
                        a.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                descriptionLayout.getLayoutParams().height = (int)(80*yDpi/160);
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        // Set the animation on the custom view
                        expanded_content.startAnimation(a);
                    } else {
                        // EXPAND IT
                        descriptionLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        valueAnimator = ValueAnimator.ofInt(itemView.getHeight(), itemView.getHeight() + (int)(80*yDpi/160));
                    }
                    valueAnimator.setDuration(200);
                    valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            itemView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                            itemView.requestLayout();
                        }
                    });
                    descriptionLayout.invalidate();
                    valueAnimator.start();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (final View view) {

                    DisplayMetrics dm = new DisplayMetrics();
                    ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                    final float yDpi = dm.ydpi;

                    ValueAnimator valueAnimator;
                    if (originalHeight == 0) {
                        originalHeight = itemView.getHeight();
                    }

                    if (expanded_content.getVisibility() == View.GONE) {
                        // EXPAND IT
                        expanded.add(getAdapterPosition());
                        heights.add(originalHeight);
                        expanded_content.setVisibility(View.VISIBLE);
                        valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * 2.0));
                    } else {
                        // SHRINK IT
                        heights.remove(expanded.indexOf(getAdapterPosition()));
                        expanded.remove((Integer) getAdapterPosition());
                        valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * 2.0), originalHeight);
                        Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
                        a.setDuration(300);
                        // Set a listener to the animation and configure onAnimationEnd
                        a.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                expanded_content.setVisibility(View.GONE);
                                descriptionLayout.getLayoutParams ().height = (int)(80*yDpi/160);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        // Set the animation on the custom view
                        expanded_content.startAnimation(a);
                    }
                    valueAnimator.setDuration(300);
                    valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                            view.requestLayout();
                        }
                    });
                    valueAnimator.start();

                }
            });
        }
    }

    public Adapter_Search(Cursor cursor, Context context) {
        super(cursor);
        mContext = context;
        expanded = new ArrayList<>();
        heights = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_card_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final ViewHolder viewHolder, final Cursor mCursor) {
        viewHolder.onBindData(mContext, mCursor);
    }

}
