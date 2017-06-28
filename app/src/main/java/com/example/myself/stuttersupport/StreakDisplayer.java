package com.example.myself.stuttersupport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * Created by Myself on 6/28/2017.
 */

public class StreakDisplayer extends RelativeLayout {
    public StreakDisplayer(Context context)
    {
        super(context);
        initControl(context);
    }

    public StreakDisplayer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context);
    }

    public StreakDisplayer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initControl(context);
    }

    /**
     * Load component XML layout
     */
    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.streak_displayer, this);
    }
}
