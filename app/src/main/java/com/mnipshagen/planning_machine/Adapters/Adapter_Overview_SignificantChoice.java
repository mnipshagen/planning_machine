package com.mnipshagen.planning_machine.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;
import com.mnipshagen.planning_machine.Utils;
import com.mnipshagen.planning_machine.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Overview_SignificantChoice extends RecyclerCursorAdapter<Adapter_Overview_SignificantChoice.ViewHolder> {

    private Context mContext;
    private SparseBooleanArray selected;
    private int amount;

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

    public void markThem() {
        int oldPos = getCursor().getPosition();
        for (int i =0; i < getItemCount(); i++) {
            getCursor().moveToPosition(i);
            String module_code = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_CODE));
            if (selected.get(i)) {
                Log.v("OverviewAdapter", "Marking module " + module_code + " at position " + i + " significant");
                Utils.setSignificant(module_code, mContext);
            } else {
                Log.v("OverviewAdapter", "Marking module " + module_code + " at position " + i + " insignificant");
                Utils.setInsignificant(module_code, mContext);
            }
        }
        getCursor().moveToPosition(oldPos);
    }

    public Adapter_Overview_SignificantChoice(Cursor cursor, Context context) {
        super(cursor);
        mContext = context;
        selected = new SparseBooleanArray();
        amount = mContext.getSharedPreferences(Utils.SAVE_DATA, Context.MODE_PRIVATE).getInt(Utils.SAVE_KEY_SIGNIFICANTS, 0);
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

        final String module_code = mCursor.getString(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_CODE));
        Log.v("OverviewAdapter", "The module code for position " + viewHolder.getAdapterPosition() + " is: " + module_code);
        if(mCursor.getInt(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_SIGNIFICANT)) != 0) {
            significant.setChecked(true);
            selected.put(viewHolder.getAdapterPosition(), true);
        } else {
            significant.setChecked(false);
            selected.put(viewHolder.getAdapterPosition(), false);
        }
        final boolean insignificant = module_code.equals("OPEN") || module_code.equals("LOG") || module_code.equals("SD");
        View.OnClickListener mOnClicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(v instanceof ToggleButton)) {
                    significant.toggle();
                }
                if (!insignificant) {
                    if(selected.get(viewHolder.getAdapterPosition(), false) ) {
                        selected.put(viewHolder.getAdapterPosition(), false);
                        amount--;
                        Log.v("OverviewAdapter", "Selected Modules: " + selected);
                    } else {
                        if (amount >= 5) {
                            Toast.makeText(mContext, "You can only choose 5 modules!", Toast.LENGTH_SHORT).show();
                            significant.toggle();
                            return;
                        }
                        selected.put(viewHolder.getAdapterPosition(), true);
                        amount++;
                        Log.v("OverviewAdapter", "Selected Modules: " + selected);
                    }
                } else {
                    Toast.makeText(mContext, "This module cannot be marked applicable.", Toast.LENGTH_SHORT).show();
                    significant.toggle();
                }
            }
        };
        significant.setOnClickListener(mOnClicker);
        viewHolder.itemView.setOnClickListener(mOnClicker);

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
        if (mCursor.getFloat(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_GRADE)) == Utils.NO_GRADE) {
            grade = "--";
        } else {
            grade = String.format("%.1f",mCursor.getFloat(mCursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_GRADE)));
        }
        graph.setCenterText(grade);
        graph.setHoleRadius(75);
    }
}
