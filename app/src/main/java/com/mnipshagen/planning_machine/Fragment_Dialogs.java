package com.mnipshagen.planning_machine;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by nipsh on 08/03/2017.
 */

public class Fragment_Dialogs {

    public static void moveCourse(String course_name, String[] entries, final long id, final Context context) {
        final String[] codes = ModuleTools.getModuleCodes(entries);
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
                        ModuleTools.moveCourse(code, id, context);
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

    public static void changeGrade(String course_name, final long id, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Grade of " + course_name);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText() != null){
                    double grade = 0.0;
                    boolean allowed = true;
                    try {
                        grade = Double.parseDouble(input.getText().toString().replace(',', '.'));
                        if (!(0.5 < grade) || !(grade <= 4.)) {
                            Toast.makeText(context, "Not a grade!", Toast.LENGTH_SHORT).show();
                            allowed = false;
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "That was not a recognisable number!", Toast.LENGTH_SHORT).show();
                        allowed = false;
                    }
                    if (allowed) {
                        ModuleTools.setCourseGrade(id, grade, context);
                    }
                } else {
                    Toast.makeText(context, "No input detected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
