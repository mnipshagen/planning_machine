package com.dreamingdude.spam;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nipsh on 26/01/2017.
 */

public class Overview_ListAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList<OverviewModule> data;
    private static LayoutInflater inflater = null;
    public Resources res;
    OverviewModule values = null;

    public Overview_ListAdapter(Activity a, ArrayList d, Resources resLocal) {
        activity = a;
        data = d;
        res = resLocal;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {

        if (data.size() <= 0) {
            return 1;
        }
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class ModuleHolder {
        public ImageView state;
        public TextView module_name;
        public TextView module_grade;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ModuleHolder holder;

        if (convertView == null) {

            vi = inflater.inflate(R.layout.module_tabitem, null);

            holder = new ModuleHolder();
            holder.state = (ImageView) vi.findViewById(R.id.module_state);
            holder.module_name = (TextView) vi.findViewById(R.id.module_name);
            holder.module_grade = (TextView) vi.findViewById(R.id.module_grade);

            vi.setTag( holder );
        }
        else {
            holder = (ModuleHolder) vi.getTag();
        }

        if (data.size() <= 0) {
            /*
            holder.module_name = (TextView) vi.findViewById(R.id.module_name);
            holder.module_name.setText("No Data"); */
        }
        else {
            values = null;
            values = (OverviewModule) data.get( position );

            holder.state.setImageResource(res.getIdentifier("com.nips.testone:drawable/"+values.getImg(), null, null));
            holder.module_name.setText( values.getCourse_name() );
            holder.course_ects.setText( values.getCourse_ects() );
            holder.module_grade.setText( values.getCourse_grade() );

            vi.setOnClickListener( new OnItemClickListener(position) );
        }

        return vi;
    }

    public void onClick (View v) {
        Log.v( "CustomAdapter", "===== click =====");
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            MainActivity sct = (MainActivity) activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
