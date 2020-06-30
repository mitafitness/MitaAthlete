package com.mita.mqtt.athlete.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mita.athlete.login.AppHelper;
import com.mita.athlete.login.UserActivity;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthletePlansResponseModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.SharedPref;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AthletePlanPaymentActivity extends AppCompatActivity {

    private APIService mAPIService;
    private EditText et_voucher;
    private Button BtnPayDesable;
    private Button BtnPayNow;
    private Button BtnApplyVoucher;
    private Handler handlerOne;
    private Runnable runnableOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_plan_payment);
        SharedPref.init(this);

        // initializing retrofit service
        mAPIService = ApiUtils.getAPIService();

        // initializing cognito
        AppHelper.init(getApplicationContext());

        // initialzing viwes
        initViws();
    }

    public void initViws() {

        et_voucher = findViewById(R.id.et_voucher);


        BtnApplyVoucher = findViewById(R.id.BtnApplyVoucher);
        BtnPayDesable = findViewById(R.id.BtnPayDesable);
        BtnPayNow = findViewById(R.id.BtnPayNow);


        // Button pay Now
        BtnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SavePlanPayment();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        // Button apply Voucher
        BtnApplyVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String voucher = et_voucher.getText().toString().trim();
                if (voucher.equals("MITA") || voucher.equals("mita")) {

                    BtnPayDesable.setVisibility(View.GONE);
                    BtnPayNow.setVisibility(View.VISIBLE);
                } else {
                    BtnPayDesable.setVisibility(View.VISIBLE);
                    BtnPayNow.setVisibility(View.GONE);
                    Toast.makeText(AthletePlanPaymentActivity.this, "Invalid Voucher", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

// Subscribing plan API Call
    public void SavePlanPayment() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
     //   dialog.show();

        String athUserId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        String PlanMapID = SharedPref.read(SharedPref.plan_map_id, "");
        Call<AthletePlansResponseModel> call = mAPIService.addAlthToMetaCoachAlth(PlanMapID, athUserId);
        call.enqueue(new Callback<AthletePlansResponseModel>() {
            @Override
            public void onResponse(Call<AthletePlansResponseModel> call, Response<AthletePlansResponseModel> response) {
                Log.i("===Response", String.valueOf(response.body()));
                if (response.body() != null) {
                    Log.i("===ResponsePay", String.valueOf(response.body().getContent()));
                    ShowSuccessDialog();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<AthletePlansResponseModel> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

    }



    // Show dialog for success subscrption of plan
    private void ShowSuccessDialog() {
        final Dialog mDialog = new Dialog(this, R.style.AppBaseTheme);
        mDialog.setContentView(R.layout.activity_success);
        TextView tv_sub_title = mDialog.findViewById(R.id.tv_sub_title);
        tv_sub_title.setText("Plan Payment  Done Successfully.!");
        mDialog.show();

        handlerOne = new Handler();
        runnableOne = new Runnable() {
            @Override
            public void run() {
                String username = SharedPref.read(SharedPref.PHONE,"");

                AppHelper.setUser(username);
                Intent userActivity = new Intent(AthletePlanPaymentActivity.this, UserActivity.class);
                userActivity.putExtra("name", username);
                startActivityForResult(userActivity, 4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                handlerOne.postDelayed(this, 3000);
                handlerOne.removeCallbacks(runnableOne);
                mDialog.dismiss();
            }
        };
        handlerOne.postDelayed(runnableOne, 3000);
    }
}
