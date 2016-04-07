package com.mynotifications;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mynotifications.notifications.MyNotification;
import com.mynotifications.notifications.NotificationControl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener, DatePickerFragment.OnDateSetListener {

    public static final String ARG_INDEX = "index";
    private static final int[] weekdayIDs = {R.id.switch_monday, R.id.switch_tuesday, R.id.switch_wednesday, R.id.switch_thursday, R.id.switch_friday, R.id.switch_saturday, R.id.switch_sunday};
    private static final String DIALOG_UNTIL = "dialog until";
    private static final String DIALOG_START_DATE = "dialog start date";
    private int daySwitchCount;
    private int index;
    private SimpleDateFormat timeParser;
    private SimpleDateFormat dateParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timeParser = new SimpleDateFormat("h:mm aa");
        dateParser = new SimpleDateFormat("EEE, MMM d, yyyy");

        //todo: make a new adapter to display icon and text in dropdown and just icon in spinner
        Spinner spinner;/* = (Spinner)findViewById(R.id.spinner_repeat_choice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_choice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

        spinner = (Spinner)findViewById(R.id.spinner_repeat_choice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_choice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner = (Spinner)findViewById(R.id.spinner_repeat_until);
        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.repeat_until_long)) {
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View v = vi.inflate(android.R.layout.simple_spinner_item, null);
                final TextView t = (TextView)v.findViewById(android.R.id.text1);
                t.setText(getResources().getStringArray(R.array.repeat_until_short)[position]);
                return v;

            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        index = getIntent().getIntExtra(ARG_INDEX, -1);
        if(index < 0) {
            EditText et = (EditText)findViewById(R.id.edit_repeat_count);
            et.setText("1");
            Switch sw = (Switch)findViewById(R.id.switch_enabled);
            sw.setChecked(true);
            Calendar calendar = Calendar.getInstance();
            TextView tv = (TextView)findViewById(R.id.text_time);
            tv.setText(timeParser.format(calendar.getTime()));
            tv = (TextView)findViewById(R.id.text_date);
            tv.setText(dateParser.format(calendar.getTime()));
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            switch(day) {
                case Calendar.MONDAY:
                    sw = (Switch)findViewById(R.id.switch_monday);
                    break;
                case Calendar.TUESDAY:
                    sw = (Switch)findViewById(R.id.switch_tuesday);
                    break;
                case Calendar.WEDNESDAY:
                    sw = (Switch)findViewById(R.id.switch_wednesday);
                    break;
                case Calendar.THURSDAY:
                    sw = (Switch)findViewById(R.id.switch_thursday);
                    break;
                case Calendar.FRIDAY:
                    sw = (Switch)findViewById(R.id.switch_friday);
                    break;
                case Calendar.SATURDAY:
                    sw = (Switch)findViewById(R.id.switch_saturday);
                    break;
                case Calendar.SUNDAY:
                    sw = (Switch)findViewById(R.id.switch_sunday);
                    break;
            }
            sw.setChecked(true);
            daySwitchCount = 1;
            tv = (TextView)findViewById(R.id.text_until);
            tv.setText(dateParser.format(calendar.getTime()));
            et = (EditText)findViewById(R.id.edit_for_count);
            et.setText("5");
        } else {
            MyNotification note = NotificationControl.get(index);
            EditText et = (EditText)findViewById(R.id.edit_name);
            et.setText(note.name);
            spinner = (Spinner)findViewById(R.id.spinner_repeat_choice);
            spinner.setSelection(note.repeatType);
            Switch sw = (Switch)findViewById(R.id.switch_enabled);
            sw.setChecked(note.enabled);
            et = (EditText)findViewById(R.id.edit_repeat_count);
            et.setText("" + note.repeatCount);
            TextView tv = (TextView)findViewById(R.id.text_time);
            tv.setText(timeParser.format(note.getStartDate()));
            tv = (TextView)findViewById(R.id.text_date);
            tv.setText(dateParser.format(note.getStartDate()));
            daySwitchCount = 0;
            for(int i = 0; i < 7; i++) {
                sw = (Switch)findViewById(weekdayIDs[i]);
                sw.setChecked(note.doesRepeatOn_a(i));
                if(sw.isChecked()) {
                    daySwitchCount++;
                }
            }
            spinner = (Spinner)findViewById(R.id.spinner_repeat_until);
            spinner.setSelection(note.untilChoice);
            tv = (TextView)findViewById(R.id.text_until);
            tv.setText(dateParser.format(note.getUntilDate()));
            et = (EditText)findViewById(R.id.edit_for_count);
            et.setText(""+note.untilCount);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(index >= 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_delete:
                NotificationControl.remove(this, index);
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void daySwitch(View view) {
        Switch sw = (Switch)view;
        if(sw.isChecked()) {
            daySwitchCount++;
        } else {
            daySwitchCount--;
        }
    }

    public void otherClick(View view) {
        DialogFragment frag;
        Bundle args;
        switch(view.getId()) {
            case R.id.text_time:
                frag = new TimePickerFragment();
                args = new Bundle();
                try {
                    Calendar current = Calendar.getInstance();
                    current.setTime(timeParser.parse(((TextView) findViewById(R.id.text_time)).getText().toString()));
                    args.putInt(TimePickerFragment.ARG_HOUR, current.get(Calendar.HOUR_OF_DAY));
                    args.putInt(TimePickerFragment.ARG_MINUTE, current.get(Calendar.MINUTE));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.text_until:
                frag = new DatePickerFragment();
                args = new Bundle();
                try {
                    Calendar current = Calendar.getInstance();
                    current.setTime(dateParser.parse(((TextView) findViewById(R.id.text_until)).getText().toString()));
                    args.putInt(DatePickerFragment.ARG_YEAR, current.get(Calendar.YEAR));
                    args.putInt(DatePickerFragment.ARG_MONTH, current.get(Calendar.MONTH));
                    args.putInt(DatePickerFragment.ARG_DAY, current.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), DIALOG_UNTIL);
                break;
            case R.id.text_date:
                frag = new DatePickerFragment();
                args = new Bundle();
                try {
                    Calendar current = Calendar.getInstance();
                    current.setTime(dateParser.parse(((TextView) findViewById(R.id.text_date)).getText().toString()));
                    args.putInt(DatePickerFragment.ARG_YEAR, current.get(Calendar.YEAR));
                    args.putInt(DatePickerFragment.ARG_MONTH, current.get(Calendar.MONTH));
                    args.putInt(DatePickerFragment.ARG_DAY, current.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), DIALOG_START_DATE);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EditText et;
        switch(parent.getId()) {
            case R.id.spinner_repeat_choice:
                if(position == MyNotification.WEEKLY) {
                    findViewById(R.id.layout_weekly).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.layout_weekly).setVisibility(View.GONE);
                }
                TextView tv = (TextView)findViewById(R.id.text_repeat_unit);
                tv.setText(getResources().getStringArray(R.array.repeat_choice_unit)[position]);
                break;
            case R.id.spinner_repeat_until:
                switch(position) {
                    case MyNotification.FOREVER:
                        findViewById(R.id.text_until).setVisibility(View.GONE);
                        findViewById(R.id.layout_for).setVisibility(View.GONE);
                        break;
                    case MyNotification.UNTIL:
                        findViewById(R.id.text_until).setVisibility(View.VISIBLE);
                        findViewById(R.id.layout_for).setVisibility(View.GONE);
                        break;
                    case MyNotification.FOR_NUMBER:
                        findViewById(R.id.text_until).setVisibility(View.GONE);
                        findViewById(R.id.layout_for).setVisibility(View.VISIBLE);
                        break;
                }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, hourOfDay);
        current.set(Calendar.MINUTE, minute);
        TextView tv = (TextView)findViewById(R.id.text_time);
        tv.setText(timeParser.format(current.getTime()));
    }

    @Override
    public void onDateSet(String tag, int year, int monthOfYear, int dayOfMonth) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.YEAR, year);
        current.set(Calendar.MONTH, monthOfYear);
        current.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        TextView tv = null;
        switch(tag) {
            case DIALOG_UNTIL:
                tv = (TextView)findViewById(R.id.text_until);
                break;
            case DIALOG_START_DATE:
                tv = (TextView)findViewById(R.id.text_date);
                break;
        }
        tv.setText(dateParser.format(current.getTime()));
    }

    public void doSave(View view) {
        if(!validateForSave()) {
            return;
        }
        SimpleDateFormat parser = new SimpleDateFormat("h:mm aa MMM d, yyyy");
        MyNotification note;
        if(index < 0) {
            note = NotificationControl.makeNew();
        } else {
            note = NotificationControl.get(index);
        }
        EditText et = (EditText)findViewById(R.id.edit_name);
        note.name = et.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.spinner_repeat_choice);
        note.repeatType = spinner.getSelectedItemPosition();
        Switch sw = (Switch)findViewById(R.id.switch_enabled);
        note.enabled = sw.isChecked();
        et = (EditText)findViewById(R.id.edit_repeat_count);
        note.repeatCount = Integer.parseInt(et.getText().toString());
        TextView tv = (TextView)findViewById(R.id.text_time);
        String whenText = tv.getText().toString();
        tv = (TextView)findViewById(R.id.text_date);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(parser.parse(whenText +" " +tv.getText().toString()));
            note.setStartTime(cal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < 7; i++) {
            sw = (Switch)findViewById(weekdayIDs[i]);
            note.setRepeatOn_a(i, sw.isChecked());
        }
        spinner = (Spinner)findViewById(R.id.spinner_repeat_until);
        note.untilChoice = spinner.getSelectedItemPosition();
        tv = (TextView)findViewById(R.id.text_until);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(parser.parse(whenText +" " +tv.getText().toString()));
            note.setUntilTime(cal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        et = (EditText)findViewById(R.id.edit_for_count);
        note.untilCount = Integer.parseInt(et.getText().toString());
        note.reset(this);
        setResult(RESULT_OK);
        finish();
    }

    private boolean validateForSave() {
        String errors = "The following items are in error and must be corrected before proceeding";
        int errorCount = 0;
        EditText et = (EditText)findViewById(R.id.edit_name);
        if(et.getText().toString().isEmpty()) {
            errorCount++;
            errors += "\n" +errorCount +". Name cannot be blank";
        }
        et = (EditText)findViewById(R.id.edit_repeat_count);
        if(et.getText().toString().isEmpty()) {
            errorCount++;
            errors += "\n" +errorCount +". Repeat count cannot be blank";
        }
        Spinner spinner = (Spinner)findViewById(R.id.spinner_repeat_choice);
        if(spinner.getSelectedItemPosition() == MyNotification.WEEKLY) {
            if(daySwitchCount == 0) {
                errorCount++;
                errors += "\n" +errorCount +". Must select a day to repeat on";
            } else {
                Calendar cal = Calendar.getInstance();
                TextView tv = (TextView)findViewById(R.id.text_date);
                try {
                    cal.setTime(dateParser.parse(tv.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int weekday = MyNotification.getWeekDay_a(cal);
                Switch sw = (Switch)findViewById(weekdayIDs[weekday]);
                if(!sw.isChecked()) {
                    errorCount++;
                    errors += "\n" +errorCount +". Must start on one of the selected weekdays";
                }
            }
        }
        spinner = (Spinner)findViewById(R.id.spinner_repeat_until);
        if(spinner.getSelectedItemPosition() == MyNotification.FOR_NUMBER) {
            et = (EditText)findViewById(R.id.edit_for_count);
            if(et.getText().toString().isEmpty()) {
                errorCount++;
                errors += "\n" +errorCount +". Repeat until count cannot be blank";
            }
        }
        if(errorCount > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage(errors);
            builder.create().show();
        }
        return errorCount == 0;
    }
}
