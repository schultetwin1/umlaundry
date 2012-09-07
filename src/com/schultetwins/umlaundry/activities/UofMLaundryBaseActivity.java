package com.schultetwins.umlaundry.activities;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.schultetwins.umlaundry.R;

abstract public class UofMLaundryBaseActivity extends Activity {
	
	public static final String ROOM_CODE         = "com.schultetwins.umlaundry.ROOM_CODE";
	public static final String BUILDING_CODE     = "com.schultetwins.umlaundry.BUILDING_CODE";
	public static final String MACHINE_CODE      = "com.schultetwins.umlaundry.MACHINE_CODE";
	public static final String WIFI_STATUS       = "com.schultetwins.umlaundry.WIFI_STATUS";
	public static final String MOBILE_STATUS     = "com.schultetwins.umlaundry.MOBILE_STATUS";
	
	public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
	
    // Whether there is a Wi-Fi connection.
    protected static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    protected static boolean mobileConnected = false;
    // Whether the display can be refreshed.
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
    public static String sPref = null;
	
	// The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

    }
    
    // Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
        // display. For example, if the user has set "Wi-Fi only" in prefs and the
        // device loses its Wi-Fi connection midway through the user using the app,
        // you don't want to refresh the display--this would force the display of
        // an error page instead of stackoverflow.com content.
        if (refreshDisplay) {
            loadPage(false);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }
    
    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }
    
    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    abstract protected void loadPage(boolean force);
    
    // Displays an error if the app is unable to load content.
    protected void showErrorPage(String msg) {
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    // Populates the activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
        case R.id.refresh:
                loadPage(true /*Force Refresh*/);
                return true;
        case android.R.id.home:
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, UofMLaundryBuildingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    /**
    *
    * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
    * which indicates a connection change. It checks whether the type is TYPE_WIFI.
    * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
    * main activity accordingly.
    *
    */
   public class NetworkReceiver extends BroadcastReceiver {

       @Override
       public void onReceive(Context context, Intent intent) {
           ConnectivityManager connMgr =
                   (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

           // Checks the user prefs and the network connection. Based on the result, decides
           // whether
           // to refresh the display or keep the current display.
           // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
           if (WIFI.equals(sPref) && networkInfo != null
                   && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
               // If device has its Wi-Fi connection, sets refreshDisplay
               // to true. This causes the display to be refreshed when the user
               // returns to the app.
               refreshDisplay = true;

               // If the setting is ANY network and there is a network connection
               // (which by process of elimination would be mobile), sets refreshDisplay to true.
           } else if (ANY.equals(sPref) && networkInfo != null) {
               refreshDisplay = true;

               // Otherwise, the app can't download content--either because there is no network
               // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
               // is no Wi-Fi connection.
               // Sets refreshDisplay to false.
           } else {
               refreshDisplay = false;
               Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
           }
       }
   }
}