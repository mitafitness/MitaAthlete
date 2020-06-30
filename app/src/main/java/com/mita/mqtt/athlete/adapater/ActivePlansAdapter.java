package com.mita.mqtt.athlete.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.ActivityAllPlans;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivePlansAdapter extends RecyclerView.Adapter<ActivePlansAdapter.MyViewHolder> {
    private Context context;

    List<ActivityAllPlans> mActivityPlanlist;
    String month = "";

    public ActivePlansAdapter(List<ActivityAllPlans> mActivityPlanlist) {
        this.mActivityPlanlist = mActivityPlanlist;

    }

    @NonNull
    @Override
    public ActivePlansAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_plan_item_list, parent, false);

        return new ActivePlansAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivePlansAdapter.MyViewHolder holder, int position) {

        ActivityAllPlans runDetails = mActivityPlanlist.get(position);

        holder.distance_covered.setText(runDetails.getActivityRun() + " KM");
        holder.tv_description.setText(runDetails.getActivityDescription1());
        holder.tv_pace.setText(runDetails.getActivityPace());

        String DateString = runDetails.getDate();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = null;
        try {
            currentDate = sd.parse(DateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar queueDateCal = Calendar.getInstance();
        int day;
        String monthofTheYear;
        if (currentDate != null) {
            queueDateCal.setTime(currentDate);
            day = queueDateCal.get(Calendar.DAY_OF_MONTH);
            holder.day_of_the_month.setText("" + day);

            monthofTheYear = getMonthForInt(queueDateCal.get(Calendar.MONTH));
            holder.day_of_the_month1.setText("" + monthofTheYear);

           /* if (monthofTheYear != month) {
              //  holder.month.setVisibility(View.GONE);
                holder.month.setText(monthofTheYear);
                holder.day_of_the_month1.setText(monthofTheYear);
                month = monthofTheYear;
                Log.i("==Check1",monthofTheYear);
            } else {
                Log.i("==Check2","sdsdsdsds");

                holder.month.setVisibility(View.GONE);
               // holder.day_of_the_month1.setVisibility(View.GONE);
            }*/

        }
    }


    @Override
    public int getItemCount() {
        if (mActivityPlanlist != null) {
            if (mActivityPlanlist.size() > 0) {
                return mActivityPlanlist.size();
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

        private TextView tv_description;
        private TextView day_of_the_month1;
        private TextView day_of_the_month;
        private TextView tv_pace;
        private TextView distance_covered;
        private TextView month;
        private TextView tv_activity_desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_description = itemView.findViewById(R.id.tv_description);
            day_of_the_month1 = itemView.findViewById(R.id.day_of_the_month1);
            day_of_the_month = itemView.findViewById(R.id.day_of_the_month);
            distance_covered = itemView.findViewById(R.id.distance_covered);
            tv_pace = itemView.findViewById(R.id.tv_pace);
            tv_activity_desc = itemView.findViewById(R.id.tv_activity_desc);
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