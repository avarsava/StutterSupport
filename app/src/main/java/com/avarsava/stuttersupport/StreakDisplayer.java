package com.avarsava.stuttersupport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Displays the Current and Best streaks on a RelativeLayout. Used on the TrackerFragment.
 */

public class StreakDisplayer extends RelativeLayout {
    /**
     * Used to display the current streak.
     */
    private TextView currentStreakView;

    /**
     * Used to display the best streak.
     */
    private TextView bestStreakView;

    /**
     * Constructor, creates RelativeLayout then inflates layout from XML.
     *
     * @param context The application context
     */
    public StreakDisplayer(Context context)
    {
        super(context);
        initControl(context);
    }

    /**
     * Constructor, creates RelativeLayout from inflates layout from XML.
     *
     * @param context The application context.
     * @param attrs AttributeSet to pass to RelativeLayout constructor.
     */
    public StreakDisplayer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context);
    }

    /**
     * Constructor, creates RelativeLayout from inflates layout from XML.
     *
     * @param context The application context.
     * @param attrs AttributeSet to pass to RelativeLayout constructor.
     * @param defStyle Style from Resources to pass to RelativeLayout constructor.
     */
    public StreakDisplayer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initControl(context);
    }

    /**
     * Loads and inflates the Layout XML, then identifies and saves the TextViews used to display
     * the streak scores.
     *
     * @param context The application context.
     */
    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.streak_displayer, this);

        //layout is inflated, assign components to local variables
        currentStreakView = (TextView)findViewById(R.id.current_streak);
        bestStreakView = (TextView)findViewById(R.id.best_streak);
    }

    /**
     * Updates the streaks and displays the new values on the TextViews.
     *
     * @see TrackerDbHelper
     * @param tdbh TrackerDbHelper is required to calculate the new values for the streaks.
     * @param sdbh StreakDbHelper is required to get the new values for the streaks.
     */
    public void update(TrackerDbHelper tdbh, StreakDbHelper sdbh){
        tdbh.updateStreaks(sdbh);

        currentStreakView.setText(getContext().getString(R.string.current_streak) + sdbh.getCurrent());
        bestStreakView.setText(getContext().getString(R.string.best_streak) + sdbh.getBest());
    }
}
