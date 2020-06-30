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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.google.gson.JsonObject;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.activity.PlansActivity;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpConfirm extends AppCompatActivity {
    private EditText username;
    private EditText confCode;

    private Button confirm;
    private TextView reqCode;
    private String userName;
    private AlertDialog userDialog;
    EditText editTextNo1, editTextNo2, editTextNo3, editTextNo4, editTextNo5, editTextNo6;
    private Dialog mDialog;
    private APIService mAPIService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAPIService = ApiUtils.getAPIService();
        SharedPref.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView main_title = (TextView) findViewById(R.id.confirm_toolbar_title);
        main_title.setText("Confirm");
        ImageView IvBack = findViewById(R.id.IvBack);
        IvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
    }

    private void init() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("name")) {
                userName = extras.getString("name");
                username = (EditText) findViewById(R.id.editTextConfirmUserId);
                username.setText(userName);

                if(extras.containsKey("Sign in")) {
                    String signIn = getIntent().getStringExtra("Sign in");

                    if (signIn.equalsIgnoreCase("sign_in")) {
                        reqConfCode();
                    }
                }

                // 25-04-2020 Lokesh
              /*  confCode = (EditText) findViewById(R.id.editTextConfirmCode);
                confCode.requestFocus();*/

                if (extras.containsKey("destination")) {
                    String dest = extras.getString("destination");
                    String delMed = extras.getString("deliveryMed");

                    TextView textviewMobile = (TextView) findViewById(R.id.textviewMobile);
                    TextView screenSubtext = (TextView) findViewById(R.id.textViewConfirmSubtext_1);
                    if (dest != null && delMed != null && dest.length() > 0 && delMed.length() > 0) {
                        screenSubtext.setText("A confirmation code was sent to " + dest + " via " + delMed);
                        textviewMobile.setText(dest);
                    } else {
                        screenSubtext.setText("A confirmation code was sent");
                        textviewMobile.setText(" ");

                    }
                }
            } else {
                TextView screenSubtext = (TextView) findViewById(R.id.textViewConfirmSubtext_1);
                screenSubtext.setText("Request for a confirmation code or confirm with the code you already have.");
            }

        }

        // 25-04-2020 Lokesh Verification Fun
        editTextNo6 = findViewById(R.id.editTextNo6);
        editTextNo1 = findViewById(R.id.editTextNo1);
        editTextNo2 = findViewById(R.id.editTextNo2);
        editTextNo3 = findViewById(R.id.editTextNo3);
        editTextNo4 = findViewById(R.id.editTextNo4);
        editTextNo5 = findViewById(R.id.editTextNo5);

        editTextNo1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (editTextNo1.getText().toString().length() == 1)     //size as per your requirement
                {
                    editTextNo2.requestFocus();
                }
            }
        });

        editTextNo2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (editTextNo2.getText().toString().length() == 1)     //size as per your requirement
                {
                    editTextNo3.requestFocus();
                }
            }
        });


        editTextNo3.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (editTextNo3.getText().toString().length() == 1)     //size as per your requirement
                {
                    editTextNo4.requestFocus();
                }
            }
        });

        editTextNo4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (editTextNo4.getText().toString().length() == 1)     //size as per your requirement
                {
                    editTextNo5.requestFocus();
                }
            }
        });

        editTextNo5.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (editTextNo5.getText().toString().length() == 1)     //size as per your requirement
                {
                    editTextNo6.requestFocus();
                }
            }
        });
        editTextNo6.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (editTextNo6.getText().toString().length() == 1)     //size as per your requirement
                {
                    editTextNo1.requestFocus();
                }
            }
        });

        username = (EditText) findViewById(R.id.editTextConfirmUserId);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdLabel);
                    label.setText(username.getHint());
                    // username.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdLabel);
                    label.setText("");
                }
            }
        });

       /* confCode = (EditText) findViewById(R.id.editTextConfirmCode);
        confCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText(confCode.getHint());
                    confCode.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText("");
                }
            }
        });*/

        confirm = (Button) findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfCode();
            }
        });

        reqCode = (TextView) findViewById(R.id.resend_confirm_req);
        reqCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqConfCode();
            }
        });

        ShowSuccessDialog();

    }


    private void sendConfCode() {
        userName = username.getText().toString();
        // 25-04-2020 Lokesh Verification Fun
        String verification_code = editTextNo1.getText().toString() + editTextNo2.getText().toString() +
                editTextNo3.getText().toString() + editTextNo4.getText().toString() + editTextNo5.getText().toString() + editTextNo6.getText().toString();

//        String confirmCode = confCode.getText().toString();
        String confirmCode = verification_code;

        if (userName == null || userName.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(username.getHint() + " cannot be empty");
            //username.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        if (confirmCode == null || confirmCode.length() < 1) {
            /*TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(confCode.getHint() + " cannot be empty");
            confCode.setBackground(getDrawable(R.drawable.text_border_error));*/
            return;
        }

        AppHelper.getPool().getUser(userName).confirmSignUpInBackground(confirmCode, true, confHandler);
    }

    private void reqConfCode() {
        userName = username.getText().toString();
        if (userName == null || userName.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(username.getHint() + " cannot be empty");
            username.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }
        AppHelper.getPool().getUser(userName).resendConfirmationCodeInBackground(resendConfCodeHandler);

    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            showDialogMessageSuccess("Success!", userName + " has been confirmed!", true);
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText("Confirmation failed!");
            username.setBackground(getDrawable(R.drawable.text_border_error));

            label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
            label.setText("Confirmation failed!");
//            confCode.setBackground(getDrawable(R.drawable.text_border_error));

            showDialogMessage("Confirmation failed", AppHelper.formatException(exception), false);
        }
    };

    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
          /*  TextView mainTitle = (TextView) findViewById(R.id.textViewConfirmTitle);
            mainTitle.setText("Confirm your account");
            confCode = (EditText) findViewById(R.id.editTextConfirmCode);
            confCode.requestFocus();*/
            showDialogMessage("Confirmation code sent.", "Code sent to " + cognitoUserCodeDeliveryDetails.getDestination() + " via " + cognitoUserCodeDeliveryDetails.getDeliveryMedium() + ".", false);
//            showDialogMessageSuccess("Confirmation code sent.", "Code sent to " + cognitoUserCodeDeliveryDetails.getDestination() + " via " + cognitoUserCodeDeliveryDetails.getDeliveryMedium() + ".", false);
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText("Confirmation code resend failed");
            // username.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
        }
    };

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        exit();
                    }
                } catch (Exception e) {
                    exit();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void showDialogMessageSuccess(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    saveAtheleteDetails();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Intent intent = new Intent(SignUpConfirm.this, MainActivity.class);
//                startActivity(intent);
                showCustomDialog();
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit() {
        Intent intent = new Intent();
        if (userName == null)
            userName = "";
        intent.putExtra("name", userName);
        setResult(RESULT_OK, intent);
        finish();
    }
    private void ShowSuccessDialog() {

        mDialog = new Dialog(this, R.style.AppBaseTheme);
        mDialog.setContentView(R.layout.mita_privacy_policy_layout);
        Button BtnAccept = mDialog.findViewById(R.id.BtnAccept);
        mDialog.show();
        mDialog.setCancelable(false);
        BtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


    }

    public void saveAtheleteDetails() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JsonObject jsonObj = new JsonObject();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateToStr = format.format(today);

        jsonObj.addProperty("athUserId", "30000");
        jsonObj.addProperty("athJoingDate", dateToStr);
        jsonObj.addProperty("profileType", "Self");
        jsonObj.addProperty("name", SharedPref.read(SharedPref.NAME, ""));
        jsonObj.addProperty("phone", SharedPref.read(SharedPref.PHONE, ""));
        jsonObj.addProperty("email", SharedPref.read(SharedPref.EMAIL, ""));
        jsonObj.addProperty("acceptPolicy", "true");

        Call<JsonObject> call = mAPIService.atheletSaveData(jsonObj);     //getStringScalar(name, age, phone, email);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("===Response", String.valueOf(response.body()));
//                Toast.makeText(AdditionalDetails.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                if (response.body() != null) {
                    String JsonRes = String.valueOf(response.body());
                    JSONObject obj = null;
                    JSONObject obj1 = null;
                    try {
                        obj = new JSONObject(JsonRes);

//                        String pageName = obj.getJSONObject("pageInfo").getString("pageName");
                        String content = String.valueOf(obj.getJSONObject("content"));
                        Log.i("SuccResSave", String.valueOf(content));

                        obj1 = new JSONObject(content);
                        String coachUserId = obj1.getString("athUserId");
                        SharedPref.write(SharedPref.ATH_USER_ID,coachUserId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                } else {

                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpConfirm.this);
                builder.setTitle("Data Not Saved").setMessage("Something went wrong..!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            userDialog.dismiss();
                            exit();
                        } catch (Exception e) {
                            exit();
                        }
                    }
                });
                userDialog = builder.create();
                userDialog.show();
                dialog.dismiss();

            }
        });

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
                Intent intent = new Intent(SignUpConfirm.this, PlansActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
