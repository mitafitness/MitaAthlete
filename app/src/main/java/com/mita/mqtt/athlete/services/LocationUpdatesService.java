/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mita.mqtt.athlete.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.activity.CSVReader;
import com.mita.mqtt.athlete.activity.CurrnetActivityDevNew;
import com.mita.mqtt.athlete.mqtt.MqttHelper;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.GlobalConstants;
import com.mita.utils.SharedPref;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that service is removed.
 */
public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "com.mita.mqtt.athlete";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    public static final String EXTRA_DUTATION = PACKAGE_NAME + ".duration";
    public static final String EXTRA_DISTANCE = PACKAGE_NAME + ".distance";
    public static final String EXTRA_PACE = PACKAGE_NAME + ".pace";
    public static final String EXTRA_HEARTRATE = PACKAGE_NAME + ".heartrate";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;


    private ArrayList<Float> accuracyList = new ArrayList<Float>();
    private ArrayList<Location> locList = new ArrayList<>();
    private double prevLong = 0.0f;
    private double prevLati = 0.0f;

    static double totDistance = 0.0;
    private static int accuracyCount = 0;
    private static float accuracyCutOff = 15.00f;
    private long seconds;
    private String paceCal;
    private String prevosTime;
    private Runnable mRunnable;
    private String duration;
    private MqttHelper mqttHelper;
    private String PreTime1, PreTime2;
    private APIService mAPIService;
    private List<String[]> Row;
    static int RowCount = 0;
    static int mitaDataCount = 0;
    private String mqttTopic;
    static boolean isStop = false;
    long currTime, pastTime;
    long preTimeSec1, preTimeSec2;
    private String HeartRate;

    public LocationUpdatesService() {
    }


    @Override
    public void onCreate() {

        mAPIService = ApiUtils.getAPIService();
        initStart();
        startMqtt();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

//        HandlerThread handlerThread = new HandlerThread(TAG);
//        handlerThread.start();
//        mServiceHandler = new Handler(handlerThread.getLooper());


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");

        readFile();
        mServiceHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {

                //  getLocation();
                RowCount++;
                if (RowCount > 470) {
                    RowCount = 3;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                currTime = System.currentTimeMillis();
                if (pastTime == 0) {
                    pastTime = currTime + 1;
                }

                duration = getDuration2(currTime, pastTime);
                pastTime = currTime;

                //  duration = getDuration(currentDateandTime, prevosTime);

                prevosTime = currentDateandTime;


                //   publishing data through mqtt
                if (mLocation != null) {

                    // Send Mqtt Start Event
                    if (mitaDataCount == 0) {
                        //RUNSUMMARY/START
                        String status = "1";
                        publishingStartEvent("START", status);
                    }

                    publishingData();

                    // Notify anyone listening for broadcasts about the new location.
                    Intent intent = new Intent(ACTION_BROADCAST);
                    intent.putExtra(EXTRA_LOCATION, mLocation);
                    intent.putExtra(EXTRA_DUTATION, duration);
                    intent.putExtra(EXTRA_DISTANCE, String.valueOf(totDistance));
                    intent.putExtra(EXTRA_PACE, paceCal);
                    intent.putExtra(EXTRA_HEARTRATE, HeartRate);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    mitaDataCount++;

                    Log.i("==handler", "handler Running ");
                }
                // Update notification content if running as a foreground service.
                if (serviceIsRunningInForeground(LocationUpdatesService.this)) {
                    mNotificationManager.notify(NOTIFICATION_ID, getNotification());
                }

                mServiceHandler.postDelayed(this, GlobalConstants.getUpdateInterval());
            }

        };

        mServiceHandler.postDelayed(mRunnable, GlobalConstants.getUpdateInterval());


        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
//        if (startedFromNotification) {
//            removeLocationUpdates();
//            stopSelf();
//        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && LUtils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");

            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        publishingMqttDataStopEvent("", "3");
        // activitySummaryUpdateStatusAndDate();
        startForeground(NOTIFICATION_ID, getNotification1());
        if (mServiceHandler != null) {
            mServiceHandler.removeCallbacks(mRunnable);
        }
        Log.i("==handler", "handler stop ");
        removeLocationUpdates();
        clearData();
        stopSelf();
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        LUtils.setRequestingLocationUpdates(this, true);

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            LUtils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            // getNotification1();
            mitaDataCount = 0;
           // initStart();
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            if (mServiceHandler != null) {

                mServiceHandler.removeCallbacks(mRunnable);
            }
            Log.i("==handler", "handler stop 2 ");
            LUtils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            LUtils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    private void initStart() {
        totDistance = 0.0;
        prevLati = 0.0;
        prevLong = 0.0;
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = LUtils.getLocationText1(duration, String.valueOf(totDistance), paceCal);
//        CharSequence text = LUtils.getLocationText1();

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CurrnetActivityDevNew.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)

//                .addAction(R.drawable.logout, getString(R.string.launch_activity), activityPendingIntent)
//                .addAction(R.drawable.logout, getString(R.string.remove_location_updates), servicePendingIntent)
                .setContentText(text)
                .setContentTitle(LUtils.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.athlete_logo)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }


    // setting notification
    private Notification getNotification1() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = LUtils.getLocationText(mLocation);
//        CharSequence text = LUtils.getLocationText1();

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CurrnetActivityDevNew.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.logout, getString(R.string.launch_activity), activityPendingIntent)
                .addAction(R.drawable.logout, getString(R.string.remove_location_updates), servicePendingIntent)
                .setContentText(text)
                .setContentTitle(LUtils.getLocationTitle1(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.athlete_logo)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        mLocation = location;


        if (mLocation != null) {

            double segDist = 0.0;
            if (mLocation.getAccuracy() <= accuracyCutOff) {
                segDist = getDistance(mLocation.getLatitude(), prevLati, mLocation.getLongitude(), prevLong);
                prevLati = mLocation.getLatitude();
                prevLong = mLocation.getLongitude();
                accuracyCount = 0;
                clearData();
            } else if (accuracyCount == 3) {
                int smallestIndex = 2; //  findSmallest( 3) ;
                Location loc = null;
                try {
                    loc = mLocation;
                } catch (ArrayIndexOutOfBoundsException aie) {
                    Log.i("==Exception", "ArrayIndexOutOfBoundsException " + aie.getMessage());

                }
                segDist = getDistance(loc.getLatitude(), prevLati, loc.getLongitude(), prevLong);

                float accValue = loc.getAccuracy();

                prevLati = loc.getLatitude();
                prevLong = loc.getLongitude();
                accuracyCount = 0;

                clearData();

            } else {
                locList.add(mLocation);
                Float f1 = new Float(mLocation.getAccuracy());
                accuracyList.add(f1);
                accuracyCount++;
                float accValue = mLocation.getAccuracy();
            }

            double distance = segDist;

            DecimalFormat df = new DecimalFormat("00.000");
            totDistance = Double.parseDouble(df.format(totDistance + distance));

            //TotalDistance = (float) totDistance;

        }


        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(GlobalConstants.getUpdateInterval());
        mLocationRequest.setFastestInterval(GlobalConstants.getFastestInterval());
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(GlobalConstants.getSmallestDisplaccement());
    }

    // pause method
    public void pauseService() {
        String status = "5";
        publishingStartEvent("PAUSE", status);

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        if (mServiceHandler != null) {

            mServiceHandler.removeCallbacks(mRunnable);
        }
        Log.i("==handler", "handler paused 3");

    }


    public int helloMita() {
        return mitaDataCount;
    }


    // send data to activity in resume method
    public Hashtable<String, String> getDataInActivityResume() {

        Hashtable<String, String> actData = new Hashtable<String, String>();
        actData.put("duration", duration);
        actData.put("pace", paceCal);
        actData.put("heart_rate", HeartRate);
        actData.put("distance", String.valueOf(totDistance));
        return actData;
    }

    // resume service call method
    public void resumeService() {
        String status = "111";
        publishingStartEvent("RESUME", status);
        requestLocationUpdates();
        //  readFile();
        mServiceHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {

                //  getLocation();
                RowCount++;
                if (RowCount > 470) {
                    RowCount = 3;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                currTime = System.currentTimeMillis();
                if (pastTime == 0) {
                    pastTime = currTime + 1;
                }

                duration = getDuration3(preTimeSec1, preTimeSec2);
                // pastTime = currTime;
                //  duration = getDuration1(PreTime1, PreTime2);

                prevosTime = PreTime1;

                if (mLocation != null) {
                    //   publishing data through mqtt
                    publishingData();

                    // Notify anyone listening for broadcasts about the new location.
                    Intent intent = new Intent(ACTION_BROADCAST);
                    intent.putExtra(EXTRA_LOCATION, mLocation);
                    intent.putExtra(EXTRA_DUTATION, duration);
                    intent.putExtra(EXTRA_DISTANCE, String.valueOf(totDistance));
                    intent.putExtra(EXTRA_PACE, paceCal);
                    intent.putExtra(EXTRA_HEARTRATE, HeartRate);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    mitaDataCount++;
                }

                Log.i("==handler", "handler resumed running");

                mServiceHandler.postDelayed(this, GlobalConstants.getUpdateInterval());
            }

        };

        mServiceHandler.postDelayed(mRunnable, GlobalConstants.getUpdateInterval());
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    // clear acccuracy array list
    private void clearData() {
        if (accuracyList != null) {
            accuracyList.clear();
        } else {
            accuracyList = new ArrayList<>();
        }

        locList = new ArrayList<Location>();
    }

    // get distance passing latitide and longitude
    public double getDistance(double lat1, double lat2, double lon1, double lon2) {


        if (lat2 == 0 || lon2 == 0)
            return 0;

        if (lat1 == 0 || lon1 == 0)
            return 0;
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }




    public String getDuration2(long currentTime, long pastTime) {
        long diffTime = currentTime - pastTime;

        long timeInSeconds = diffTime / 1000;

        seconds += timeInSeconds;

        Log.i("===Seconds", " Current is : " + timeInSeconds + " P : " + pastTime + " C : " + currentTime + " S :  " + seconds);
        int hours = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        int secnd = (int) (seconds % 60);
        paceCal = getPaceCal2(seconds, (float) totDistance);
        //Log.i("===PaceCal" ,"Pace Calculation : "+paceCal);
        String timeDiff = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, min, secnd);
        preTimeSec1 = currentTime;
        preTimeSec2 = pastTime;
        //paceCal = getPaceCal(hours, min, secnd, (float) totDistance);
        return timeDiff;
    }

    public String getDuration3(long currentTime, long pastTime) {
        long diffTime = currentTime - pastTime;

        long timeInSeconds = diffTime / 1000;

        seconds += timeInSeconds;

        Log.i("===Seconds", " Current is : " + timeInSeconds + " P : " + pastTime + " C : " + currentTime + " S :  " + seconds);
        int hours = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        int secnd = (int) (seconds % 60);
        paceCal = getPaceCal2(seconds, (float) totDistance);
        String timeDiff = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, min, secnd);
        preTimeSec1 = currentTime;
        preTimeSec2 = pastTime;
       // paceCal = getPaceCal(hours, min, secnd, (float) totDistance);
        return timeDiff;
    }


    public String getPaceCal2(long paceSeconds, float distance) {

        float timeInMinits = paceSeconds/60.0f;
        int paceInMinits;
        int paceInSeconds = 0;
       // Log.i("==PaceCalculation", "Pace Caculation : "+timeInMinits );
        //float inMinits = paceSeconds / 60;
        // calculate pace based on seconds
        if (distance == 0) {
            paceInMinits = 0;
        } else {
            paceInMinits = (int) (timeInMinits / distance);
            paceInSeconds = (int) ((timeInMinits % distance) * 60);
        }
        Log.i("==PaceCa", " paceInSeconds :  " + paceInSeconds +" distance : "+ distance+ " paceInMinits : " + paceInMinits + " timeInMinits : " +timeInMinits+" TotalSeconds :  " + paceSeconds);
        String text = " paceInSeconds :  " + paceInSeconds +" distance : "+ distance+ " paceInMinits : " + paceInMinits + " timeInMinits : " +timeInMinits+" TotalSeconds :  " + paceSeconds;

        // writing external log file
        File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/log.file");

        //file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ii + "audio1.mp3");

        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DecimalFormat formattingObject = new DecimalFormat("00");
        return "" + paceInMinits + ":" + formattingObject.format(paceInSeconds);
    }


    // initiazing mqtt service
    private void startMqtt() {
        TextView tvmsg = null;
        mqttHelper = new MqttHelper(getApplicationContext(), tvmsg);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug", "Sucessfully");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Debug", "ConnectionLost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//                Log.w("Debug", mqttMessage.toString());
//                String message;
//                message = new String(mqttMessage.getPayload());
//                Log.i("==MqttMsg", message);
//                JSONObject jsonObj = new JSONObject(message);
//                String audio_string = jsonObj.getString("audio_string");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.w("Debug", "DeliveryCompleted");
            }

        });

    }

    // Publishing data through mqtt to subscriber
    public void publishingData() {

        String athleteUsrId = SharedPref.read(SharedPref.ATH_USER_ID, "");

        mqttTopic = "topic/" + SharedPref.read(SharedPref.RUN_ACTIVITY_COACHId, "") + "/" + athleteUsrId + "/ACTIVITY_RUN/RUN_DATA";

        JSONObject manJson = new JSONObject();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            String timestamp = sdf1.format(new Date());

            String SENSOR_ID = "SENSOR_ID";
            String SENSOR_TYPE = "{" + "SENSOR_TYPE" + ":" + "Mobile}";
            String Time_ms = Row.get(RowCount)[0]; //Time(ms)
            String steps = Row.get(RowCount)[6]; //Steps
            String pace = Row.get(RowCount)[7]; //Pace(min/km)
            String cadence = Row.get(RowCount)[8]; //Cadence
            HeartRate = Row.get(RowCount)[9]; //HeartRate

            String used = Row.get(RowCount)[11]; //Used
            String fixtedTime = Row.get(RowCount)[12]; //FixTime(s)
            String stepSensorTime = Row.get(RowCount)[13]; //StepSensorTime(s)
            String MaxAcc = Row.get(RowCount)[14]; //MaxAcc
            String MinAcc = Row.get(RowCount)[15]; //MinAcc
            String elevationGap = Row.get(RowCount)[16]; //ElevationGap(m)
            String rr_ms = Row.get(RowCount)[17]; //RR (ms)
            String lat = null, longt = null, accurecy = null, altitude = null;

            if (mLocation != null) {
                lat = String.valueOf(mLocation.getLatitude());
                longt = String.valueOf(mLocation.getLongitude());
                accurecy = String.valueOf(mLocation.getAccuracy());
                altitude = String.valueOf(mLocation.getAltitude());
            }

            String athUserId = SharedPref.read(SharedPref.ATH_USER_ID, "");
            manJson.put("sensorId", SENSOR_ID);
            manJson.put("althRunActivityId", SharedPref.read(SharedPref.RUN_ACTIVITY_ID, ""));
            manJson.put("absTimeStamp", currentDateandTime);
            manJson.put("hrRRTime", rr_ms);
            manJson.put("elevation", elevationGap);
            manJson.put("distance", String.valueOf(Double.valueOf(totDistance)));
            manJson.put("latitude", lat);
            manJson.put("longitude", longt);
            manJson.put("accuracy", accurecy);
            manJson.put("altitude", altitude);
            manJson.put("steps", steps);

            /*if (paceCal != null) {

                if (Double.parseDouble(paceCal.trim()) >= GlobalConstants.PACE_THRESOLD) {
                    String st = "Pace Rate Crossed Throsold  : " + paceCal.trim();
                    publishingAttentionNeededPace(st, "2");
                }
            }*/
            if (paceCal == null) {
                paceCal = "";
            }
            manJson.put("pace", paceCal);
            manJson.put("cadence", cadence);

           /* if (HeartRate != null) {
                if (Integer.parseInt(HeartRate.trim()) >= GlobalConstants.HEARTRATE_THRESOLD) {
                    String st = "Heart Rate Crossed Throsold=" + HeartRate.trim();
                    publishingAttentionNeededHeartRate(st, "2");
                    Toast.makeText(this, "Test : Attention H R", Toast.LENGTH_SHORT).show();
                }

                manJson.put("heartRate", HeartRate.trim());
            } */

            manJson.put("heartRate", HeartRate.trim());

            manJson.put("absTime", timestamp);
            manJson.put("run", "");
            manJson.put("duration", duration);
            manJson.put("used", used);
            manJson.put("fixtedTime", fixtedTime);
            manJson.put("stepSensorTime", stepSensorTime);
            manJson.put("MinAcc", MinAcc);
            manJson.put("MaxAcc", MaxAcc);
            manJson.put("athUserId", athUserId);

            mqttHelper.publishData(manJson.toString(), mqttTopic);
            Log.i("==MqttPublisherData", String.valueOf(manJson));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Publishing data through mqtt to subscriber
    public void publishingStartEvent(String eventType, String status) {

        String athleteUsrId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        //TOPIC/1/1/RUNSUMMARY/START
        mqttTopic = "topic/" + SharedPref.read(SharedPref.RUN_ACTIVITY_COACHId, "") + "/" + athleteUsrId + "/RUNSUMMARY/" + eventType;

        JSONObject manJson = new JSONObject();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            String timestamp = sdf1.format(new Date());

            String SENSOR_ID = "SENSOR_ID";
            String athUserId = SharedPref.read(SharedPref.ATH_USER_ID, "");
            manJson.put("sensorId", SENSOR_ID);
            manJson.put("althRunActivityId", SharedPref.read(SharedPref.RUN_ACTIVITY_ID, ""));
            manJson.put("absTimeStamp", currentDateandTime);
            manJson.put("athUserId", athUserId);
            manJson.put("status", status);
            if (eventType.equals("PAUSE")) {

                manJson.put("pauseAt", timestamp);
            }

            if (eventType.equals("RESUME")) {

                manJson.put("resumeAt", timestamp);
            }

            mqttHelper.publishData(manJson.toString(), mqttTopic);
            Log.i("==MqttPublisherData", "MQTT START ACTIVITY DATA" + String.valueOf(manJson));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Publishing data through mqtt to subscriber
    public void publishingAttentionNeededHeartRate(String st, String status) {

        String athleteUsrId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        //TOPIC/1/1/RUNSUMMARY/START
        mqttTopic = "topic/" + SharedPref.read(SharedPref.RUN_ACTIVITY_COACHId, "") + "/" + athleteUsrId + "/RUNSUMMARY/ATTENTION_NEEDED";

        JSONObject manJson = new JSONObject();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            String timestamp = sdf1.format(new Date());
            String HeartRate = Row.get(RowCount)[9]; //HeartRate
            String SENSOR_ID = "SENSOR_ID";
            String athUserId = SharedPref.read(SharedPref.ATH_USER_ID, "");
            manJson.put("sensorId", SENSOR_ID);
            manJson.put("althRunActivityId", SharedPref.read(SharedPref.RUN_ACTIVITY_ID, ""));
            manJson.put("absTimeStamp", currentDateandTime);
            manJson.put("athUserId", athUserId);
            manJson.put("status", status);
            manJson.put("attnHeartRate", st);
            manJson.put("attnHeartRateTime", currentDateandTime);
            mqttHelper.publishData(manJson.toString(), mqttTopic);
            Log.i("==MqttPublisherData", "MQTT START ACTIVITY DATA" + String.valueOf(manJson));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Publishing data through mqtt to subscriber START, PAUSE, RESUME
    public void publishingMqttDataStopEvent(String st, String status) {

        String athleteUsrId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        //TOPIC/1/1/RUNSUMMARY/START
        mqttTopic = "topic/" + SharedPref.read(SharedPref.RUN_ACTIVITY_COACHId, "") + "/" + athleteUsrId + "/RUNSUMMARY/STOP";

        JSONObject manJson = new JSONObject();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            String timestamp = sdf1.format(new Date());

            String SENSOR_ID = "SENSOR_ID";
            String athUserId = SharedPref.read(SharedPref.ATH_USER_ID, "");
            manJson.put("sensorId", SENSOR_ID);
            manJson.put("althRunActivityId", SharedPref.read(SharedPref.RUN_ACTIVITY_ID, ""));
            manJson.put("absTimeStamp", currentDateandTime);
            manJson.put("athUserId", athUserId);
            manJson.put("status", status);
            manJson.put("runCount", "1");
            manJson.put("totActivityRun", String.valueOf(totDistance));
            manJson.put("totActivityDuration", duration);
            manJson.put("totActivityPace", paceCal);
            mqttHelper.publishData(manJson.toString(), mqttTopic);
            Log.i("==MqttPublisherDataStop", "MQTT STOP ACTIVITY DATA" + String.valueOf(manJson));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Publishing data through mqtt to subscriber
    public void publishingAttentionNeededPace(String st, String status) {

        String athleteUsrId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        //TOPIC/1/1/RUNSUMMARY/START
        mqttTopic = "topic/" + SharedPref.read(SharedPref.RUN_ACTIVITY_COACHId, "") + "/" + athleteUsrId + "RUNSUMMARY/ATTENTION_NEEDED";

        JSONObject manJson = new JSONObject();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            String timestamp = sdf1.format(new Date());
            String SENSOR_ID = "SENSOR_ID";
            String athUserId = SharedPref.read(SharedPref.ATH_USER_ID, "");
            manJson.put("sensorId", SENSOR_ID);
            manJson.put("althRunActivityId", SharedPref.read(SharedPref.RUN_ACTIVITY_ID, ""));
            manJson.put("absTimeStamp", currentDateandTime);
            manJson.put("athUserId", athUserId);
            manJson.put("status", status);
            manJson.put("attnPace", st);
            if (paceCal == null) {
                paceCal = "";
            }
            manJson.put("attnPaceTime", currentDateandTime);
            mqttHelper.publishData(manJson.toString(), mqttTopic);
            Log.i("==MqttPublisherData", "MQTT START ACTIVITY DATA" + String.valueOf(manJson));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void activitySummaryUpdateStatusAndDate() {

        //Log.i("---RunActID", RunActID);
        String RunActID = SharedPref.read(SharedPref.RUN_ACTIVITY_ID, "");
        String status = "3"; // finsihed
        Call<ResponseBody> call = mAPIService.activitySummaryUpdateStatusAndDate(RunActID, status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Log.i("===ResSucStats", String.valueOf(response.body()) + " Update Success");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("===ErrorResStatus", t.getMessage().toString());

            }
        });

    }

    // reading CSV File from local
    public void readFile() {
        InputStream inputStream = getResources().openRawResource(R.raw.athlete_run);
        CSVReader csvFile = new CSVReader(inputStream);
        Row = csvFile.read();

    }
}
