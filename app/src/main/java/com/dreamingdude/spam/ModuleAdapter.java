package com.dreamingdude.spam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Moritz on 13.10.2016.
 */

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.MyViewHolder> {

    private Context mContext;
    private List<Module> moduleList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView module_title, module_credits, module_grades;

        public MyViewHolder(View view){
            super(view);
            module_title = (TextView) view.findViewById(R.id.module_title);
            module_credits = (TextView) view.findViewById(R.id.module_credits);
            module_grades = (TextView) view.findViewById(R.id.module_grade);
        }
    }

    public ModuleAdapter(Context mContext, List<Module> moduleList){
        this.mContext = mContext;
        this.moduleList = moduleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.module_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.module_title.setText(module.getName());
        holder.module_credits.setText(Integer.toString(module.getOptCompCredits()));
        holder.module_grades.setText(Float.toString(module.getAvgGrade()));

    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }
}
