package com.avarsava.stuttersupport;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Alexis Varsava <av11sl@brocku.ca>
 * @author Weston James Knight
 * @version 1.6
 * @since   1.1
 *
 * Provides a backend interface for use by parents, teachers, SLP, &c. Provides information on
 * completed activities, access to override settings, and the ability to export activity data
 * to CSV for processing and reporting
 *
 * TODO: A tappable Calendar interface would go great here
 */
public class ParentTeacherInterfaceActivity extends Activity {
    /**
     * Used to access activity completion data
     */
    private TrackerDbHelper trackerDbHelper;

    /**
     * Used to display information from the Tracker DB to the screen
     */
    public static TextView activitiesToday;
    public static TextView activitiesThisMonth;
    public static TextView activitiesThisWeek;
    public static TextView activitiesAllTime;
    public static TextView numberDeepBreathe;
    public static TextView numberScriptReading;
    public static TextView numberTrainGame;

    /**
     * Inflates XML to screen and assigns TextViews to internal objects.
     *
     * @param savedInstanceState Bundle used by Android for internal messaging
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_teacher_interface);
        trackerDbHelper = new TrackerDbHelper(this);

        activitiesToday = (TextView) findViewById(R.id.activitiesToday);
        activitiesThisWeek = (TextView) findViewById(R.id.activitiesThisWeek);
        activitiesThisMonth = (TextView) findViewById(R.id.activitiesThisMonth);
        activitiesAllTime = (TextView) findViewById(R.id.activitiesAllTime);
        numberDeepBreathe = (TextView) findViewById(R.id.numberDeepBreathe);
        numberScriptReading = (TextView) findViewById(R.id.numberScriptReading);
        numberTrainGame = (TextView) findViewById(R.id.numberTrainGame);

        refreshData();
    }

    /**
     * Handles button presses
     *
     * @param view Button which was pressed
     */
    public void onClick(View view){
        //Handle each type of button
        switch (view.getId()) {
            case R.id.ptiSettingsButton:
               Intent settingsIntent = new Intent(this,SettingsScreenActivity.class);
               settingsIntent.putExtra("prefs", R.xml.parent_teacher_interface_prefs);
                startActivity(settingsIntent);
                break;

            case R.id.exportButton:
                exportDatabase();
                break;
        }
    }

    /**
     * Refreshes screen. Pulls data from TrackerDB and writes to TextViews.
     */
    public void refreshData() {
        Date currentDate = new Date();
        String today = (currentDate.getYear() + 1900) + "-"
                + (currentDate.getMonth()) + "-" + currentDate.getDate();
        String lastWeek = (currentDate.getYear() + 1900) + "-"
                + (currentDate.getMonth()) + "-" + (currentDate.getDate() - 7);
        String lastMonth = (currentDate.getYear() + 1900) + "-"
                + (currentDate.getMonth() - 1) + "-" + currentDate.getDate();

        activitiesToday.setText("Activities completed today: " +
                queryCount("SELECT * FROM tracker WHERE date BETWEEN '" +
                        today + "' AND '" + today + "'") + ".");
        activitiesThisWeek.setText("Activities completed this week: " +
                queryCount("SELECT * FROM tracker WHERE date BETWEEN '" +
                        lastWeek + "' AND '" + today + "'") + ".");
        activitiesThisMonth.setText("Activities completed this month: " +
                queryCount("SELECT * FROM tracker WHERE date BETWEEN '" +
                        lastMonth + "' AND '" + today + "'") + ".");
        activitiesAllTime.setText("Activities completed all time: " +
                queryCount("SELECT * FROM tracker WHERE date BETWEEN '" +
                        "2000-1-1" + "' AND '" + today + "'") + ".");
        numberDeepBreathe.setText("Number of Deep Breathe activities completed: " +
                queryCount("SELECT * FROM tracker WHERE activity = '" +
                        "DeepBreathe" + "'") + ".");
        numberScriptReading.setText("Number of Script Reading activities completed: " +
                queryCount("SELECT * FROM tracker WHERE activity = '" +
                        "ScriptReading" + "'") + ".");
        numberTrainGame.setText("Number of Train Game activities completed: " +
                queryCount("SELECT * FROM tracker WHERE activity = '" +
                        "TrainGame" + "'") + ".");
    }

    /**
     * Returns the number of results in the database for the given query string
     *
     * @param query SQL to execute
     * @return integer of number of lines returned by database
     */
    private int queryCount(String query){
        SQLiteDatabase db = trackerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Exports the entire database to a .csv file on the users external storage
     */
    private void exportDatabase(){
        int permission =
                ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {//Check if permission to store files
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        }

        SQLiteDatabase db = trackerDbHelper.getReadableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM tracker";//Query all items in database
        cursor = db.rawQuery(query, null);
        int rows,cols;
        String exportDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "SolutionsForStuttering.csv";//file name
        String filePath = exportDir + File.separator + fileName;
        File saveFile = new File(filePath);

        FileWriter writer = null;
        try {writer = new FileWriter(saveFile);} catch (IOException e) {e.printStackTrace();}

        BufferedWriter buffer= new BufferedWriter(writer);
        rows = cursor.getCount();
        cols = cursor.getColumnCount();
        if(rows > 0 ){//Checking database is not empty
            cursor.moveToFirst();
            for(int i=0;i<cols;i++){
                if( i!=cols-1) {//if not last item
                    try {
                        buffer.write
                                (cursor.getColumnName(i) + ",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {//last item
                    try {
                        buffer.write(cursor.getColumnName(i));
                    } catch (IOException e) {e.printStackTrace();}
                }
            }
            try {buffer.newLine();} catch (IOException e) {e.printStackTrace();}

            for(int i=0;i<rows;i++){
                cursor.moveToPosition(i);
                for(int j=0;j<cols;j++){
                    if(j!=cols-1){//if not last item
                        try {
                            buffer.write(cursor.getString(j) + ",");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            buffer.write(cursor.getString(j));//last item
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {buffer.newLine();} catch (IOException e) {e.printStackTrace();}
            }
            try {buffer.flush();} catch (IOException e) {e.printStackTrace();}
            db.close();
        }
    }
}