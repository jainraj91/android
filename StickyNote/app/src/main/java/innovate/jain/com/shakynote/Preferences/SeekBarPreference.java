package innovate.jain.com.shakynote.Preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import innovate.jain.com.shakynote.service.SensorListenerService;
import innovate.jain.com.shakynote.utils.AppUtils;


public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, OnClickListener {

    private static final String androidns = "http://schemas.android.com/apk/res/android";

    private SeekBar mSeekBar;
    private Context mContext;

    private int mDefault, mMax, mValue = 0;

    public SeekBarPreference(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;

        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(androidns, "max", 20);
    }

    @Override
    protected View onCreateDialogView() {

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);

        int dp20 = AppUtils.convertDpToPixel(20, mContext);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist()) {
            mValue = getPersistedInt(mDefault);
        }

        mSeekBar.setPadding(dp20, dp20, dp20, dp20);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);

        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
        else
            mValue = (Integer) defaultValue;
    }

    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek) {
    }

    @Override
    public void showDialog(Bundle state) {

        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (shouldPersist()) {

            mValue = mSeekBar.getProgress();
            persistInt(mSeekBar.getProgress());
            callChangeListener(Integer.valueOf(mSeekBar.getProgress()));
            Intent serviceIntent = new Intent(mContext, SensorListenerService.class);
            mContext.startService(serviceIntent);
        }

        getDialog().dismiss();
    }
    // ------------------------------------------------------------------------------------------
}

