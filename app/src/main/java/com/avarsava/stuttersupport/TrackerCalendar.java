package com.avarsava.stuttersupport;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Displays a calendar which highlights the current date with blue text, and highlights days on
 * which at least one activity has been successfully completed with a red background.
 * Takes inspiration from https://github.com/ahmed-alamir/CalendarView
 */
public class TrackerCalendar extends LinearLayout
{
    /**
     * How many days to display in total. Ensures 6 weeks are displayed.
     */
    private static final int DAYS_COUNT = 42;

    /**
     * Displays the date as Month + Year above the calendar.
     */
    private TextView txtDate;

    /**
     * The grid which is the backbone of the calendar.
     */
    private GridView grid;

    /**
     * Contain's today's date.
     */
    private Calendar currentDate = Calendar.getInstance();

    /**
     * The application context.
     */
    private Context context = getContext();

    /**
     * Constructor, creates RelativeLayout from inflates layout from XML.
     *
     * @param context The application context.
     */
    public TrackerCalendar(Context context)
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
    public TrackerCalendar(Context context, AttributeSet attrs)
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
    public TrackerCalendar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initControl(context);
    }

    /**
     * Loads and inflates the Layout XML, then identifies and saves the Views used to display the
     * current date and the calendar grid.
     *
     * @param context The application context.
     */
    private void initControl(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.tracker_calendar, this);

        // layout is inflated, assign local variables to components
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    /**
     * Draws all the cells in the grid needed to create a calendar, then uses the CalendarAdapter
     * to display highlighting on the dates specified in the HashSet passed in.
     *
     * @param dates The set of dates which should be highlighted on the calendar.
     */
    public void updateCalendar(HashSet<Date> dates)
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
        grid.setAdapter(new CalendarAdapter(getContext(), cells, dates));

        // update title
        txtDate.setText(DbDate.getMonthAndYear(getContext()));
    }

    /**
     * Internal class used to display calendar.
     */
    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        /**
         * Days on which at least one activity has been successfully completed.
         */
        private HashSet<Date> eventDays;

        /**
         * Used for layout inflation.
         */
        private LayoutInflater inflater;

        /**
         * Creates a new CalendarAdapter containing a specified set of dates, with highlighting on
         * the secondary set of dates.
         *
         * @param context The application context
         * @param days The full set of days to display on the calendar
         * @param eventDays The set of days which should be highlighted to signal activity
         */
        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.tracker_calendar_day, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        /**
         * Creates the calendar date square. Not called explicitly.
         *
         * @param position Used to find today's date
         * @param view Calendar date square
         * @param parent ViewGroup of cells that make up calendar
         * @return Finished calendar date View
         */
        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.tracker_calendar_day, parent, false);

            // if this day has an event, specify event image
            view.setBackgroundResource(0);
            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {
                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year)
                    {
                        // mark this day for event
                        view.setBackgroundColor(Color.YELLOW);
                        break;
                    }
                }
            }

            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            if (month != today.getMonth() || year != today.getYear())
            {
                // if this day is outside current month, grey it out
                ((TextView)view).setTextColor(Color.GRAY);
            }
            else if (day == today.getDate())
            {
                // if it is today, set it to bold
                ((TextView)view).setTypeface(null, Typeface.BOLD);
            }

            // set text
            ((TextView)view).setText(String.valueOf(date.getDate()));

            return view;
        }
    }
}