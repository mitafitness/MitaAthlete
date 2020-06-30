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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChooseMfaContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.google.android.material.navigation.NavigationView;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthleteDetailsResponseModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private NavigationView nDrawer;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    // Screen fields
    private EditText inUsername;
    private EditText inPassword;
    private EditText countryCode;

    //Continuations
    private MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private NewPasswordContinuation newPasswordContinuation;
    private ChooseMfaContinuation mfaOptionsContinuation;

    // User Details
    private String mCountryCodeStr;
    private String username;
    private String password;
    private SharedPreferences.Editor loginPrefsEditor;
    private SharedPreferences loginPreferences;
    private boolean saveLogin;
    private APIService mAPIService;

    // Mandatory overrides first
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAPIService = ApiUtils.getAPIService();
        SharedPref.init(this);

        // Set toolbar for this screen
       /* toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        TextView main_title = (TextView) findViewById(R.id.main_toolbar_title);
        main_title.setText("Sign in");*/
//        setSupportActionBar(toolbar);

        // Set navigation drawer for this screen
        mDrawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        nDrawer = (NavigationView) findViewById(R.id.nav_view);
        setNavDrawer();

        // Initialize application
        AppHelper.init(getApplicationContext());
        initApp();
        findCurrent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Open/Close the navigation drawer when menu icon is selected
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                // Register user
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    if (!name.isEmpty()) {
                        String username1 = name.substring(3, name.length());
                        inUsername.setText(username1);
                        inPassword.setText("");
                        inPassword.requestFocus();
                    }
                    String userPasswd = data.getStringExtra("password");
                    if (!userPasswd.isEmpty()) {
                        inPassword.setText(userPasswd);
                    }
                    if (!name.isEmpty() && !userPasswd.isEmpty()) {
                        // We have the user details, so sign in!
                        mCountryCodeStr = countryCode.getText().toString().trim();
                        username = name;
                        password = userPasswd;
                        AppHelper.getPool().getUser(mCountryCodeStr + "" + username).getSessionInBackground(authenticationHandler);
                    }
                }
                break;
            case 2:
                // Confirm register user
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    if (!name.isEmpty()) {
                        String username1 = name.substring(3, name.length());
                        inUsername.setText(username1);
                        inPassword.setText("");
                        inPassword.requestFocus();
                    }
                }
                break;
            case 3:
                // Forgot password
                if (resultCode == RESULT_OK) {
                    String newPass = data.getStringExtra("newPass");
                    String code = data.getStringExtra("code");
                    if (newPass != null && code != null) {
                        if (!newPass.isEmpty() && !code.isEmpty()) {
                            showWaitDialog("Setting new password...");
                            forgotPasswordContinuation.setPassword(newPass);
                            forgotPasswordContinuation.setVerificationCode(code);
                            forgotPasswordContinuation.continueTask();
                        }
                    }
                }
                break;
            case 4:
                // User
                if (resultCode == RESULT_OK) {
                    clearInput();
                    String name = data.getStringExtra("TODO");
                    if (name != null) {
                        if (!name.isEmpty()) {
                            name.equals("exit");
                            onBackPressed();
                        }
                    }
                }
                break;
            case 5:
                //MFA
                closeWaitDialog();
                if (resultCode == RESULT_OK) {
                    String code = data.getStringExtra("mfacode");
                    if (code != null) {
                        if (code.length() > 0) {
                            showWaitDialog("Signing in...");
                            multiFactorAuthenticationContinuation.setMfaCode(code);
                            multiFactorAuthenticationContinuation.continueTask();
                        } else {
                            inPassword.setText("");
                            inPassword.requestFocus();
                        }
                    }
                }
                break;
            case 6:
                //New password
                closeWaitDialog();
                Boolean continueSignIn = false;
                if (resultCode == RESULT_OK) {
                    continueSignIn = data.getBooleanExtra("continueSignIn", false);
                }
                if (continueSignIn) {
                    continueWithFirstTimeSignIn();
                }
                break;
            case 7:
                // Choose MFA
                closeWaitDialog();
                if (resultCode == RESULT_OK) {
                    String option = data.getStringExtra("mfaOption");
                    if (option != null) {
                        if (option.length() > 0) {
                            Log.d(TAG, " -- Selected Option: " + option);
                            conitnueWithSelectedMfa(option);
                        } else {
                            inPassword.setText("");
                            inPassword.requestFocus();
                            TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                            label.setText("Login cancelled");
                        }
                    }
                }
        }
    }

    // App methods
    // Register user - start process
    public void signUp(View view) {
        signUpNewUser();
    }

    // Login if a user is already present
    public void logIn(View view) {
        signInUser();
    }

    // Forgot password processing
    public void forgotPassword(View view) {
        forgotpasswordUser();
    }


    // Private methods
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
            case R.id.nav_sign_up:
                // Start sign-up
                signUpNewUser();
                break;
            case R.id.nav_sign_up_confirm:
                // Confirm new user
                confirmUser();
                break;
            case R.id.nav_sign_in_forgot_password:
                // User has forgotten the password, start the process to set a new password
                forgotpasswordUser();
                break;
            case R.id.nav_about:
                // For the inquisitive
                Intent aboutAppActivity = new Intent(this, AboutApp.class);
                startActivity(aboutAppActivity);
                break;

        }
    }

    private void signUpNewUser() {
        Intent registerActivity = new Intent(this, RegisterUser.class);
        startActivityForResult(registerActivity, 1);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void signInUser() {
        mCountryCodeStr = countryCode.getText().toString().trim();
        username = inUsername.getText().toString().trim();
        if (username == null || username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText(inUsername.getHint() + " cannot be empty");
            //   inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        AppHelper.setUser(mCountryCodeStr + "" + username);

        password = inPassword.getText().toString().trim();
        if (password == null || password.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
            label.setText(inPassword.getHint() + " cannot be empty");
            //     inPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        //showWaitDialog("Signing in...");
        AppHelper.getPool().getUser(mCountryCodeStr + "" + username).getSessionInBackground(authenticationHandler);
    }

    private void forgotpasswordUser() {
        mCountryCodeStr = countryCode.getText().toString().trim();
        username = inUsername.getText().toString().trim();
        if (username == null) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText(inUsername.getHint() + " cannot be empty");
            //  inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        if (username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText(inUsername.getHint() + " cannot be empty");
            // inUsername.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("");
        AppHelper.getPool().getUser(mCountryCodeStr + "" + username).forgotPasswordInBackground(forgotPasswordHandler);
    }

    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination", forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
    }

    private void mfaAuth(MultiFactorAuthenticationContinuation continuation) {
        multiFactorAuthenticationContinuation = continuation;
        Intent mfaActivity = new Intent(this, MFAActivity.class);
        mfaActivity.putExtra("mode", multiFactorAuthenticationContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(mfaActivity, 5);
    }

    private void firstTimeSignIn() {
        Intent newPasswordActivity = new Intent(this, NewPassword.class);
        startActivityForResult(newPasswordActivity, 6);
    }

    private void selectMfaToSignIn(List<String> options, Map<String, String> parameters) {
        Intent chooseMfaActivity = new Intent(this, ChooseMFA.class);
        AppHelper.setMfaOptionsForDisplay(options, parameters);
        startActivityForResult(chooseMfaActivity, 7);
    }

    private void conitnueWithSelectedMfa(String option) {
        // mfaOptionsContinuation.setChallengeResponse("ANSWER", option);
        mfaOptionsContinuation.setMfaOption(option);
        mfaOptionsContinuation.continueTask();
    }

    private void continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AppHelper.getPasswordForFirstTimeLogin());
        Map<String, String> newAttributes = AppHelper.getUserAttributesForFirstTimeLogin();
        if (newAttributes != null) {
            for (Map.Entry<String, String> attr : newAttributes.entrySet()) {
                Log.d(TAG, String.format(" -- Adding attribute: %s, %s", attr.getKey(), attr.getValue()));
                newPasswordContinuation.setUserAttribute(attr.getKey(), attr.getValue());
            }
        }
        try {
            newPasswordContinuation.continueTask();
        } catch (Exception e) {
            closeWaitDialog();
            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText("Sign-in failed");
            //inPassword.setBackground(getDrawable(R.drawable.text_border_error));

            label = (TextView) findViewById(R.id.textViewUserIdMessage);
            label.setText("Sign-in failed");
            // inUsername.setBackground(getDrawable(R.drawable.text_border_error));

            showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }
    }

    private void confirmUser() {
        Intent confirmActivity = new Intent(this, SignUpConfirm.class);
        confirmActivity.putExtra("source", "main");
        startActivityForResult(confirmActivity, 2);

    }

    private void launchUser() {
        Intent userActivity = new Intent(this, UserActivity.class);
        userActivity.putExtra("name", username);
        startActivityForResult(userActivity, 4);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void findCurrent() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();

        username = user.getUserId();
        if (username != null) {
            String username1 = username.substring(3, username.length());
            AppHelper.setUser(username);
            inUsername.setText(username1);
            user.getSessionInBackground(authenticationHandler);
        }

    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if (username != null) {
            this.username = username;
            AppHelper.setUser(username);
        }

        if (this.password == null) {
            String username1 = username.substring(3, username.length());
            inUsername.setText(username1);
            password = inPassword.getText().toString();
            if (password == null) {
                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                label.setText(inPassword.getHint() + " enter password");
                // inPassword.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }

            if (password.length() < 1) {
                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                label.setText(inPassword.getHint() + " enter password");
                //inPassword.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    // initialize app
    private void initApp() {
        countryCode = findViewById(R.id.country_code_edt);
        inUsername = (EditText) findViewById(R.id.editTextUserId);
        inUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewUserIdLabel);
                    label.setText(R.string.Username);
                    //inUsername.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewUserIdLabel);
                    label.setText("");
                }
            }
        });

        inPassword = (EditText) findViewById(R.id.editTextUserPassword);
        inPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewUserPasswordLabel);
                    label.setText(R.string.Password);
                    //inPassword.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewUserPasswordLabel);
                    label.setText("");
                }
            }
        });
    }


    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            closeWaitDialog();
            showDialogMessage("Password successfully changed!", "");
            inPassword.setText("");
            inPassword.requestFocus();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            closeWaitDialog();
            getForgotPasswordCode(forgotPasswordContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            String errorMessage = AppHelper.formatException(e);
            if (errorMessage.contains("Username/client id combination not found.")) {
                showDialogMessage("Forgot password failed", "Username not found");
            } else {
                showDialogMessage("Forgot password failed", AppHelper.formatException(e));
            }
        }
    };

    //
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, " -- Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            closeWaitDialog();

            try {
                getAthleteDetailsByPhoneNum();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            closeWaitDialog();
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            closeWaitDialog();
            mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            String error = AppHelper.formatException(e);
            if (error.contains("User is not confirmed.")) {
                Intent intent = new Intent(MainActivity.this, SignUpConfirm.class);
                intent.putExtra("name", username);
                intent.putExtra("Sign in", "sign_in");
                startActivity(intent);
            } else {
                TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
                label.setText("Sign-in failed");
                // inPassword.setBackground(getDrawable(R.drawable.text_border_error));

                label = (TextView) findViewById(R.id.textViewUserIdMessage);
                label.setText("Sign-in failed");
                //  inUsername.setBackground(getDrawable(R.drawable.text_border_error));

                showDialogMessage("Sign-in failed", AppHelper.formatException(e));
            }
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) continuation;
                AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                closeWaitDialog();
                firstTimeSignIn();
            } else if ("SELECT_MFA_TYPE".equals(continuation.getChallengeName())) {
                closeWaitDialog();
                mfaOptionsContinuation = (ChooseMfaContinuation) continuation;
                List<String> mfaOptions = mfaOptionsContinuation.getMfaOptions();
                selectMfaToSignIn(mfaOptions, continuation.getParameters());
            }
        }
    };

    private void clearInput() {

        if (countryCode == null) {
            countryCode = findViewById(R.id.country_code_edt);
        }
        if (inUsername == null) {
            inUsername = (EditText) findViewById(R.id.editTextUserId);
        }

        if (inPassword == null) {
            inPassword = (EditText) findViewById(R.id.editTextUserPassword);
        }

        inUsername.setText("");
        inUsername.requestFocus();
        //inUsername.setBackground(getDrawable(R.drawable.text_border_selector));
        inPassword.setText("");
        //inPassword.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
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


    public void getAthleteDetailsByPhoneNum() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();

        Call<AthleteDetailsResponseModel> call = mAPIService.getAthleteDetailsByPhoneNumber(username);     //getStringScalar(name, age, phone, email);
        call.enqueue(new Callback<AthleteDetailsResponseModel>() {
            @Override
            public void onResponse(Call<AthleteDetailsResponseModel> call, Response<AthleteDetailsResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===ResponseBody", String.valueOf(response.body().getCode()));
                    if (response.body().getCode().equals("201")) {
                        Log.i("getDetailsByPhone", "No Data");
                    } else {
                        String description = response.body().getContent().getDecription();
                        String name = response.body().getContent().getName();
                        String email = response.body().getContent().getEmail();
                        String mobilno = response.body().getContent().getPhone();
                        String PlanExp = String.valueOf(response.body().getContent().getPlanExp());
                        String AthleteUserId = String.valueOf(response.body().getContent().getAthUserId());
                        String PhotoUrl = String.valueOf(response.body().getContent().getPhotoUrl());
                        SharedPref.write(SharedPref.ATH_USER_ID, AthleteUserId);
                        SharedPref.write(SharedPref.NAME, name);
                        SharedPref.write(SharedPref.EMAIL, email);
                        SharedPref.write(SharedPref.PHONE, mobilno);
                        SharedPref.write(SharedPref.PHOTO_URL, PhotoUrl);
                        SharedPref.write(SharedPref.PlanExp, PlanExp);
                        launchUser();

                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthleteDetailsResponseModel> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }


    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            System.exit(0);
        } else {

            Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
