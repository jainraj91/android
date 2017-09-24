package innovate.jain.com.shakynote.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by rajat on 1/2/2016
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private OnTimeSelected onTimeSelected;

    public void setOnTimeSelected(OnTimeSelected onTimeSelected) {
        this.onTimeSelected = onTimeSelected;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        onTimeSelected.setTimeValues(hourOfDay, minute);
    }

    public interface OnTimeSelected {
        void setTimeValues(int hourOfDay, int minute);
    }

}
