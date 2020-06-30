package com.mita.athlete.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mita.mqtt.athlete.R;
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

public class AdditionalDetails extends AppCompatActivity {

    private String Gender;
    private String sharePhoto, shareVoice;

    TextView tv_name, tv_email, tv_mobile;
    EditText et_address, et_age, et_desc, et_profileUrl;
    private AlertDialog userDialog;
    private APIService mAPIService;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);
        SharedPref.init(this);
        initViews();
    }

    public void initViews() {
        ImageView IvBack = findViewById(R.id.IvBack);
        IvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_mobile = findViewById(R.id.tv_mobile);
        et_address = findViewById(R.id.et_address);
        et_age = findViewById(R.id.et_age);
        et_desc = findViewById(R.id.et_desc);
        et_profileUrl = findViewById(R.id.et_profileUrl);
        Button btn_save = findViewById(R.id.btn_save);
        mAPIService = ApiUtils.getAPIService();

        tv_name.setText(SharedPref.read(SharedPref.NAME, ""));
        tv_email.setText(SharedPref.read(SharedPref.EMAIL, ""));
        tv_mobile.setText(SharedPref.read(SharedPref.PHONE, ""));
        userName = SharedPref.read(SharedPref.NAME, "");

        RadioGroup radiofamily = (RadioGroup) findViewById(R.id.radiofamily);
        RadioButton M = (RadioButton) findViewById(R.id.M);
        RadioButton F = (RadioButton) findViewById(R.id.F);

        radiofamily.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.M) {
                    //do work when radioButton2 is active
                    Gender = "M";

                } else if (checkedId == R.id.F) {
                    //do work when radioButton3 is active
                    Gender = "F";
                }

            }
        });

        RadioGroup radiofamilySharePhoto = (RadioGroup) findViewById(R.id.radiofamilySharePhoto);
        RadioButton Yes = (RadioButton) findViewById(R.id.yes);
        RadioButton no = (RadioButton) findViewById(R.id.no);
        radiofamilySharePhoto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.yes) {
                    //do work when radioButton2 is active
                    sharePhoto = "true";

                } else if (checkedId == R.id.no) {
                    //do work when radioButton3 is active
                    sharePhoto = "false";
                }

            }
        });
        radiofamilySharePhoto.check(R.id.yes);


        RadioGroup radiofamilyShareVoice = (RadioGroup) findViewById(R.id.radiofamilyShareVoice);
        RadioButton Yesvoice = (RadioButton) findViewById(R.id.yes1);
        RadioButton novoice = (RadioButton) findViewById(R.id.no1);
        radiofamilyShareVoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.yes1) {
                    //do work when radioButton2 is active
                    shareVoice = "true";

                } else if (checkedId == R.id.no1) {
                    //do work when radioButton3 is active
                    shareVoice = "false";
                }

            }
        });
        radiofamilyShareVoice.check(R.id.yes1);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    sendPost(tv_name.getText().toString(), et_age.getText().toString(), tv_mobile.getText().toString(), tv_email.getText().toString());
                    sendPost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void sendPost() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(AdditionalDetails.this);
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
        jsonObj.addProperty("profileURL", et_profileUrl.getText().toString());
        jsonObj.addProperty("name", SharedPref.read(SharedPref.NAME, ""));
        jsonObj.addProperty("address", et_address.getText().toString());
        jsonObj.addProperty("phone", SharedPref.read(SharedPref.PHONE, ""));
        jsonObj.addProperty("email", SharedPref.read(SharedPref.EMAIL, ""));
        jsonObj.addProperty("age", et_age.getText().toString());
        jsonObj.addProperty("sex", Gender);
        jsonObj.addProperty("photoUrl", "");
        jsonObj.addProperty("decription", et_desc.getText().toString());
        jsonObj.addProperty("shareParmissionPhoto", sharePhoto);
        jsonObj.addProperty("sharePermissionVoice", shareVoice);
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


                    final AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalDetails.this);
                    builder.setTitle("Data Saved").setMessage("Addtional Details Data Saved Successfully").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       /* Intent intent = new Intent(AdditionalDetails.this, UserActivity.class);
                        startActivity(intent);*/
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
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalDetails.this);
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
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalDetails.this);
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

    private void exit() {
        Intent intent = new Intent(AdditionalDetails.this, MainActivity.class);
        if (userName == null)
            userName = "";
        intent.putExtra("name", userName);
        startActivity(intent);
//        finish();
    }



    /*public void onPostClicked() {
        //creating the json object to send
    *//*    JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user","Asif");
        JsonArray citiesArray = new JsonArray();
        citiesArray.add("Dhaka");
        citiesArray.add("Örebro");
        jsonObject.add("cities", citiesArray);*//*
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateToStr = format.format(today);
        int phone = 1122332211;
        JsonObject jsonObj_ = new JsonObject();
        jsonObj_.addProperty("athUserId", "50001");
        jsonObj_.addProperty("athJoingDate", dateToStr);
        jsonObj_.addProperty("profileType", "Self");
        jsonObj_.addProperty("profileURL", " ");
//            jsonObj_.put("name", SharedPref.read(SharedPref.NAME, ""));
        jsonObj_.addProperty("name", "LokeshTEst");
        jsonObj_.addProperty("address", et_address.getText().toString());
//            jsonObj_.put("phone", SharedPref.read(SharedPref.PHONE,""));
        jsonObj_.addProperty("phone", phone);
        jsonObj_.addProperty("email", "lokesh13@gmail.com");
//            jsonObj_.put("email", SharedPref.read(SharedPref.EMAIL, ""));
        jsonObj_.addProperty("age", et_age.getText().toString());
        jsonObj_.addProperty("sex", Gender);
        jsonObj_.addProperty("photoUrl", "");
        jsonObj_.addProperty("decription", et_desc.getText().toString());
        jsonObj_.addProperty("shareParmissionPhoto", sharePhoto);
        jsonObj_.addProperty("sharePermissionVoice", shareVoice);
        jsonObj_.addProperty("athRunPlanId", "4");
        Log.i("===Response", String.valueOf(jsonObj_));
        // Using the Retrofit
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, "http://13.232.87.31:8080/");
        Call<JsonObject> call = jsonPostService.postRawJSON(jsonObj_);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Log.i("===response-success", response.body().toString());
                    response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("===response-failure", call.toString());
            }
        });
    }

*/
    /*private JsonObject ApiJsonMap() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateToStr = format.format(today);
        int phone = 1122332211;
        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject jsonObj_ = new JSONObject();
            jsonObj_.put("athUserId", "50001");
            jsonObj_.put("athJoingDate", dateToStr);
            jsonObj_.put("profileType", "Self");
            jsonObj_.put("profileURL", " ");
//            jsonObj_.put("name", SharedPref.read(SharedPref.NAME, ""));
            jsonObj_.put("name", "LokeshTEst");
            jsonObj_.put("address", et_address.getText().toString());
//            jsonObj_.put("phone", SharedPref.read(SharedPref.PHONE,""));
            jsonObj_.put("phone", phone);
            jsonObj_.put("email", "lokesh13@gmail.com");
//            jsonObj_.put("email", SharedPref.read(SharedPref.EMAIL, ""));
            jsonObj_.put("age", et_age.getText().toString());
            jsonObj_.put("sex", Gender);
            jsonObj_.put("photoUrl", "");
            jsonObj_.put("decription", et_desc.getText().toString());
            jsonObj_.put("shareParmissionPhoto", sharePhoto);
            jsonObj_.put("sharePermissionVoice", shareVoice);
            jsonObj_.put("athRunPlanId", "4");
            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());

            //print parameter
            Log.i("===MY gson.JSON:  ", "AS PARAMETER  " + gsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gsonObject;
    }

    private void ApiCallMethod() {
        try {
            final ProgressDialog dialog;
            dialog = new ProgressDialog(AdditionalDetails.this);
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            Call<JsonObject> registerCall = ApisClient.getService().atheletSaveData(ApiJsonMap());
            registerCall.enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> registerCall, retrofit2.Response<JsonObject> response) {

                    try {
                        //print respone
                        Log.i("===Full json gson => ", new Gson().toJson(response));
                        JSONObject jsonObj = new JSONObject(new Gson().toJson(response).toString());
                        Log.i("===responce => ", jsonObj.getJSONObject("body").toString());

                        if (response.isSuccessful()) {
                            Log.i("===Response", "Success");
                            //  showDialogMessageSuccess("Addtional Details.", "Additinal Details Saved.!", true);

                            dialog.dismiss();

                        } else {
                            Log.i("===Response", "Fail");
                            dialog.dismiss();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Log.i("===Tag1", "error=" + e.toString());

                            dialog.dismiss();
                        } catch (Resources.NotFoundException e1) {
                            e1.printStackTrace();
                            Log.i("===FailRes",e1.getMessage().toString());
                        }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    try {
                        Log.i("===Tag2", "error" + t.toString());

                        dialog.dismiss();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                        Log.i("===Tag3", "error" + e.toString());
                    }
                }

            });


        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }*/


    private void showDialogMessageSuccess(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AdditionalDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }
}
