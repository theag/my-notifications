package com.mynotifications;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by nbp184 on 2016/04/05.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String ARG_HOUR = "hour";
    public static final String ARG_MINUTE = "minute";
    private TimePickerDialog.OnTimeSetListener listener = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof TimePickerDialog.OnTimeSetListener) {
            listener = (TimePickerDialog.OnTimeSetListener)activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), listener, getArguments().getInt(ARG_HOUR), getArguments().getInt(ARG_MINUTE), false);
    }
}
