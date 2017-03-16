package com.mnipshagen.planning_machine.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.mnipshagen.planning_machine.ModuleTools;

/**
 * Created by nipsh on 11/03/2017.
 */

public class SetGradeDialog extends DialogFragment {

    public interface GradeDialogListener {
        public void onGradeDialogPositiveClick(DialogFragment dialog, Double grade);
    }

    GradeDialogListener mListener;

    public SetGradeDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        try {
            Bundle args = getArguments();
            String course_name = args.getString("course_name");
            final Long id = args.getLong("id");

            final Context context = getContext();
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
                            mListener.onGradeDialogPositiveClick(SetGradeDialog.this, grade);
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
            return builder.create();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Needs Bundle to work!");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (GradeDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
