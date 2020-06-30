package com.mita.mqtt.athlete.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.adapater.CoachAdapter;
import com.mita.mqtt.athlete.model.CoachAllModel;
import com.mita.mqtt.athlete.model.CoachListResponse;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Coaches_list extends AppCompatActivity {

    // dashboard changes
    private RecyclerView mRecyclerView;
    List<CoachAllModel> mCoachesListArray = new ArrayList<>();
    private CoachAdapter mAdapter;
    private APIService mAPIService;
    private String plan_id;
    private String plan_map_id;
    static String coach_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaches_list);
        // initialing layout views
        initViews();

        // initializing Retrofit api service
        mAPIService = ApiUtils.getAPIService();
        SharedPref.init(this);

    }

    public void initViews() {
        Intent intent = getIntent();
        plan_id = intent.getStringExtra("plan_id");
        plan_map_id = intent.getStringExtra("plan_map_id");
//        Toast.makeText(this, plan_id, Toast.LENGTH_SHORT).show();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCoaches);
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
        // coaches list
        try {
            getallCoachs();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("===ExecRes", e.getMessage().toString());
        }
    }

   // get all choachs by plans
    public void getallCoachs() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(Coaches_list.this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
     //   dialog.show();
        String PlanId = SharedPref.read(SharedPref.plan_id, "");

        Call<CoachListResponse> call = mAPIService.getCoachByPlanId(PlanId);     //getStringScalar(name, age, phone, email);

        call.enqueue(new Callback<CoachListResponse>() {
            @Override
            public void onResponse(Call<CoachListResponse> call, Response<CoachListResponse> response) {
                Log.i("===ResponseBody", String.valueOf(response.body().getCode()));
                if (response.body() != null) {
                    if (response.body().getCode().equals("201")) {
                        Log.i("Coachslist", "No Data Found");
                    } else {
                        mCoachesListArray = response.body().getContent();
                        Log.i("===ResNew", "Number of Coaches: " + response.body().getCode());
                        mAdapter = new CoachAdapter(mCoachesListArray , getApplicationContext());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CoachListResponse> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }

    // oclick coachs card
    public void getActivityPlan(int position) {
        Intent intent = new Intent(Coaches_list.this, CoachViewActivity.class);
        intent.putExtra("plan_id", plan_id);
        intent.putExtra("plan_map_id", plan_map_id);
        intent.putExtra("coach_id", mCoachesListArray.get(position).getCoachId());
        String CoachId = mCoachesListArray.get(position).getCoachId();
        String PlanMapId = String.valueOf(mCoachesListArray.get(position).getMetaCoachAlthMapId());
        SharedPref.write(SharedPref.plan_map_id, PlanMapId);
        SharedPref.write(SharedPref.coach_id, CoachId);
        coach_id = String.valueOf(mCoachesListArray.get(position).getCoachId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }


}
