package innovate.jain.com.shakynote.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by rajat on 1/9/2016
 */
public class RobottoRegularEditText extends EditText {
    public RobottoRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
    }
}
