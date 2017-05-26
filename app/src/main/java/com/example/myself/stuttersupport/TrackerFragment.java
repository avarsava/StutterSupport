package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackerFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static final TrackerFragment newInstance(String message){
        TrackerFragment f = new TrackerFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        String message = getArguments().getString(EXTRA_MESSAGE);

        View rootView = (View) inflater.inflate(R.layout.fragment_tracker_menu,
                container, false);
        TextView messageTextView = (TextView) rootView.findViewById(R.id.textView);
        messageTextView.setText(message);
        return rootView;
    }
}
