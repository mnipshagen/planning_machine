package com.mnipshagen.planning_machine;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by nipsh on 08/03/2017.
 */

public class Fragment_Dialogs {

    public static void moveCourse(String course_name, String[] entries, final long id, final Context context) {
        final String[] codes = Utils.getModuleCodes(entries);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final int[] sel = {0};
        builder.setTitle("Move " + course_name)
                .setSingleChoiceItems(entries, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sel[0] = which;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int item = sel[0];
                        String code = codes[item];
                        Utils.moveCourse(code, id, context);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
