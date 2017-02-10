package com.mnipshagen.planning_machine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nipsh on 29/01/2017.
 */

public class Adapter_Overview extends RecyclerView.Adapter <Adapter_Overview.ViewHolder> {

    private List<Module> modules;
    private Context mContext;

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

    public Adapter_Overview(Context context, List<Module> data) {
        modules = data;
        mContext = context;
    }

    @Override
    public Adapter_Overview.ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listItem = inflater.inflate(R.layout.list_entry, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder (Adapter_Overview.ViewHolder viewHolder, int position) {
        ImageView state = viewHolder.state;
        TextView name = viewHolder.name;
        TextView ects = viewHolder.ects;
        TextView grade = viewHolder.grade;


        Module module = modules.get(position);
        state.setImageResource(R.drawable.inprogress);
        name.setText(module.getName());
        ects.setText("4 ECTS");
        grade.setText("1.0");

    }

    @Override
    public int getItemCount () {
        return modules.size();
    }
}
