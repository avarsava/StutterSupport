package com.example.myself.stuttersupport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameStarterMenuFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String message;
    private Class attachedClass;

    public static final GameStarterMenuFragment newInstance(String gameName, Class buttonActivity){
        GameStarterMenuFragment f = new GameStarterMenuFragment();
        f.message = gameName;
        f.attachedClass = buttonActivity;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = (View) inflater.inflate(R.layout.fragment_game_starter_menu,
                container, false);
        TextView messageTextView = (TextView) rootView.findViewById(R.id.textView);
        messageTextView.setText(message);
        return rootView;
    }

    public String getMessage(){
        return message;
    }

    public Class getAttachedClass(){
        return attachedClass;
    }
}
