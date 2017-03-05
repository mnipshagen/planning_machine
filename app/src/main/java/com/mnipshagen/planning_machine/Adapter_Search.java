package com.mnipshagen.planning_machine;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Search extends RecyclerCursorAdapter<Adapter_Search.ViewHolder>{

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ects, studipCode, termYear, lecturers, fields, description;
        public LinearLayout expanded_content, descriptionLayout;
        public int originalHeight = 0;
        public Button addCourse;
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
            desc = (ImageButton) view.findViewById(R.id.result_expandDescription);
            descriptionLayout = (LinearLayout) view.findViewById(R.id.result_descriptionContainer);
        }
    }

    public Adapter_Search(Cursor cursor, Context context) {
        super(cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_card_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final ViewHolder viewHolder, final Cursor mCursor) {
        final TextView name, ects, studipCode, termYear, lecturers, fields, description;
        final Button addCourse = viewHolder.addCourse;
        final ImageButton desc = viewHolder.desc;
        final LinearLayout expanded_content, descriptionLayout;
        expanded_content = viewHolder.expanded_content;
        descriptionLayout = viewHolder.descriptionLayout;

        name = viewHolder.name;
        ects = viewHolder.ects;
        studipCode = viewHolder.studipCode;
        termYear = viewHolder.termYear;
        lecturers = viewHolder.lecturers;
        fields = viewHolder.fields;
        description = viewHolder.description;
        // TODO referenzen durch direct links ersetzen
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mCursor.getPosition();
                mCursor.moveToPosition(viewHolder.getAdapterPosition());
                final String[] modules = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)).split(",");
                mCursor.moveToPosition(pos);

                // display a dialog to add the course to the module
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final int[] selected = {0};
                builder.setTitle("Add to which Module?")
                        .setSingleChoiceItems(modules, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                selected[0] = which;
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int select = selected[0];
                                // when one was selected, write it into the database
                                // get database
                                SQLiteDatabase db = SQL_Database.getInstance(mContext).getWritableDatabase();
                                // collect the "_id"
                                long rowID = getItemId(viewHolder.getAdapterPosition());
                                // all the columns we need to copy
                                String columns =    SQL_Database.COURSES_COLUMN_COURSE_ID + "," +
                                        SQL_Database.COURSE_COLUMN_COURSE + "," +
                                        SQL_Database.COURSE_COLUMN_COURSE_DESC + "," +
                                        SQL_Database.COURSE_COLUMN_ECTS + "," +
                                        SQL_Database.COURSE_COLUMN_TERM + "," +
                                        SQL_Database.COURSE_COLUMN_YEAR + "," +
                                        SQL_Database.COURSE_COLUMN_CODE + "," +
                                        SQL_Database.COURSE_COLUMN_TYPE + "," +
                                        SQL_Database.COURSE_COLUMN_INFIELD_TYPE + "," +
                                        SQL_Database.COURSE_COLUMN_TEACHERS + "," +
                                        SQL_Database.COURSE_COLUMN_TEACHERS_STR + "," +
                                        SQL_Database.COURSE_COLUMN_FIELDS + "," +
                                        SQL_Database.COURSE_COLUMN_FIELDS_STR + "," +
                                        SQL_Database.COURSE_COLUMN_SINGLE_FIELD;
                                // we need to encode the module name to its corresponding code
                                // using contains as there might be blanks and (PM) inside module name
                                String module;
                                if (modules[select].contains("ficial")) {
                                    module = "KI";
                                }else if (modules[select].contains("roinfo")) {
                                    module = "NI";
                                }else if (modules[select].contains("psych")) {
                                    module = "KNP";
                                }else if (modules[select].contains("lingu")) {
                                    module = "CL";
                                }else if (modules[select].contains("bio")) {
                                    module = "NW";
                                }else if (modules[select].contains("sophy")) {
                                    module = "PHIL";
                                }else if (modules[select].contains("ompute")) {
                                    module = "INF";
                                }else if (modules[select].contains("athem")) {
                                    module = "MAT";
                                }else {
                                    module = "OPEN";
                                }

                                db.execSQL("INSERT INTO " + SQL_Database.COURSES_TABLE_NAME +
                                        "(" + columns + ") SELECT " + columns + " FROM " +
                                        SQL_Database.COURSE_TABLE_NAME + " WHERE _id = " + rowID);
                                db.execSQL("UPDATE " + SQL_Database.COURSES_TABLE_NAME + " SET " +
                                        SQL_Database.COURSES_COLUMN_MODULE + "= '" + module +
                                        "' WHERE _id = (SELECT MAX(_id) FROM courses)");
                            }
                        })
                        .setPositiveButton("Add and go to Module", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int select = selected[0];
                                // when one was selected, write it into the database
                                // get database
                                SQLiteDatabase db = SQL_Database.getInstance(mContext).getWritableDatabase();
                                // collect the "_id"
                                long rowID = getItemId(viewHolder.getAdapterPosition());
                                // all the columns we need to copy
                                String columns =    SQL_Database.COURSES_COLUMN_COURSE_ID + "," +
                                        SQL_Database.COURSE_COLUMN_COURSE + "," +
                                        SQL_Database.COURSE_COLUMN_COURSE_DESC + "," +
                                        SQL_Database.COURSE_COLUMN_ECTS + "," +
                                        SQL_Database.COURSE_COLUMN_TERM + "," +
                                        SQL_Database.COURSE_COLUMN_YEAR + "," +
                                        SQL_Database.COURSE_COLUMN_CODE + "," +
                                        SQL_Database.COURSE_COLUMN_TYPE + "," +
                                        SQL_Database.COURSE_COLUMN_INFIELD_TYPE + "," +
                                        SQL_Database.COURSE_COLUMN_TEACHERS + "," +
                                        SQL_Database.COURSE_COLUMN_TEACHERS_STR + "," +
                                        SQL_Database.COURSE_COLUMN_FIELDS + "," +
                                        SQL_Database.COURSE_COLUMN_FIELDS_STR + "," +
                                        SQL_Database.COURSE_COLUMN_SINGLE_FIELD;
                                // we need to encode the module name to its corresponding code
                                // using contains as there might be blanks and (PM) inside module name
                                String module = ModuleTools.getModuleCode(modules[select]);

                                db.execSQL("INSERT INTO " + SQL_Database.COURSES_TABLE_NAME +
                                        "(" + columns + ") SELECT " + columns + " FROM " +
                                        SQL_Database.COURSE_TABLE_NAME + " WHERE _id = " + rowID);
                                db.execSQL("UPDATE " + SQL_Database.COURSES_TABLE_NAME + " SET " +
                                        SQL_Database.COURSES_COLUMN_MODULE + "= '" + module +
                                        "' WHERE _id = (SELECT MAX(_id) FROM courses)");


                                Cursor cursor = db.rawQuery("SELECT name,ects_comp,ects_optcomp,ects_current,ects_current,ects_inprogress,grade FROM " + SQL_Database.MODULE_TABLE_NAME + " WHERE code = '" + module + "'", null);
                                cursor.moveToFirst();

                                String name = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_NAME));
                                int compECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_COMP));
                                int optcompECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP));

                                cursor.close();

                                Intent intent = new Intent(mContext, Activity_Module.class);
                                intent.putExtra("Name",name);
                                intent.putExtra("Module", module);
                                intent.putExtra("compECTS", compECTS);
                                intent.putExtra("optcompECTS", optcompECTS);
                                mContext.startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
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
                    valueAnimator = ValueAnimator.ofInt(viewHolder.itemView.getHeight(), viewHolder.itemView.getHeight() - (int)(80*yDpi/160));
                    Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
                    a.setDuration(300);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            descriptionLayout.getLayoutParams().height = (int)(80*yDpi/160);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    // Set the animation on the custom view
                    expanded_content.startAnimation(a);
                } else {
                    // EXPAND IT
                    descriptionLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    valueAnimator = ValueAnimator.ofInt(viewHolder.itemView.getHeight(), viewHolder.itemView.getHeight() + (int)(80*yDpi/160));
                }
                valueAnimator.setDuration(200);
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        viewHolder.itemView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                        viewHolder.itemView.requestLayout();
                    }
                });
                descriptionLayout.invalidate();
                valueAnimator.start();
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (final View view) {

                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                final float yDpi = dm.ydpi;

                ValueAnimator valueAnimator;
                if (viewHolder.originalHeight == 0) {
                    viewHolder.originalHeight = viewHolder.itemView.getHeight();
                }

                if (expanded_content.getVisibility() == View.GONE) {
                    // EXPAND IT
                    expanded_content.setVisibility(View.VISIBLE);
                    viewHolder.itemView.setActivated(true);
//                        TransitionManager.beginDelayedTransition(rv);
                    valueAnimator = ValueAnimator.ofInt(viewHolder.originalHeight, viewHolder.originalHeight + (int) (viewHolder.originalHeight * 2.0));
                } else {
                    // SHRINK IT
                    viewHolder.itemView.setActivated(false);
                    valueAnimator = ValueAnimator.ofInt(viewHolder.originalHeight + (int) (viewHolder.originalHeight * 2.0), viewHolder.originalHeight);
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
                valueAnimator.setDuration(200);
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
                lectureInfo = "Unknown(" + courseType +") in ";
                break;
        }
        lectureInfo = lectureInfo.concat(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)));
        fields.setText(lectureInfo);
        lecturers.setText("taught by " + mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TEACHERS_STR)));
        description.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE_DESC)));
    }

}
