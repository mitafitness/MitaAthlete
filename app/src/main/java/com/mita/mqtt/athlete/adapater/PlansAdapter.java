package com.mita.mqtt.athlete.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.activity.PlansActivity;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthletePnalsModel;

import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.MyViewHolder> {
    private Context context;

    List<AthletePnalsModel> mPlansList;

    public PlansAdapter(List<AthletePnalsModel> mCoachesList) {
        this.mPlansList = mCoachesList;

    }

    @NonNull
    @Override
    public PlansAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_item_list, parent, false);

        return new PlansAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlansAdapter.MyViewHolder holder, final int position) {

        AthletePnalsModel coacheslist = mPlansList.get(position);

        holder.tv_title.setText(coacheslist.getMetaPlanTitle());
        holder.tv_line_one.setText(coacheslist.getDescription1());
        holder.tv_line_two.setText(coacheslist.getDescription2());
        holder.tv_line_three.setText(coacheslist.getDescription3());
        holder.tv_noof_coaches.setText(coacheslist.getNoOfCoachs()+" Coaches");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlansActivity) holder.itemView.getContext()).getCoachesActivity(position);
            }
        });
      /*  Glide.with(PlansAdapter.this)
                .load(mPlansList.get(position).getImage_url())
                .centerCrop()
                .placeholder(R.drawable.profile_image_coach)
                .into(holder.IvCoach);*/


    }

    @Override
    public int getItemCount() {
        if (mPlansList != null) {
            if (mPlansList.size() > 0) {
                return mPlansList.size();
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

        private TextView tv_title;
        private TextView tv_line_one;
        private TextView tv_line_two;
        private TextView tv_line_three;
        private TextView tv_noof_coaches;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_line_one = (TextView) itemView.findViewById(R.id.tv_line_one);
            tv_line_two = (TextView) itemView.findViewById(R.id.tv_line_two);
            tv_line_three = (TextView) itemView.findViewById(R.id.tv_line_three);
            tv_noof_coaches = (TextView) itemView.findViewById(R.id.tv_noof_coaches);
        }
    }


}
