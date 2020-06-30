package com.mita.mqtt.athlete.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthleteDetailsResponseModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.GlobalConstants;
import com.mita.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoachViewActivity extends AppCompatActivity {
    private TextView tv_name, tv_mobilenum, tv_email, tv_description, tv_specialize, tv_coaching_since, tv_noOf_athelete_coached, tv_fav_diastance;
    private TextView tv_fav_event;
    private Button BtnSelectPlan;
    private APIService mAPIService;
    private String CoachId;
    private String plan_id;
    private String plan_map_id;
    private ImageView iv_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_view);
        mAPIService = ApiUtils.getAPIService();
        SharedPref.init(this);
        initViews();
    }

    public void initViews() {

        Intent intent = getIntent();
        CoachId = intent.getStringExtra("coach_id");
        plan_id = intent.getStringExtra("plan_id");
        plan_map_id = intent.getStringExtra("plan_map_id");
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_mobilenum = findViewById(R.id.tv_mobilenum);
        tv_description = findViewById(R.id.tv_description);
        tv_specialize = findViewById(R.id.tv_specialize);
        tv_coaching_since = findViewById(R.id.tv_coaching_since);
        tv_noOf_athelete_coached = findViewById(R.id.tv_noOf_athelete_coached);
        tv_fav_diastance = findViewById(R.id.tv_fav_diastance);
        tv_fav_event = findViewById(R.id.tv_fav_event);
        BtnSelectPlan = findViewById(R.id.BtnSelectPlan);
        iv_profile = findViewById(R.id.iv_profile);
        BtnSelectPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoachViewActivity.this, AthletePlanPurchaseSummaryActivity.class);
                intent.putExtra("coach_id", CoachId);
                intent.putExtra("plan_id", plan_id);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        ImageView Iv_back = findViewById(R.id.Iv_back);
        Iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        try {
            getCoachDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // get coach details by passing selected coach id
    public void getCoachDetails() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
     //   dialog.show();
        String coach_id = SharedPref.read(SharedPref.coach_id, "");
        Call<AthleteDetailsResponseModel> call = mAPIService.getCoachDetailsById(coach_id);
        call.enqueue(new Callback<AthleteDetailsResponseModel>() {
            @Override
            public void onResponse(Call<AthleteDetailsResponseModel> call, Response<AthleteDetailsResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===Response", "Coache Details: " + response.body().getCode());
                    if (response.body().getCode().equals("201")) {
                        Log.i("CoachView", "No Data Found");
                    } else {
                        int coachUserId = response.body().getContent().getCoachUserId();
                        String description = response.body().getContent().getDecription();
                        String name = response.body().getContent().getName();
                        String email = response.body().getContent().getEmail();
                        String mobilno = response.body().getContent().getPhone();
                        String specializesIn = response.body().getContent().getSpecializesIn();
                        String coachingScince = response.body().getContent().getCoachingScince();
                        String noOfAlthICoached = response.body().getContent().getNoOfAlthICoached();
                        String favriteDistance = response.body().getContent().getFavriteDistance();
                        String favrteEvent = response.body().getContent().getFavrteEvent();
                        String pramotionalVideios = response.body().getContent().getPramotionalVideios();
                        String photoUrl = response.body().getContent().getPhotoUrl();
                        Picasso.with(CoachViewActivity.this).load(GlobalConstants.getPhotoPathCoach() + photoUrl)
                                .placeholder(R.drawable.profile_image_coach)
                                .into(iv_profile);
                        tv_name.setText(name);
                        tv_email.setText(email);
                        tv_mobilenum.setText(mobilno);
                        tv_description.setText(description);
                        tv_specialize.setText(specializesIn);
                        tv_coaching_since.setText(coachingScince);
                        tv_noOf_athelete_coached.setText(noOfAlthICoached);
                        tv_fav_diastance.setText(favriteDistance);
                        tv_fav_event.setText(favrteEvent);
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
}
