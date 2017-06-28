package com.example.myself.stuttersupport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Myself on 6/28/2017.
 */

public class StreakDisplayer extends RelativeLayout {
    private TextView currentStreakView;
    private TextView bestStreakView;

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

        //layout is inflated, assign components to local variables
        currentStreakView = (TextView)findViewById(R.id.current_streak);
        bestStreakView = (TextView)findViewById(R.id.best_streak);
    }

    public void update(TrackerDbHelper tdbh, StreakDbHelper sdbh){
        tdbh.updateStreaks(sdbh);

        currentStreakView.setText("Current Streak: " + sdbh.getCurrent());
        bestStreakView.setText("Best Streak: " + sdbh.getBest());
    }
}
