package com.mita.mqtt.athlete.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.activity.AthletePastRunActivity;
import com.mita.mqtt.athlete.model.AthleteActivitySummaryContentModel;
import com.mita.mqtt.athlete.model.AthleteActivitySummaryModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;


public class RunFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private List<LatLng> polyLineList = new ArrayList<>();
    private Polyline greyPolyLine;
    private String latitude;
    private String longitude;
    private APIService mAPIService;
    private String AthleteRunActivityId;
    private List<AthleteActivitySummaryContentModel> mAthActivitySummarlist;
    private int RowCount = 0;
    private Handler handlerOne;
    private Runnable runnableOne;
    private int TIME_INTERVEL = 3000;
    private Double lat ,longt;

    public RunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_run, container, false);
        mAPIService = ApiUtils.getAPIService();
        mMapView = (MapView) v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(RunFragment.this); //this is important

        return v;

    }

    public void getAthleteActivitySummary() throws IOException {

        //   dialog.show();
        Call<AthleteActivitySummaryModel> call = mAPIService.getAthleteRunActivity(AthleteRunActivityId);

        call.enqueue(new Callback<AthleteActivitySummaryModel>() {
            @Override
            public void onResponse(Call<AthleteActivitySummaryModel> call, Response<AthleteActivitySummaryModel> response) {
                if (response.body() != null) {
                    if (mAthActivitySummarlist != null) {
                        // Log.i("===ResContentSize", String.valueOf(response.body().getContent().size()));
                        mAthActivitySummarlist = response.body().getContent();
                        //seekbar.setMax(response.body().getContent().size());
                        handlerOne = new Handler();
                        runnableOne = new Runnable() {
                            @Override
                            public void run() {
                                RowCount++;
                                // seekbar.setProgress((int) RowCount);

                                if (RowCount < mAthActivitySummarlist.size()) {

                                     lat = mAthActivitySummarlist.get(RowCount).getLatitude();
                                     longt = mAthActivitySummarlist.get(RowCount).getLongitude();
                                    LatLng latLng = new LatLng(lat, longt);

                                    addToLine(latLng);
                                    Log.i("==FrgPlyCnt-O", RowCount + " ::: " + TIME_INTERVEL);
                                }

                                handlerOne.postDelayed(this, TIME_INTERVEL);
                            }
                        };
                        handlerOne.postDelayed(runnableOne, TIME_INTERVEL);
                    }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(13.076785, 77.564156)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.076785, 77.564156), 15));
        //if (latitude != null && longitude != null) {
//            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longt)));
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longt), 15));
      //  }
    }

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

    private void addToLine(LatLng pt) {
        polyLineList.add(pt);
//        Log.i("===ERT", String.valueOf(polyLineList));
        if (greyPolyLine != null) {
            greyPolyLine.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(25);
//        polylineOptions.endCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_black_24dp), 16));
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = mGoogleMap.addPolyline(polylineOptions);
//        greyPolyLine.setEndCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_black_24dp), 16));
        greyPolyLine.setEndCap(new CustomCap(bitmapDescriptorFromVector(getContext(), R.drawable.ic_navigation_black_24dp), 16));
//        greyPolyLine.setEndCap(new CustomCap(bitmapDescriptorFromVector(this, R.drawable.ic_navigation_black_24dp)), 16););
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}