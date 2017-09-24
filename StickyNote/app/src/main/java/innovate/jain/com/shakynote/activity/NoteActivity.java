package innovate.jain.com.shakynote.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import innovate.jain.com.shakynote.R;
import innovate.jain.com.shakynote.fragments.DatePickerFragment;
import innovate.jain.com.shakynote.fragments.TimePickerFragment;
import innovate.jain.com.shakynote.model.Note;
import innovate.jain.com.shakynote.task.AddNote;
import innovate.jain.com.shakynote.task.LoadNotes;
import innovate.jain.com.shakynote.task.UpdateNote;
import innovate.jain.com.shakynote.utils.AppUtils;

public class NoteActivity extends Activity implements OnClickListener, DatePickerFragment.OnDateSelected, TimePickerFragment.OnTimeSelected {

    private EditText title, description;
    private int i;
    private ImageView add;
    private List<Note> notes;
    private TextView dateCreated;
    private RelativeLayout noteLayout;
    private int color;
    private boolean isLocked;
    private View topLeft, topMid, topRight, centerLeft,
            centerMid, centerRight, bottomLeft, bottomMid, bottomRight;
    private TextView time, timer;
    private Button date, ok;
    private int day, month, year, hourOfDay, minute;
    private LinearLayout buttons;
    private PopupWindow popup;
    private int id;
    private long timePaused = 0, timeStarted = 0;
    private Handler customHandler = new Handler();
    private int noteType;
    //0 means not checked
    //1 means checked
    private int alarmSwitchState;
    private MediaRecorder mediaRecorder;
    private String outputFile;

    private LinearLayout typeSpecifier;
    private RelativeLayout mediaContainer;
    private LinearLayout recordingExistedLayout;
    private MediaPlayer mediaPlayer;
    private RelativeLayout buttonLayout;
    private LinearLayout playExisting, recordAgain;
    private ImageView playExistingImage;
    private boolean isRecording;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_note);

        typeSpecifier = (LinearLayout) findViewById(R.id.type_specifier);
        mediaContainer = (RelativeLayout) findViewById(R.id.media_container);
        buttonLayout = (RelativeLayout) findViewById(R.id.button_layout);
        recordingExistedLayout = (LinearLayout) findViewById(R.id.recording_existed_layout);

        playExisting = (LinearLayout) findViewById(R.id.play_existing);
        recordAgain = (LinearLayout) findViewById(R.id.record_again);
        playExistingImage = (ImageView) findViewById(R.id.play_existing_image);

        LinearLayout writtenNoteContainer = (LinearLayout) findViewById(R.id.written_note_container);
        LinearLayout voiceNoteContainer = (LinearLayout) findViewById(R.id.voice_note_container);

        recordAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaContainer.setVisibility(View.VISIBLE);
                typeSpecifier.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                recordingExistedLayout.setVisibility(View.GONE);
            }
        });

        writtenNoteContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                description.setVisibility(View.VISIBLE);
                typeSpecifier.setVisibility(View.GONE);
                mediaContainer.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.VISIBLE);
                noteType = 0;
            }
        });

        voiceNoteContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                description.setVisibility(View.GONE);
                typeSpecifier.setVisibility(View.GONE);
                mediaContainer.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.VISIBLE);
                noteType = 1;
            }
        });

        timer = (TextView) findViewById(R.id.voice_timer);

        id = getIntent().getIntExtra("id", 0);

        setFinishOnTouchOutside(false);

        Random random = new Random();
        color = random.nextInt(9);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        buttons = (LinearLayout) findViewById(R.id.buttons);
        ImageView options = (ImageView) findViewById(R.id.options);
        ImageView share = (ImageView) findViewById(R.id.share);
        ImageView record = (ImageView) findViewById(R.id.record_stop);
        ImageView stop = (ImageView) findViewById(R.id.stop);

        record.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                stopRecording();
                mediaRecorder = new MediaRecorder();

                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/shaky__recordings/";
                new File(outputDirectory).mkdirs();
                outputFile = outputDirectory + System.currentTimeMillis() + ".3gp";

                mediaRecorder.setOutputFile(outputFile);
                timeStarted = SystemClock.uptimeMillis();
                customHandler.post(updateTimerThread);

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    isRecording = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                mediaContainer.setVisibility(View.GONE);
                recordingExistedLayout.setVisibility(View.VISIBLE);
            }
        });

        playExisting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(outputFile);
                            mediaPlayer.prepare();

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    playExistingImage.setImageDrawable(ContextCompat.getDrawable(NoteActivity.this, R.drawable.ic_play_circle));
                                }
                            });

                            int duration = mediaPlayer.getDuration();
                            Toast.makeText(NoteActivity.this, "" + duration, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mediaPlayer.start();
                    playExistingImage.setImageDrawable(ContextCompat.getDrawable(NoteActivity.this, R.drawable.ic_pause_circle));
                } else {
                    mediaPlayer.pause();
                    playExistingImage.setImageDrawable(ContextCompat.getDrawable(NoteActivity.this, R.drawable.ic_play_circle));
                }
            }
        });

        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (description.getText() != null && !description.getText().toString().equals("")) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, description.getText().toString());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            }
        });

        options.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.getMeasuredHeight() == 0) {
                    int px = AppUtils.convertDpToPixel(50, NoteActivity.this);
                    expandView(buttons, px);
                } else {
                    expandView(buttons, 0);
                }
            }
        });

        ImageView addAlarm = (ImageView) findViewById(R.id.add_alarm);
        addAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NoteActivity.this);
                View dialogView = View.inflate(NoteActivity.this, R.layout.select_time_dialog, null);


                date = (Button) dialogView.findViewById(R.id.edit_date_button);
                time = (TextView) dialogView.findViewById(R.id.edit_time_button);
                ok = (Button) dialogView.findViewById(R.id.ok);

                Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar);
                toolbar.setTitle("");
                final SwitchCompat swich = (SwitchCompat) toolbar.findViewById(R.id.alarm_switch);
                if (alarmSwitchState == 1) {
                    swich.setChecked(true);
                } else {
                    swich.setChecked(false);
                }

                swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            swich.setChecked(true);
                        } else {
                            swich.setChecked(false);
                        }
                    }
                });

                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();

                alertDialog.setCanceledOnTouchOutside(false);
                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (swich.isChecked()) {
                            alarmSwitchState = 1;
                        } else {
                            alarmSwitchState = 0;
                        }
                        alertDialog.dismiss();
                    }
                });

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                if (alarmSwitchState == 0) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    day = c.get(Calendar.DAY_OF_MONTH);
                    month = c.get(Calendar.MONTH);
                    year = c.get(Calendar.YEAR);
                    hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                    date.setText(simpleDateFormat.format(new Date()));
                    date.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerFragment newFragment = new DatePickerFragment();
                            newFragment.setOnDateSelected(NoteActivity.this);
                            newFragment.show(getFragmentManager(), "datePicker");
                        }
                    });

                    simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    time.setText(simpleDateFormat.format(new Date()));
                    time.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerFragment newFragment = new TimePickerFragment();
                            newFragment.setOnTimeSelected(NoteActivity.this);
                            newFragment.show(getFragmentManager(), "datePicker");
                        }
                    });
                } else {
                    Calendar c = Calendar.getInstance();

                    c.set(Calendar.DAY_OF_MONTH, day);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);

                    date.setText(simpleDateFormat.format(c.getTime()));
                    date.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerFragment newFragment = new DatePickerFragment();
                            newFragment.setOnDateSelected(NoteActivity.this);
                            newFragment.show(getFragmentManager(), "datePicker");
                        }
                    });

                    simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    time.setText(simpleDateFormat.format(c.getTime()));
                    time.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerFragment newFragment = new TimePickerFragment();
                            newFragment.setOnTimeSelected(NoteActivity.this);
                            newFragment.show(getFragmentManager(), "datePicker");
                        }
                    });
                }


                alertDialog.show();
            }
        });

        noteLayout = (RelativeLayout) findViewById(R.id.note_layout);

        dateCreated = (TextView) findViewById(R.id.date_created);

        ImageView left = (ImageView) findViewById(R.id.left);
        ImageView right = (ImageView) findViewById(R.id.right);

        add = (ImageView) findViewById(R.id.add);
        ImageView delete = (ImageView) findViewById(R.id.delete);

        ImageView selectColor = (ImageView) findViewById(R.id.select_color);
        selectColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupWindow(NoteActivity.this);
                View layout = View.inflate(NoteActivity.this, R.layout.select_color, null);
                popup.setContentView(layout);

                final float scale = NoteActivity.this.getResources().getDisplayMetrics().density;
                int pixels = (int) (150 * scale + 0.5f);
                // Set content width and height
                popup.setHeight(pixels);
                popup.setWidth(pixels);
                // Closes the popup window when touch outside of it - when looses focus
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);
                // Show anchored to button
                popup.showAsDropDown(v, 0, -pixels);

                topLeft = layout.findViewById(R.id.top_left);
                topMid = layout.findViewById(R.id.top_mid);
                topRight = layout.findViewById(R.id.top_right);
                centerLeft = layout.findViewById(R.id.center_left);
                centerMid = layout.findViewById(R.id.center_mid);
                centerRight = layout.findViewById(R.id.center_right);
                bottomLeft = layout.findViewById(R.id.bottom_left);
                bottomMid = layout.findViewById(R.id.bottom_mid);
                bottomRight = layout.findViewById(R.id.bottom_right);

                topLeft.setOnClickListener(NoteActivity.this);
                topMid.setOnClickListener(NoteActivity.this);
                topRight.setOnClickListener(NoteActivity.this);
                centerLeft.setOnClickListener(NoteActivity.this);
                centerMid.setOnClickListener(NoteActivity.this);
                centerRight.setOnClickListener(NoteActivity.this);
                bottomLeft.setOnClickListener(NoteActivity.this);
                bottomMid.setOnClickListener(NoteActivity.this);
                bottomRight.setOnClickListener(NoteActivity.this);

            }
        });

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotes();
                outputFile = null;
                i = notes.size();
                resetNote();

            }
        });


        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLocked) {
                    Toast.makeText(NoteActivity.this, "Please unlock the device to access messages", Toast.LENGTH_LONG).show();
                }

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                if (i > 0 && i < notes.size()) {
                    saveNotes();
                    stopRecording();
                    i--;
                    Note note = notes.get(i);
                    fillData(note);
                }
            }
        });

        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isLocked) {
                    Toast.makeText(NoteActivity.this, "Please unlock the device to access messages", Toast.LENGTH_LONG).show();
                }
                if (notes.size() > 0) {
                    if (i >= 0 && i <= notes.size()) {
                        saveNotes();
                        stopRecording();
                        if (i != notes.size() - 1) {
                            i++;
                        }

                        if (i < notes.size()) {
                            Note note = notes.get(i);
                            fillData(note);
                        }
                    }
                }
            }
        });

        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode() && myKM.isKeyguardSecure()) {
            isLocked = true;
            description.setVisibility(View.GONE);
            mediaContainer.setVisibility(View.GONE);
            typeSpecifier.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.GONE);
            notes = new ArrayList<>();
            add.setVisibility(View.INVISIBLE);
            noteLayout.setBackgroundColor(AppUtils.getParsedColor(color, NoteActivity.this));
        } else {
            isLocked = false;
            getLoaderManager().restartLoader(2, null, noteLoader);
        }


    }

    public void resetNote() {

        typeSpecifier.setVisibility(View.VISIBLE);
        description.setVisibility(View.GONE);
        mediaContainer.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        recordingExistedLayout.setVisibility(View.GONE);

        title.setText(null);
        description.setText(null);
        dateCreated.setText(null);
        Random random = new Random();
        color = random.nextInt(9);
        alarmSwitchState = 0;
//                noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_5));
        noteLayout.setBackgroundColor(AppUtils.getParsedColor(color, NoteActivity.this));
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timer.setText(AppUtils.getTimerInfo(timePaused, timeStarted));
            customHandler.post(this);
        }
    };


    public LoaderManager.LoaderCallbacks<List<Note>> noteLoader = new LoaderManager.LoaderCallbacks<List<Note>>() {
        @Override
        public Loader<List<Note>> onCreateLoader(int id, Bundle args) {
            return new LoadNotes(NoteActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {

            notes = data;
            if (notes != null && notes.isEmpty()) {
                add.setVisibility(View.INVISIBLE);
                description.setVisibility(View.GONE);
                mediaContainer.setVisibility(View.GONE);
                typeSpecifier.setVisibility(View.VISIBLE);
            } else {
                add.setVisibility(View.VISIBLE);
            }

            if (notes != null && !notes.isEmpty()) {
                for (int index = 0; index < notes.size(); index++) {
                    Note note = notes.get(index);
                    if (note.getId() == id) {
                        i = index;
                    }
                }
                Note note = notes.get(i);
                fillData(note);

            } else {
                noteLayout.setBackgroundColor(AppUtils.getParsedColor(color, NoteActivity.this));
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Note>> loader) {

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    public void expandView(final View v, int toHeight) {
        final int fromHeight = v.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        animator.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        saveNotes();
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        if (v == topLeft) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_1));
            color = 1;
            popup.dismiss();
        } else if (v == topMid) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_2));
            color = 2;
            popup.dismiss();
        } else if (v == topRight) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_3));
            color = 3;
            popup.dismiss();
        } else if (v == centerLeft) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_4));
            color = 4;
            popup.dismiss();
        } else if (v == centerMid) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_5));
            color = 5;
            popup.dismiss();
        } else if (v == centerRight) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_6));
            color = 6;
            popup.dismiss();
        } else if (v == bottomLeft) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_7));
            color = 7;
            popup.dismiss();
        } else if (v == bottomMid) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_8));
            color = 8;
            popup.dismiss();
        } else if (v == bottomRight) {
            noteLayout.setBackgroundColor(ContextCompat.getColor(NoteActivity.this, R.color.box_9));
            color = 9;
            popup.dismiss();
        }
    }

    @Override
    public void setDate(int year, int month, int day) {

        this.day = day;
        this.month = month;
        this.year = year;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date d = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        date.setText(simpleDateFormat.format(d));
    }

    @Override
    public void setTimeValues(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        time.setText(hourOfDay + ":" + minute);
    }

    public void fillData(Note note) {
        title.setText(note.getTitle());
        if ((noteType = note.getType()) == 0) {
            description.setText(note.getDescription());
            mediaContainer.setVisibility(View.GONE);
            description.setVisibility(View.VISIBLE);
            recordingExistedLayout.setVisibility(View.GONE);
        } else {
            description.setText(null);
            outputFile = note.getDescription();
            mediaContainer.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            recordingExistedLayout.setVisibility(View.VISIBLE);
        }

        color = note.getColor();
        noteLayout.setBackgroundColor(AppUtils.getParsedColor(color, NoteActivity.this));

        if (note.isActive() == 1) {
            day = note.getDay();
            month = note.getMonth();
            year = note.getYear();
            minute = note.getMin();
            hourOfDay = note.getHour();
            alarmSwitchState = 1;
        } else {
            alarmSwitchState = 0;
        }

        long creationDate = note.getDateCreated();
        Date date = new Date(creationDate);
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.US);
        dateCreated.setText(format.format(date));
    }

    public void saveNotes() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Note note = new Note();
        if (notes != null && i == notes.size()) {
            if ((description.getText() != null && !description.getText().toString().trim().equals("")) || (noteType == 1 && outputFile != null)) {

                if (title.getText() == null || title.getText().toString().trim().equals("")) {
                    note.setTitle("Untitled");
                } else {
                    note.setTitle(title.getText().toString());
                }

                if (noteType == 0) {
                    note.setDescription(description.getText().toString());
                } else {
                    note.setDescription(outputFile);
                }
                note.setDateCreated(System.currentTimeMillis());
                note.setColor(color);
                note.setYear(year);
                note.setMonth(month);
                note.setDay(day);
                note.setHour(hourOfDay);
                note.setMin(minute);
                note.setType(noteType);

                if (alarmSwitchState == 1) {
                    note.setIsActive(1);
                } else if (alarmSwitchState == 0) {
                    note.setIsActive(0);
                }

                new AddNote(NoteActivity.this, note).execute();
                notes.add(0, note);
            }
        } else {
            if ((description.getText() != null && !description.getText().toString().isEmpty()) || (outputFile != null && !outputFile.isEmpty())) {

                Note prevNote = notes.get(i);
                //Update only if something has changed
                String prevDesc = prevNote.getDescription();

                String currentDesc = "";
                if (description == null || description.getText().toString().isEmpty()) {
                    currentDesc = outputFile;
                } else {
                    currentDesc = description.getText().toString();
                }

                String prevTitle = prevNote.getTitle();
                String currentTitle = title.getText().toString();

                int prevColor = prevNote.getColor();
                int prevDay = prevNote.getDay();
                int prevMonth = prevNote.getMonth();
                int prevYear = prevNote.getYear();
                int prevHour = prevNote.getHour();
                int prevMinute = prevNote.getMin();
                int prevState = prevNote.isActive();

                int currentColor = color;
                if (!prevDesc.equals(currentDesc) || !prevTitle.equals(currentTitle) || prevColor != currentColor || alarmSwitchState != prevState
                        || (prevDay != day || prevMonth != month || prevYear != year || prevHour != hourOfDay || prevMinute != minute)
                        && alarmSwitchState == 1) {

                    if (title.getText() == null || title.getText().toString().trim().equals("")) {
                        prevNote.setTitle("Untitled");
                    } else {
                        prevNote.setTitle(title.getText().toString());
                    }
                    prevNote.setDateCreated(System.currentTimeMillis());
                    if (description != null && !description.getText().toString().isEmpty()) {
                        prevNote.setDescription(description.getText().toString());
                    } else {
                        prevNote.setDescription(outputFile);
                    }
                    prevNote.setColor(color);
                    prevNote.setType(noteType);

                    if (alarmSwitchState == 1) {
                        prevNote.setIsActive(1);
                        prevNote.setYear(year);
                        prevNote.setMonth(month);
                        prevNote.setDay(day);
                        prevNote.setHour(hourOfDay);
                        prevNote.setMin(minute);
                    } else if (alarmSwitchState == 0) {
                        prevNote.setIsActive(0);
                    }

                    new UpdateNote(NoteActivity.this, prevNote).execute();
                }
            }
        }
    }

    public void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            customHandler.removeCallbacks(updateTimerThread);
            mediaPlayer = null;
            isRecording = false;
        }
    }
}
