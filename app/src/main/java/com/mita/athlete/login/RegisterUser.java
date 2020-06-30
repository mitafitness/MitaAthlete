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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.CodeDeliveryDetailsType;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.mita.mqtt.athlete.R;
import com.mita.utils.SharedPref;


public class RegisterUser extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText givenName;
    private EditText email;
    private EditText phone;
    private EditText countryCode;

    private Button signUp;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private String usernameInput;
    private String userPasswd;

    private SharedPreferences regePreferences;
    private SharedPreferences.Editor regePrefsEditor;
    private String userInputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Init SharedPref
        SharedPref.init(RegisterUser.this);
        regePreferences = getSharedPreferences("RegePrefs", MODE_PRIVATE);
        regePrefsEditor = regePreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // get back to main screen
            String value = extras.getString("TODO");
            if (value.equals("exit")) {
                onBackPressed();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_Register);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView main_title = (TextView) findViewById(R.id.signUp_toolbar_title);
        main_title.setText("Sign up");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
    }


    // This will create the list/form for registration
    private void init() {
        // Lokesh 25-04-2020 Back press fun
        ImageView IvBack = findViewById(R.id.IvBack);
        IvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        countryCode = findViewById(R.id.country_code_edt);
        username = (EditText) findViewById(R.id.editTextRegUserId);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegUserIdLabel);
                    label.setText(username.getHint());
                    // username.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewRegUserIdMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegUserIdLabel);
                    label.setText("");
                }
            }
        });
        //
        password = (EditText) findViewById(R.id.editTextRegUserPassword);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegUserPasswordLabel);
                    label.setText(password.getHint());
                    // password.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewUserRegPasswordMessage);
                label.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegUserPasswordLabel);
                    label.setText("");
                }
            }
        });
        //
        givenName = (EditText) findViewById(R.id.editTextRegGivenName);
        givenName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegGivenNameLabel);
                    label.setText(givenName.getHint());
                    //givenName.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewRegGivenNameMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegGivenNameLabel);
                    label.setText("");
                }
            }
        });
        //
        email = (EditText) findViewById(R.id.editTextRegEmail);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegEmailLabel);
                    label.setText(email.getHint());
                    //  email.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewRegEmailMessage);
                label.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegEmailLabel);
                    label.setText("");
                }
            }
        });
        //
        phone = (EditText) findViewById(R.id.editTextRegPhone);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegPhoneLabel);
//                    label.setText(phone.getHint() + " with country code and no seperators");
                    label.setText(phone.getHint());
                    // phone.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewRegPhoneMessage);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewRegPhoneLabel);
                    label.setText("");
                }
            }
        });

        signUp = (Button) findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read user data and register
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

               /* usernameInput = username.getText().toString().trim();
                if (usernameInput == null || usernameInput.isEmpty()) {
                    TextView view = (TextView) findViewById(R.id.textViewRegUserIdMessage);
                    view.setText(username.getHint() + " cannot be empty");
                    //  username.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }*/

                String userpasswordInput = password.getText().toString();
                userPasswd = userpasswordInput;
                if (userpasswordInput == null || userpasswordInput.isEmpty()) {
                    TextView view = (TextView) findViewById(R.id.textViewUserRegPasswordMessage);
                    view.setText(password.getHint() + " cannot be empty");
                    // password.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }

                String userInput = givenName.getText().toString();
                if (userInput != null) {
                    if (userInput.length() > 0) {
                        userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get(givenName.getHint()).toString(), userInput);
                    }
                }

                userInput = email.getText().toString().trim();
                if (userInput != null) {
                    if (userInput.length() > 0) {
                        userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get(email.getHint()).toString(), userInput);
                    }
                }

                userInputPhone = countryCode.getText().toString().trim() +""+phone.getText().toString().trim();
                String Phone = phone.getText().toString().trim();
                if (Phone != null) {
                    if (Phone.length() > 0) {
                        userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get(phone.getHint()).toString(), userInputPhone);
                    }
                }

               // showWaitDialog("Signing up...");
                String phoneNumber = countryCode.getText().toString() +""+phone.getText().toString();
                SharedPref.write(SharedPref.NAME, givenName.getText().toString());
                SharedPref.write(SharedPref.EMAIL, email.getText().toString());
                SharedPref.write(SharedPref.PHONE, phoneNumber);

                AppHelper.getPool().signUpInBackground(userInputPhone, userpasswordInput, userAttributes, null, signUpHandler);

            }
        });
    }

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
            // Check signUpConfirmationState to see if the user is already confirmed
            closeWaitDialog();
            Boolean regState = signUpResult.getUserConfirmed();
            if (regState) {
                // User is already confirmed
                showDialogMessage("Sign up successful!", userInputPhone + " has been Confirmed", true);
            } else {
                // User is not confirmed
                confirmSignUp(signUpResult.getCodeDeliveryDetails());
            }
        }


        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            TextView label = (TextView) findViewById(R.id.textViewRegUserIdMessage);
            label.setText("Sign up failed");
            // username.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Sign up failed", AppHelper.formatException(exception), false);
        }
    };

    private void confirmSignUp(CodeDeliveryDetailsType cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, SignUpConfirm.class);
        intent.putExtra("source", "signup");
        intent.putExtra("name", userInputPhone);
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                String name = null;
                if (data.hasExtra("name")) {
                    name = data.getStringExtra("name");
                }
                exit(name, userPasswd);
            }
        }
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exit) {
                        exit(userInputPhone);
                    }
                } catch (Exception e) {
                    if (exit) {
                        exit(userInputPhone);
                    }
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

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    private void exit(String uname) {
        exit(uname, null);
    }

    private void exit(String uname, String password) {
        Intent intent = new Intent();
        if (uname == null) {
            uname = "";
        }
        if (password == null) {
            password = "";
        }
        intent.putExtra("name", uname);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void SignIn(View view) {
        onBackPressed();
    }
}
