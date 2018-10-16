package scout.uw.edu.uwscout;

import android.app.Application;

import scout.uw.edu.uwscout.utils.UserPreferences;

//import scout.uw.edu.scout_android_poc.services.TurbolinksSessionManager;

/**
 * Created by ezturner on 8/23/16.
 */
public class Scout extends Application {

    private static final String LOG_TAG = Scout.class.getSimpleName();
    private static Scout instance;

    public static Scout getInstance(){
        return instance;
    }

    private UserPreferences userPreferences;
    //private ScoutLocation scoutLocation;

    @Override
    public void onCreate(){
        super.onCreate();
        new UserPreferences(getApplicationContext());
        instance = this;
        userPreferences = new UserPreferences(this);

        // temporary removal for initial release - 9/7/17
        //scoutLocation = new ScoutLocation(getApplicationContext());
    }

    public UserPreferences getPreferences(){
        return userPreferences;
    }
}
