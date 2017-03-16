package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Overview_SignificantChoice extends RecyclerCursorAdapter<Adapter_Overview_SignificantChoice.ViewHolder> {

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public com.github.mikephil.charting.charts.PieChart graph;
        public TextView name, subname;
        public ToggleButton significant;

        public ViewHolder (View view) {
            super(view);
            graph = (com.github.mikephil.charting.charts.PieChart) view.findViewById(R.id.module_pie);
            name = (TextView) view.findViewById(R.id.module_name);
            subname = (TextView) view.findViewById(R.id.module_subname);
            significant = (ToggleButton) view.findViewById(R.id.module_toggle);
        }
    }

    public Adapter_Overview_SignificantChoice(Cursor cursor, Context context) {
       super(cursor);
       mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_entry_choice_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final ViewHolder viewHolder, final Cursor mCursor) {
        com.github.mikephil.charting.charts.PieChart graph;
        TextView name, subname;
        final ToggleButton significant;
        name = viewHolder.name;
        subname = viewHolder.subname;
        graph = viewHolder.graph;
        significant = viewHolder.significant;

        significant.setChecked(mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_SIGNIFICANT)) == 1);
        final String module_code = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_CODE));
        final boolean openStudies = module_code.equals("OPEN");
        significant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!openStudies) {
                    if (!ModuleTools.toggleSignificant(module_code, mContext)) {
                        Toast.makeText(mContext, "Could not change state. Do you already have 5 significant modules?", Toast.LENGTH_LONG).show();
                        significant.toggle();
                    }
                } else {
                    Toast.makeText(mContext, "Open Studies cannot be marked significant.", Toast.LENGTH_SHORT).show();
                    significant.toggle();
                }
            }
        });

        name.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_NAME)));
        String credits = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS)) + " ECTS";
        subname.setText(credits);

        List<PieEntry> entries = new ArrayList<>();
        // will hold the colours to use
        int[] col;

        int ip_credits, achv_credits, comp_credits, optcomp_credits;
        ip_credits = mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_IPECTS));
        achv_credits = mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS));
        comp_credits = mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_COMP));
        optcomp_credits = mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP));

        // if in progress + achieved credits are not enough to fulfill the compulsory part
        // the missing credits until compulsory is fulfilled are displayed in orange
        if(ip_credits + achv_credits < comp_credits) {
            int todo_ects = optcomp_credits-comp_credits;
            entries.add(new PieEntry(todo_ects, ""));
            int todo_comp = comp_credits - achv_credits - ip_credits;
            entries.add(new PieEntry(todo_comp, ""));
            col = new int[] {
                    mContext.getResources().getColor(R.color.markMarked),
                    mContext.getResources().getColor(R.color.markBachelor),
                    mContext.getResources().getColor(R.color.markInProgress),
                    mContext.getResources().getColor(R.color.markCompleted)  };
        }
        // if compulsory credits are achieved, then we have no need for the orange part
        // and the ects still to do are only depending on the achieved and in progress credits
        else {
            int todo_ects = optcomp_credits - achv_credits - ip_credits;
            entries.add(new PieEntry(todo_ects, ""));
            col = new int[] {
                    mContext.getResources().getColor(R.color.markMarked),
                    mContext.getResources().getColor(R.color.markInProgress),
                    mContext.getResources().getColor(R.color.markCompleted)  };
        }
        // add in progress and achieved credits to chart
        entries.add(new PieEntry(ip_credits, ""));
        entries.add(new PieEntry(achv_credits, ""));
        // create the pieSet from the entries created above
        PieDataSet pieSet = new PieDataSet(entries, "Credits towards Module completion");
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
        graph.setHoleRadius(75);
    }
}
