package com.nisnis.batp.logisticbuddy.model.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastianuskh on 9/5/16.
 */
public class VehicleRoutingAdapter extends RecyclerView.Adapter<VehicleRoutingAdapter.VehicleRoutingViewHolder> {

    private final SimpleMatrix vehicleRouting;

    public VehicleRoutingAdapter(SimpleMatrix vehicleRouting) {
        this.vehicleRouting = vehicleRouting;
    }

    @Override
    public VehicleRoutingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VehicleRoutingViewHolder(new RecyclerView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(VehicleRoutingViewHolder holder, int position) {
        List<Double> row = new ArrayList<>();
        for(int i = 0; i < vehicleRouting.numCols(); i ++){
            row.add(vehicleRouting.get(position, i));
        }
        holder.setRow(row, position + 1);
    }

    @Override
    public int getItemCount() {
        return vehicleRouting.numRows();
    }

    class VehicleRoutingViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rowMatrix;
        RowAdapter adapter;

        public VehicleRoutingViewHolder(View itemView) {
            super(itemView);
            rowMatrix = (RecyclerView) itemView;
            adapter = new RowAdapter();
            rowMatrix.setAdapter(adapter);
            rowMatrix.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }


        public void setRow(List<Double> row, int i) {
            adapter.setData(row, i);
        }
    }
}
