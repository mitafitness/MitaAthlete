package com.mita.mqtt.athlete.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mita.athlete.login.AppHelper;
import com.mita.athlete.login.UserActivity;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.adapater.PlansAdapter;
import com.mita.mqtt.athlete.model.AthletePlansResponseModel;
import com.mita.mqtt.athlete.model.AthletePnalsModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlansActivity extends AppCompatActivity {

    // dashboard changes
    private RecyclerView mRecyclerView;
    List<AthletePnalsModel> mMetaPlanList = new ArrayList<>();
    private PlansAdapter mAdapter;
    private APIService mAPIService;
    static String plan_map_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        SharedPref.init(this);
        initViews();
        mAPIService = ApiUtils.getAPIService();

    }

    public void initViews() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewPlans);
        ImageView Iv_back = findViewById(R.id.Iv_back);
        Iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // all plans api call
        try {
            getallPlans();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("===ExecRes", e.getMessage().toString());
        }
    }


    // get all created plans by coach
    public void getallPlans() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(PlansActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        //   dialog.show();

        Call<AthletePlansResponseModel> call = mAPIService.getAthletsPlans();     //getStringScalar(name, age, phone, email);

        call.enqueue(new Callback<AthletePlansResponseModel>() {
            @Override
            public void onResponse(Call<AthletePlansResponseModel> call, Response<AthletePlansResponseModel> response) {
                if (response.body() != null) {
                    Log.i("===ResponseBody", String.valueOf(response.body().getCode()));

                    if (response.body().getCode().equals("201")) {
                        Log.i("AthPlanslist", "No Data Found");
                    } else {
                        mMetaPlanList = response.body().getContent();
                        Log.i("===ResNew", "Number of Coaches received: " + mMetaPlanList.size());
                        mAdapter = new PlansAdapter(mMetaPlanList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mAdapter);

                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthletePlansResponseModel> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

    }


    // onclic plan card
    public void getCoachesActivity(int position) {

        Intent intent = new Intent(PlansActivity.this, Coaches_list.class);
        intent.putExtra("plan_id", String.valueOf(mMetaPlanList.get(position).getMetaPlanInfoId()));
        intent.putExtra("plan_map_id", String.valueOf(mMetaPlanList.get(position).getMetaCoachAlthMapId()));

        String PlanId = mMetaPlanList.get(position).getMetaPlanInfoId();
        String PlanMapId = String.valueOf(mMetaPlanList.get(position).getMetaCoachAlthMapId());
        SharedPref.write(SharedPref.plan_id, PlanId);
        plan_map_id = String.valueOf(mMetaPlanList.get(position).getMetaCoachAlthMapId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
    @Override
    public void onBackPressed() {

        String username = SharedPref.read(SharedPref.PHONE,"");
        AppHelper.setUser(username);
        Intent userActivity = new Intent(PlansActivity.this, UserActivity.class);
        userActivity.putExtra("name", username);
        startActivityForResult(userActivity, 4);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
