package com.mita.mqtt.athlete.dashboard;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.google.android.material.navigation.NavigationView;
import com.mita.athlete.login.AboutApp;
import com.mita.athlete.login.AddAttributeActivity;
import com.mita.athlete.login.AppHelper;
import com.mita.athlete.login.ChangePasswordActivity;
import com.mita.athlete.login.DashboardDetailsFinishedAdapter;
import com.mita.athlete.login.DeviceSettings;
import com.mita.athlete.login.MainActivity;
import com.mita.athlete.login.SettingsActivity;
import com.mita.athlete.login.UserActivity;
import com.mita.athlete.login.VerifyActivity;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.activity.AthleteProfileUpdateActivity;
import com.mita.mqtt.athlete.activity.PlansActivity;
import com.mita.mqtt.athlete.model.AthleteUpcomingPlanAllModel;
import com.mita.mqtt.athlete.model.AthleteUpcomingPlanResponseModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorsListActivity extends AppCompatActivity {
    private final String TAG = "SensorsListActivity";

    private NavigationView nDrawer;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private ListView attributesList;

    // Cognito user objects
    private CognitoUser user;
    private CognitoUserSession session;
    private CognitoUserDetails details;

    // User details
    private String username;

    // To track changes to user details
    private final List<String> attributesToDelete = new ArrayList<>();

    private TextView navHeaderSubTitle;
    private TextView tv_nav_email;
    // dashboard changes
    private RecyclerView mRecyclerView;
    List<AthleteUpcomingPlanAllModel> mRunDetailsFinishedList = new ArrayList<>();
    private DashboardDetailsFinishedAdapter mAdapterFinished;
    private APIService mAPIService;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private boolean saveLogin;

    private androidx.appcompat.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_list);
        mAPIService = ApiUtils.getAPIService();
        SharedPref.init(this);
        // Set toolbar for this screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * If Athlete is new User creating window to select Plan
         * checking planExp value is true showing choose plan window
         * else No.
         */
        String PlanExp = SharedPref.read(SharedPref.PlanExp, "");
        if (PlanExp.equals("true")) {
            showCustomDialog();
        }

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

        ImageView iv_edit_profile = (ImageView) navigationHeader.findViewById(R.id.iv_edit_profile);
        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorsListActivity.this, AthleteProfileUpdateActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Find which menu item was selected
        int menuItem = item.getItemId();

        // Do the task
        if (menuItem == R.id.user_update_attribute) {
            //updateAllAttributes();
            showWaitDialog("Updating...");
            getDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        exit();
        signOut();
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 20:
                // Settings
                if (resultCode == RESULT_OK) {
                    boolean refresh = data.getBooleanExtra("refresh", true);
                    if (refresh) {
                        //  showAttributes();
                    }
                }
                break;
            case 21:
                // Verify attributes
                if (resultCode == RESULT_OK) {
                    boolean refresh = data.getBooleanExtra("refresh", true);
                    if (refresh) {
                        //  showAttributes();
                    }
                }
                break;
            case 22:
                // Add attributes
                if (resultCode == RESULT_OK) {
                    boolean refresh = data.getBooleanExtra("refresh", true);
                    if (refresh) {
                        //  showAttributes();
                    }
                }
                break;
        }
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
                Intent userActivity = new Intent(this, UserActivity.class);
                userActivity.putExtra("name", username);
                startActivityForResult(userActivity, 4);
                break;
            case R.id.dashboard:
                // dashboard
               /* Intent intent1 = new Intent(SensorsListActivity.this, CurrnetActivity.class);
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
               /* Intent in = new Intent(SensorsListActivity.this, AthleteSummaryActivity.class);
                startActivity(in);*/
                break;
            case R.id.nav_user_add_attribute:
                // Add a new attribute
                addAttribute();
                break;

            case R.id.nav_user_change_password:
                // Change password
                changePassword();
                break;
            case R.id.nav_user_verify_attribute:
                // Confirm new user
                // confirmUser();
                attributesVerification();
                break;
            case R.id.nav_user_settings:
                // Show user settings
                showSettings();
                break;
            case R.id.nav_user_sign_out:
                // Sign out from this account
                signOut();
                break;
            case R.id.nav_user_trusted_devices:
                showTrustedDevices();
                break;
            case R.id.nav_user_about:
                // For the inquisitive
                Intent aboutAppActivity = new Intent(this, AboutApp.class);
                startActivity(aboutAppActivity);
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

    // Get user details from CIP service
    private void getDetails() {
        // AppHelper.getPool().getUser(username).getDetailsInBackground(detailsHandler);
    }

    @Override
    public void onResume() {
        super.onResume();

        getFinishedRunForAthelet();
    }

    // get Finished Acthlete Activity Details
    private void getFinishedRunForAthelet() {

        mRecyclerView = findViewById(R.id.sensors_details_recycler_view);
        if (mRunDetailsFinishedList != null) {
            if (mRunDetailsFinishedList.size() > 0) {
                mRunDetailsFinishedList.clear();
            }
        }

        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        // Toast.makeText(this, AthleteId, Toast.LENGTH_SHORT).show();
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


    // Update attributes
    private void updateAttribute(String attributeType, String attributeValue) {

        if (attributeType == null || attributeType.length() < 1) {
            return;
        }
        CognitoUserAttributes updatedUserAttributes = new CognitoUserAttributes();
        updatedUserAttributes.addAttribute(attributeType, attributeValue);
        //  Toast.makeText(getApplicationContext(), attributeType + ": " + attributeValue, Toast.LENGTH_LONG);
        showWaitDialog("Updating...");
        AppHelper.getPool().getUser(AppHelper.getCurrUser()).updateAttributesInBackground(updatedUserAttributes, updateHandler);
    }

    // Show user MFA Settings
    private void showSettings() {
        Intent userSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivityForResult(userSettingsActivity, 20);
    }

    // Add a new attribute
    private void addAttribute() {
        Intent addAttrbutesActivity = new Intent(this, AddAttributeActivity.class);
        startActivityForResult(addAttrbutesActivity, 22);
    }

    // Delete attribute
    private void deleteAttribute(String attributeName) {
        showWaitDialog("Deleting...");
        List<String> attributesToDelete = new ArrayList<>();
        attributesToDelete.add(attributeName);
        AppHelper.getPool().getUser(AppHelper.getCurrUser()).deleteAttributesInBackground(attributesToDelete, deleteHandler);
    }

    // Change user password
    private void changePassword() {
        Intent changePssActivity = new Intent(this, ChangePasswordActivity.class);
        startActivity(changePssActivity);
    }

    // Verify attributes
    private void attributesVerification() {
        Intent attrbutesActivity = new Intent(this, VerifyActivity.class);
        startActivityForResult(attrbutesActivity, 21);
    }

    private void showTrustedDevices() {
        Intent trustedDevicesActivity = new Intent(this, DeviceSettings.class);
        startActivity(trustedDevicesActivity);
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


        // Get the user name
        Bundle extras = getIntent().getExtras();
//        username = AppHelper.getCurrUser();
//        Log.i("===UserName",username+" : "+SharedPref.read(SharedPref.PHONE,""));
        username = SharedPref.read(SharedPref.PHONE, "");
        ;
        user = AppHelper.getPool().getUser(username);
        getDetails();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        loginPrefsEditor.putBoolean("saveLogin", true);
        loginPrefsEditor.commit();
    }

//    GetDetailsHandler detailsHandler = new GetDetailsHandler() {
//        @Override
//        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
//            closeWaitDialog();
//            // Store details in the AppHandler
//            AppHelper.setUserDetails(cognitoUserDetails);
//            //  showAttributes();
//            // Trusted devices?
//            handleTrustedDevice();
//        }
//
//        @Override
//        public void onFailure(Exception exception) {
//            closeWaitDialog();
//            showDialogMessage("Could not fetch user details!", AppHelper.formatException(exception), true);
//        }
//    };

    private void handleTrustedDevice() {
        CognitoDevice newDevice = AppHelper.getNewDevice();
        if (newDevice != null) {
            AppHelper.newDevice(null);
            trustedDeviceDialog(newDevice);
        }
    }

    private void updateDeviceStatus(CognitoDevice device) {
        device.rememberThisDeviceInBackground(trustedDeviceHandler);
    }

    private void trustedDeviceDialog(final CognitoDevice newDevice) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remember this device?");
        //final EditText input = new EditText(SensorsListActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        //input.setLayoutParams(lp);
        //input.requestFocus();
        //builder.setView(input);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //String newValue = input.getText().toString();
                    showWaitDialog("Remembering this device...");
                    updateDeviceStatus(newDevice);
                    userDialog.dismiss();
                } catch (Exception e) {
                    // Log failure
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    // Log failure
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    // Callback handlers

    UpdateAttributesHandler updateHandler = new UpdateAttributesHandler() {
        @Override
        public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
            // Update successful
            if (attributesVerificationList.size() > 0) {
                showDialogMessage("Updated", "The updated attributes has to be verified", false);
            }
            getDetails();
        }

        @Override
        public void onFailure(Exception exception) {
            // Update failed
            closeWaitDialog();
            showDialogMessage("Update failed", AppHelper.formatException(exception), false);
        }
    };

    GenericHandler deleteHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            closeWaitDialog();
            // Attribute was deleted
            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);

            // Fetch user details from the the service
            getDetails();
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            // Attribute delete failed
            showDialogMessage("Delete failed", AppHelper.formatException(e), false);

            // Fetch user details from the service
            getDetails();
        }
    };

    GenericHandler trustedDeviceHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            // Close wait dialog
            closeWaitDialog();
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            showDialogMessage("Failed to update device status", AppHelper.formatException(exception), true);
        }
    };

    private void showUserDetail(final String attributeType, final String attributeValue) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(attributeType);
        final EditText input = new EditText(SensorsListActivity.this);
        input.setText(attributeValue);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        input.requestFocus();
        builder.setView(input);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String newValue = input.getText().toString();
                    if (!newValue.equals(attributeValue)) {
                        showWaitDialog("Updating...");
                        updateAttribute(AppHelper.getSignUpFieldsC2O().get(attributeType), newValue);
                    }
                    userDialog.dismiss();
                } catch (Exception e) {
                    // Log failure
                }
            }
        }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    deleteAttribute(AppHelper.getSignUpFieldsC2O().get(attributeType));
                } catch (Exception e) {
                    // Log failure
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exit) {
                        exit();
                    }
                } catch (Exception e) {
                    // Log failure
                    Log.e(TAG, " -- Dialog dismiss failed");
                    if (exit) {
                        exit();
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
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
                Intent intent = new Intent(SensorsListActivity.this, PlansActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }




    // Floating action button
    public void fabSensors(View view) {
    }
}
