package com.nisnis.batp.logisticbuddy.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sebastianuskh on 9/5/16.
 */
public class RowAdapter extends RecyclerView.Adapter<RowAdapter.RowViewHolder>{

    List<Double> row;
    int numberTruck;

    public void setData(List<Double> row, int numberTruck){
        this.row = row;
        this.numberTruck = numberTruck;
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RowViewHolder(new TextView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        if(position == 0){
            holder.setText("Truck Number " + numberTruck + " : ");
        } else {
            holder.setText(String.valueOf(row.get(position - 1)));
        }
    }

    @Override
    public int getItemCount() {
        return row.size() + 1;
    }

    class RowViewHolder extends RecyclerView.ViewHolder{

        public RowViewHolder(View itemView) {
            super(itemView);
        }

        public void setText(String s) {
            ((TextView)this.itemView).setText(s);
        }
    }

}
