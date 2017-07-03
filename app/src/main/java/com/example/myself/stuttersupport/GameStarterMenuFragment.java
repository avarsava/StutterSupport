package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameStarterMenuFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private int bgResource;
    private Class attachedClass;

    public static final GameStarterMenuFragment newInstance(int pic, Class buttonActivity){
        GameStarterMenuFragment f = new GameStarterMenuFragment();
        f.bgResource = pic;
        f.attachedClass = buttonActivity;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = (View) inflater.inflate(R.layout.fragment_game_starter_menu,
                container, false);
        rootView.setBackgroundDrawable(getResources().getDrawable(bgResource));
        return rootView;
    }

    public Class getAttachedClass(){
        return attachedClass;
    }
}
