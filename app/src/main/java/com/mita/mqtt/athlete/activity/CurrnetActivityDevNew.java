package com.mita.mqtt.athlete.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;

import android.Manifest;

import android.content.pm.PackageManager;

import android.net.Uri;

import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.mita.mqtt.athlete.BuildConfig;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthleteTodaysRunResponseModel;
import com.mita.mqtt.athlete.mqtt.MqttHelper;
import com.mita.mqtt.athlete.services.LUtils;
import com.mita.mqtt.athlete.services.LocationUpdatesService;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;

import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * <p>
 * Note: Users have three options in "Q" regarding location:
 * <ul>
 *     <li>Allow all the time</li>
 *     <li>Allow while app is in use, i.e., while app is in foreground</li>
 *     <li>Not allow location at all</li>
 * </ul>
 * Because this app creates a foreground service (tied to a Notification) when the user navigates
 * away from the app, it only needs location "while in use." That is, there is no need to ask for
 * location all the time (which requires additional permissions in the manifest).
 * <p>
 * "Q" also now requires developers to specify foreground service type in the manifest (in this
 * case, "location").
 * <p>
 * Note: For Foreground Services, "P" requires additional permission in manifest. Please check
 * project manifest for more information.
 * <p>
 * Note: for apps running in the background on "O" devices (regardless of the targetSdkVersion),
 * location may be computed less frequently than requested when the app is not in the foreground.
 * Apps that use a foreground service -  which involves displaying a non-dismissable
 * notification -  can bypass the background location limits and request location updates as before.
 * <p>
 * This sample uses a long-running bound and started service for location updates. The service is
 * aware of foreground status of this activity, which is the only bound client in
 * this sample. After requesting location updates, when the activity ceases to be in the foreground,
 * the service promotes itself to a foreground service and continues receiving location updates.
 * When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that foreground service is removed.
 * <p>
 * While the foreground service notification is displayed, the user has the option to launch the
 * activity from the notification. The user can also remove location updates directly from the
 * notification. This dismisses the notification and stops the service.
 */

public class CurrnetActivityDevNew extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = CurrnetActivityDevNew.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // UI elements.
    private Button mBtnStart;
    private Button mBtnStop;
    private Button Btn_pause;
    private Button Btn_Resume;
    TextView tv_duration_time;
    TextView tv_distance;
    TextView tv_hrt_bt;
    TextView tv_pace_num;
    ImageView Iv_back;
    LinearLayout LLpauseBtn;


    public static String AudioTOPIC = "";
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    private String AthleteRunActivityId;
    private String AthletePlanGoal;
    private String coachId;
    private String RunCount;
    private String RunActID;
    private APIService mAPIService;
    private String status = "0";
    private Dialog alertDialog;
    private ProgressDialog dialog;
    private boolean isRunStart;

    // audio play

    private File file2;
    private MediaRecorder mediaRecorder;
    public static final int RequestPermissionCode = 1;
    private MediaPlayer mediaPlayer;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_currnet);

        startMqttSubscribeCoach();
        SharedPref.init(this);
        mAPIService = ApiUtils.getAPIService();
        dialog = new ProgressDialog(CurrnetActivityDevNew.this);
        dialog.setMessage("Acquiring GPS Coordinats ...");
        dialog.setCanceledOnTouchOutside(true);
        Intent intent = getIntent();
        AthleteRunActivityId = intent.getStringExtra("AthleteRunActivityId");
        AthletePlanGoal = intent.getStringExtra("AthletePlanGoal");
        coachId = intent.getStringExtra("coachId");
        RunCount = intent.getStringExtra("RunCount");

        String athleteID = SharedPref.read(SharedPref.ATH_USER_ID, "");
        AudioTOPIC = "/TOPIC/" + coachId + "/" + athleteID + "/FEEDBACK/REALRUN";
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String CurrDate = sdf1.format(new Date());
        Date date1 = null;
        try {
            date1 = sdf1.parse(CurrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView tv_activityname = findViewById(R.id.tv_activityname);

        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String DayName = outFormat.format(date1);
        tv_activityname.setText(DayName + " Long Run");
        TextView tv_km_goal = findViewById(R.id.tv_km_goal);
        tv_km_goal.setText(AthletePlanGoal + " KM TO GOAL");

//        Toast.makeText(getApplicationContext(), AthleteRunActivityId, Toast.LENGTH_SHORT).show();
        SharedPref.write(SharedPref.RUN_ACTIVITY_COACHId, coachId);

        // Check that the user hasn't revoked permissions by going to Settings.
        if (LUtils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrnetActivityDevNew.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        mBtnStart = (Button) findViewById(R.id.Btn_start);
        mBtnStop = (Button) findViewById(R.id.Btn_stop);
        Btn_pause = (Button) findViewById(R.id.Btn_pause);
        Btn_Resume = (Button) findViewById(R.id.Btn_Resume);


        tv_duration_time = findViewById(R.id.tv_duration_time);
        tv_distance = findViewById(R.id.tv_distance);
        tv_hrt_bt = findViewById(R.id.tv_hrt_bt);
        tv_pace_num = findViewById(R.id.tv_pace_num);
        Iv_back = findViewById(R.id.Iv_back);
        LLpauseBtn = findViewById(R.id.LLpauseBtn);
        Iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunStart = true;
                SharedPref.write(SharedPref.RUN_MODE, "true");
                dialog.show();
//                if (!checkPermissions()) {
//                    requestPermissions();
//                } else {
                mService.requestLocationUpdates();
                getValidateByAthelActivityId();
                Iv_back.setVisibility(View.INVISIBLE);
                mBtnStart.setVisibility(View.GONE);
                LLpauseBtn.setVisibility(View.VISIBLE);
                // }
                startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                status = "3"; // Finished
//                activitySummaryUpdateStatusAndDate();
                SharedPref.write(SharedPref.RUN_MODE, "false");
                mBtnStop.setText("Stopped");
                mBtnStop.setTextColor(getResources().getColor(R.color.colorMetaTheme));

                mBtnStop.setBackground(getResources().getDrawable(R.drawable.buttun_border));
                Btn_pause.setClickable(false);
                Btn_Resume.setClickable(false);
                SharedPref.write(SharedPref.RUN_MODE, "stop");

                mService.removeLocationUpdates();
                AlertDialog();
            }
        });

        Btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound && mService != null)
                    mService.pauseService();
//                isRunStart = false;
//                status = "5"; // pause
//                activitySummaryUpdateStatusAndDate();
                Btn_Resume.setVisibility(View.VISIBLE);
                Btn_pause.setVisibility(View.GONE);
            }
        });

        Btn_Resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBound && mService != null)
                    mService.resumeService();

                Btn_Resume.setVisibility(View.GONE);
                Btn_pause.setVisibility(View.VISIBLE);


            }
        });

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(LUtils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

        if (SharedPref.read(SharedPref.RUN_MODE, "").equals("true")) {

            Iv_back.setVisibility(View.INVISIBLE);
            mBtnStart.setVisibility(View.GONE);
            LLpauseBtn.setVisibility(View.VISIBLE);
        }

    }

    public void AlertDialog() {

        final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#6b379f'>Alert!</font>"));
        alertDialogBuilder.setMessage(Html.fromHtml("<font color='#6b379f'>This Will Stop The Run.!</font>"));

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //mService.removeLocationUpdates();

//                        status = "3"; // Finished
//                        activitySummaryUpdateStatusAndDate();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();

                    }
                });

        alertDialogBuilder.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        if (mService != null) {
            if (mService.helloMita() != 0) {

                // Toast.makeText(getApplicationContext(), String.valueOf(mService.helloMita()), Toast.LENGTH_SHORT).show();
                Hashtable<String, String> serviceData = new Hashtable<String, String>();
                serviceData = mService.getDataInActivityResume();
                tv_duration_time.setText(serviceData.get("duration"));
                tv_distance.setText(serviceData.get("distance"));
                tv_hrt_bt.setText(serviceData.get("heart_rate"));
                tv_pace_num.setText(serviceData.get("pace"));


            }
            //Toast.makeText(getApplicationContext(), serviceData.get("duration"), Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(CurrnetActivityDevNew.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(CurrnetActivityDevNew.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Toast.makeText(CurrnetActivityDevNew.this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CurrnetActivityDevNew.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            String duration = intent.getStringExtra(LocationUpdatesService.EXTRA_DUTATION);
            String distance = intent.getStringExtra(LocationUpdatesService.EXTRA_DISTANCE);
            String pace = intent.getStringExtra(LocationUpdatesService.EXTRA_PACE);
            String heartRate = intent.getStringExtra(LocationUpdatesService.EXTRA_HEARTRATE);
            if (duration != null && distance != null && pace != null) {

                if (duration.equals("null")) {
                    tv_duration_time.setText("00:00:00");
                } else {
                    tv_duration_time.setText(duration);
                }
                tv_hrt_bt.setText(heartRate);
                tv_distance.setText(distance);
                tv_pace_num.setText(pace);
                dialog.dismiss();
            }

            if (location != null) {
                // Toast.makeText(CurrnetActivityDevNew.this, LUtils.getLocationText(location), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(LUtils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(LUtils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
//        if (requestingLocationUpdates) {
//            mBtnStart.setEnabled(false);
//            mBtnStop.setEnabled(true);
//        } else {
//            mBtnStart.setEnabled(true);
//            mBtnStop.setEnabled(false);
//        }
    }

    // get Acthlete Todays Run Activity ID Validation
    private void getValidateByAthelActivityId() {
        Call<AthleteTodaysRunResponseModel> call = mAPIService.getValidateByAthelActivityId(AthleteRunActivityId);
        call.enqueue(new Callback<AthleteTodaysRunResponseModel>() {
            @Override
            public void onResponse(Call<AthleteTodaysRunResponseModel> call, Response<AthleteTodaysRunResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===ResTodysRunvalcoVal", String.valueOf(response.body().getCode()));

                    String AthleteRunActivityIdValidate = response.body().getTodayCard().getAthelRunPlanActId();

                    Log.i("==ValidateRunActID", AthleteRunActivityIdValidate);
                    // Log.i("==ValidateRunCount", String.valueOf(RunCount));

                    RunActID = AthleteRunActivityIdValidate;


                    SharedPref.write(SharedPref.RUN_ACTIVITY_ID, RunActID);

//                    status = "1"; // Running
//                    activitySummaryUpdateStatusAndDate();

                }

            }

            @Override
            public void onFailure(Call<AthleteTodaysRunResponseModel> call, Throwable t) {
                Log.i("===ErrorResTodaysRun", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void activitySummaryUpdateStatusAndDate() {

        //Log.i("---RunActID", RunActID);
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

    @Override
    public void onBackPressed() {
        if (isRunStart) {
            final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder.setTitle(Html.fromHtml("<font color='#6b379f'>Alert!</font>"));
            alertDialogBuilder.setMessage(Html.fromHtml("<font color='#6b379f'>Do You Wnat to Stop The Run.?</font>"));

            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            status = "3"; // Finished
                            activitySummaryUpdateStatusAndDate();
                            mService.removeLocationUpdates();
                            finish();

                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            // stopService(new Intent(CurrnetActivityDev.this, LocationMonitoringService.class));
            finish();
        }
    }

    private void startMqttSubscribeCoach() {
        TextView tvmsg = findViewById(R.id.tvmsg);
        mqttHelper = new MqttHelper(getApplicationContext(), tvmsg);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.i("DebugCur", "SubScribed Sucessfully!!!!");

            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Debug", " SubScribe ConnectionLost - " + throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                // Log.i("==Debug", mqttMessage.toString());
                String message;
                JSONObject mainObj = new JSONObject();
                JSONArray ja = new JSONArray();
                // if (TOPIC.equals(topic)) {
                message = new String(mqttMessage.getPayload());
                Log.i("===AMqttAudioMsg", message);

                JSONObject jsonObj = new JSONObject(message);
                String AudioMsg = jsonObj.getString("feedBackStream");
                Log.i("===Aaudio", AudioMsg);
                Toast.makeText(CurrnetActivityDevNew.this, "Received a Voice Msg From Coach : " + coachId, Toast.LENGTH_SHORT).show();
                byte[] decoded = Base64.decode(AudioMsg, 0);

                Log.i("===ADecodedAudio: ", String.valueOf(decoded));
                if (checkPermission()) {
                    try {
                        Random random = new Random();
                        int ii = 100000;
                        ii = random.nextInt(ii);
                        file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ii + "audio1.mp3");
                        FileOutputStream os = new FileOutputStream(file2, true);
                        os.write(decoded);
                        os.close();
                        Log.i("===ADecodedFile", String.valueOf(file2));
                        MediaRecorderReady();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("===ACatchEx", e.getMessage());
                    }
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(String.valueOf(file2));
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(CurrnetActivityDevNew.this, "Playing coach msg", Toast.LENGTH_SHORT).show();
                    mediaPlayer.start();
                } else {
                    Toast.makeText(CurrnetActivityDevNew.this, "222", Toast.LENGTH_SHORT).show();

                    requestPermission();
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.i("Debug", "DeliveryCompleted : " + iMqttDeliveryToken.toString());
            }
        });

    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(file2);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(CurrnetActivityDevNew.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
