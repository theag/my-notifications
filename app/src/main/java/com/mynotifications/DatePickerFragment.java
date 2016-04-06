package com.mynotifications;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by nbp184 on 2016/04/05.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String ARG_YEAR = "year";
    public static final String ARG_MONTH = "month";
    public static final String ARG_DAY = "day";
    private DatePickerDialog.OnDateSetListener listener = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof DatePickerDialog.OnDateSetListener) {
            listener = (DatePickerDialog.OnDateSetListener)activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), listener, getArguments().getInt(ARG_YEAR), getArguments().getInt(ARG_MONTH), getArguments().getInt(ARG_DAY));
    }
}
