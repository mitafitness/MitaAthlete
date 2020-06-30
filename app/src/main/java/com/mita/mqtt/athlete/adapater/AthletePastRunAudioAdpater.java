package com.mita.mqtt.athlete.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.activity.AthletePastRunActivity;
import com.mita.mqtt.athlete.model.AthleteActivitySummaryContentModel;

import java.util.List;

public class AthletePastRunAudioAdpater extends RecyclerView.Adapter<AthletePastRunAudioAdpater.MyViewHolder> {
    private Context context;

    List<AthleteActivitySummaryContentModel> mAthleteList;
    private int pval = 0;
    private ProgressBar mSeekbaBar;

    public AthletePastRunAudioAdpater(List<AthleteActivitySummaryContentModel> mAthleteList, Context context) {
        this.mAthleteList = mAthleteList;
        this.context = context;
    }

    @NonNull
    @Override
    public AthletePastRunAudioAdpater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.athlete_past_run_audio_item_list, parent, false);
        return new AthletePastRunAudioAdpater.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AthletePastRunAudioAdpater.MyViewHolder holder, final int position) {

        AthleteActivitySummaryContentModel athleteActivitySummaryContentModel = mAthleteList.get(position);

        holder.tv_audio_duration.setText(athleteActivitySummaryContentModel.getStreDuration());

        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   ((AthletePastRunActivity)holder.itemView.getContext()).getPlayAudioClipId(position);
            }
        });


//        mSeekbaBar.setProgress(pval);
//        mSeekbaBar.post(new Runnable() {
//            @Override
//            public void run() {
//                mSeekbaBar.setProgress(pval);
//            }
//        });


        /*holder.mSeekbaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                holder.tv_audio_duration.setText(pval);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        if (mAthleteList != null) {
            if (mAthleteList.size() > 0) {
                return mAthleteList.size();
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

        ImageView iv_play;
        SeekBar mSeekbaBar;
        TextView tv_audio_duration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mSeekbaBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            iv_play = (ImageView) itemView.findViewById(R.id.iv_play);
            tv_audio_duration = (TextView) itemView.findViewById(R.id.tv_audio_duration);
        }
    }


}
