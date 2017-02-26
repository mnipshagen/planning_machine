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

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Module extends RecyclerCursorAdapter<Adapter_Module.ViewHolder> {

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public com.github.mikephil.charting.charts.PieChart graph;
        public TextView name, subname;

        public ViewHolder (View view) {
            super(view);
            graph = (com.github.mikephil.charting.charts.PieChart) view.findViewById(R.id.module_pie);
            name = (TextView) view.findViewById(R.id.module_name);
            subname = (TextView) view.findViewById(R.id.module_subname);
        }
    }

    public Adapter_Module(Cursor cursor, Context context) {
        super(cursor);
        mContext = context;
    }

    @Override
    public Adapter_Module.ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_entry, parent, false);
        return new Adapter_Module.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final Adapter_Module.ViewHolder viewHolder, final Cursor mCursor) {
        com.github.mikephil.charting.charts.PieChart graph;
        TextView name, subname;
        name = viewHolder.name;
        subname = viewHolder.subname;
        graph = viewHolder.graph;

        name.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_COURSE)));
        String credits = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_ECTS)) + " ECTS";
        subname.setText(credits);

        List<PieEntry> entries = new ArrayList<>();
        // will hold the colours to use
        int state = mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.COURSES_COLUMN_STATE));
        int[] col;
        switch (state) {
            case 0:
                col = new int[] {mContext.getResources().getColor(R.color.markMarked)};
                break;
            case 1:
                col = new int[] {mContext.getResources().getColor(R.color.markInProgress)};
                break;
            case 2:
                col = new int[] {mContext.getResources().getColor(R.color.markCompleted)};
                break;
            default:
                col = new int[] {mContext.getResources().getColor(R.color.half_black)};
        }

        // add in progress and achieved credits to chart
        entries.add(new PieEntry(1, ""));
        // create the pieSet from the entries created above
        PieDataSet pieSet = new PieDataSet(entries, "");
        // we display the formatted information on the chart and as such do not draw the actual
        // values. (no need for two times the same information)
        pieSet.setDrawValues(false);
        // set the colours to it
        pieSet.setColors(col);
        // and create a chart data set out of our pie information
        PieData pieData = new PieData(pieSet);

        // attach the data to the pie chart and format the chart
        graph.setData(pieData);
        // no description and no legend needed
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
        // right now the chart has no interaction
        graph.setRotationEnabled(false);
        graph.setHighlightPerTapEnabled(false);
        String grade;
        if (mCursor.getFloat(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_GRADE)) == 0.0f) {
            grade = "--";
        } else {
            grade = String.format("%.1f",mCursor.getFloat(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_GRADE)));
        }
        graph.setCenterText(grade);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }
}
