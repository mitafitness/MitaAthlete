/*
 * Copyright 2013-2017 Amazon.com,
 * Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the
 * License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, express or implied. See the License
 * for the specific language governing permissions and
 * limitations under the License.
 */

package com.mita.athlete.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.navigation.NavigationView;
import com.mita.mqtt.athlete.activity.AthleteProfileUpdateActivity;
import com.mita.mqtt.athlete.activity.AthletePastRunActivity;
import com.mita.mqtt.athlete.activity.CurrnetActivityDevNew;
import com.mita.mqtt.athlete.activity.PlansActivity;
import com.mita.mqtt.athlete.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;

import com.mita.mqtt.athlete.model.AthleteDetailsResponseModel;
import com.mita.mqtt.athlete.model.AthleteTodaysRunResponseModel;
import com.mita.mqtt.athlete.model.AthleteUpcomingPlanAllModel;
import com.mita.mqtt.athlete.model.AthleteUpcomingPlanResponseModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.GlobalConstants;
import com.mita.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    private final String TAG = "UserActivity";

    private NavigationView nDrawer;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    // Cognito user objects
    private CognitoUser user;

    // User details
    private String username;


    private TextView navHeaderSubTitle;
    private TextView tv_nav_email;
    // dashboard changes
    private RecyclerView mRecyclerView;
    List<AthleteUpcomingPlanAllModel> mRunDetailsFinishedList = new ArrayList<>();
    List<AthleteUpcomingPlanAllModel> mRunDetailsUpcomingList = new ArrayList<>();
    private DashboardDetailsFinishedAdapter mAdapterFinished;
    private DashboardDetailsUpcommingAdapter mAdapterUpcoming;
    private APIService mAPIService;

    LinearLayout LLFinishedAcive, LLUpcommingInActive, LLFinishedInAcive, LLUpcommingActive;
    private String AthleteRunActivityId;
    private String AthletePlanGoal;
    private String AthleteRunActivityIdValidate;
    private int RunCount;
    private String coachId;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private int finishTabFlag = 1;
    private String TodaysRunCode;
    private ImageView iv_profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAPIService = ApiUtils.getAPIService();
        SharedPref.init(this);
        // Set toolbar for this screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set navigation drawer for this screen
        mDrawer = (DrawerLayout) findViewById(R.id.user_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        nDrawer = (NavigationView) findViewById(R.id.nav_view);
        setNavDrawer();

        // initializing the views from layout
        init();
        View navigationHeader = nDrawer.getHeaderView(0);

        navHeaderSubTitle = (TextView) navigationHeader.findViewById(R.id.textViewNavUserSub);
        tv_nav_email = (TextView) navigationHeader.findViewById(R.id.tv_nav_email);
        tv_nav_email.setText(SharedPref.read(SharedPref.EMAIL, ""));
        navHeaderSubTitle.setText(SharedPref.read(SharedPref.NAME, ""));

        iv_profile = (ImageView) navigationHeader.findViewById(R.id.iv_profile);

        ImageView iv_edit_profile = (ImageView) navigationHeader.findViewById(R.id.iv_edit_profile);
        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, AthleteProfileUpdateActivity.class);
                startActivity(intent);
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_user_menu, menu);
        return true;
    }


    @Override
    public void onBackPressed() {

        final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#6b379f'>Alert!</font>"));
        alertDialogBuilder.setMessage(Html.fromHtml("<font color='#6b379f'>Do You Want To Exit From App.?</font>"));

        alertDialogBuilder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        signOut();
                        finish();

                    }
                });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        //alertDialog.setCancelable(false);
        alertDialog.show();
    }


    // Handle when the a navigation item is selected
    private void setNavDrawer() {
        nDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                performAction(item);
                return true;
            }
        });
    }

    // Perform the action for the selected navigation item
    private void performAction(MenuItem item) {
        // Close the navigation drawer
        mDrawer.closeDrawers();

        // Find which item was selected
        switch (item.getItemId()) {
            case R.id.nav_athlete_run:
              /*  Intent intent = new Intent(UserActivity.this, Coaches_list.class);
                startActivity(intent);*/
                break;
            case R.id.dashboard:
                // dashboard
               /* Intent intent1 = new Intent(UserActivity.this, CurrnetActivity.class);
                startActivity(intent1);*/
                break;
            case R.id.nav_athlete_Plan:

                // plans
                String PlanExp = SharedPref.read(SharedPref.PlanExp, "");
                if (PlanExp.equals("true")) {
                    showCustomDialog();
                } else {
                    AlertDialog();
                }

                // plan is already active,!
                break;
            case R.id.nav_athlete_Sensors:
                // Sensors
               /* Intent in = new Intent(UserActivity.this, AthleteSummaryActivity.class);
                startActivity(in);*/
                break;
            case R.id.nav_user_sign_out:
                // Sign out from this account
                signOut();
                break;

        }
    }

    public void AlertDialog() {

        final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#6b379f'>Alert!</font>"));
        alertDialogBuilder.setMessage(Html.fromHtml("<font color='#6b379f'>Plan is already active.!</font>"));

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


    @Override
    public void onResume() {
        super.onResume();
        if (finishTabFlag == 1) {
            LLFinishedAcive.setVisibility(View.VISIBLE);
            LLUpcommingActive.setVisibility(View.GONE);
            LLFinishedInAcive.setVisibility(View.GONE);
            LLUpcommingInActive.setVisibility(View.VISIBLE);

        }
        getFinishedRunForAthelet();
        getTodaysRunForAthelet();
        try {
            getAthleteDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get Finished Acthlete Activity Details
    private void getFinishedRunForAthelet() {

        mRecyclerView = findViewById(R.id.dashboard_details_recycler_view);
        if (mRunDetailsFinishedList != null) {
            if (mRunDetailsFinishedList.size() > 0) {
                mRunDetailsFinishedList.clear();
            }
        }


        final ProgressDialog dialog;
        dialog = new ProgressDialog(UserActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();
        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        Call<AthleteUpcomingPlanResponseModel> call = mAPIService.getFinishedRunForAthelet(AthleteId);
        call.enqueue(new Callback<AthleteUpcomingPlanResponseModel>() {
            @Override
            public void onResponse(Call<AthleteUpcomingPlanResponseModel> call, Response<AthleteUpcomingPlanResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===ResFinish", String.valueOf(response.body().getCode()));
                    if (response.body().getCode().equals("201")) {
                        Log.i("AthFinishlist", "No Data Found");
                    } else {
                        mRunDetailsFinishedList = response.body().getContent();
                        mAdapterFinished = new DashboardDetailsFinishedAdapter(mRunDetailsFinishedList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mAdapterFinished);

                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthleteUpcomingPlanResponseModel> call, Throwable t) {
                Log.i("===ErrorResFinish", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


    }


    // get Acthlete Todays Run Activity Details
    private void getTodaysRunForAthelet() {

        final ProgressDialog dialog;
        dialog = new ProgressDialog(UserActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();
        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        Call<AthleteTodaysRunResponseModel> call = mAPIService.getTodaysRunForAthelet(AthleteId, "");
        call.enqueue(new Callback<AthleteTodaysRunResponseModel>() {

            @Override
            public void onResponse(Call<AthleteTodaysRunResponseModel> call, Response<AthleteTodaysRunResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===ResTodaysRunCode", String.valueOf(response.body().getCode()));
                    TextView tv_NoTodaysRun = findViewById(R.id.tv_NoTodaysRun);
                    LinearLayout LLTodaysRun = findViewById(R.id.LLTodaysRun);
                    TextView tv_athlete_name = findViewById(R.id.tv_athlete_name);
                    TextView tv_date = findViewById(R.id.tv_date);
                    TextView tv_distance_goal = findViewById(R.id.tv_distance_goal);
                    TextView tv_duration = findViewById(R.id.tv_duration);
                    TextView tv_description = findViewById(R.id.tv_description);
                    TextView tv_pace = findViewById(R.id.tv_pace);
                    TextView tv_coach_name = findViewById(R.id.tv_coach_name);
                    TextView tv_plan_name = findViewById(R.id.tv_plan_name);
                    tv_athlete_name.setText("Hi " + SharedPref.read(SharedPref.NAME, ""));
                    TodaysRunCode = response.body().getCode();
                    if (response.body().getCode().equals("201")) {
                        LLTodaysRun.setVisibility(View.GONE);
                        tv_NoTodaysRun.setVisibility(View.VISIBLE);

                    } else {
                        LLTodaysRun.setVisibility(View.VISIBLE);
                        tv_NoTodaysRun.setVisibility(View.GONE);

                        tv_distance_goal.setText(response.body().getContent().getPlanGoal() + "KMs");
                        tv_duration.setText(response.body().getContent().getDuration() + " MIN");
                        tv_description.setText(response.body().getContent().getDescription());
                        tv_pace.setText(response.body().getContent().getPace());
                        tv_coach_name.setText("Coach : " + response.body().getContent().getCoachName());
                        tv_plan_name.setText("Plan : " + response.body().getContent().getMetaPlanName());

                        AthleteRunActivityId = response.body().getContent().getAthelRunPlanActId();
                        RunCount = response.body().getContent().getRunCount();
                        AthletePlanGoal = response.body().getContent().getPlanGoal();
                        coachId = response.body().getContent().getCoachId();
                        //    Toast.makeText(UserActivity.this, AthleteRunActivityId, Toast.LENGTH_SHORT).show();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String CurrDate = sdf1.format(new Date());
                        Date date1 = null;

                        try {
                            date1 = sdf1.parse(CurrDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat outFormat = new SimpleDateFormat("E, dd MMM yyyy");
                        String DayName = outFormat.format(date1);
                        tv_date.setText(DayName);

                        Log.i("==AthleteRunActivityId", AthleteRunActivityId);
                        Log.i("==RunCount", String.valueOf(RunCount));

                        if(SharedPref.read(SharedPref.RUN_MODE,"").equals("true")){
                            Intent intent = new Intent(getApplicationContext(), CurrnetActivityDevNew.class);
                            intent.putExtra("AthleteRunActivityId", AthleteRunActivityId);
                            intent.putExtra("RunCount", String.valueOf(RunCount));
                            intent.putExtra("AthletePlanGoal", AthletePlanGoal);
                            intent.putExtra("coachId", coachId);
                            startActivity(intent);


                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }

                        //  tv_date.setText(response.body().getContent().getDate());

                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthleteTodaysRunResponseModel> call, Throwable t) {
                Log.i("===ErrorResTodaysRun", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


    }


    // get Upcoming Acthlete Activity Details
    private void getUpcomingPlanOfAthForCoach() {

        mRecyclerView = findViewById(R.id.dashboard_details_recycler_view);
        if (mRunDetailsUpcomingList != null) {
            if (mRunDetailsUpcomingList.size() > 0) {
                mRunDetailsUpcomingList.clear();
            }
        }
        final ProgressDialog dialog;
        dialog = new ProgressDialog(UserActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();
        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        Call<AthleteUpcomingPlanResponseModel> call = mAPIService.getUpcomingRunForAthelet(AthleteId);
        call.enqueue(new Callback<AthleteUpcomingPlanResponseModel>() {
            @Override
            public void onResponse(Call<AthleteUpcomingPlanResponseModel> call, Response<AthleteUpcomingPlanResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===ResUpcoming", String.valueOf(response.body().getCode()));
                    if (response.body().getCode().equals("201")) {
                        Log.i("AthUpcominglist", "No Data Found");
                    } else {
                        mRunDetailsUpcomingList = response.body().getContent();
                        mAdapterUpcoming = new DashboardDetailsUpcommingAdapter(mRunDetailsUpcomingList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mAdapterUpcoming);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthleteUpcomingPlanResponseModel> call, Throwable t) {
                Log.i("===ErrorResUpcoming", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

    }

    // Sign out user
    private void signOut() {
        user.signOut();
        Intent trustedDevicesActivity = new Intent(this, MainActivity.class);
        startActivity(trustedDevicesActivity);
        exit();
    }

    // Initialize this activity
    private void init() {

        LLFinishedInAcive = findViewById(R.id.LLFinishedInAcive);
        LLFinishedAcive = findViewById(R.id.LLFinishedAcive);
        LLUpcommingInActive = findViewById(R.id.LLUpcommingInActive);
        LLUpcommingActive = findViewById(R.id.LLUpcommingActive);


        TextView tv_Activeupcoming = findViewById(R.id.tv_Activeupcoming);
        TextView tv_InActiveupcoming = findViewById(R.id.tv_InActiveupcoming);
        TextView tv_ActiveFinished = findViewById(R.id.tv_ActiveFinished);
        TextView tv_InActiveFinished = findViewById(R.id.tv_InActiveFinished);

        tv_InActiveupcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLUpcommingInActive.setVisibility(View.GONE);
                LLUpcommingActive.setVisibility(View.VISIBLE);
                LLFinishedInAcive.setVisibility(View.VISIBLE);
                LLFinishedAcive.setVisibility(View.GONE);
                getUpcomingPlanOfAthForCoach();
                mRunDetailsFinishedList.clear();
                if (mAdapterFinished != null) {
                    mAdapterFinished.notifyDataSetChanged();
                }
            }
        });

        tv_Activeupcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLUpcommingInActive.setVisibility(View.GONE);
                LLUpcommingActive.setVisibility(View.VISIBLE);
                LLFinishedInAcive.setVisibility(View.VISIBLE);
                LLFinishedAcive.setVisibility(View.GONE);
                getUpcomingPlanOfAthForCoach();
                mRunDetailsFinishedList.clear();
                if (mAdapterFinished != null) {
                    mAdapterFinished.notifyDataSetChanged();
                }
            }
        });

        tv_ActiveFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLUpcommingInActive.setVisibility(View.VISIBLE);
                LLUpcommingActive.setVisibility(View.GONE);
                LLFinishedInAcive.setVisibility(View.GONE);
                LLFinishedAcive.setVisibility(View.VISIBLE);
                getFinishedRunForAthelet();
                mRunDetailsUpcomingList.clear();
                if (mAdapterUpcoming != null) {
                    mAdapterUpcoming.notifyDataSetChanged();
                }
            }
        });

        tv_InActiveFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLUpcommingInActive.setVisibility(View.VISIBLE);
                LLUpcommingActive.setVisibility(View.GONE);
                LLFinishedInAcive.setVisibility(View.GONE);
                LLFinishedAcive.setVisibility(View.VISIBLE);
                getFinishedRunForAthelet();
                mRunDetailsUpcomingList.clear();
                if (mAdapterUpcoming != null) {
                    mAdapterUpcoming.notifyDataSetChanged();
                }
            }
        });


        // Get the user name
        Bundle extras = getIntent().getExtras();
//        username = AppHelper.getCurrUser();
//        Log.i("===UserName",username+" : "+SharedPref.read(SharedPref.PHONE,""));
        username = SharedPref.read(SharedPref.PHONE, "");

        user = AppHelper.getPool().getUser(username);


    }





    private void exit() {
        Intent intent = new Intent();
        if (username == null)
            username = "";
        intent.putExtra("name", username);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.select_plan_acitivity, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        Button BtnSelectPlan = dialogView.findViewById(R.id.BtnSelectPlan);
        //String phoneNumber = SharedPref.read(Share)
        BtnSelectPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, PlansActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    // OnClick of Finished list row Athlete Past Run Activity Will display
    public void AthleteFinishedRun(int position) {
        if (mRunDetailsFinishedList != null) {
            Intent intent = new Intent(getApplicationContext(), AthletePastRunActivity.class);
            Log.i("===actRunId", mRunDetailsFinishedList.get(position).getAthelRunPlanActId());
            intent.putExtra("AthleteRunActivityId", mRunDetailsFinishedList.get(position).getAthelRunPlanActId());
            // Toast.makeText(this, AthleteRunActivityId, Toast.LENGTH_SHORT).show();
            intent.putExtra("CoachName", mRunDetailsFinishedList.get(position).getCoachName());
            intent.putExtra("CoachPhone", mRunDetailsFinishedList.get(position).getCoachPhone());
            intent.putExtra("CoachProfilePhoto", mRunDetailsFinishedList.get(position).getCoachPhoto());
            intent.putExtra("date", mRunDetailsFinishedList.get(position).getDate());
            intent.putExtra("CoachPhotoUrl", mRunDetailsFinishedList.get(position).getCoachPhoto());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            Toast.makeText(this, mRunDetailsFinishedList.get(position).getAthelRunPlanActId(), Toast.LENGTH_SHORT).show();
        }
    }

    // onclick lets go
    public void ClickStartAthCurrentRun(View view) {

        Intent intent = new Intent(getApplicationContext(), CurrnetActivityDevNew.class);
        intent.putExtra("AthleteRunActivityId", AthleteRunActivityId);
        intent.putExtra("RunCount", String.valueOf(RunCount));
        intent.putExtra("AthletePlanGoal", AthletePlanGoal);
        intent.putExtra("coachId", coachId);
        startActivity(intent);
        Toast.makeText(this, AthleteRunActivityId, Toast.LENGTH_SHORT).show();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    // Floating action button
    public void FabRunAthlete(View view) {
        if (!TodaysRunCode.equals("201")) {
            Intent intent = new Intent(getApplicationContext(), CurrnetActivityDevNew.class);
            intent.putExtra("AthleteRunActivityId", AthleteRunActivityId);
            intent.putExtra("RunCount", String.valueOf(RunCount));
            intent.putExtra("AthletePlanGoal", AthletePlanGoal);
            intent.putExtra("coachId", coachId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    // fetching user details
    public void getAthleteDetails() throws IOException {


        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        Call<AthleteDetailsResponseModel> call = mAPIService.getAtheletById(AthleteId);     //getStringScalar(name, age, phone, email);

        call.enqueue(new Callback<AthleteDetailsResponseModel>() {
            @Override
            public void onResponse(Call<AthleteDetailsResponseModel> call, Response<AthleteDetailsResponseModel> response) {
                if (response.body() != null) {
                    String photoUrl = response.body().getContent().getPhotoUrl();
                    String ExpPlan = String.valueOf(response.body().getContent().getPlanExp());
                    SharedPref.write(SharedPref.PlanExp, ExpPlan);
                    /*
                     * If Athlete is new User creating window to select Plan
                     * checking planExp value is true showing choose plan window
                     * else No.
                     */
                    String PlanExp = SharedPref.read(SharedPref.PlanExp, "");
                    if (PlanExp.equals("true")) {
                        showCustomDialog();
                    }

                    Picasso.with(UserActivity.this)
                            .load(GlobalConstants.getPhotoPathAthelete() + photoUrl)
                            .placeholder(R.drawable.athlete_image)
                            .into(iv_profile);
                }
            }

            @Override
            public void onFailure(Call<AthleteDetailsResponseModel> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());

            }
        });
    }

}
