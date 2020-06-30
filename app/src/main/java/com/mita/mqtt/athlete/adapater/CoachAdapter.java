package com.mita.mqtt.athlete.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.activity.Coaches_list;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.CoachAllModel;
import com.mita.utils.GlobalConstants;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.MyViewHolder> {
    private Context context;

    List<CoachAllModel> mCoachesList;

    public CoachAdapter(List<CoachAllModel> mCoachesList, Context context) {
        this.mCoachesList = mCoachesList;
        this.context = context;

    }


    @NonNull
    @Override
    public CoachAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.coaches_item_list, parent, false);

        return new CoachAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CoachAdapter.MyViewHolder holder, final int position) {

        CoachAllModel coacheslist = mCoachesList.get(position);

        holder.tv_coach_name.setText(coacheslist.getCoachname());
        holder.tv_coach_email.setText(coacheslist.getCoachEmail());
        holder.tv_coach_mobile.setText(coacheslist.getCoachPhone());
        String StartDate =  coacheslist.getStartDate();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date date1 = null;
        try {
            date1 = sdf1.parse(StartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(context).load(GlobalConstants.getPhotoPathCoach() + coacheslist.getCoachPhotoUrl())
                .placeholder(R.drawable.profile_image_coach)
                .into(holder.IvCoach);

        SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM yyyy");
        String DayName = outFormat.format(date1);
        holder.tv_startDate.setText(String.format("Start Date : %s", DayName));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Coaches_list) holder.itemView.getContext()).getActivityPlan(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (mCoachesList != null) {
            if (mCoachesList.size() > 0) {
                return mCoachesList.size();
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

        TextView tv_coach_name, tv_coach_email, tv_coach_mobile,tv_startDate;
        Button Btn_coach_select;
        ImageView IvCoach;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            IvCoach = (ImageView) itemView.findViewById(R.id.IvCoach);
            tv_coach_name = (TextView) itemView.findViewById(R.id.tv_coach_name);
            tv_coach_email = (TextView) itemView.findViewById(R.id.tv_coach_email);
            tv_coach_mobile = (TextView) itemView.findViewById(R.id.tv_coach_mobile);
            tv_startDate = (TextView) itemView.findViewById(R.id.tv_startDate);
            Btn_coach_select = (Button) itemView.findViewById(R.id.Btn_coach_select);
        }
    }


}
