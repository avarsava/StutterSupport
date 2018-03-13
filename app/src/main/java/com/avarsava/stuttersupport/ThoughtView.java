package com.avarsava.stuttersupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Displays a thought and the corresponding mood.s
 */

public class ThoughtView extends View {
    public ThoughtView(Context context, String t, String m) {
        super(context);
        LayoutInflater layoutInflater
                = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_thought, null);
    }
}
