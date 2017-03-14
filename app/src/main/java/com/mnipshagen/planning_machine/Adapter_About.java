package com.mnipshagen.planning_machine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.entity.Library;

import java.util.List;

/**
 * Created by nipsh on 14/03/2017.
 */

public class Adapter_About extends RecyclerView.Adapter<Adapter_About.ViewHolder> {

    private Context mContext;
    private List<Library> libs;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, website, description;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.about_library_title);
            author = (TextView) view.findViewById(R.id.about_library_author);
            website = (TextView) view.findViewById(R.id.about_library_website);
            description = (TextView) view.findViewById(R.id.about_library_description);
        }
    }

    public Adapter_About(List<Library> libs, Context context){
        mContext = context;
        this.libs = libs;
    }

    @Override
    public Adapter_About.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_about_library, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Adapter_About.ViewHolder vh, int position) {
        final Library l = libs.get(position);
        vh.title.setText(l.getLibraryName());
        vh.author.setText(l.getAuthor());
        vh.website.setText(l.getAuthorWebsite());
        vh.description.setText(l.getLibraryDescription());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(l.getLibraryWebsite()));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return libs.size();
    }
}
