package com.mnipshagen.planning_machine.Dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;
import com.mnipshagen.planning_machine.ModuleTools;
import com.mnipshagen.planning_machine.R;

/**
 * Created by nipsh on 12/03/2017.
 */

public class AddUnlistedCourseDialog extends DialogFragment {

    public AddUnlistedCourseDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String module = "Open Studies";
        String code = "Open";
        if (args != null){
            code = args.getString("module_code");
            module = ModuleTools.codeToName(code, getContext()) + ", Open Studies";
        }

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.unlisted_course);
        final EditText name = (EditText) dialog.findViewById(R.id.unknown_courseTitle);
        final EditText ects = (EditText) dialog.findViewById(R.id.unknown_ECTS);
        final Spinner term = (Spinner) dialog.findViewById(R.id.unknown_spinner);
        final EditText year = (EditText) dialog.findViewById(R.id.unknown_year);
        Button butt_add = (Button) dialog.findViewById(R.id.unknown_butt_add);
        Button butt_cancel = (Button) dialog.findViewById(R.id.unknown_butt_cancel);
        final Button butt_modules = (Button) dialog.findViewById(R.id.unknown_butt_modules);
        butt_modules.setText(module);

        String[] temp = getResources().getStringArray(R.array.searchSpinnerModules);
        final String[] modules = new String[temp.length -1];
        System.arraycopy(temp, 1, modules, 0, modules.length);
        final boolean[] checked = new boolean[modules.length];
        for(int i = 0; i < checked.length; i++) checked[i] = false;
        if (code != null){
            int modID = ModuleTools.codeToListID(code);
            checked[modID -1] = true;
        }

        dialog.setTitle("Add an unlisted course");
        butt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        butt_modules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] checktmp = new boolean[checked.length];
                System.arraycopy(checked,0,checktmp,0,checked.length);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose applicable Modules")
                        .setMultiChoiceItems(modules, checked, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                checktmp[which] = isChecked;
                            }
                        })
                        .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.arraycopy(checktmp,0,checked,0,checked.length);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < modules.length; i++){
                                    if (checked[i]) sb.append(modules[i]).append(", ");
                                }
                                sb.append("Open Studies");
                                butt_modules.setText(sb.toString());
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
        });
        final String finalCode = code;
        butt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put(SQL_Database.COURSES_COLUMN_COURSE, name.getText().toString());
                cv.put(SQL_Database.COURSES_COLUMN_ECTS, ects.getText().toString());
                cv.put(SQL_Database.COURSES_COLUMN_TERM, term.getSelectedItem().toString());
                cv.put(SQL_Database.COURSES_COLUMN_YEAR, year.getText().toString());
                cv.put(SQL_Database.COURSES_COLUMN_FIELDS_STR, butt_modules.getText().toString());
                cv.put(SQL_Database.COURSES_COLUMN_MODULE, finalCode);

                getContext().getContentResolver().insert(DataProvider.COURSES_DB_URI,cv);

                dialog.dismiss();
            }
        });

        return dialog;
    }
}
