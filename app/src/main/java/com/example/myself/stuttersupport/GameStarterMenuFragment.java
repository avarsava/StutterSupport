package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Fragments used in the ViewPager on the Main Menu to scroll between game options.
 */
public class GameStarterMenuFragment extends Fragment {
    /**
     * Resources ID of the background image to display on the screen.
     */
    private int bgResource;

    /**
     * Class of Activity to start when the button is pressed.
     */
    private Class attachedClass;

    /**
     * Resources ID of the Settings file associated with the game.
     */
    private int attachedSettingsFile;

    /**
     * Creates a new menu screen for the game.
     *
     * @param pic Resources ID of the background image to display for this game.
     * @param buttonActivity Class of the Activity to start for this game.
     * @param settingsId Resources ID of the settings file associated with this game.
     * @return a new GameStarterMenuFragment with the submitted attributes.
     */
    public static final GameStarterMenuFragment newInstance(int pic, Class buttonActivity,
                                                            int settingsId){
        GameStarterMenuFragment f = new GameStarterMenuFragment();
        f.bgResource = pic;
        f.attachedClass = buttonActivity;
        f.attachedSettingsFile = settingsId;
        return f;
    }

    /**
     * Called automatically when Fragment is created. Inflates layout, draws background.
     *
     * @param inflater LayoutInflator to inflate the layout.
     * @param container ViewGroup containing this Fragment.
     * @param savedInstanceState Used for Android internal communication.
     * @return new View to populate Fragment with.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = (View) inflater.inflate(R.layout.fragment_game_starter_menu,
                container, false);
        rootView.setBackgroundDrawable(getResources().getDrawable(bgResource));
        return rootView;
    }

    /**
     * Gets the class associated with this Fragment. Used to launch game.
     *
     * @return Class associated with this Fragment.
     */
    public Class getAttachedClass(){
        return attachedClass;
    }

    /**
     * Gets the Resource ID of the settings file associated with this game.
     *
     * @return Resource ID of the settings file associated with this game.
     */
    public int getAttachedSettingsFile() {
        return attachedSettingsFile;
    }
}
