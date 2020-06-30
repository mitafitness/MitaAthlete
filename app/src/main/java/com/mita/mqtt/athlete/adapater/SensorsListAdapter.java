package com.mita.mqtt.athlete.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.dashboard.SensorsListActivity;
import com.mita.mqtt.athlete.model.SensorListModel;

import java.util.ArrayList;

public class SensorsListAdapter extends RecyclerView.Adapter<SensorsListAdapter.ViewHolder> {

    Context context;
    private ArrayList<SensorListModel> sensorListModel;

    public  SensorsListAdapter(Context context, ArrayList<SensorListModel> dateListingModels) {
        this.context = context;
        this.sensorListModel = dateListingModels;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensors_item_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    public void removeAllData(){
        this.sensorListModel.clear();
        notifyDataSetChanged();
    }

    public void addNewData(ArrayList<SensorListModel> paginationModels) {
        for (final SensorListModel paginationModel : paginationModels) {
            this.sensorListModel.add(paginationModel);
            notifyItemInserted(this.sensorListModel.size() - 1);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

//        if (sensorListModel.get(position).getResponse().equals("fail")){
//
//            viewHolder.tvNoData.setVisibility(View.VISIBLE);
//
//        } else {
//            viewHolder.tvTransactionDate.setText(dateListingModels.get(position).getCreatedTime());
//
//        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ((SensorsListActivity)viewHolder.itemView.getContext()).getSensors(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sensorListModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_sensor_name;
        ImageView IvSensor;
        ImageView IvAncleSensors;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_sensor_name = itemView.findViewById(R.id.tv_sensor_name);
            IvSensor = itemView.findViewById(R.id.IvSensor);
            IvAncleSensors = itemView.findViewById(R.id.IvAncleSensors);


        }
    }

}