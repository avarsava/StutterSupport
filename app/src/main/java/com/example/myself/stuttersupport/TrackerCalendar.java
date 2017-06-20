package com.example.myself.stuttersupport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Myself on 6/20/2017.
 * Takes inspiration from
 * https://github.com/ahmed-alamir/CalendarView
 */

public class TrackerCalendar extends LinearLayout
{
    private static final int DAYS_COUNT = 42;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private Calendar currentDate = Calendar.getInstance();

    public TrackerCalendar(Context context)
    {
        super(context);
        initControl(context);
    }

    public TrackerCalendar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context);
    }

    public TrackerCalendar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initControl(context);
    }

    /**
     * Load component XML layout
     */
    private void initControl(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.tracker_calendar, this);

        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        //btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        //btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    /**
    private void updateCalendar()
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells (42 days calendar as per our business logic)
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        ((CalendarAdapter)grid.getAdapter()).updateData(cells);

        // update title
        txtDate.setText(sdf.format(currentDate.getTime()));
    }
     **/
}