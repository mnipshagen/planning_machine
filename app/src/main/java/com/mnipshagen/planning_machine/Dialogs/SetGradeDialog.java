package com.mnipshagen.planning_machine.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.mnipshagen.planning_machine.ModuleTools;
import com.mnipshagen.planning_machine.R;

/**
 * Created by nipsh on 11/03/2017.
 */

public class SetGradeDialog extends DialogFragment {

    public interface GradeDialogListener {
        void onGradeDialogPositiveClick(DialogFragment dialog, Double grade);
    }

    GradeDialogListener mListener;

    public SetGradeDialog() {}

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            Bundle args = getArguments();
            String course_name = args.getString("course_name");
            final Long id = args.getLong("id");

            final Dialog dialog = new Dialog(getContext());
            dialog.setTitle("Set grade of " + course_name);
            dialog.setContentView(R.layout.setgrade);
            final NumberPicker first = (NumberPicker) dialog.findViewById(R.id.setGradeNP1);
            final NumberPicker second = (NumberPicker) dialog.findViewById(R.id.setGradeNP2);
            final String[] firstItems = {"1", "2", "3", "4"};
            final String[] secondItems = {"0", "3", "7"};

            first.setMaxValue(4);
            first.setMinValue(1);
            first.setDisplayedValues(firstItems);
            first.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (newVal == 4) {
                        second.setMaxValue(0);
                    } else {
                        second.setMaxValue(2);
                    }
                }
            });
            second.setMaxValue(2);
            second.setMinValue(0);
            second.setDisplayedValues(secondItems);

            Button cancel = (Button) dialog.findViewById(R.id.setGradeCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button ok = (Button) dialog.findViewById(R.id.setGradeOK);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double grade;
                    int g1 = Integer.valueOf(firstItems[first.getValue()-1]);
                    int g2 = Integer.valueOf(secondItems[second.getValue()]);
                    grade = (float) g1 + ((float) g2 / 10.0);

                    ModuleTools.setCourseGrade(id, grade, getContext());
                    mListener.onGradeDialogPositiveClick(SetGradeDialog.this, grade);
                    dialog.dismiss();
                }
            });

            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Missing Bundle Items!");
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
