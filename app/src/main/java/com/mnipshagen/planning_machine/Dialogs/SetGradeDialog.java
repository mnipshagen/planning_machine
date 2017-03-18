package com.mnipshagen.planning_machine.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mnipshagen.planning_machine.Activities.Activity_Module;
import com.mnipshagen.planning_machine.Utils;
import com.mnipshagen.planning_machine.R;

/**
 * Created by nipsh on 11/03/2017.
 */

public class SetGradeDialog extends DialogFragment {

    public static final String TITLE = "title";
    public static final String ID  = "id";
    public static final String IS_MODULE = "is_module";
    public static final long NO_ID = -1;

    public interface GradeDialogListener {
        void onGradeDialogPositiveClick(DialogFragment dialog, Double grade, boolean is_module);
    }

    GradeDialogListener mListener;

    public SetGradeDialog() {}

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            Bundle args = getArguments();
            final String title = args.getString(TITLE);
            final boolean is_module = args.getBoolean(IS_MODULE);
            final Long id = args.getLong(ID);

            final Dialog dialog = new Dialog(getContext());
            dialog.setTitle("Set grade of " + title);
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
                    if(is_module) {
                        ((ToggleButton)((Activity_Module)getContext())
                                .findViewById(R.id.toggleOral)).toggle();
                    }
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

                    if(is_module) {
                        if (!Utils.toggleOral(Utils.getModuleCode(title), grade, getContext())) {
                            Toast.makeText(getContext(), "Could not change state. Did you already do 2 oral exams?", Toast.LENGTH_LONG).show();
                            ((ToggleButton)((Activity_Module)getContext())
                                    .findViewById(R.id.toggleOral)).toggle();
                        }
                    } else {
                        if(id != NO_ID) {
                            Utils.setCourseGrade(id, grade, getContext());
                        }
                    }
                    if (mListener != null) {
                        mListener.onGradeDialogPositiveClick(SetGradeDialog.this, grade, is_module);
                    }
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
        if (context instanceof GradeDialogListener){
            mListener = (GradeDialogListener) context;
        }
    }
}
