package com.mita.mqtt.athlete.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mita.athlete.login.AppHelper;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthletePlanPurchaseSummaryResModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.GlobalConstants;
import com.mita.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
AthletePlanPurchaseSummaryActivity extends AppCompatActivity {
    private TextView tv_plan_change, tv_plan_title, tv_description, tv_specialize, tv_name, tv_email, tv_address, tv_price;
    private ImageView iv_profile;
    private String plan_id;
    private String coach_id;
    private APIService mAPIService;
    private String planEndDate;
    private Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_plan_purchase_summary);

        // initializing retrofit service
        mAPIService = ApiUtils.getAPIService();

        // initializing cognito
        AppHelper.init(getApplicationContext());

        // initializing sharedPreff
        SharedPref.init(this);

        // initialzing viwes
        initViews();
    }

    public void initViews() {

        Intent intent = getIntent();
        plan_id = intent.getStringExtra("plan_id");
        coach_id = intent.getStringExtra("coach_id");
        tv_plan_change = findViewById(R.id.tv_click_change);

        // change plan button
        tv_plan_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlansActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        tv_plan_title = findViewById(R.id.tv_plan_title);
        tv_description = findViewById(R.id.tv_description);
        tv_specialize = findViewById(R.id.tv_specialize);
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_address = findViewById(R.id.tv_address);
        tv_price = findViewById(R.id.tv_price);
        iv_profile = findViewById(R.id.iv_profile);
        ImageView Iv_back = findViewById(R.id.Iv_back);
        Button BtnConfirmPlan = findViewById(R.id.BtnConfirmPlan);
        BtnConfirmPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // cheking plan expired or not using plan end date and current date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/M/dd");
                Date d1 = null, d2 = null;
                // parsing data data variable to dd MMM yyyy formate
                try {
                    d1 = sdformat.parse(planEndDate);
                    d2 = sdformat.parse(currentDate);

                    if (d1 != null && d2 != null) {
                        if (d1.compareTo(d2) < 0) {
                            planExpiredAlertDialog();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), AthletePlanPaymentActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i("==Err", e.getMessage());
                }

            }
        });

        // backpress icon
        Iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //  plan summary API Call
        try {
            getPlanDetailsSummary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // fetch plan summary details
    public void getPlanDetailsSummary() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();
        String PlanMapId = SharedPref.read(SharedPref.plan_map_id, "");

        Call<AthletePlanPurchaseSummaryResModel> call = mAPIService.getPlanDetailsSummary(PlanMapId);
        call.enqueue(new Callback<AthletePlanPurchaseSummaryResModel>() {
            @Override
            public void onResponse(Call<AthletePlanPurchaseSummaryResModel> call, Response<AthletePlanPurchaseSummaryResModel> response) {
                if (response.body() != null) {
                    Log.i("===Response", "Plan summary code: " + response.body().getCode());
                    if (response.body().getCode().equals("201")) {
                        Log.i("CoachView", "No Data Found");
                    } else {
                        tv_plan_title.setText(response.body().getContent().getMetaPlanTitle());
                        tv_description.setText(response.body().getContent().getDescription1());
                        tv_specialize.setText(response.body().getContent().getDescription2());
                        tv_name.setText(response.body().getContent().getCoachname());
                        tv_email.setText(response.body().getContent().getCoachEmail());
                        tv_address.setText(response.body().getContent().getCoachAddress());
                        tv_price.setText(String.valueOf(response.body().getContent().getPrice()));
                        String photoUrl = response.body().getContent().getCoachPhotoUrl();
                        planEndDate = response.body().getContent().getEndDate();
                        Picasso.with(AthletePlanPurchaseSummaryActivity.this)
                                .load(GlobalConstants.getPhotoPathCoach() + photoUrl)
                                .placeholder(R.drawable.profile_image_coach)
                                .into(iv_profile);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthletePlanPurchaseSummaryResModel> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }

    public void planExpiredAlertDialog() {

        final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#6b379f'>Alert!</font>"));
        alertDialogBuilder.setMessage(Html.fromHtml("<font color='#6b379f'>Plan Expired, Please Select Another Plan.!</font>"));

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), PlansActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
}
