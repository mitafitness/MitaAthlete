package com.mita.athlete.login;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthleteUpcomingPlanAllModel;


import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardDetailsFinishedAdapter extends RecyclerView.Adapter<DashboardDetailsFinishedAdapter.MyViewHolder> {
    private Context context;

    List<AthleteUpcomingPlanAllModel> mRundetailsList;
    String month = "";

    public DashboardDetailsFinishedAdapter(List<AthleteUpcomingPlanAllModel> mRundetailsList) {
        this.mRundetailsList = mRundetailsList;

    }

    @NonNull
    @Override
    public DashboardDetailsFinishedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_row_finished, parent, false);

        return new DashboardDetailsFinishedAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DashboardDetailsFinishedAdapter.MyViewHolder holder, final int position) {

        AthleteUpcomingPlanAllModel runDetails = mRundetailsList.get(position);

        if (runDetails.getDuration() == null){
            holder.distanceCoveredInTime.setText("0.0 Min");

        }else{
            holder.distanceCoveredInTime.setText(runDetails.getDuration() + " Min");

        }
        //   holder.tv_current_run_distance.setText(runDetails.getRealRun() + "KM");
        DecimalFormat df2 = new DecimalFormat("#.##");
        df2.setRoundingMode(RoundingMode.UP);

        if (runDetails.getPlanGoal() == null) {
            holder.tv_distance.setText("0.0KM");
        } else {
           // String RealRun = String.format("%.2f", Float.parseFloat(runDetails.getRealRun())); //Distance(km)
            holder.tv_distance.setText(runDetails.getPlanGoal()+"KM");
        }

        if (runDetails.getRealRun() == null) {
            holder.distanceCovered.setText("0.0KM");
        } else {
           // String RealRun = String.format("%.2f", Float.parseFloat(runDetails.getRealRun())); //Distance(km)
            holder.distanceCovered.setText(runDetails.getRealRun() + "KM");
        }

        String DateString = runDetails.getDate();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = null;
        try {
            currentDate = sd.parse(DateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String DayName = outFormat.format(currentDate);
        holder.tv_activity_details.setText(runDetails.getPlanGoal() + "KM Run on " + DayName);

        // Log.i("===ParseDate", "Currnt Date : " + runDetails.getDate() + " week name :" + DayName);


        Calendar queueDateCal = Calendar.getInstance();
        int day;
        String monthofTheYear;
        if (currentDate != null) {
            queueDateCal.setTime(currentDate);
            day = queueDateCal.get(Calendar.DAY_OF_MONTH);
            holder.dayOfTheMonth.setText("" + day);

            monthofTheYear = getMonthForInt(queueDateCal.get(Calendar.MONTH));
            if (monthofTheYear != month) {
                holder.month.setVisibility(View.VISIBLE);
                holder.month.setText(monthofTheYear);
                month = monthofTheYear;
            } else {
                holder.month.setVisibility(View.GONE);
            }

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UserActivity) holder.itemView.getContext()).AthleteFinishedRun(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mRundetailsList != null) {
            if (mRundetailsList.size() > 0) {
                return mRundetailsList.size();
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView dayOfTheMonth;
        private TextView tv_activity_details;
        private TextView distanceCovered;
        private TextView distanceCoveredInTime;
        private TextView month;
        private TextView tv_distance;
        private TextView tv_current_run_distance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfTheMonth = itemView.findViewById(R.id.day_of_the_month);
            tv_activity_details = itemView.findViewById(R.id.tv_activity_details);
            distanceCovered = itemView.findViewById(R.id.distance_covered);
            distanceCoveredInTime = itemView.findViewById(R.id.distance_covered_in_time);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            // tv_current_run_distance = itemView.findViewById(R.id.tv_current_run_distance);
            month = itemView.findViewById(R.id.month);
        }
    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

}