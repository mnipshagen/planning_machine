package com.mnipshagen.planning_machine.DataProviding;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.mnipshagen.planning_machine.ModuleTools;

/**
 * Created by nipsh on 17/03/2017.
 */

public class ModuleObserver extends ContentObserver {

    private Context mContext;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ModuleObserver(Handler handler) {
        super(handler);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void removeContext() {
        mContext = null;
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        ModuleTools.refreshAllModules(mContext);
    }
}
