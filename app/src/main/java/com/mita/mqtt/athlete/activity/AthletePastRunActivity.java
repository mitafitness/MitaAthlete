package com.mita.mqtt.athlete.activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.mita.athlete.login.UserActivity;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.adapater.AthletePastRunAudioAdpater;
import com.mita.mqtt.athlete.adapater.CoachAdapter;
import com.mita.mqtt.athlete.fragment.HrmFragment;
import com.mita.mqtt.athlete.fragment.PaceFragment;
import com.mita.mqtt.athlete.fragment.RunFragment;
import com.mita.mqtt.athlete.model.AthleteActivitySummaryContentModel;
import com.mita.mqtt.athlete.model.AthleteActivitySummaryModel;
import com.mita.mqtt.athlete.model.PastRunAudioFeedBackModel;
import com.mita.mqtt.athlete.mqtt.MqttHelper;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.GlobalConstants;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.google.android.gms.maps.model.JointType.ROUND;

/*
    Athlete can see his past run in this activity.

 */


public class AthletePastRunActivity extends AppCompatActivity implements OnMapReadyCallback, OnChartValueSelectedListener {

    private TextView mtv_heartbeat;
    private TextView mtv_durationtime;
    private TextView mtv_distance;
    private TextView mtv_pace;
    private APIService mAPIService;
    List<AthleteActivitySummaryContentModel> mAthActivitySummarlist = new ArrayList<>();
    List<AthleteActivitySummaryContentModel> mAthActivityAudiolist = new ArrayList<>();
    static int RowCount = 0;
    private Handler handlerOne;
    private Runnable runnableOne;

    private String AthleteRunActivityId;
    private int TIME_INTERVEL = 3000;
    private ImageView iv_play_inactive, iv_play;
    private ImageView iv_pause, iv_pause_active;
    private Handler handlerF;
    private Runnable runnableF;
    private Runnable runnableB;
    private Handler handlerB;
    private boolean fowrdIsClick = false;
    private boolean bowrdIsClick = false;
    private boolean playIsClick = false;
    private SeekBar seekbar;
    private androidx.appcompat.app.AlertDialog alertDialog;

    private boolean fClick = true;
    private boolean bClick = true;
    private GoogleMap googleMap;

    private List<LatLng> polyLineList = new ArrayList<>();
    private Polyline greyPolyLine;

    private LinearLayout llRunMap, llHrmGraph, llPaceGraph;
    private LinearLayout llMapFragment, llPaceGraphChart, llHrmGraphChart;
    private TextView paceLable, runLable, hrmLable;

    private LineChart paceLineChart;
    private LineChart hrmLineChart;
    Typeface mTf;
    private final int[] colors = new int[]{
            Color.rgb(137, 230, 81),
            Color.rgb(240, 240, 30),
            Color.rgb(89, 199, 250),
            Color.rgb(250, 104, 104)
    };
    private LineDataSet set1;
    private ArrayList<Entry> paceValues;
    private RecyclerView recyclerview_audio;

    // audio play

    private File file2;
    private MediaRecorder mediaRecorder;
    public static final int RequestPermissionCode = 1;
    private MediaPlayer mediaPlayer;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private MqttHelper mqttHelper;
    private String AudioFeedBackId;
    private String FeedBackStreemString;
    private Handler seekHandler;
    private SeekBar AudioSeekBar;
    int streemDuration = 1000;
    private TextView seekBarHint;
    private int seekBarPosition = 1;
    private Runnable runn;
    private int AudiodurationAbsTimeSeconds;
    private Handler hndlr;
    private TextView startTime, songTime;
    private static int oTime = 0, sTime = 0, eTime = 0, fTime = 5000, bTime = 5000;
    private Handler hdlr = new Handler();
    private Button buttonplay;
    private Button buttonPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_summary);
        set1 = new LineDataSet(paceValues, "DataSet 1");

        // initializing the retrofit service
        mAPIService = ApiUtils.getAPIService();

        // initializing views
        initViews();

        // initializing google map views
        initMapViwes();
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(AthletePastRunActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
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

    public void initViews() {

        // using getIntent method receiving data from previous activity
        Intent intent = getIntent();
        AthleteRunActivityId = intent.getStringExtra("AthleteRunActivityId");
        String CoachName = intent.getStringExtra("CoachName");
        String CoachPhone = intent.getStringExtra("CoachPhone");
        String CoachPhotoUrl = intent.getStringExtra("CoachPhotoUrl");
        String date = intent.getStringExtra("date");
        try {
            getAthletePastRunInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // initializing audio list view
        recyclerview_audio = findViewById(R.id.recyclerview_audio);

        paceLineChart = findViewById(R.id.chart1);
        paceLineChart.setTouchEnabled(true);
        IMarker marker = new NewMarkerView(this, R.layout.new_marker_layout);
        paceLineChart.setMarker(marker);
        paceLineChart.setOnChartValueSelectedListener(this);

        hrmLineChart = findViewById(R.id.chart2);
        hrmLineChart.setTouchEnabled(true);
        hrmLineChart.setMarker(marker);
        hrmLineChart.setOnChartValueSelectedListener(this);
        hrmLineChart.setDrawGridBackground(false);

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");

        // parsing data data variable to dd MMM yyyy formate
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = null;
        try {
            currentDate = sd.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        seekbar = findViewById(R.id.seekBar);


        //    Set seekbar to max 6 lenth ( position )
        seekbar.setMax(6);

        // set seekbar progress to 3 length (position)
        seekbar.setProgress(3);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position,
                                          boolean fromUser) {
                if (position == 0) {
                    int position1 = 1;
                    TIME_INTERVEL = 1000 * position1;
                } else {
                    TIME_INTERVEL = 1000 * position;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mtv_heartbeat = findViewById(R.id.tv_heartbeat);
        mtv_durationtime = findViewById(R.id.tv_durationtime);
        mtv_pace = findViewById(R.id.tv_pace);
        mtv_distance = findViewById(R.id.tv_distance);
        ImageView IvCoach = findViewById(R.id.IvCoach);

        // displaying coach image using picaso lib
        Picasso.with(AthletePastRunActivity.this)
                .load(GlobalConstants.getPhotoPathCoach() + CoachPhotoUrl)
                .placeholder(R.drawable.profile_image_coach)
                .into(IvCoach);

        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat outFormat1 = new SimpleDateFormat("dd MMM yyyy");
        String DayName = outFormat.format(currentDate);
        String DateName = outFormat1.format(currentDate);
        TextView tv_date = findViewById(R.id.tv_date);
        tv_date.setText(DateName);
        TextView tv_duration_lable = findViewById(R.id.tv_duration_lable);
        tv_duration_lable.setText(DayName + " Long Run");

        TextView tv_coach_name = findViewById(R.id.tv_coach_name);
        TextView tv_coach_mobile = findViewById(R.id.tv_coach_mobile);
        tv_coach_name.setText(CoachName);
        tv_coach_mobile.setText(CoachPhone);
        ImageView ivBack = findViewById(R.id.IvBack);


        // back press button
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // activirty transition from left to right
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                onBackPressed();
            }
        });

        iv_play = findViewById(R.id.iv_play);
        iv_play_inactive = findViewById(R.id.iv_play_inactive);

        iv_pause = findViewById(R.id.iv_pause);
        iv_pause_active = findViewById(R.id.iv_pause_active);

        ImageView iv_backward = findViewById(R.id.iv_backward);
        ImageView iv_forward = findViewById(R.id.iv_forward);


        // past run forward buttion
        iv_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fClick) {
                    fClick = false;
                    bClick = true;
                    if (playIsClick) {
                        fowrdIsClick = true;

                        if (bowrdIsClick) {
                            if (handlerB != null) {
                                handlerB.removeCallbacks(runnableB);
                            }
                        }
                        if (runnableOne != null) {
                            handlerOne.removeCallbacks(runnableOne);
                        }
                        if (RowCount > 0) {

                            handlerF = new Handler();

                            runnableF = new Runnable() {
                                @Override
                                public void run() {
                                    RowCount++;
                                    if (RowCount == mAthActivitySummarlist.size()) {
                                        ShowAlertDialog();
                                    }
                                    if (RowCount < mAthActivitySummarlist.size()) {
                                        String Distance = String.valueOf(mAthActivitySummarlist.get(RowCount).getDistance());
                                        mtv_distance.setText(Distance);
                                        mtv_durationtime.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getDuration()));
                                        mtv_heartbeat.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getHeartRate()));
                                        mtv_pace.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getPace()));
                                        double lat = mAthActivitySummarlist.get(RowCount).getLatitude();
                                        double longt = mAthActivitySummarlist.get(RowCount).getLongitude();
                                        LatLng latLng = new LatLng(lat, longt);

                                        //  show map with line
                                        addToLine(latLng);
                                        Log.i("==PlayCount--One", RowCount + " ::: " + TIME_INTERVEL);


                                    }
                                    handlerF.postDelayed(this, TIME_INTERVEL);
                                }
                            };
                            handlerF.postDelayed(runnableF, TIME_INTERVEL);
                        }
                    }
                }
            }
        });

        // past run backward button
        iv_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bClick) {
                    bClick = false;
                    fClick = true;
                    if (playIsClick) {
                        bowrdIsClick = true;
                        if (fowrdIsClick) {
                            if (handlerF != null) {
                                handlerF.removeCallbacks(runnableF);
                            }
                        }

                        if (runnableOne != null) {
                            handlerOne.removeCallbacks(runnableOne);
                        }
                        if (RowCount > 0) {
                            handlerB = new Handler();

                            runnableB = new Runnable() {
                                @Override
                                public void run() {
                                    if (RowCount > 0) {
                                        RowCount--;

                                        if (RowCount < mAthActivitySummarlist.size()) {
                                            String Distance = String.valueOf(mAthActivitySummarlist.get(RowCount).getDistance());
                                            mtv_distance.setText(Distance);
                                            mtv_durationtime.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getDuration()));
                                            mtv_heartbeat.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getHeartRate()));
                                            mtv_pace.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getPace()));
                                            double lat = mAthActivitySummarlist.get(RowCount).getLatitude();
                                            double longt = mAthActivitySummarlist.get(RowCount).getLongitude();
                                            LatLng latLng = new LatLng(lat, longt);

                                            //  show map with line
                                            addToLine(latLng);
                                            Log.i("==PlayCount--One", RowCount + " ::: " + TIME_INTERVEL);


                                        }
                                    } else {
                                        handlerB.removeCallbacks(runnableB);
                                    }
                                    handlerB.postDelayed(this, TIME_INTERVEL);
                                }
                            };
                            handlerB.postDelayed(runnableB, TIME_INTERVEL);
                        }
                    }
                }
            }
        });


        //  past run play button
        iv_play_inactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_play_inactive.setVisibility(View.GONE);
                iv_pause.setVisibility(View.VISIBLE);
                playIsClick = true;
                fClick = true;
                bClick = true;

                if (runnableB != null) {
                    handlerB.removeCallbacks(runnableB);
                }
                if (runnableF != null) {
                    handlerF.removeCallbacks(runnableB);
                }

                handlerOne = new Handler();

                runnableOne = new Runnable() {
                    @Override
                    public void run() {
                        RowCount++;
                        if (RowCount == mAthActivitySummarlist.size()) {
                            ShowAlertDialog();
                        }
                        if (RowCount < mAthActivitySummarlist.size()) {
                            String Distance = String.valueOf(mAthActivitySummarlist.get(RowCount).getDistance());
                            mtv_distance.setText(Distance);
                            mtv_durationtime.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getDuration()));
                            mtv_heartbeat.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getHeartRate()));
                            mtv_pace.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getPace()));
                            double lat = mAthActivitySummarlist.get(RowCount).getLatitude();
                            double longt = mAthActivitySummarlist.get(RowCount).getLongitude();
                            LatLng latLng = new LatLng(lat, longt);

                            //  show map with line
                            addToLine(latLng);
                            Log.i("==PlayCount--One", RowCount + " ::: " + TIME_INTERVEL);


                        }
                        handlerOne.postDelayed(this, TIME_INTERVEL);
                    }
                };
                handlerOne.postDelayed(runnableOne, TIME_INTERVEL);
            }
        });

        // fetch api data from DB clicing on this button
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playIsClick = true;
                iv_play.setVisibility(View.GONE);
                iv_pause.setVisibility(View.VISIBLE);
                try {
                    getAthleteActivitySummary();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        // pause button
        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playIsClick = false;
                iv_play_inactive.setVisibility(View.VISIBLE);
                iv_pause.setVisibility(View.GONE);
                if (runnableOne != null) {
                    handlerOne.removeCallbacks(runnableOne);
                }
                if (runnableB != null) {
                    handlerB.removeCallbacks(runnableB);
                }
                if (runnableF != null) {
                    handlerF.removeCallbacks(runnableF);
                }

            }
        });

        llRunMap = findViewById(R.id.llRunMap);
        llHrmGraph = findViewById(R.id.llHrmGraph);
        llPaceGraph = findViewById(R.id.llPaceGraph);

        llMapFragment = findViewById(R.id.llMapFragment);
        llPaceGraphChart = findViewById(R.id.llPaceGraphChart);
        llHrmGraphChart = findViewById(R.id.llHrmGraphChart);

        paceLable = findViewById(R.id.paceLable);
        runLable = findViewById(R.id.runLable);
        hrmLable = findViewById(R.id.hrmLable);


    }

    private void setupChart(LineChart chart, LineData data, int color) {

        ((LineDataSet) data.getDataSetByIndex(0)).setCircleHoleColor(color);

        // no description text
        chart.getDescription().setEnabled(false);

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10, 0, 10, 0);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setEnabled(false);

        // animate calls invalidate()...
        chart.animateX(2500);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        float x = e.getX();
        float y = e.getY();
//        Toast.makeText(this,""+x+","+y,Toast.LENGTH_LONG).show();
        paceLineChart.highlightValue(h);
    }

    @Override
    public void onNothingSelected() {

    }

    private float getTimeInMilliseconds(String time) {
        //Specifying the pattern of input date and time
        float timeinMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        try {
            //formatting the dateString to convert it into a Date
            Date date = sdf.parse(time);
            timeinMilliseconds = date.getTime();
            System.out.println("Given Time in milliseconds : " + date.getTime());

//            Calendar calendar = Calendar.getInstance();
//            //Setting the Calendar date and time to the given date and time
//            calendar.setTime(date);
//            System.out.println("Given Time in milliseconds : "+calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeinMilliseconds;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleMap = null;
        if (runnableF != null) {
            handlerF.removeCallbacks(runnableF);
        }
        if (runnableB != null) {
            handlerB.removeCallbacks(runnableB);
        }
        if (runnableOne != null) {
            handlerOne.removeCallbacks(runnableOne);
        }

    }

    // fetch data from DB using retrifit lib
    public void getAthleteActivitySummary() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();
        Toast.makeText(this, AthleteRunActivityId, Toast.LENGTH_SHORT).show();
        Call<AthleteActivitySummaryModel> call = mAPIService.getAthleteRunActivity(AthleteRunActivityId);
        call.enqueue(new Callback<AthleteActivitySummaryModel>() {
            @Override
            public void onResponse(Call<AthleteActivitySummaryModel> call, Response<AthleteActivitySummaryModel> response) {
                if (response.body() != null) {
                    // pace graph

                    if (response.body().getCode().equals("201")) {
                        Log.i("AthPastRun", "No Data Found");
                    } else {
                        ArrayList<Entry> values = new ArrayList<>();
                        Log.i("llog",response.body().getCode());
                        if (response.body().getContent() != null) {
                            for (int i = 0; i < response.body().getContent().size(); i++) {
                                float duration = getTimeInMilliseconds(response.body().getContent().get(i).getAbsTime());
//                        float duration = i;
                                float pace = 0;
                                if (response.body().getContent().get(i).getPace() != null) {

                                    String myPace = response.body().getContent().get(i).getPace();
                                    myPace.indexOf(':');
                                    int myPosition = myPace.indexOf(':');
                                    String myPaceSub = myPace.substring(0,myPosition);
                                    pace = Float.parseFloat(myPaceSub);
                                }
                                values.add(new Entry(duration, pace));
                            }
                        }
                        // create a dataset and give it a type
                        LineDataSet set1 = new LineDataSet(values, "DataSet 1");


                        set1.setLineWidth(1.75f);
                        set1.setCircleRadius(5f);
                        set1.setCircleHoleRadius(2.5f);
                        set1.setColor(Color.WHITE);
                        set1.setCircleColor(Color.WHITE);
                        set1.setHighLightColor(Color.WHITE);
                        set1.setDrawValues(false);

                        LineData data = new LineData(set1);
                        data.setValueTypeface(mTf);

                        setupChart(paceLineChart, data, colors[0]);

                        // hrm graph
                        ArrayList<Entry> values1 = new ArrayList<>();

                        for (int i = 0; i < response.body().getContent().size(); i++) {
                            float duration = getTimeInMilliseconds(response.body().getContent().get(i).getAbsTime());
//                        float duration = i;
                            float pace = response.body().getContent().get(i).getHeartRate().floatValue();
                            values1.add(new Entry(duration, pace));
                        }

                        // create a dataset and give it a type
                        LineDataSet set11 = new LineDataSet(values, "DataSet 1");
                        // set1.setFillAlpha(110);
                        // set1.setFillColor(Color.RED);

                        set11.setLineWidth(1.75f);
                        set11.setCircleRadius(5f);
                        set11.setCircleHoleRadius(2.5f);
                        set11.setColor(Color.WHITE);
                        set11.setCircleColor(Color.WHITE);
                        set11.setHighLightColor(Color.WHITE);
                        set11.setDrawValues(false);

                        LineData data1 = new LineData(set11);
                        data1.setValueTypeface(mTf);

                        setupChart(hrmLineChart, data1, colors[3]);


                        if (mAthActivitySummarlist != null) {
                            RowCount = 0;
                            mAthActivitySummarlist = response.body().getContent();

                            mAthActivityAudiolist = response.body().getFeedBack();
                            Log.i("===ResNew", "Audio FeedBak : " + response.body().getFeedBack());
                            AthletePastRunAudioAdpater audioAdapter = new AthletePastRunAudioAdpater(mAthActivityAudiolist, getApplicationContext());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
                            recyclerview_audio.setLayoutManager(mLayoutManager);
                            recyclerview_audio.setItemAnimator(new DefaultItemAnimator());
                            recyclerview_audio.setAdapter(audioAdapter);

                            handlerOne = new Handler();
                            runnableOne = new Runnable() {
                                @Override
                                public void run() {
                                    RowCount++;
                                    if (RowCount == mAthActivitySummarlist.size()) {
                                        ShowAlertDialog();
                                    }

                                    if (RowCount < mAthActivitySummarlist.size()) {
                                        String Distance = String.valueOf(mAthActivitySummarlist.get(RowCount).getDistance());
                                        mtv_distance.setText(Distance);
                                        mtv_durationtime.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getDuration()));
                                        mtv_heartbeat.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getHeartRate()));
                                        mtv_pace.setText(String.valueOf(mAthActivitySummarlist.get(RowCount).getPace()));
                                        double lat = mAthActivitySummarlist.get(RowCount).getLatitude();
                                        double longt = mAthActivitySummarlist.get(RowCount).getLongitude();
                                        LatLng latLng = new LatLng(lat, longt);

                                        //  show map with line
                                        addToLine(latLng);
                                        Log.i("==PlayCount--One", RowCount + " ::: " + TIME_INTERVEL);


                                    }

                                    handlerOne.postDelayed(this, TIME_INTERVEL);
                                }
                            };
                            handlerOne.postDelayed(runnableOne, TIME_INTERVEL);
                        }
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthleteActivitySummaryModel> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i("CancelReq", "request was cancelled");
                } else {
                    Log.i("CancelReq", "other larger issue, i.e. no network connection?");
                }
                Log.i("===ErrorResponse", t.getMessage());
                dialog.dismiss();
            }
        });

    }

    public void getAthletePastRunInit() throws IOException {

        Call<AthleteActivitySummaryModel> call = mAPIService.getAthleteRunActivity(AthleteRunActivityId);

        call.enqueue(new Callback<AthleteActivitySummaryModel>() {
            @Override
            public void onResponse(Call<AthleteActivitySummaryModel> call, Response<AthleteActivitySummaryModel> response) {
                if (response.body() != null) {

                    mAthActivityAudiolist = response.body().getFeedBack();
                    Log.i("===ResNew", "Audio FeedBak : " + response.body().getFeedBack());
                    AthletePastRunAudioAdpater audioAdapter = new AthletePastRunAudioAdpater(mAthActivityAudiolist, getApplicationContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
                    recyclerview_audio.setLayoutManager(mLayoutManager);
                    recyclerview_audio.setItemAnimator(new DefaultItemAnimator());
                    recyclerview_audio.setAdapter(audioAdapter);

                }
            }

            @Override
            public void onFailure(Call<AthleteActivitySummaryModel> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i("CancelReq", "request was cancelled");
                } else {
                    Log.i("CancelReq", "other larger issue, i.e. no network connection?");
                }
                Log.i("===ErrorResponse", t.getMessage());
            }
        });

    }


    // Show dialog after play complete
    private void ShowAlertDialog() {
        final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#6b379f'>Alert!</font>"));
        alertDialogBuilder.setMessage(Html.fromHtml("<font color='#6b379f'>Run Completed.!</font>"));

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });

        alertDialogBuilder.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        //alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void initMapViwes() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GlobalConstants.getPastRunLatitude(), GlobalConstants.getPastRunLongitude()), 2.5f));

    }


    // converting navigation icon to bitmap
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_navigation_black_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // drawing the lines using latitude longitude
    private void addToLine(LatLng pt) {
        polyLineList.add(pt);

        // moving map camera postion to particular point
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(String.valueOf(pt.latitude), String.valueOf(pt.longitude))));

        polyLineList.add(pt);
        Log.i("===ERT", String.valueOf(polyLineList));
        if (greyPolyLine != null) {
            greyPolyLine.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);
        greyPolyLine.setEndCap(new CustomCap(bitmapDescriptorFromVector(this, R.drawable.ic_navigation_black_24dp), 10));
    }


    // moving camera position
    private CameraPosition getCameraPositionWithBearing(String latitude, String longitude) {
        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(latitude)), Double.parseDouble(String.valueOf(longitude)));
        return new CameraPosition.Builder().target(latLng).zoom(15).build();
    }


    public void PaceClick(View view) {
        llRunMap.setVisibility(View.GONE);
        llHrmGraph.setVisibility(View.GONE);
        llPaceGraph.setVisibility(View.VISIBLE);

        llPaceGraphChart.setVisibility(View.VISIBLE);
        llMapFragment.setVisibility(View.GONE);
        llHrmGraphChart.setVisibility(View.GONE);
    }

    public void RunClick(View view) {
        llRunMap.setVisibility(View.VISIBLE);
        llHrmGraph.setVisibility(View.GONE);
        llPaceGraph.setVisibility(View.GONE);

        llPaceGraphChart.setVisibility(View.GONE);
        llMapFragment.setVisibility(View.VISIBLE);
        llHrmGraphChart.setVisibility(View.GONE);
    }

    public void HrmClick(View view) {
        llRunMap.setVisibility(View.GONE);
        llHrmGraph.setVisibility(View.VISIBLE);
        llPaceGraph.setVisibility(View.GONE);

        llPaceGraphChart.setVisibility(View.GONE);
        llMapFragment.setVisibility(View.GONE);
        llHrmGraphChart.setVisibility(View.VISIBLE);
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("request", "onRequestPermissionResult");


        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Toast.makeText(AthletePastRunActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AthletePastRunActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getPlayAudioClipId(int position) {
        AudioFeedBackId = mAthActivityAudiolist.get(position).getId();
        Log.i("===AudioStreemId", mAthActivityAudiolist.get(position).getId());

        try {
            getFeedBackAudioById();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("===AErrCatch", " msg : " + e.getMessage());
        }

    }

    // fetch Audio feed back data passing feedback id
    public void getFeedBackAudioById() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();
        Call<PastRunAudioFeedBackModel> call = mAPIService.getFeedBackAudioById(AudioFeedBackId);

        call.enqueue(new Callback<PastRunAudioFeedBackModel>() {
            @Override
            public void onResponse(Call<PastRunAudioFeedBackModel> call, Response<PastRunAudioFeedBackModel> response) {
                if (response.body() != null) {
                    Log.i("===AudioStreemMsg", String.valueOf(response.body().getMeassage()));

                    FeedBackStreemString = response.body().getContent().getFeedBackStream();
                    String time = response.body().getContent().getStreDuration(); //mm:ss
                    String[] units = time.split(":"); //will break the string up into an array
                    int minutes = Integer.parseInt(units[0]); //first element
                    int seconds = Integer.parseInt(units[1]); //second element
                    AudiodurationAbsTimeSeconds = 60 * minutes + seconds; //add up our values
                    // Log.i("==dssds",String.valueOf(AudiodurationAbsTimeSeconds) + " : " + time);

                    showCustomDialogPlayAudio();
                } else {
                    Log.i("===AerrorFeed", "error audio feed back");
                }
            }

            @Override
            public void onFailure(Call<PastRunAudioFeedBackModel> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i("===ACancelReq1", "request was cancelled");
                } else {
                    Log.i("===ACancelReq2", "other larger issue, i.e. no network connection?");
                }
                Log.i("===AerrorResponse", t.getMessage());
                dialog.dismiss();
            }
        });

    }

    private void showCustomDialogPlayAudio() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.pastrun_play_audio_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        buttonplay = dialogView.findViewById(R.id.buttonplay);
        buttonPause = dialogView.findViewById(R.id.buttonPause);
        seekBarHint = dialogView.findViewById(R.id.textView);
        AudioSeekBar = dialogView.findViewById(R.id.seekBar);
        TextView tv_cancel = dialogView.findViewById(R.id.tv_cancel);

        startTime = dialogView.findViewById(R.id.txtStartTime);
        songTime = dialogView.findViewById(R.id.txtSongTime);

        //String phoneNumber = SharedPref.read(Share)
        buttonplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConvertStringToFile(FeedBackStreemString);
                buttonplay.setVisibility(View.GONE);
                buttonPause.setVisibility(View.VISIBLE);
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.pause();

                buttonplay.setVisibility(View.VISIBLE);
                buttonPause.setVisibility(View.GONE);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                alertDialog.dismiss();
            }
        });


        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void ConvertStringToFile(String AudioMsg) {
        Log.i("===Aaudio", AudioMsg);
        byte[] decoded = Base64.decode(AudioMsg, 0);
        Log.i("===ADecodedAudio: ", String.valueOf(decoded));
        if (checkPermission()) {
            try {
                Random random = new Random();
                int ii = 100000;
                ii = random.nextInt(ii);
                file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ii + "audio1.3gp");
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

                mediaPlayer.start();
                AudioSeekBar.setMax(mediaPlayer.getDuration());
                eTime = mediaPlayer.getDuration();
                sTime = mediaPlayer.getCurrentPosition();
                if (oTime == 0) {
                    AudioSeekBar.setMax(eTime);
                    oTime = 1;
                }
                songTime.setText(String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(eTime),
                        TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));

                startTime.setText(String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(sTime),
                        TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));

                AudioSeekBar.setProgress(sTime);
                hdlr.postDelayed(UpdateSongTime, 100);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            requestPermission();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            sTime = mediaPlayer.getCurrentPosition();
            startTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));

            AudioSeekBar.setProgress(sTime);

            hdlr.postDelayed(this, 100);

            if (String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(eTime),
                    TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))).equals(String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))))) {
                buttonplay.setVisibility(View.VISIBLE);
                buttonPause.setVisibility(View.GONE);
            }
        }
    };

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
        ActivityCompat.requestPermissions(AthletePastRunActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


}
