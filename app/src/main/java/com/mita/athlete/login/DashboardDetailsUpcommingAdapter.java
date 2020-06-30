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

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardDetailsUpcommingAdapter extends RecyclerView.Adapter<DashboardDetailsUpcommingAdapter.MyViewHolder> {
    private Context context;

    List<AthleteUpcomingPlanAllModel> mRundetailsList;
    String month = "";

    public DashboardDetailsUpcommingAdapter(List<AthleteUpcomingPlanAllModel> mRundetailsList) {
        this.mRundetailsList = mRundetailsList;

    }

    @NonNull
    @Override
    public DashboardDetailsUpcommingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_row_upcomming, parent, false);

        return new DashboardDetailsUpcommingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardDetailsUpcommingAdapter.MyViewHolder holder, int position) {

        AthleteUpcomingPlanAllModel runDetails = mRundetailsList.get(position);

        holder.distanceCovered.setText(runDetails.getPlanGoal() + "KM");
        holder.tv_pace.setText(runDetails.getPace());
        holder.tv_description.setText(runDetails.getDescription());

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
        holder.tv_activity_details.setText( DayName + " Long Run");

        //Log.i("===ParseDate", "Currnt Date : " + runDetails.getDate() + " week name :" + DayName);

        Calendar queueDateCal = Calendar.getInstance();
        int day;
        String monthofTheYear;
        if (currentDate != null) {
            queueDateCal.setTime(currentDate);
            day = queueDateCal.get(Calendar.DAY_OF_MONTH);
            holder.dayOfTheMonth.setText("" + day);

            monthofTheYear = getMonthForInt(queueDateCal.get(Calendar.MONTH));
            holder.tv_month.setText(monthofTheYear);
            if (monthofTheYear != month) {
                holder.month.setVisibility(View.VISIBLE);
                holder.month.setText(monthofTheYear);
                month = monthofTheYear;
            } else {
                holder.month.setVisibility(View.GONE);
            }

        }
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
        private TextView tv_pace;
        private TextView month;
        private TextView tv_distance;
        private TextView tv_description;
        private TextView tv_month;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfTheMonth = itemView.findViewById(R.id.day_of_the_month);
            tv_activity_details = itemView.findViewById(R.id.tv_activity_details);
            distanceCovered = itemView.findViewById(R.id.distance_covered);
            tv_pace = itemView.findViewById(R.id.tv_pace);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_month = itemView.findViewById(R.id.tv_month);
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