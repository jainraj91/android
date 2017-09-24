package innovate.jain.com.shakynote.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RenderingService extends Service {
    private WindowManager windowManager;
    private ImageView floatingFaceBubble;

    public void onCreate() {
        super.onCreate();
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(this);
        textView.setHeight(200);
        textView.setWidth(1100);
        textView.setText("NOTE");
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setBackgroundColor(Color.parseColor("#F44336"));

        view.addView(textView);

        EditText editText = new EditText(this);
        editText.setHeight(480);
        editText.setWidth(500);
        editText.setFocusable(true);
        editText.setText("This is the description");
        editText.setTextColor(Color.parseColor("#000000"));
        editText.setBackgroundColor(Color.parseColor("#00000000"));

        view.addView(editText);


        view.setBackgroundColor(Color.parseColor("#FFFACD"));
        floatingFaceBubble = new ImageView(this);
        //a face floating bubble as imageView
//        floatingFaceBubble.setImageResource(R.drawable.stickynote);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //here is all the science of params
        final LayoutParams myParams = new LayoutParams(
                1100,
                900,
                LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        myParams.x = 0;
        myParams.y = 300;
        // add a floatingfacebubble icon in window
        windowManager.addView(view, myParams);
        try {
            //for moving the picture on touch and slide
            view.setOnTouchListener(new View.OnTouchListener() {
                LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //remove face bubble on long press
                    if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getX()) {
                        windowManager.removeView(floatingFaceBubble);
                        stopSelf();
                        return false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(v, myParams);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}