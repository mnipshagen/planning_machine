//package com.mnipshagen.planning_machine;
//
//import android.database.Cursor;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
///**
// * Created by nipsh on 29/01/2017.
// */
//
//public class Adapter_Search extends RecyclerCursorAdapter<Adapter_Search.ViewHolder>{
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView name, ects, studipCode, termYear, lecturers, fields, description, type;
//
//        public ViewHolder (View view) {
//            super(view);
//            name = (TextView) view.findViewById(R.id.resultName);
//            ects = (TextView) view.findViewById(R.id.resultECTS);
//            studipCode = (TextView) view.findViewById(R.id.resultStudIP);
//            termYear = (TextView) view.findViewById(R.id.resultTermYear);
//            lecturers = (TextView) view.findViewById(R.id.resultLecturers);
//            fields = (TextView) view.findViewById(R.id.resultFields);
//            description = (TextView) view.findViewById(R.id.resultDescriptionHolder);
//            type = (TextView) view.findViewById(R.id.resultCourseType);
//        }
//    }
//
//    public Adapter_Search(Cursor cursor) {
//        super(cursor);
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.search_list_entry, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder (ViewHolder viewHolder, Cursor cursor) {
//
//        TextView name, ects, studipCode, termYear, lecturers, fields, description, type;
//        name = viewHolder.name;
//        ects = viewHolder.ects;
//        studipCode = viewHolder.studipCode;
//        termYear = viewHolder.termYear;
//        lecturers = viewHolder.lecturers;
//        fields = viewHolder.fields;
//        description = viewHolder.description;
//        type = viewHolder.type;
//
//        String termandyear =
//                String.format("%s%s",
//                    cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TERM)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_YEAR))
//                            .substring(2));
//
//        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE)));
//        String credits =
//                cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_ECTS)) + " ECTS";
//        ects.setText(credits);
//        studipCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_CODE)));
//        termYear.setText(termandyear);
//        String lectureInfo = "";
//        String courseType = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TYPE));
//        courseType = courseType==null ? "":courseType;
//        switch(courseType) {
//            case "L":
//                lectureInfo = "Lecture by ";
//                break;
//            case "B":
//                lectureInfo = "Blockcourse by ";
//                break;
//            case "S":
//                lectureInfo = "Seminar by ";
//                break;
//            case "C":
//                lectureInfo = "Kolloquium by ";
//                break;
//            default:
//                lectureInfo = "Unknown CourseType by ";
//                break;
//        }
//        lectureInfo = lectureInfo.concat(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_TEACHERS_STR)));
//        lecturers.setText(lectureInfo);
//        fields.setText("in " + cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_FIELDS_STR)));
//        description.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_COURSE_DESC)));
//        type.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.COURSE_COLUMN_INFIELD_TYPE)));
//    }
//}
