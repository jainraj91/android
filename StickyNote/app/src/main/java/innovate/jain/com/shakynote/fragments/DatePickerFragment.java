package innovate.jain.com.shakynote.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by rajat on 1/2/2016
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnDateSelected onDateSelected;

    public void setOnDateSelected(OnDateSelected onDateSelected) {
        this.onDateSelected = onDateSelected;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
//        TimePickerFragment newFragment = new TimePickerFragment();
//        newFragment.setDay(day);
//        newFragment.setYear(year);
//        newFragment.setMonth(month);
//        newFragment.show(getFragmentManager(), "timePicker");

        onDateSelected.setDate(year, month, day);
    }

    public interface OnDateSelected {
        void setDate(int year, int month, int day);
    }
}
