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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.mita.mqtt.athlete.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText passwordInput;
    private EditText codeInput;

    EditText editTextNo1, editTextNo2, editTextNo3, editTextNo4, editTextNo5, editTextNo6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(null, null);
            }
        });

        TextView main_title = (TextView) findViewById(R.id.forgot_password_toolbar_title);
        main_title.setText("Forgot password");

        init();
    }

    public void forgotPassword(View view) {
        getCode();
    }

    private void init() {

        // Lokesh 25-04-2020 Back press fun
        ImageView IvBack = findViewById(R.id.IvBack);
        IvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("destination")) {
                String dest = extras.getString("destination");
                String delMed = extras.getString("deliveryMed");
                TextView message = (TextView) findViewById(R.id.textViewForgotPasswordMessage);
                String textToDisplay = "Code to set a new password was sent to " + dest + " via " + delMed;
                message.setText(textToDisplay);
            }
        }

        passwordInput = (EditText) findViewById(R.id.editTextForgotPasswordPass);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewForgotPasswordUserIdLabel);
                    label.setText(passwordInput.getHint());
                  //  passwordInput.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewForgotPasswordUserIdMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewForgotPasswordUserIdLabel);
                    label.setText("");
                }
            }
        });

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


        codeInput = (EditText) findViewById(R.id.editTextForgotPasswordCode);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewForgotPasswordCodeLabel);
                    label.setText(codeInput.getHint());
                   // codeInput.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewForgotPasswordCodeMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewForgotPasswordCodeLabel);
                    label.setText("");
                }
            }
        });
    }

    private void getCode() {
        String newPassword = passwordInput.getText().toString();

        if (newPassword == null || newPassword.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewForgotPasswordUserIdMessage);
            label.setText(passwordInput.getHint() + " cannot be empty");
          //  passwordInput.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

//        String verCode = codeInput.getText().toString();
        String verification_code = editTextNo1.getText().toString() + editTextNo2.getText().toString() +
                editTextNo3.getText().toString() + editTextNo4.getText().toString() + editTextNo5.getText().toString() + editTextNo6.getText().toString();

        String verCode = verification_code;

        if (verCode == null || verCode.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewForgotPasswordCodeMessage);
            label.setText(codeInput.getHint() + " cannot be empty");
           // codeInput.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }
        exit(newPassword, verCode);
    }

    private void exit(String newPass, String code) {
        Intent intent = new Intent();
        if (newPass == null || code == null) {
            newPass = "";
            code = "";
        }
        intent.putExtra("newPass", newPass);
        intent.putExtra("code", code);
        setResult(RESULT_OK, intent);
        finish();
    }
}
